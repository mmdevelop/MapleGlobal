package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Ginseng
 */
public class DropMesosHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int mesos = slea.readInt();
        if (mesos < 10 || mesos > 50000) {
            AutobanManager.getInstance().addPoints(c, 1000, 0, "Dropping " + mesos + " mesos");
            return;
        }
        if (player.getMeso() >= mesos) {
            player.gainMeso(-mesos, false, true);
            player.getMap().spawnMesoDrop(mesos, mesos, player.getPosition(), player, player, true);
        }
    }

}
