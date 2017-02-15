package net.sf.odinms.net.channel.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.CashItemFactory;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.packets.CCashShop;

/*
 * @author Novak
 */

public class BuyCSItemHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int action = slea.readByte();
        switch (action) {
            case 2: 
            // Lenght: 6
            // 02 - Action
            // 00 - Boolean Maplepoints (mPoints > 0)
            // A9 96 98 00 - Item
            {
                boolean maplePoints = slea.readByte() > 0;
                int snCS = slea.readInt();
                CashItemInfo item = CashItemFactory.getItem(snCS);
                if (c.getPlayer().getCSPoints(maplePoints ? 2 : 1) >= item.getPrice()) {
                    c.getPlayer().modifyCSPoints(maplePoints ? 2 : 1, -item.getPrice());
                } else {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    AutobanManager.getInstance().autoban(c, "Trying to purchase from the CS when they have no NX");
                    return;
                }
                if (item.getId() >= 5390000 && item.getId() <= 5390002) {
                    c.getPlayer().dropMessage(1, "You may not purchase this item");
                    return;
                }
                if (item.getId() >= 5000000 && item.getId() <= 5000100) {
                    int petId = MaplePet.createPet(item.getId());
                    if (petId == -1) {
                        return;
                    }
                    MapleInventoryManipulator.addById(c, item.getId(), (short) 1, null, petId);
                } else {
                    MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount(), "");
                }
                CashItemInfo Item = CashItemFactory.getItem(snCS);
                c.getSession().write(CCashShop.showBoughtCSItem(c.getPlayer(), item));
                c.getSession().write(CCashShop.sendCash(c.getPlayer()));
                c.getSession().write(CCashShop.sendCSItemInventory(item));
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            }
            case 4:
                //04 - action
                //38 2D 31 01 - WhishList Item 1
                //00 00 00 00 - WhishList Item 2 
                //00 00 00 00 - WhishList Item 3 
                //00 00 00 00 - WhishList Item 4 
                //00 00 00 00 - WhishList Item 5 
                //00 00 00 00 - WhishList Item 6 
                //00 00 00 00 - WhishList Item 7 
                //00 00 00 00 - WhishList Item 8 
                //00 00 00 00 - WhishList Item 9 
                //00 00 00 00 - WhishList Item 10
                try {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("DELETE FROM wishlist WHERE charid = ?");
                    ps.setInt(1, c.getPlayer().getId());
                    ps.executeUpdate();
                    ps.close();
                    while (slea.available() > 0) { //Should loop 10
                        int sn = slea.readInt();
                        if (sn != 0) {
                            ps = con.prepareStatement("INSERT INTO wishlist(charid, sn) VALUES(?, ?) ");
                            ps.setInt(1, c.getPlayer().getId());
                            ps.setInt(2, sn);
                            ps.executeUpdate();
                            ps.close();
                            con.close();
                        }
                    }
                } catch (SQLException se) {
                }
                c.getSession().write(CCashShop.sendWishList(c.getPlayer().getId(), true));
                break;
            case 5:
                //Length : 3
                //05 - action
                //00 - ToIncrease?
                //01 - ToCharge
                byte toIncrease = slea.readByte();
                byte toCharge = slea.readByte();
                if (c.getPlayer().getCSPoints(toCharge) >= 4000 && c.getPlayer().getStorage().getSlots() < 48) {
                    c.getPlayer().modifyCSPoints(toCharge, -4000);
                    if (toIncrease == 0) {
                        c.getPlayer().getStorage().gainSlots(4);
                    }
                } else {
                    c.getPlayer().dropMessage(1, "Expanding this inventory type is currently unsupported.");
                }
                c.getSession().write(CCashShop.sendCash(c.getPlayer()));//?
                c.getSession().write(MaplePacketCreator.enableActions());//?
                break;
            case 28: 
            {
                // Package
                slea.skip(1);
                int useNX = slea.readInt();
                int snCS = slea.readInt();
                CashItemInfo item = CashItemFactory.getItem(snCS);
                if (c.getPlayer().getCSPoints(useNX) >= item.getPrice()) {
                    c.getPlayer().modifyCSPoints(useNX, -item.getPrice());
                } else {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    AutobanManager.getInstance().autoban(c, "Trying to purchase from the CS when they have no NX.");
                    return;
                }
                for (int i : CashItemFactory.getPackageItems(item.getId())) {
                    if (i >= 5000000 && i <= 5000100) {
                        int petId = MaplePet.createPet(i);
                        if (petId == -1) {
                            c.getSession().write(MaplePacketCreator.enableActions());
                            return;
                        }
                        MapleInventoryManipulator.addById(c, i, (short) 1, null, petId);
                    } else {
                        MapleInventoryManipulator.addById(c, i, (short) item.getCount());
                    }
                }
                c.getSession().write(CCashShop.showBoughtCSItem(c.getPlayer(), item));
                c.getSession().write(CCashShop.sendCash(c.getPlayer()));//?
                c.getSession().write(MaplePacketCreator.enableActions());//?
                break;
            }
            case 30: 
            {
                int snCS = slea.readInt();
                CashItemInfo item = CashItemFactory.getItem(snCS);
                if (c.getPlayer().getMeso() >= item.getPrice()) {
                    c.getPlayer().gainMeso(-item.getPrice(), false);
                    MapleInventoryManipulator.addById(c, item.getId(), (short) item.getCount());
                } else {
                    c.getSession().write(MaplePacketCreator.enableActions());
                    AutobanManager.getInstance().autoban(c, "Trying to purchase from the CS with an insufficient amount.");
                }
                break;
            }
            default:
                ;
                break;
        }
    }
}
