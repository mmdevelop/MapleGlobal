package net.sf.odinms.tools;

import java.util.Collection;
import java.util.stream.Collectors;

import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * MapleGlobal.org - 2016
 *
 * @Author - Straight Edgy
 */
public class CWvsContext {

    //OP = 27 (v12)
    public static class GivePopularityResult {

        /**
         * 0: ok, use giveFameResponse<br>
         * 1: The user name is incorrectly entered. 2: Users under level 15 are
         * unable to toggle with fame. 3: You can't raise or drop a level of
         * fame anymore for today. 4: You can't raise or drop a level of fame of
         * that character anymore for this month. 5: received fame, use
         * receiveFame()<br>
         * 6+: The level of fame has neither been raised or dropped due to an
         * unexpected error.
         */
        public static MaplePacket getError(int status) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.GIVE_POPULARITY_RESULT.getValue());
            mplew.writeInt(status);
            return mplew.getPacket();
        }

        /**
         * Packet sent to recipient of fame.
         *
         * @param from - The MapleCharacter sending the fame.
         * @param mode - Increase or decrease.
         */
        public static MaplePacket receivePopularity(MapleCharacter from, int mode) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.GIVE_POPULARITY_RESULT.getValue());
            mplew.write(5);
            mplew.writeMapleAsciiString(from.getName());
            mplew.write(mode);
            return mplew.getPacket();
        }

        /**
         * Packet sent to giver of fame.
         *
         * @param from - The MapleCharacter receiving fame.
         * @param mode - Increase or decrease.
         * @param newFame - The new amount of fame.
         */
        public static MaplePacket givePopularity(MapleCharacter to, int mode, int newFame) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.GIVE_POPULARITY_RESULT.getValue());
            mplew.write(0);
            mplew.writeMapleAsciiString(to.getName());
            mplew.write(mode);
            mplew.writeInt(newFame);
            return mplew.getPacket();
        }

    }

    public static class Message {

        public static class QuestRecordMessage {

            public static MaplePacket update(MapleQuestStatus quest) {
                MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                mplew.write(1); //Quest Record Message
                mplew.write(1); //Update
                mplew.writeInt(quest.getQuest().getId());
                mplew.writeMapleAsciiString(quest.getData());
                return mplew.getPacket();
            }

        }

    }

    //OP = 35 (v12)
    public static class PartyResult {

        public enum Result {
            INVITE(4),
            //6 is what? decode4
            CREATE(7), //decode4, decode4, decode4, decode2, decode2
            ALREADY_JOINED(8), // Already have joined a party.
            BEGINNER_CANT_CREATE(9), // A beginner can't create a party.
            REQUEST_DID_NOT_WORK(10), // Your request for a party didn't work due to an unexpected error.
            YET_TO_JOIN_A_PARTY(12), // You have yet to join a party.
            PARTY_FULL(16), // The party you're trying to join is already in full capacity.
            NOT_ACCEPTING(19), // %s is currently blocking any party invitations.
            TAKING_CARE_OF_ANOTHER(20), // %s is taking care of another invitation.
            DENIED_REQUEST(21); // %s have denied request to the party.

            private int mode;

            private Result(int mode) {
                this.mode = mode;
            }

            public int getValue() {
                return mode;
            }
        }

        /**
         * Mode: 7 = DC 8 = Already have joined a party. 9 = A beginner can't
         * create a party. 10 = Your request for a party didn't work due to an
         * unexpected error. 11 = DC 12 = You have yet to join a party. 13 =
         * Your request for a party didn't work due to an unexpected error. 14 =
         * DC 15 = Already have joined a party. 16 = The party you're trying to
         * join is already in full capacity. 17 = Your request for a party
         * didn't work due to an unexpected error. 18 = Your request for a party
         * didn't work due to an unexpected error. 19 =
         *
         * @return
         */
        public static MaplePacket partyResult(Result result) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.PARTY_RESULT.getValue());
            mplew.write(result.getValue());
            return mplew.getPacket();
        }

        public static MaplePacket partyResult(Result result, String name) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.PARTY_RESULT.getValue());
            mplew.write(result.getValue());
            mplew.writeMapleAsciiString(name);
            return mplew.getPacket();
        }

        public static MaplePacket invite(MapleCharacter from) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.PARTY_RESULT.getValue());
            mplew.write(Result.INVITE.getValue());
            mplew.writeInt(from.getParty().getId());
            mplew.writeMapleAsciiString(from.getName());
            return mplew.getPacket();
        }

        public static MaplePacket notAcceptingInvitations(String name) {
            return partyResult(Result.NOT_ACCEPTING, name);
        }

        public static MaplePacket takingCareOfAnotherInvitation(String name) {
            return partyResult(Result.TAKING_CARE_OF_ANOTHER, name);
        }

        public static MaplePacket deniedRequest(String name) {
            return partyResult(Result.DENIED_REQUEST, name);
        }

        public static MaplePacket createParty(MapleCharacter ldr) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.PARTY_RESULT.getValue());
            mplew.write(Result.CREATE.getValue());
            mplew.writeInt(ldr.getId());
            mplew.writeInt(ldr.getPartyId());
            mplew.writeInt(1);
            mplew.writeShort(1);
            mplew.writeShort(1);
            return mplew.getPacket();
        }

        public static MaplePacket disbandParty(MapleParty party, MaplePartyCharacter target) {

            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.PARTY_RESULT.getValue());
            mplew.write(11);

            mplew.writeInt(party.getId()); //Party ID???
            mplew.writeInt(target.getId()); //The target player ID
            mplew.write(0);
            return mplew.getPacket();
        }
    }

    //OP = 36 (v12)
    public static class FriendResult {

        public enum Result {
            UPDATE(7),
            INVITE(9),
            BUDDY_LIST_FULL(11), //Your buddy list is full.
            TARGET_BUDDY_LIST_FULL(12), //The user's buddy list is full
            ALREADY_BUDDY(13), //That character is already registered as your buddy.
            GAME_MASTER(14), //Gamemaster is not available as a buddy.
            NOT_REGISTRERED(15), //That character is not registered.
            DUE_TO_UNKNOWN_ERROR(16), //Also 17, 19 & 22.
            CHANGE_CHANNEL(20),
            UPDATE_CAPACITY(21);
            private int mode;

            private Result(int mode) {
                this.mode = mode;
            }

            public int getValue() {
                return mode;
            }
        }

        public static MaplePacket invite(int cidFrom, String from) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.FRIEND_RESULT.getValue());
            mplew.write(Result.INVITE.getValue());
            mplew.writeInt(cidFrom);
            mplew.writeMapleAsciiString(from);
            mplew.writeInt(cidFrom);
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(from, '\0', 13));
            mplew.write(1);
            mplew.writeInt(0);// ?
            mplew.write(1);
            return mplew.getPacket();
        }

        public static MaplePacket getBuddyList(Collection<BuddylistEntry> buddylist) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.FRIEND_RESULT.getValue());
            mplew.write(Result.UPDATE.getValue());
            mplew.write(buddylist.size());
            for (BuddylistEntry buddy : buddylist) {
                if (buddy.isVisible()) {
                    mplew.writeInt(buddy.getCharacterId()); // cid
                    mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getName(), '\0', 13));
                    mplew.write(0);
                    mplew.writeInt(buddy.getChannel() - 1);
                }
            }
            for (int x = 0; x < buddylist.size(); x++) {
                mplew.writeInt(0);
            }
            return mplew.getPacket();
        }

        public static MaplePacket friendChangeChannel(int cid, int channel) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.FRIEND_RESULT.getValue());
            mplew.write(Result.CHANGE_CHANNEL.getValue());
            mplew.writeInt(cid);
            mplew.write(0);
            mplew.writeInt(channel);
            return mplew.getPacket();
        }

        public static MaplePacket updateBuddyCapacity(int slots) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.FRIEND_RESULT.getValue());
            mplew.write(Result.UPDATE_CAPACITY.getValue());
            mplew.write(slots);
            return mplew.getPacket();
        }

        public static MaplePacket getErrorMessage(Result result) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.FRIEND_RESULT.getValue());
            mplew.write(result.getValue());
            return mplew.getPacket();
        }

    }

    //OP = 38 (v12)
    public static class BroadcastMsg {

        /**
         * Possible values for <code>type</code>:<br>
         * 0: [Notice] (BLUE)<br>
         * 1: Popup<br>
         * 2: Blue Text Light Blue BG<br>
         * 3: Super Megaphone<br>
         * 4: Scrolling message at top<br>
         */
        public static MaplePacket sendNotice(String message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.BROADCAST_MSG.getValue());
            mplew.write(0); //Notice
            mplew.writeMapleAsciiString(message); //Message
            return mplew.getPacket();
        }

        public static MaplePacket sendMegaphone(String message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.BROADCAST_MSG.getValue());
            mplew.write(2); //Megaphone
            mplew.writeMapleAsciiString(message); //Message
            return mplew.getPacket();
        }

        public static MaplePacket sendSuperMegaphone(String message, int channel, boolean icon) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.BROADCAST_MSG.getValue());
            mplew.write(3); //Megaphone
            mplew.writeMapleAsciiString(message); //Message
            mplew.write(channel - 1); //Channel sent from
            mplew.write(icon ? 1 : 0); //Display whisper icon
            return mplew.getPacket();
        }

        /**
         * Scrolling Server Message (top of the screen).
         */
        public static MaplePacket sendServerMessage(String message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(SendPacketOpcode.BROADCAST_MSG.getValue());
            mplew.write(4); //Server Message   
            mplew.write(1); //boolean for (?) (1 = display, 0 = hide (?))
            mplew.writeMapleAsciiString(message); //Message
            return mplew.getPacket();
        }

    }
}
