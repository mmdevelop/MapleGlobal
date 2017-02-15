package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CLogin;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Audace
 */
public class SetGenderHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        boolean accept = slea.readByte() == 1;
        if (accept) {
            byte gender = slea.readByte();
            c.setGender(gender);
            c.getSession().write(CLogin.SetAccountResult.success(c));
        } else {
            c.updateLoginState(MapleClient.LOGIN_NOTLOGGEDIN); //Reset client login state after aborting Gender request.
        }
    }
}
