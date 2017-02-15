package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Ginseng
 */
public class PortalScriptHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        
        slea.readByte();
        String warp = slea.readMapleAsciiString();
        ;
        MaplePortal portal = player.getMap().getPortal(warp);
        byte bit1 = slea.readByte();
        byte bit2 = slea.readByte();
        ;
        
        if(player.getBuffedValue(MapleBuffStat.MORPH) != null && player.getBuffedValue(MapleBuffStat.COMBO) != null) {
            player.cancelEffectFromBuffStat(MapleBuffStat.MORPH);
            player.cancelEffectFromBuffStat(MapleBuffStat.COMBO);
        }
        if (player.getBuffedValue(MapleBuffStat.PUPPET) != null) {
            player.cancelBuffStats(MapleBuffStat.PUPPET);
        }
        
        if (portal != null) {
            portal.enterPortal(c);
        } else {
            c.enableActions();
        }
        
    }
    
}
