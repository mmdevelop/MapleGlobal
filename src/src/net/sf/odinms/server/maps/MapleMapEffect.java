package net.sf.odinms.server.maps;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapleMapEffect {
    private String msg;
    private int itemId;
    private boolean active = true;

    public MapleMapEffect(String msg, int itemId) {
        this.msg = msg;
        this.itemId = itemId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MaplePacket makeDestroyData() {
        return CField.BlowWeather.removeMapEffect();
    }

    public MaplePacket makeStartData() {
        return CField.BlowWeather.startMapEffect(msg, itemId, active);
    }

    public void sendStartData(MapleClient client) {
        client.getSession().write(makeStartData());
    }
}