package net.sf.odinms.tools;

import java.util.List;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShopItem;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 * @author Straight Edgy, Exile
 */

public class NpcPacket {

    /**
     * Gets a NPC spawn packet.
     *
     * @param life The NPC to spawn.
     * @return The NPC spawn packet.
     */
    public static MaplePacket spawnNPC(MapleNPC life) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.NPC_ENTER_FIELD.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        if (life.getF() == 1) {
            mplew.write(0);
        } else {
            mplew.write(1);
        }
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.NPC_CHANGE_CONTROLLER.getValue());
        mplew.write(1);
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        if (life.getF() == 1) {
            mplew.write(0);
        } else {
            mplew.write(1);
        }
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(MiniMap ? 1 : 0);

        return mplew.getPacket();
    }

    /**
     *
     * @param npc - The NPC to display
     * @param mode - The type of dialogue
     * @param talk - The string of text dialogue.
     * @param endBytes - The ending bytes for the packet
     * @return The scriptMessage packet
     */
    public static MaplePacket getScriptMessage(int npc, byte mode, String talk, String endBytes) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc); //The NPC ID
        mplew.write(mode); //The byte mode
        mplew.writeMapleAsciiString(talk); //The string of text dialogue.
        mplew.write(HexTool.getByteArrayFromHexString(endBytes));
        return mplew.getPacket();
    }

    public static MaplePacket getScriptMessage(int npc, byte mode, String talk) {
        return getScriptMessage(npc, mode, talk, "");
    }

    /**
     * Gets a packet requesting a Membershop Avatar selection dialogue, display
     *
     * @param npc, saying @param talk, displaying @param styles styles
     *
     * @param npc - The NPC to display
     * @param talk - The dialogue text to display
     * @param styles - The array of available style id's
     * @return The Membershop Avatar packet
     */
    public static MaplePacket askMembershopAvatar(int npc, String talk, int styles[]) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc); //The NPC ID
        mplew.write(5); //The byte mode
        mplew.writeMapleAsciiString(talk); //The string of text dialogue.
        mplew.write(styles.length); //The length of the array containing available styles.
        for (int i = 0; i < styles.length; i++) {
            mplew.writeInt(styles[i]); //Send each style ID.
        }
        return mplew.getPacket();
    }

    public static MaplePacket askNext(int npc, String text) {
        return getScriptMessage(npc, (byte) 0, text, "00 01");
    }

    public static MaplePacket askPrev(int npc, String text) {
        return getScriptMessage(npc, (byte) 0, text, "01 00");
    }

    public static MaplePacket askNextPrev(int npc, String text) {
        return getScriptMessage(npc, (byte) 0, text, "01 01");
    }

    public static MaplePacket askOk(int npc, String text) {
        return getScriptMessage(npc, (byte) 0, text, "00 00");
    }

    public static MaplePacket askYesNo(int npc, String text) {
        return getScriptMessage(npc, (byte) 1, text);
    }

    public static MaplePacket askAcceptDecline(int npc, String text) {
        return getScriptMessage(npc, (byte) 0x0C, text);
    }

    public static MaplePacket askSelection(int npc, String text) {
        return getScriptMessage(npc, (byte) 4, text);
    }

    public static MaplePacket getNPCTalkNum(int npc, String talk, int def, int min, int max) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4); // ?
        mplew.writeInt(npc);
        mplew.write(3);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(def);
        mplew.writeInt(min);
        mplew.writeInt(max);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket getNPCTalkText(int npc, String talk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4); // ?
        mplew.writeInt(npc);
        mplew.write(2);
        mplew.writeMapleAsciiString(talk);
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getNPCShop(int sid, List<MapleShopItem> items) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOP.getValue());
        mplew.writeInt(sid);
        ;
        mplew.writeShort(items.size()); // item count
        for (MapleShopItem item : items) {
            mplew.writeInt(item.getItemId());
            mplew.writeInt(item.getPrice());
            if (ii.isThrowingStar(item.getItemId())) {
                mplew.writeShort(0);
                mplew.writeInt(0);
                mplew.writeShort(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
                mplew.writeShort(ii.getSlotMax(item.getItemId()));
            } else {
                mplew.writeShort(item.getBuyable());
            }
        }
        return mplew.getPacket();
    }

    /**
     * code 
     * 0 = Buy 
     * 8 = Sell 
     * 32 = due to an error the trade did not happen
     *
     * @param code
     * @return
     */
    public static MaplePacket confirmShopTransaction(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOP_TRANSACTION.getValue());
        mplew.write(code); // recharge == 8?
        return mplew.getPacket();
    }
    
    public static MaplePacket setNPCController(MapleNPC npc)
    {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(128);
        mplew.write(1);                             //?
        mplew.writeInt(npc.getObjectId());
        mplew.writeInt(npc.getId());
        mplew.writeShort(npc.getPosition().x);
        mplew.writeShort(npc.getCy());
        if (npc.getF() == 1) {mplew.write(1);}
        else {mplew.write(0);}
        mplew.write(npc.getFh());
        mplew.write(npc.getRx0());
        mplew.write(npc.getRx1());
        mplew.write(1);
        
        return mplew.getPacket();
    }
}
