package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CLogin;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * @author Straight Edgy
 */

public final class CheckPinCodeHandler extends AbstractMaplePacketHandler {

    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte mode = slea.readByte();
        byte action = mode;
        try {
            action = slea.readByte();
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {}
        ;
        if (mode == 1 || mode == 3) {
            if (action == 1) { //Initial Pin Request post-login
            	if (!c.acceptedEula) {
            		c.announce(CLogin.CheckPasswordResult.getEula());
            	} else if (c.getPin() == null || c.getPin().length() != 4) {
					c.announce(CLogin.PinOperation.registerPin());
                } else {
                    c.announce(CLogin.PinOperation.requestPin());
                }
            } else if (action == 0) {  //Pin Response
            	// 1 0 0 0 4 0, Always 1, 4 indicates length of the pin.
            	slea.skip(6);
            	if (slea.available() == 4) { //Why not use slea.available() instead, now we know for sure
	                String pin = "";
	                pin += slea.readAsciiString(4); //Read the bytes containing Decimal ASCII Values for the inputed Pin
	                ;
	                if (pin.equals(c.getPin())) {
	                    c.announce(CLogin.PinOperation.pinAccepted());
	                    return;
	                } else {
	                	c.announce(CLogin.PinOperation.requestPinAfterFailure());
	                }
            	} else {
            		c.announce(CLogin.PinOperation.requestPinAfterFailure());
            	}
            }
        } else if (mode == 2 && action == 0) { //Change Pin Request
            slea.skip(6); //Skip to Pin bytes
            if (slea.available() == 4) {
	            String pin = slea.readAsciiString(4); //Read the 4 bytes containing Decimal ASCII Values for the inputted Pin
	            if (pin.equals(c.getPin())) {
	                c.announce(CLogin.PinOperation.registerPin());
	            } else {
	            	c.announce(CLogin.PinOperation.requestPinAfterFailure());
	            }
            } else {
            	c.announce(CLogin.PinOperation.requestPinAfterFailure());
            }
        } else { //Cancel Pin Request
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN); //Reset client login state after aborting Pin request.
        }
    }
}