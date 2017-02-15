package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Straight Edgy
 */

public class SitRequestHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        short chair = slea.readShort();
        player.setChair(chair);
        player.getMap().broadcastMessage(player, MaplePacketCreator.showChair(chair), true);
        c.enableActions();
    }
}
