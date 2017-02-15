package net.sf.odinms.net.channel.handler;

import static net.sf.odinms.client.BuddyList.BuddyOperation.DELETED;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.odinms.client.BuddyList;
import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.CharacterNameAndId;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.BuddyList.BuddyAddResult;
import net.sf.odinms.client.BuddyList.BuddyOperation;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.remote.ChannelWorldInterface;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.CWvsContext;
import net.sf.odinms.tools.CWvsContext.FriendResult;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class FriendOperationHandler extends AbstractMaplePacketHandler {
	
	private static class CIDNameBuddyCapacity extends CharacterNameAndId {
		private int buddyCapacity;

		public CIDNameBuddyCapacity(int id, String name, int buddyCapacity) {
			super(id, name);
			this.buddyCapacity = buddyCapacity;
		}

		public int getBuddyCapacity() {
			return buddyCapacity;
		}
	}
	
	private void nextPendingRequest(MapleClient c) {
		CharacterNameAndId pendingRequest = c.getPlayer().getBuddylist().pollPendingRequest();
		if (pendingRequest != null) {
			c.announce(CWvsContext.FriendResult.invite(pendingRequest.getId(), pendingRequest.getName()));
		}
	}
	
	private CIDNameBuddyCapacity getCharacterIdAndNameFromDatabase(String name) throws SQLException {
		Connection con = DatabaseConnection.getConnection();
		PreparedStatement ps = con.prepareStatement("SELECT id, name, buddyCapacity FROM characters WHERE name LIKE ?");
		ps.setString(1, name);
		ResultSet rs = ps.executeQuery();
		CIDNameBuddyCapacity ret = null;
		if (rs.next()) {
			ret = new CIDNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), rs.getInt("buddyCapacity"));
		}
		rs.close();
		ps.close();
                con.close();
		return ret;		
	}

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		WorldChannelInterface worldInterface = c.getChannelServer().getWorldInterface();
		MapleCharacter player = c.getPlayer();
		BuddyList buddylist = player.getBuddylist();
		byte mode = slea.readByte();
		
		try {
			if (mode == 1) { //Add Friend
				String name = slea.readMapleAsciiString();	
				BuddylistEntry entry = buddylist.get(name);
				if (entry != null && !entry.isVisible()) { // Already in Buddy List
					c.announce(CWvsContext.FriendResult.getErrorMessage(FriendResult.Result.ALREADY_BUDDY));
				} else if (buddylist.isFull()) { // Buddy List is full
					c.announce(CWvsContext.FriendResult.getErrorMessage(FriendResult.Result.BUDDY_LIST_FULL));
				} else {
					int channel;
					CIDNameBuddyCapacity charWithId = null;
					MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
					if (target != null) {
						channel = c.getChannel();
						charWithId = new CIDNameBuddyCapacity(target.getId(), target.getName(), target.getBuddyCapacity());
					} else {
						channel = worldInterface.find(name);
						charWithId = getCharacterIdAndNameFromDatabase(name);
					}
					
					if (charWithId != null) {
						BuddyAddResult buddyAddResult = null;
						if (channel != -1) {
							ChannelWorldInterface channelInterface = worldInterface.getChannelInterface(channel);
							buddyAddResult = channelInterface.requestBuddyAdd(name, c.getChannel(), player.getId(), player.getName());
						} else {
							Connection con = DatabaseConnection.getConnection();
							PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
							ps.setInt(1, charWithId.getId());
							ResultSet rs = ps.executeQuery();
							if (!rs.next()) {
								throw new RuntimeException("Result set expected");
							} else {
								int count = rs.getInt("buddyCount");
								if (count >= charWithId.getBuddyCapacity()) {
									buddyAddResult = BuddyAddResult.BUDDYLIST_FULL;
								}
							}
							rs.close();
							ps.close();
							ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
							ps.setInt(1, charWithId.getId());
							ps.setInt(2, player.getId());
							rs = ps.executeQuery();
							if (rs.next()) {
								buddyAddResult = BuddyAddResult.ALREADY_ON_LIST;
							}
							rs.close();
							ps.close();
                                                        con.close();
						}
						
						if (buddyAddResult == BuddyAddResult.BUDDYLIST_FULL) {
							c.announce(CWvsContext.FriendResult.getErrorMessage(FriendResult.Result.TARGET_BUDDY_LIST_FULL));
						} else {
							int displayChannel = -1;
							if (buddyAddResult == BuddyAddResult.ALREADY_ON_LIST && channel != -1) {
								displayChannel = channel;
								notifyRemoteChannel(c, channel, charWithId.getId(), BuddyOperation.ADDED);
							} else if (buddyAddResult != BuddyAddResult.ALREADY_ON_LIST && channel == -1) {
								Connection con = DatabaseConnection.getConnection();
								PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, 1)");
								ps.setInt(1, charWithId.getId());
								ps.setInt(2, player.getId());
								ps.executeUpdate();
								ps.close();
                                                                con.close();
							}
							buddylist.put(new BuddylistEntry(charWithId.getName(), charWithId.getId(), displayChannel, true));
							c.announce(CWvsContext.FriendResult.getBuddyList(buddylist.getBuddies()));
						}
					} else {
						c.announce(CWvsContext.FriendResult.getErrorMessage(FriendResult.Result.NOT_REGISTRERED));
					}
				}		
			} else if (mode == 2) { //Accept
				int cid = slea.readInt();
				if (!buddylist.isFull()) {
					int channel = worldInterface.find(cid);
					String name = null;
					MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterById(cid);
					if (chr == null) {
						Connection con = DatabaseConnection.getConnection();
						PreparedStatement ps = con.prepareStatement("SELECT name FROM characters WHERE id = ?");
						ps.setInt(1, cid);
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							name = rs.getString("name");
						}
						rs.close();
						ps.close();
                                                con.close();
					} else {
						name = chr.getName();
					}
					
					if (name != null) {
						buddylist.put(new BuddylistEntry(name, cid, channel, true));
						c.announce(CWvsContext.FriendResult.getBuddyList(buddylist.getBuddies()));
						notifyRemoteChannel(c, channel, cid, BuddyOperation.ADDED);
					}
				} else {
					c.announce(CWvsContext.FriendResult.getErrorMessage(FriendResult.Result.BUDDY_LIST_FULL));
				}
				nextPendingRequest(c);
			} else if (mode == 3) { //Delete
				int targetID = slea.readInt();
				if (buddylist.containsVisible(targetID)) {
					try {
						notifyRemoteChannel(c, worldInterface.find(targetID), targetID, DELETED);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				buddylist.remove(targetID);
				c.announce(CWvsContext.FriendResult.getBuddyList(buddylist.getBuddies()));
				nextPendingRequest(c);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, BuddyOperation operation)
			throws RemoteException {
		WorldChannelInterface worldInterface = c.getChannelServer().getWorldInterface();
		MapleCharacter player = c.getPlayer();

		if (remoteChannel != -1) {
			ChannelWorldInterface channelInterface = worldInterface.getChannelInterface(remoteChannel);
			channelInterface.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation);
		}
	}
}
