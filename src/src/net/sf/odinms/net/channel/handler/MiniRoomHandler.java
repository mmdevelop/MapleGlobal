package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.PlayerInteraction.IPlayerInteractionManager;
import net.sf.odinms.server.PlayerInteraction.MapleMiniGame;
import net.sf.odinms.server.PlayerInteraction.MapleMiniGame.MiniGameType;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Straight Edgy
 */
public class MiniRoomHandler extends AbstractMaplePacketHandler {

    public enum Action {
        CREATE(0), //Create Mini Room.
        INVITE(2), //Invite to Mini Room.
        DECLINE(3), //Decline Invite to Mini Room
        VISIT(4),
        CHAT(6), //Chat in Mini Room
        EXIT(10), //Exit Mini Room
        OPEN(11),
        SET_ITEMS(13),
        SET_MESO(14),
        CONFIRM(15),
        ADD_ITEM(18),
        BUY(20),
        //Mini Game UI
        REQUEST_DRAW(24), //Request game draw.
        RESPOND_DRAW(25), //Respond to draw request
        GIVE_UP(26), //Forfeit game.

        READY(32), //Ready to play.
        UN_READY(33), //Un-Ready to play.
        START(35), //Start Minigame

        OMOK_MOVE(38); //Place omok piece.

        byte mode;

        private Action(int mode) {
            this.mode = (byte) mode;
        }

        public byte getMode() {
            return mode;
        }

    }

    public enum Type {
        OMOK(1),
        MATCH_CARD(2),
        TRADE(3),
        PLAYER_SHOP(4);

        private byte type;

        private Type(int type) {
            this.type = (byte) type;
        }

        public byte getValue() {
            return type;
        }
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte mode = slea.readByte();
        if (mode == Action.CREATE.getMode()) { //Create Mini Room
            create(slea, c);
        } else if (mode == Action.INVITE.getMode()) { //Invite to Trade
            MapleTrade.inviteTrade(player, player.getMap().getCharacterById(slea.readInt()));
        } else if (mode == Action.DECLINE.getMode()) { //Decline Trade
            MapleTrade.declineTrade(player);
        } else if (mode == Action.VISIT.getMode()) { //Enter Miniroom
            enter(slea, c);
        } else if (mode == Action.CHAT.getMode()) { //Mini Room Chat
            chat(slea, c);
        } else if (mode == Action.EXIT.getMode()) { //Exit Mini Room
            exit(slea, c);
        } else if (mode == Action.OPEN.getMode()) {

        } else if (mode == Action.SET_ITEMS.getMode()) {
            addTradeItem(slea, c);
        } else if (mode == Action.SET_MESO.getMode()) {
            player.getTrade().setMeso(slea.readInt());
        } else if (mode == Action.CONFIRM.getMode()) {
            MapleTrade.completeTrade(player);
        } else if (mode == Action.REQUEST_DRAW.getMode()) {

        } else if (mode == Action.RESPOND_DRAW.getMode()) {

        } else if (mode == Action.GIVE_UP.getMode()) {

        } else if (mode == Action.READY.getMode()) { //Set Mini Game ready.
            player.getMiniGame().setReady();
        } else if (mode == Action.UN_READY.getMode()) { //Set Mini Game un-ready
            player.getMiniGame().setUnReady();
        } else if (mode == Action.START.getMode()) {
            player.getMiniGame().start();
        } else if (mode == Action.OMOK_MOVE.getMode()) {
            player.getMiniGame().setPiece(slea.readInt(), slea.readInt(), slea.readByte(), player);
        }
    }

    private void create(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        byte type = slea.readByte();
        if (type == Type.OMOK.getValue() || type == Type.MATCH_CARD.getValue()) {
            String description = slea.readMapleAsciiString();
            String password = null;
            if (slea.readByte() == 1) {
                password = slea.readMapleAsciiString();
            }
            int pieceType = slea.readByte();
            MapleMiniGame game = new MapleMiniGame(player, description, pieceType);
            if (type == Type.OMOK.getValue()) {
                game.setGameType(MiniGameType.OMOK);
            } else {
                if (pieceType == 0) {
                    game.setMatchesToWin(6);
                } else if (pieceType == 1) {
                    game.setMatchesToWin(10);
                } else if (pieceType == 2) {
                    game.setMatchesToWin(15);
                }
                game.setGameType(MiniGameType.MATCH_CARDS);
            }
            player.setMiniGame(game);
            game.sendMiniGame(c);
            player.getMap().addMapObject(game);
            player.getMap().broadcastMessage(CField.UserPool.MiniRoomBalloon.create(player));
        } else if (type == Type.TRADE.getValue()) {
            MapleTrade.startTrade(player);
        } else if (type == Type.PLAYER_SHOP.getValue()) {
            //TO-DO Shops
        }
    }

    private void enter(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player.getTrade() != null && player.getTrade().getPartner() != null) {
            MapleTrade.visitTrade(player, player.getTrade().getPartner().getChr());
        } else {
            MapleMapObject ob = player.getMap().getMapObject(slea.readInt());
            if (ob instanceof MapleMiniGame) {
                if (player.isAvailable()) {
                    MapleMiniGame game = (MapleMiniGame) ob;
                    if (game.hasFreeSlot()) {
                        game.addVisitor(player);
                    }
                }
            } else if (ob instanceof IPlayerInteractionManager) {
                //TO-DO Shops
            }

        }
    }

    private void exit(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player.getMiniGame() != null) {
            if (player.getMiniGame().isOwner(player)) {
                player.getMiniGame().close();
            } else {
                player.getMiniGame().removeVisitor(player);
            }
        } else if (player.getTrade() != null) {
            MapleTrade.exit(slea, c);
        }
    }

    private void chat(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        if (player.getTrade() != null) {
            player.getTrade().chat(slea.readMapleAsciiString());
        } else if (player.getMiniGame() != null) {
            player.getMiniGame().chat(slea.readMapleAsciiString(), player);
        } else if (player.getInteraction() != null) {
            //TO-DO Shops
        }
    }

    private void addTradeItem(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
        IItem item = player.getInventory(ivType).getItem((byte) slea.readShort());
        short quantity = slea.readShort();
        byte slot = slea.readByte();
        if (player.getTrade() != null && item != null) {
            if (quantity > 4000) {
                AutobanManager.getInstance().autoban(c, "Trade PE");
                return;
            }

            if (ii.isDropRestricted(item.getItemId())) {
                c.enableActions();
                return;
            }

            if (item.getQuantity() >= quantity && quantity >= 0 || ii.isThrowingStar(item.getItemId())) {
                IItem tradeItem = item.copy();
                if (ii.isThrowingStar(item.getItemId())) {
                    tradeItem.setQuantity(item.getQuantity());
                    MapleInventoryManipulator.removeFromSlot(c, ivType, item.getPosition(), item.getQuantity(), true);
                } else {
                    tradeItem.setQuantity(quantity);
                    MapleInventoryManipulator.removeFromSlot(c, ivType, item.getPosition(), quantity, true);
                }
                tradeItem.setPosition(slot);
                player.getTrade().addItem(tradeItem);
            }
        }
    }
}
