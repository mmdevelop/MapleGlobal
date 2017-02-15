package net.sf.odinms.server.maps;

import java.awt.Point;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.portal.PortalScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.fourthjobquests.FourthJobQuestsPortalHandler;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapleGenericPortal implements MaplePortal {

    private String name;
    private String target;
    private Point position;
    private int targetmap;
    private int type;
    private int id;
    private String scriptName;
    private boolean portalState;

    public MapleGenericPortal(int type) {
        this.type = type;
        portalState = OPEN;
    }

    @Override
    public void setPortalState(boolean state) {
        this.portalState = state;
    }

    @Override
    public boolean getPortalState() {
        return portalState;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public int getTargetMapId() {
        return targetmap;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getScriptName() {
        return scriptName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    @Override
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public void enterPortal(MapleClient c) {
        boolean changed = false;
        if (getScriptName() != null) {
            changed = PortalScriptManager.getInstance().executePortalScript(this, c);
        } else if (getTargetMapId() != 999999999) {
            MapleMap to = c.getPlayer().getEventInstance() == null ? c.getChannelServer().getMapFactory().getMap(getTargetMapId()) : c.getPlayer().getEventInstance().getMapInstance(getTargetMapId());
            MaplePortal pto = to.getPortal(getTarget());
            System.out.println("pto name again: " + pto.getName());
            if (pto == null) {
                pto = to.getPortal(0);
            }
            c.getPlayer().changeMap(to, pto);
            changed = true;
        }
        if (!changed) {
            c.enableActions();
        }
    }
}
