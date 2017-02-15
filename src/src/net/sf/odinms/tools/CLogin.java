package net.sf.odinms.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.login.LoginServer;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;
import net.sf.odinms.tools.packets.CharacterData;

public class CLogin {

	//OP = 1 (v12)
	public static class CheckPasswordResult {
		
	    /**
	     * 0: RecvOP 8, Pin Request Handler <br>
	     * 12: RecvOP 8, Pin Request Handler <br>
	     * 19: EULA Terms (RecvOP 6) <br>
	     */
	    public static MaplePacket getAuthSuccess(MapleClient c) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.CHECK_PASSWORD_RESULT.getValue());
	        mplew.write(0); //Login state
	        mplew.write(0); //nRegStatID
	        mplew.writeInt(0); //nUseDay
	        mplew.writeInt(c.getAccID()); //Account id
	        mplew.write(c.getGender()); //Gender (if gender = 10, gender selection, RecvOP 7)
	        mplew.write(c.isGm() ? 1 : 0); //Player.Admin (GradeCode)
	        mplew.write(1); //Country ID
	        mplew.writeMapleAsciiString(c.getAccountName()); //Username
	        mplew.write(0); //Purchase Exp
	        mplew.write(0); //Chatblock Reason 1-5
	        mplew.writeLong(0); //Chat Unlock Date
	        return mplew.getPacket();
	    }
	    

	    /**
	     * Gets a auth failed packet.
	     *
	     * Possible values for <code>reason</code>:<br>
	     * 3: This is an ID that has been deleted or blocked from connection. Please
	     * check again.<br>
	     * 4: This is an incorrect password. Please check again.<br>
	     * 5: This is not a registered ID. Please check again.<br>
	     * 6: Connection failed due to system error. Please try again later.<br>
	     * 7: This is an ID that is already logged in, or the server is under
	     * inspection. Please check again.<br>
	     * 8: Connection failed due to system error. Please try again later.<br>
	     * 9: Connection failed due to system error. Please try again later.<br>
	     * 10: Could not be processed due to too many connection requests to the
	     * server. Please try again later.<br>
	     * 11: Only those who are 20 years old or older can use this. Please use
	     * another channel.<br>
	     * 13: Unable to log-on as a master at IP Please check again.<br>
	     * 14: You have either selected the wrong gateway, or you have yet to change
	     * your person information. (Yes opens nexon.com)<br>
	     * 17: You have either selected the wrong gateway, or you have yet to change your personal information.<br>
	     * 19: EULA Terms (RecvOP 4)<br>
	     *
	     * @param reason The reason logging in failed.
	     * @return The login failed packet.
	     */
	    public static MaplePacket getAuthFailed(int reason) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
	        mplew.write(SendPacketOpcode.CHECK_PASSWORD_RESULT.getValue());
	        mplew.write(reason);
	        mplew.write(0);
	        mplew.writeInt(0);
	        return mplew.getPacket();
	    }
	    
	    public static MaplePacket getEula() {
	    	return getAuthFailed(19);
	    }
		
	}
	
	//OP = 2 (v12)
	public static class CheckUserLimitResult {

	    public static MaplePacket getStatus() {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.CHECK_USER_LIMIT_RESULT.getValue());
	        mplew.write(0);  //Warning
	        //mplew.write(0); //Population marker
	        return mplew.getPacket();
	    }
		
	}
	
	//OP = 3 (v12)
	public static class SetAccountResult {
		
		//Gender packet (after setting gender)
		
	    public static MaplePacket success(MapleClient c) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
	        mplew.write(SendPacketOpcode.SET_ACCOUNT_RESULT.getValue());
	        mplew.write(c.getGender());
	        mplew.write(1); //Success 1 = Accept, 0 = Decline.
	        return mplew.getPacket();
	    }
		
	}
	
	//OP = 4 (v12)
	public static class ConfirmEULAResult {
		
	    public static MaplePacket success() {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.CONFIRM_EULA_RESULT.getValue());
	        mplew.write(1);
	        return mplew.getPacket();
	    }
	    
	    public static MaplePacket failed() {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.CONFIRM_EULA_RESULT.getValue());
	        mplew.write(0);
	        return mplew.getPacket();
	    }
		
	}
	
	//OP = 6 (v12)
	public static class PinOperation {
		
	    /**
	     * Gets a packet detailing a PIN operation. Possible values for
	     * <code>mode</code>:<br>
	     * 0 - PIN was accepted<br>
	     * 1 - Register a new PIN<br>
	     * 2 - Invalid pin / Reenter<br>
	     * 3 - Connection failed due to system error<br>
	     * 4 - Enter the pin
	     *
	     * @param mode The mode.
	     * @return The pin operation packet of byte mode.
	     */
		
	    public static MaplePacket pinOperation(byte mode) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
	        mplew.write(SendPacketOpcode.PIN_OPERATION.getValue());
	        mplew.write(mode);
	        return mplew.getPacket();
	    }
	    
	    /**
	     * Gets a packet requesting the client enter a PIN.
	     * @return The request PIN packet.
	     */
	    
	    public static MaplePacket requestPin() {
	        return pinOperation((byte) 4);
	    }

	    /**
	     * Gets a packet requesting the PIN after a failed attempt.
	     * @return The failed PIN packet.
	     */
	    
	    public static MaplePacket requestPinAfterFailure() {
	        return pinOperation((byte) 2);
	    }

	    /**
	     * Gets a packet requesting to register a PIN.
	     * @return The register PIN packet.
	     */
	    
	    public static MaplePacket registerPin() {
	        return pinOperation((byte) 1);
	    }

	    /**
	     * Gets a packet saying the PIN has been accepted.
	     * @return The PIN accepted packet.
	     */
	    
	    public static MaplePacket pinAccepted() {
	        return pinOperation((byte) 0);
	    }


		
	}
	
	//OP = 7 (v12)
	public static class PinAssigned {

	    /**
	     * Gets a packet saying the PIN was registered.
	     */
		
	    public static MaplePacket successfully() {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.PIN_ASSIGNED.getValue());
	        mplew.write(0);
	        return mplew.getPacket();
	    }
	    
	    public static MaplePacket failed() {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.PIN_ASSIGNED.getValue());
	        mplew.write(1);
	        return mplew.getPacket();
	    }
		
	}
	
	//OP = 8 (v12)
	public static class WorldInformation {
		
	    public static MaplePacket getServerList() {
	        Map<Integer, Integer> channelLoad = LoginServer.getInstance().getLoad();
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.WORLD_INFORMATION.getValue()); //SERVERLIST
	        mplew.write(20);
	        mplew.writeMapleAsciiString("Tespia"); // World Name
	        
	        //Comments below Is v30
	        //mplew.write(3); //World State (Flag): 1 = E(vent) 2 = N(ew) 3 = H(ot) 0 && 4 >= Nothing 
	        //mplew.writeMapleAsciiString("Welcome to MapleGlobal!"); //World Event Description
	        //mplew.write(0); // Disable character creation = 1, Enable = 0

	        int lastChannel = 1;
	        Set<Integer> channels = channelLoad.keySet();
	        for (int i = 21; i > 0; i--) {
	            if (channels.contains(i)) {
	                lastChannel = i;
	                break;
	            }
	        }
	        mplew.write(lastChannel);
	        
	        int load;
	        for (int i = 1; i <= lastChannel; i++) {
	            if (channels.contains(i)) {
	                load = channelLoad.get(i);
	            } else {
	                load = 1200;
	            }
	            mplew.writeMapleAsciiString("Tespia - " + (i));
	            mplew.writeInt(load); // server load, get connected size
	            mplew.write(0); // Tespia is the only world
	            mplew.write((byte) i - 1);
	            mplew.write(0); // unk
	        }
	        return mplew.getPacket();
	    }

	    public static MaplePacket getEndOfServerList() {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.WORLD_INFORMATION.getValue()); //SERVERLIST
	        mplew.write(-1);
	        return mplew.getPacket();
	    }
		
	}
	
	//OP = 9 (v12)
	public static class selectWorldResult {
		
	    /**
	     * Gets a packet with a list of characters.
	     *
	     * @param c The MapleClient to load characters of.
	     * @param serverId The ID of the server requested.
	     * @return The character list packet.
	     */
	    public static MaplePacket sendCharacterList(MapleClient c, int serverId) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.SELECT_WORLD_RESULT.getValue());
	        mplew.write(0); //Success (others display error messages)
	        List<MapleCharacter> chars = c.loadCharacters(serverId);
	        mplew.write(chars.size());
	        for (MapleCharacter chr : chars) {
	            CharacterData.addCharacterStats(mplew, chr);
	            CharacterData.addCharacterLook(mplew, chr);
	        }
	        mplew.write(0);
	        return mplew.getPacket();
	    }

	}
	
	//OP = 10 (v12)
	public static class SelectCharacterResult {
		
	}
	
	//OP = 11 (v12)
	public static class CheckDuplicatedIDResult {
		
	    public static MaplePacket available(String charname, boolean available) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.CHECK_DUPLICATED_ID_RESULT.getValue());
	        mplew.writeMapleAsciiString(charname);
	        mplew.write(available ? 0 : 1);
	        return mplew.getPacket();
	    }		
		
	}
	
	//OP = 12 (v12)
	public static class CreateNewCharacterResult {
		
	    public static MaplePacket addNewCharacter(MapleCharacter chr, boolean worked) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.CREATE_NEW_CHARACTER_RESULT.getValue());
	        mplew.write(worked ? 0 : 1);
	        if (worked) {
	            CharacterData.addCharacterStats(mplew, chr);
	            CharacterData.addCharacterLook(mplew, chr);
	            mplew.write(0); //rank disabled
                // mplew.writeInt(chr.getRank()); // world rank
                // mplew.writeInt(chr.getRankMove()); // move (negative is downwards)
                // mplew.writeInt(chr.getJobRank()); // job rank
                // mplew.writeInt(chr.getJobRankMove()); // move (negative is downwards)
	        }
	        return mplew.getPacket();
	    }
		
	}
	
	//OP = 13 (v12)
	public static class DeleteCharacterResult {
		
	    public static MaplePacket deleteCharacter(int cid, byte result) {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
	        mplew.write(SendPacketOpcode.DELETE_CHARACTER_RESULT.getValue());
	        mplew.writeInt(cid);
	        mplew.write(result); // 22 - Cannot delete Guild Master Character.
	        return mplew.getPacket();
	    }

		
	}
	
	//This is not in v12, saving for later versions.
	public static class RelogResponse {
		
	    /**
	     * Gets the response to a relog request.
	     * @return The relog response packet.
	     */
	    public static MaplePacket getResponse() {
	        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
	        mplew.write(SendPacketOpcode.RELOG_RESPONSE.getValue());
	        mplew.write(1);
	        return mplew.getPacket();
	    }
		
	}
	
}
