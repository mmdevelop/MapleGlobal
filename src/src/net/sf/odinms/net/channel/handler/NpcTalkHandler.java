package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.NpcPacket;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Ginseng
 */

public class NpcTalkHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int oid = slea.readInt();

        MapleMapObject obj = c.getPlayer().getMap().getMapObject(oid);
        if (obj instanceof MapleNPC) {
            MapleNPC npc = (MapleNPC) obj;
            if (NPCScriptManager.getInstance() != null) {
                NPCScriptManager.getInstance().dispose(c);
            }
            if (!c.getPlayer().getCheatTracker().Spam(1000, 4)) {
                if (npc.hasShop()) {
                    if (c.getPlayer().getShop() != null) {
                        c.getPlayer().setShop(null);
                        c.getSession().write(NpcPacket.confirmShopTransaction((byte) 20));
                    }
                    ;
                    npc.sendShop(c);
                } else {
                    if (c.getCM() != null || c.getQM() != null) {
                        c.enableActions();
                        return;
                    }
                    NPCScriptManager.getInstance().start(c, npc.getId());
                }
            }
        }
    }
}
