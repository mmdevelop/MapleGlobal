package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CLogin;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Audace
 */
public class ConfirmEULAHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        // 01 = Accept, 00 = Decline
        boolean accept = slea.readByte() == 1;
        ;
        if (accept) {
            c.setEulaAccepted();
            c.announce(CLogin.ConfirmEULAResult.success());
            c.announce(CLogin.CheckPasswordResult.getAuthSuccess(c));
        } else {
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN); //Reset client login state after aborting EULA request.
        }
    }

}
