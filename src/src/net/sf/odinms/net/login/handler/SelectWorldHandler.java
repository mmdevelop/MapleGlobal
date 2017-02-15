package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class SelectWorldHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte world = slea.readByte();
        byte channel = (byte) (slea.readByte() + 1);
        if (channel > 0 && channel < 21) {
            c.setWorld(world);
            c.setChannel(channel);
            c.sendCharList(world);
        }
    }
}
