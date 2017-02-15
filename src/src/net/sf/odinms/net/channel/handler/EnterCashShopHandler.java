package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.packets.CCashShop;

/**
 * MapleGlobal.org - 2016
 *
 * @Author - Novak
 *
 * Special credits: Eric
 */
public class EnterCashShopHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        c.announce(MaplePacketCreator.cashShopDisabled()); //cs nonexistant before v8. TODO implement later.
                
        
//            while (c.getPlayer().getNoPets() > 0) {
//                c.getPlayer().unequipAllPets();
//            }
//            try {
//                WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
//                wci.addBuffsToStorage(c.getPlayer().getId(), c.getPlayer().getAllBuffs());
//                wci.addCooldownsToStorage(c.getPlayer().getId(), c.getPlayer().getAllCooldowns());
//            } catch (RemoteException e) {
//                c.getChannelServer().reconnectWorld();
//            }
//            c.getPlayer().getMap().removePlayer(c.getPlayer());
//            c.getSession().write(CCashShop.OnSetCashShop(c));
//            c.getPlayer().setInCS(true);
//            c.getSession().write(CCashShop.sendCash(c.getPlayer()));
//            c.getSession().write(CCashShop.sendWishList(c.getPlayer().getId(), true));
//            c.getPlayer().saveToDB(true, true);
    }
}
