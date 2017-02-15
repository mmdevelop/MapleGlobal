package net.sf.odinms.tools.packets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import static net.sf.odinms.net.login.LoginWorker.log;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;
import net.sf.odinms.tools.MaplePacketCreator;

/**
 *
 * @author Novak
 */
public class CCashShop {
    
    public static MaplePacket OnSetCashShop(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        MapleCharacter chr = c.getPlayer();
        mplew.write(SendPacketOpcode.SET_CASH_SHOP.getValue());
        mplew.writeShort(-1); //flags
        CharacterData.addCharacterStats(mplew, chr);
        CharacterData.addInventoryInfo(mplew, chr);
        mplew.writeShort(0);
        mplew.write(1); // 0 = beta or someshit lol
        mplew.writeMapleAsciiString(c.getAccountName());
        mplew.writeShort(0); // wishlist i think.. o.o
        addTopItems(mplew, chr);
        return mplew.getPacket();
    }
    
    private static void addTopItems(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        for (byte i = 1; i <= 8; i++) {
            for (byte j = 0; j <= 1; j++) {
                mplew.writeInt(10000281); // best items, these are just first id's in Commodity
                mplew.writeInt(i);
                mplew.writeInt(j);

                mplew.writeInt(10000282);
                mplew.writeInt(i);
                mplew.writeInt(j);

                mplew.writeInt(10000283);
                mplew.writeInt(i);
                mplew.writeInt(j);

                mplew.writeInt(10000284);
                mplew.writeInt(i);
                mplew.writeInt(j);

                mplew.writeInt(10000285);
                mplew.writeInt(i);
                mplew.writeInt(j);
            }
        }
        mplew.writeShort(5); // Stock 
        mplew.writeInt(-1); // 1 = Sold Out, 2 = Not Sold      
        mplew.writeInt(20900028);
        mplew.writeInt(0); // 1 = Sold Out, 2 = Not Sold
        mplew.writeInt(20900027);
        mplew.writeInt(2); // 1 = Sold Out, 2 = Not Sold
        mplew.writeInt(20900026);
        mplew.writeInt(4); // 1 = Sold Out, 2 = Not Sold
        mplew.writeInt(20900026);
        mplew.writeInt(5); // 1 = Sold Out, 2 = Not Sold
        mplew.writeInt(20900026);

        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
    }

    public static MaplePacket sendCash(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.CS_CASH.getValue());
        mplew.writeInt(chr.getCSPoints(1)); // Paypal/PayByCash NX
        mplew.writeInt(chr.getCSPoints(2)); // Maple Points
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSItem(MapleCharacter chr, CashItemInfo item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        mplew.write(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeLong(item.hashCode()); // Cash ID
        mplew.writeInt(chr.getId()); // Player ID
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01"));
        mplew.writeInt(item.getId());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01"));
        mplew.writeShort(item.getCount()); // quantity is always 1?
        mplew.writeAsciiString(ii.getName(item.getId()));
        mplew.writeLong(0); // Expiration.. items never expire so we shouldnt have a problem.
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01"));
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSQuestItem(byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeInt(365);
        mplew.write(0);
        mplew.writeShort(1);
        mplew.write(position);
        mplew.write(0);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static MaplePacket showCouponRedeemedItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.writeShort(0x3A);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(0x1A);
        mplew.writeInt(itemid);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Sends a new (cash) item inventory.
     * 
     * Because this version seemingly doesn't support CS inventories entirely yet,
     * This packet will place a new item in the inventory, and set the CS inventory window
     * To focus on the tab it was placed in.
     * 
     * @param item The item that is added.
     * @return
     */
    public static MaplePacket sendCSItemInventory(CashItemInfo item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x2F);
        
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int slot = ii.getInventoryType(item.getId()).getType();
        ;
        
        //EQUIP THROWS NULLPOINTER AT ADDITEMINFO
        mplew.writeShort((byte) slot);
        mplew.write(slot);//2
        Item newItem = new Item(item.getId(), (byte) slot, (short) 1);//2090000 as item always works?
        MaplePacketCreator.addItemInfo(mplew, newItem, false, true);
        return mplew.getPacket();
    }

    // wrong do..
    public static MaplePacket sendWishList(int characterid, boolean update) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CS_OPERATION.getValue());
        if (update) {
            mplew.write(0x39);
        } else {
            mplew.write(0x33);
        }
        Connection con = DatabaseConnection.getConnection();
        int i = 10;

        try {
            PreparedStatement ps = con.prepareStatement("SELECT sn FROM wishlist WHERE charid = ? LIMIT 10");
            ps.setInt(1, characterid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mplew.writeInt(rs.getInt("sn"));
                i--;
            }
            rs.close();
            ps.close();
            con.close();
        } catch (SQLException se) {
            log.info("Error getting wishlist data:", se);
        }
        while (i > 0) {
            mplew.writeInt(0);
            i--;
        }
        return mplew.getPacket();
    }

    public static MaplePacket wrongCouponCode() {
        // FE 00 40 87
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CS_OPERATION.getValue());
        mplew.write(0x40);
        mplew.write(0x87);

        return mplew.getPacket();
    }
}
