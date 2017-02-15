package net.sf.odinms.server.PlayerInteraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.maps.AbstractMapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapleMiniGame extends AbstractMapleMapObject {

    private MapleCharacter owner;
    private MapleCharacter visitor;
    
    private String description;
    
    private int pieceType;
    
    private MiniGameType gameType;
    private int[] piece = new int[250];
    private List<Integer> list4x3 = new ArrayList<Integer>();
    private List<Integer> list5x4 = new ArrayList<Integer>();
    private List<Integer> list6x5 = new ArrayList<Integer>();
    boolean ready = false;
    private int loser = 1;
    private boolean started; // 0 = waiting, 1 = in progress
    private int firstslot = 0;
    private int visitorpoints = 0;
    private int ownerpoints = 0;
    private int matchestowin = 0;

    public enum MiniGameType {
        OMOK, MATCH_CARDS
    }

    public MapleMiniGame(MapleCharacter owner, String description, int type) {
        this.owner = owner;
        this.pieceType = type;
        this.description = description;
    }


    public void close() {
        owner.getMap().broadcastMessage(CField.UserPool.MiniRoomBalloon.delete(owner));
        owner.getMap().removeMapObject(this);
    	owner.setMiniGame(null);
    }

    public void setStarted(boolean start) {
        started = start;
    }

    public boolean getStarted() {
        return started;
    }
    
    public boolean isOwner(MapleCharacter c) {
    	return owner.equals(c);
    }

    public void setFirstSlot(int type) {
        firstslot = type;
    }

    public int getFirstSlot() {
        return firstslot;
    }
    
    public String getDescription() {
    	return description;
    }
    
    public MapleCharacter getVisitor() {
    	return visitor;
    }
    
    public void setVisitor(MapleCharacter visitor) {
    	this.visitor = visitor;
    }
    
    public void removeVisitor(MapleCharacter player) {
        if (visitor == player) {
        	player.setMiniGame(null);
            visitor = null;
            this.getOwner().getClient().announce(CField.MiniRoomBase.MiniGame.removeVisitor());
            this.owner.getMap().broadcastMessage(CField.UserPool.MiniRoomBalloon.create(owner));
        }
    }
    
    public void addVisitor(MapleCharacter player) {
    	if (visitor == null) {
    		visitor = player;
            player.setMiniGame(this);
    		this.owner.getClient().announce(CField.MiniRoomBase.MiniGame.addVisitor(visitor));
            this.owner.getMap().broadcastMessage(CField.UserPool.MiniRoomBalloon.create(owner));
            player.getClient().announce(CField.MiniRoomBase.MiniGame.join(player));
    	}
    }
    
    public boolean hasFreeSlot() {
        return visitor == null;
    }
    
    public void broadcast(MaplePacket packet) {
    	if (owner.getClient() != null && owner.getClient().getSession() != null) {
    		owner.getClient().announce(packet);
    	}
    	
    	if (visitor != null) {
    		visitor.getClient().announce(packet);
    	}
    }
    
    public void broadcast(MaplePacket packet, boolean showOwner) {
    	if (showOwner) {
	    	broadcast(packet);
    	} else {
        	if (visitor.getClient() != null && visitor.getClient().getSession() != null) {
        		visitor.getClient().announce(packet);
        	}
    	}
    }

    public void setOwnerPoints() {
        ownerpoints++;
        if (ownerpoints + visitorpoints == matchestowin) {
            if (ownerpoints == visitorpoints) {
                broadcast(MaplePacketCreator.getMiniGameTie(this));
            } else if (ownerpoints > visitorpoints) {
                broadcast(MaplePacketCreator.getMiniGameWin(this, 0));
            } else if (visitorpoints > ownerpoints) {
                broadcast(MaplePacketCreator.getMiniGameWin(this, 1));
            }
        }
        ownerpoints = 0;
        visitorpoints = 0;
    }

    public void setVisitorPoints() {
        visitorpoints++;
        if ((ownerpoints + visitorpoints) == matchestowin) {
            if (ownerpoints > visitorpoints) {
                broadcast(MaplePacketCreator.getMiniGameWin(this, 0));
            } else if (visitorpoints > ownerpoints) {
                broadcast(MaplePacketCreator.getMiniGameWin(this, 1));
            } else if (ownerpoints == visitorpoints) {
                broadcast(MaplePacketCreator.getMiniGameTie(this));
            }
        }
        ownerpoints = 0;
        visitorpoints = 0;
    }

    public int getOwnerPoints() {
        return ownerpoints;
    }

    public void setMatchCardPoints(int winnerslot) { // 1 = owner, 2 = visitor 3 = tie
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;
        try {
            if (winnerslot < 3) {
                ps = con.prepareStatement("UPDATE characters SET matchcardwins = matchcardwins + 1 + WHERE name = ?");
                if (winnerslot == 1) {
                    ps.setString(1, owner.getName());
                } else if (winnerslot == 2) {
                    ps.setString(1, visitor.getName());
                }
                ps.executeUpdate();
                ps.close();

                ps = con.prepareStatement("UPDATE characters SET matchcardlosses = matchcardlosses + 1 WHERE name = ?");
                if (winnerslot == 1) {
                    ps.setString(1, visitor.getName());
                } else if (winnerslot == 2) {
                    ps.setString(1, owner.getName());
                }
                ps.executeUpdate();
                ps.close();
            } else if (winnerslot == 3) {
                ps = con.prepareStatement("UPDATE characters SET matchcardties = matchcardties + 1 WHERE name = ? OR name = ?");
                ps.setString(1, owner.getName());
                ps.setString(2, visitor.getName());
                ps.executeUpdate();
                ps.close();
            }
            con.close();
        } catch (SQLException e) {
            owner.dropMessage("Exception has occured: " + e);
            return;
        }
    }

    public int getOmokPoints(String type, boolean getOwner) { // wins, losses, ties
        Connection con = DatabaseConnection.getConnection();
        int points = 0;

        try {
            PreparedStatement ps = con.prepareStatement("SELECT omok" + type + " FROM characters WHERE name = ?");
            ps.setString(1, owner.getName());
            ResultSet rs = ps.executeQuery();
            rs.next();
            points = rs.getInt("omok" + type);
            rs.close();
            ps.close();
            con.close();
            return points;
        } catch (SQLException e) {
            owner.dropMessage("Exception has occured: " + e);
        }
        return points;
    }

    public int getMatchCardPoints(String type) { // wins, losses, ties
        Connection con = DatabaseConnection.getConnection();
        int points = 0;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT matchcard" + type + " FROM characters WHERE name = ?");
            ps.setString(1, owner.getName());
            ResultSet rs = ps.executeQuery();
            rs.next();
            points = rs.getInt("matchcard" + type);
            rs.close();
            ps.close();
            con.close();
            return points;
        } catch (SQLException e) {
            owner.dropMessage("Exception has occured: " + e);
        }
        return points;
    }

    public void setOmokPoints(int winnerslot) { // 1 = owner, 2 = visitor 3 = tie
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps;

        try {
            if (winnerslot < 3) {
                ps = con.prepareStatement("UPDATE characters SET omokwins = omokwins + 1 WHERE name = ?");
                if (winnerslot == 1) {
                    ps.setString(1, owner.getName());
                }
                if (winnerslot == 2) {
                    ps.setString(1, visitor.getName());
                }
                ps.executeUpdate();
                ps.close();
                ps = con.prepareStatement("UPDATE characters SET omoklosses = omoklosses + 1 WHERE name = ?");
                if (winnerslot == 1) {
                    ps.setString(1, visitor.getName());
                }
                if (winnerslot == 2) {
                    ps.setString(1, owner.getName());
                }
                ps.executeUpdate();
                ps.close();
            } else if (winnerslot == 3) {
                ps = con.prepareStatement("UPDATE characters SET omokties = omokties + 1 WHERE name = ? OR name = ?");
                ps.setString(1, owner.getName());
                ps.setString(2, visitor.getName());
                ps.executeUpdate();
                ps.close();
            }
            con.close();
        } catch (SQLException e) {
            return;
        }
    }

    public int getVisitorPoints() {
        return ownerpoints;
    }

    public void setMatchesToWin(int type) {
        matchestowin = type;
    }

    public void setGameType(MiniGameType game) {
        gameType = game;
        if (game == MiniGameType.MATCH_CARDS) {
            if (matchestowin == 6) {
                for (int i = 0; i < matchestowin; i++) {
                    list4x3.add(i);
                    list4x3.add(i);
                }
            } else if (matchestowin == 10) {
                for (int i = 0; i < matchestowin; i++) {
                    list5x4.add(i);
                    list5x4.add(i);
                }
            } else if (matchestowin == 15) {
                for (int i = 0; i < matchestowin; i++) {
                    list6x5.add(i);
                    list6x5.add(i);
                }
            }
        }
    }

    public MiniGameType getGameType() {
        return gameType;
    }

    public void shuffleList() {
        if (matchestowin == 6) {
            Collections.shuffle(list4x3);
        } else if (matchestowin == 10) {
            Collections.shuffle(list5x4);
        } else if (matchestowin == 15) {
            Collections.shuffle(list6x5);
        }
    }

    public int getCardId(int slot) {
        int cardid = 0;
        if (matchestowin == 6) {
            cardid = list4x3.get(slot - 1);
        } else if (matchestowin == 10) {
            cardid = list5x4.get(slot - 1);
        } else if (matchestowin == 15) {
            cardid = list6x5.get(slot - 1);
        }
        return cardid;
    }

    public int getMatchesToWin() {
        return matchestowin;
    }

    public void setLoser(int type) {
        loser = type;
    }

    public int getLoser() {
        return loser;
    }

    public MapleCharacter getOwner() {
        return owner;
    }

    public void setReady() {
        ready = true;
    	broadcast(CField.MiniRoomBase.MiniGame.ready());
    }
    
    public void setUnReady() {
    	ready = false;
    	broadcast(CField.MiniRoomBase.MiniGame.unReady());
    }
    
    public void start() {
    	if (getGameType() == MiniGameType.OMOK) {
    		broadcast(CField.MiniRoomBase.MiniGame.start(getLoser()));
    	} else {
    		shuffleList();
    		broadcast(MaplePacketCreator.getMatchCardStart(this));
    	}
    	setStarted(true);
    }

    public boolean isReady() {
        return ready;
    }

    public void setPiece(int move1, int move2, int type, MapleCharacter chr) {
        int slot = ((move2 * 15) + (move1 + 1));
        if (piece[slot] == 0) {
            piece[slot] = type;
            broadcast(CField.MiniRoomBase.MiniGame.omokMove(move1, move2, type));
            for (int y = 0; y < 15; y++) {
                for (int x = 0; x < 11; x++) {
                    if (searchCombo(x, y, type)) {
                        if (isOwner(chr)) {
                            broadcast(MaplePacketCreator.getMiniGameWin(this, 0));
                            setStarted(false);
                            setLoser(0);
                        } else {
                            broadcast(MaplePacketCreator.getMiniGameWin(this, 1));
                            this.setStarted(false);
                            this.setLoser(1);
                        }
                        for (int y2 = 0; y2 < 15; y2++) {
                            for (int x2 = 0; x2 < 15; x2++) {
                                int slot2 = ((y2 * 15) + (x2 + 1));
                                piece[slot2] = 0;

                            }
                        }
                    }
                }
            }
            for (int y = 0; y < 15; y++) {
                for (int x = 4; x < 15; x++) {
                    if (searchCombo2(x, y, type)) {
                        if (isOwner(chr)) {
                            broadcast(MaplePacketCreator.getMiniGameWin(this, 0));
                            setStarted(false);
                            setLoser(0);
                        } else {
                            broadcast(MaplePacketCreator.getMiniGameWin(this, 1));
                            setStarted(false);
                            setLoser(1);
                        }
                        for (int y2 = 0; y2 < 15; y2++) {
                            for (int x2 = 0; x2 < 15; x2++) {
                                int slot2 = ((y2 * 15) + (x2 + 1));
                                piece[slot2] = 0;

                            }
                        }
                    }
                }
            }
        }

    }

    public boolean searchCombo(int x, int y, int type) {
        boolean winner = false;
        int slot = ((y * 15) + (x + 1));
        if (piece[slot] == type) {
            if (piece[slot + 1] == type) {
                if (piece[slot + 2] == type) {
                    if (piece[slot + 3] == type) {
                        if (piece[slot + 4] == type) {
                            winner = true;
                        }
                    }
                }
            }
        }
        if (piece[slot] == type) {
            if (piece[slot + 16] == type) {
                if (piece[slot + 32] == type) {
                    if (piece[slot + 48] == type) {
                        if (piece[slot + 64] == type) {
                            winner = true;
                        }
                    }
                }
            }
        }
        if (piece[slot] == type) {
            if (piece[slot + 15] == type) {
                if (piece[slot + 30] == type) {
                    if (piece[slot + 45] == type) {
                        if (piece[slot + 60] == type) {
                            winner = true;
                        }
                    }
                }
            }
        }
        return winner;
    }

    public boolean searchCombo2(int x, int y, int type) {
        boolean winner = false;
        int slot = ((y * 15) + (x + 1));
        if (piece[slot] == type) {
            if (piece[slot + 15] == type) {
                if (piece[slot + 30] == type) {
                    if (piece[slot + 45] == type) {
                        if (piece[slot + 60] == type) {
                            winner = true;
                        }
                    }
                }
            }
        }
        if (piece[slot] == type) {
            if (piece[slot + 14] == type) {
                if (piece[slot + 28] == type) {
                    if (piece[slot + 42] == type) {
                        if (piece[slot + 56] == type) {
                            winner = true;
                        }
                    }
                }
            }
        }
        return winner;
    }

    public void sendMiniGame(MapleClient c) {
    	c.announce(CField.MiniRoomBase.MiniGame.create(c, this, pieceType));
    }
    
    public void chat(String message, MapleCharacter player) {
		owner.getClient().announce(MaplePacketCreator.shopChat(player.getName() + " : " + message, 0));
		if (visitor != null) {
			visitor.getClient().announce(MaplePacketCreator.shopChat(player.getName() + " : " + message, 1));
		}
    }
    
    @Override
    public void sendDestroyData(MapleClient client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.SHOP;
    }
    
    public void setPieceType(int type) {
        pieceType = type;
    }

    public int getPieceType() {
        return pieceType;
    }
}