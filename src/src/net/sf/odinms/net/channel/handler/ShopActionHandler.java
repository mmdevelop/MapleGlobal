package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Straight Edgy
 */

public class ShopActionHandler extends AbstractMaplePacketHandler {

    public ShopActionHandler() {
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte action = slea.readByte();
        ;
        if (action == 0) { // Buy
            slea.readShort();
            int itemId = slea.readInt();
            short quantity = slea.readShort();
            c.getPlayer().getShop().buy(c, itemId, quantity);
        } else if (action == 1) { // Sell
            byte slot = (byte) slea.readShort();
            int itemId = slea.readInt();
            MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemId);
            short quantity = slea.readShort();
            c.getPlayer().getShop().sell(c, type, slot, quantity);
        } else if (action == 2) { // Recharge
            byte slot = (byte) slea.readShort();
            c.getPlayer().getShop().recharge(c, slot);
        } else if (action == 3) { // Exit Shop
            
        }
    }
}
