package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CLogin;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Straight Edgy
 */
public class UpdatePinCodeHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
    	if (slea.available() == 7) {
	        boolean accept = slea.readByte() == 1;
	        if (accept) {
	            slea.skip(2);
	            String pin = slea.readAsciiString(4);
	            try {
	                Integer.parseInt(pin);
	                c.setPin(pin);
	                c.announce(CLogin.PinAssigned.successfully());
	            } catch(NumberFormatException e) {//Packet Editting
	            	c.disconnect();
	            	return;
	            }
	        }
    	} else if (slea.available() != 1){
    		c.announce(CLogin.PinAssigned.failed());
    	}
        c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN); //Reset client login state after pin assignment.
    }
}
