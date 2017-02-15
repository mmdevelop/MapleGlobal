package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CLogin;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class WorldStatusRequestHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.announce(CLogin.CheckUserLimitResult.getStatus());
    }
}
