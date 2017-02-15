package net.sf.odinms.tools;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.PlayerInteraction.IPlayerInteractionManager;
import net.sf.odinms.server.PlayerInteraction.MapleMiniGame;
import net.sf.odinms.server.PlayerInteraction.MapleMiniGame.MiniGameType;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;
import net.sf.odinms.tools.packets.CharacterData;

/**
 * MapleGlobal.org - 2016
 * @Author - Straight Edgy
 */

public class CField {
	
	//OP = 48 (v12)
	public static class GroupMessage {

	    public static MaplePacket multiChat(String name, String chattext, int mode) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.GROUP_MESSAGE.getValue());
	        mplew.write(mode);
	        mplew.writeMapleAsciiString(name);
	        mplew.writeMapleAsciiString(chattext);
	        return mplew.getPacket();
	    }

	    public static class BuddyChat {
	    	
	    	public static MaplePacket chat(String from, String message) {
		        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		        mplew.write(SendPacketOpcode.GROUP_MESSAGE.getValue());
		        mplew.write(0);
		        mplew.writeMapleAsciiString(from);
		        mplew.writeMapleAsciiString(message);
		        return mplew.getPacket();
	    	}
	    	
	    }
	    
	    public static class PartyChat {
	    	
	    	public static MaplePacket chat(String from, String message) {
		        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		        mplew.write(SendPacketOpcode.GROUP_MESSAGE.getValue());
		        mplew.write(1);
		        mplew.writeMapleAsciiString(from);
		        mplew.writeMapleAsciiString(message);
		        return mplew.getPacket();
	    	}
	    	
	    }
		
	}

	//OP = 52 (v12)
	public static class BlowWeather {

		public static MaplePacket startMapEffect(String msg, int itemid, boolean active) {
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(SendPacketOpcode.BLOW_WEATHER.getValue());
			mplew.write(active ? 0 : 1);
			mplew.writeInt(itemid);
			if (active) {
				mplew.writeMapleAsciiString(msg);
			}
			return mplew.getPacket();
		}

		public static MaplePacket removeMapEffect() {
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(SendPacketOpcode.BLOW_WEATHER.getValue());
			mplew.write(0);
			mplew.writeInt(0);
			return mplew.getPacket();
		}

	}

	//OP = 61 (v12)
	public static class WarnMessage {

		/**
		 * Gets a packet displaying a pop-up with the message.
		 *
		 * @param message
		 *            - the message to display.
		 * @return
		 */
		public static MaplePacket display(String message) {
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(SendPacketOpcode.WARN_MESSAGE.getValue());
			mplew.writeMapleAsciiString(message);
			return mplew.getPacket();
		}
	}

	//OP >= 63 && <= 106 (v12)
	public static class UserPool {
		
		//OP = 66 (v12)
		public static class Chat {
			
			public static MaplePacket sendMessage(int from, String message) {
				return chat(from, message, false);
			}
			
			public static MaplePacket sendAdminMessage(int from, String message) {
				return chat(from, message, true);
			}
			
		    /**
		     * Gets a general chat packet.
		     *
		     * @param from - The character ID who sent the chat.
		     * @param message - The message to the chat.
		     * @param admin - (white background if admin).
		     * @return The general chat packet.
		     */
		    private static MaplePacket chat(int from, String message, boolean admin) {
		        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		        mplew.write(SendPacketOpcode.CHAT.getValue());
		        mplew.writeInt(from);
		        mplew.write(admin ? 1 : 0);
		        mplew.writeMapleAsciiString(message);
		        return mplew.getPacket();
		    }
			
		}
		
		//OP = 67 (v12)
		public static class MiniRoomBalloon {

			public static MaplePacket create(MapleCharacter c) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				MapleMiniGame game = c.getMiniGame();
				boolean omok = game.getGameType() == MiniGameType.OMOK;
				mplew.write(SendPacketOpcode.MINI_ROOM_BALLOON.getValue());
				mplew.writeInt(c.getId()); //dwCharacterID
				mplew.write(omok ? 1 : 2); //MiniRoomSpec
				mplew.writeInt(game.getObjectId()); //dwMiniRoomSN (Serial)
				mplew.writeMapleAsciiString(game.getDescription()); //sTitle
				mplew.write(0); //bPrivate
				mplew.write(omok ? 0 : game.getMatchesToWin() == 6 ? 2 : game.getMatchesToWin() == 10 ? 1 : 0); //nMiniRoomSpec
				mplew.write(game.hasFreeSlot() ? 1 : 2); // nCurUsers
				mplew.write(2); // nMaxUsers
				mplew.write(game.getStarted() ? 1 : 0); //bGameOn
				return mplew.getPacket();
			}

			public static MaplePacket delete(MapleCharacter c) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BALLOON.getValue());
				mplew.writeInt(c.getId()); //dwCharacterID
				mplew.write(0); //Delete
				return mplew.getPacket();
			}
		}

		//OP = 92 (v12)
		public static class Emotion {
			
			public static MaplePacket getEmotion(int cid, int emotion) {
		        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		        mplew.write(SendPacketOpcode.FACIAL_EXPRESSION.getValue());
		        mplew.writeInt(cid); //dwCharacterID
		        mplew.writeInt(emotion); //nEmotion
		        return mplew.getPacket();
			}
			
		}
		
		//OP = 97 (v12)
		public static class ReceiveHP {
			
			public static MaplePacket getPartyMemberHp(int cid, int curhp, int maxhp) {
		        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		        mplew.write(SendPacketOpcode.UPDATE_PARTYMEMBER_HP.getValue());
		        mplew.writeInt(cid);
		        mplew.writeInt(curhp);
		        mplew.writeInt(maxhp);
		        return mplew.getPacket();
			}
			
		}

	}
	
	//OP = 174 (v12)
	public static class Messenger {

		public static MaplePacket invite(String name, int id) {
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(SendPacketOpcode.MESSENGER.getValue());
			mplew.write(3);
			mplew.writeMapleAsciiString(name);
			mplew.write(0);
			mplew.writeInt(id);
			mplew.write(0);
			return mplew.getPacket();
		}

		public static MaplePacket chat(String text) {
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(SendPacketOpcode.MESSENGER.getValue());
			mplew.write(6);
			mplew.writeMapleAsciiString(text);
			return mplew.getPacket();
		}

		public static MaplePacket join(int position) {
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(SendPacketOpcode.MESSENGER.getValue());
			mplew.write(1);
			mplew.write(position);
			return mplew.getPacket();
		}

		public static MaplePacket removePlayer(int position) {
			MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
			mplew.write(SendPacketOpcode.MESSENGER.getValue());
			mplew.write(2);
			mplew.write(position);
			return mplew.getPacket();
		}

		public static class Note {

			public static MaplePacket note(String name, boolean invite, int mode) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MESSENGER.getValue());
				mplew.write(invite ? 4 : 5);
				mplew.writeMapleAsciiString(name);
				mplew.write(mode);
				return mplew.getPacket();
			}

			public static MaplePacket inviteSent(String name) {
				return note(name, true, 1);
			}

			public static MaplePacket inviteDeclined(String name) {
				return note(name, false, 0);
			}

			public static MaplePacket inviteFailed(String name) {
				return note(name, true, 0);
			}

		}

	}

	//OP = 177 (v12)
	public static class MiniRoomBase {

		public static class MiniGame {
			
			public static MaplePacket join(MapleCharacter c) {
				return create(c.getClient(), c.getMiniGame(), c.getMiniGame().getPieceType());
			}

			public static MaplePacket create(MapleClient c, MapleMiniGame game, int piece) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(5);
				boolean omok = game.getGameType() == MiniGameType.OMOK;
				mplew.write(omok ? 1 : 2); // 1 = COmokDlg::COmokDlg, 2 =
											// CMemoryGameDlg::CMemoryGameDlg
				mplew.write(omok ? 4 : 4);
				mplew.write(game.isOwner(c.getPlayer()) ? 0 : 1);
				
				mplew.write(0);
				CharacterData.addAvatarLook(mplew, game.getOwner(), true);
				mplew.writeMapleAsciiString(game.getOwner().getName());

				if (game.getVisitor() != null) {
					mplew.write(1);
					CharacterData.addAvatarLook(mplew, game.getVisitor(), true);
					mplew.writeMapleAsciiString(game.getVisitor().getName());
				}
				mplew.write(0xFF);

				mplew.write(0); //Slot 0 (Owner Slot)
				mplew.writeInt(omok ? 1 : 2); // Game Type (Omok = 1, Match Card = 2)
				mplew.writeInt(game.getOmokPoints("wins", true));
				mplew.writeInt(game.getOmokPoints("ties", true));
				mplew.writeInt(game.getOmokPoints("losses", true));
				mplew.writeInt(2000);

				if (game.getVisitor() != null) {
					mplew.write(1); //Slot 1 (Visitor Slot)
					mplew.writeInt(omok ? 1 : 2); // Game Type (Omok = 1, Match Card = 2)
					mplew.writeInt(game.getOmokPoints("wins", false));
					mplew.writeInt(game.getOmokPoints("ties", false));
					mplew.writeInt(game.getOmokPoints("losses", false));
					mplew.writeInt(2000);
				}
				mplew.write(0xFF);
				
				mplew.writeMapleAsciiString(game.getDescription());
				mplew.write(piece); //mplew.write(piece);
				
				mplew.write(0);
				mplew.write(0);
				return mplew.getPacket();

			}

			public static MaplePacket removeVisitor() {
				final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(10); // Leave
				mplew.write(1); // Position
				return mplew.getPacket();
			}

			public static MaplePacket addVisitor(MapleCharacter c) {
				final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(4); // Visit
				mplew.write(1); // Slot
				CharacterData.addAvatarLook(mplew, c, true);
				mplew.writeMapleAsciiString(c.getName());
				mplew.writeInt(1);
				MapleMiniGame game = c.getMiniGame();
				mplew.writeInt(game.getOmokPoints("wins", false));
				mplew.writeInt(game.getOmokPoints("ties", false));
				mplew.writeInt(game.getOmokPoints("losses", false));
				mplew.writeInt(2000);
				return mplew.getPacket();
			}
			
			public static MaplePacket ready() {
				final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(32); // Set ready.
				return mplew.getPacket();			
			}
			
			public static MaplePacket unReady() {
				final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(33); // Set ready.
				return mplew.getPacket();			
			}
			
		    public static MaplePacket start(int loser) {
		        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
		        mplew.write(35);
		        mplew.write(loser == 0 ? 1 : 0);
		        return mplew.getPacket();
		    }
		    
		    public static MaplePacket omokMove(int x, int y, int piece) {
		        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
		        mplew.write(38);
		        mplew.writeInt(x);
		        mplew.writeInt(y);
		        mplew.write(piece);
		        return mplew.getPacket();
		    }
		}

		public static class Trade {

			public static MaplePacket invite(MapleCharacter from) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(2);
				mplew.write(3);
				mplew.writeMapleAsciiString(from.getName());
				mplew.writeInt(123456); // Room ID, doesn't really matter...(?)
				return mplew.getPacket();
			}

			public static MaplePacket addPartner(MapleCharacter c) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(4);
				mplew.write(1);
				CharacterData.addAvatarLook(mplew, c, true);
				mplew.writeMapleAsciiString(c.getName());
				return mplew.getPacket();
			}

			/**
			 * @param c
			 *            - MapleClient the client connecting to / creating the
			 *            trade.
			 * @param trade
			 *            - MapleTrade the trade the client is connecting to.
			 * @param join
			 *            - boolean user joining existing, or creating new
			 *            trade.
			 */

			public static MaplePacket start(MapleClient c, MapleTrade trade, boolean join) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(5);
				mplew.write(3);
				mplew.write(2);
				mplew.write(join ? 1 : 0);
				if (join) {
					mplew.write(0);
					CharacterData.addAvatarLook(mplew, trade.getPartner().getChr(), true);
					mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
				}
				mplew.write(join ? 1 : 0);
				CharacterData.addAvatarLook(mplew, c.getPlayer(), true);
				mplew.writeMapleAsciiString(c.getPlayer().getName());
				mplew.write(0xFF);
				return mplew.getPacket();
			}

			public static MaplePacket cancelTrade(byte pos) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(10);
				mplew.write(pos);
				mplew.write(2);
				return mplew.getPacket();
			}

			public static MaplePacket completeTrade(byte pos, boolean success) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(10);
				mplew.write(pos);
				mplew.write(success ? 5 : 6);
				return mplew.getPacket();
			}

			public static MaplePacket addItem(byte pos, IItem item) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(13);
				mplew.write(pos);
				mplew.write(item.getPosition());
				mplew.write(item.getType());
				MaplePacketCreator.addItemInfo(mplew, item, false, true);
				return mplew.getPacket();
			}

			public static MaplePacket setMesos(byte pos, int meso) {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(14);
				mplew.write(pos);
				mplew.writeInt(meso);
				return mplew.getPacket();
			}

			public static MaplePacket confirmTrade() {
				MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
				mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
				mplew.write(15);
				return mplew.getPacket();
			}

		}

	}
}
