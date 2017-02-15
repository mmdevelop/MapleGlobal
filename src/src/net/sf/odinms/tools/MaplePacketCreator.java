package net.sf.odinms.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.odinms.client.IEquip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.IEquip.ScrollResult;
import net.sf.odinms.client.MapleDisease;
import net.sf.odinms.client.MapleRing;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.ByteArrayMaplePacket;
import net.sf.odinms.net.LongValueHolder;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
//import net.sf.odinms.net.channel.handler.SummonDamageHandler.SummonAttackEntry;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.server.PlayerInteraction.MaplePlayerShopItem;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.LittleEndianWriter;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;
import net.sf.odinms.net.world.guild.*;
import net.sf.odinms.server.PlayerInteraction.MapleMiniGame;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.PlayerInteraction.IPlayerInteractionManager;
import net.sf.odinms.server.PlayerInteraction.MaplePlayerShop;
import net.sf.odinms.server.maps.MapleSummon;
import net.sf.odinms.tools.packets.CharacterData;

public class MaplePacketCreator {

    private static Logger log = LoggerFactory.getLogger(MaplePacketCreator.class);
    private final static byte[] CHAR_INFO_MAGIC = new byte[]{(byte) 0xff, (byte) 0xc9, (byte) 0x9a, 0x3b};
    private final static byte[] ITEM_MAGIC = new byte[]{(byte) 0x80, 5};
    public static final List<Pair<MapleStat, Integer>> EMPTY_STATUPDATE = Collections.emptyList();
    private final static long FT_UT_OFFSET = 116444592000000000L; // EDT

    private static long getKoreanTimestamp(long realTimestamp) {
        long time = (realTimestamp / 1000 / 60); // convert to minutes
        return ((time * 600000000) + FT_UT_OFFSET);
    }

    private static long getTime(long realTimestamp) {
        long time = (realTimestamp / 1000); // convert to seconds
        return ((time * 10000000) + FT_UT_OFFSET);
    }

    /**
     * Sends a hello packet.
     *
     * @param mapleVersion The maple client version.
     * @param sendIv the IV used by the server for sending
     * @param recvIv the IV used by the server for receiving
     * @param testServer
     * @return
     */
    public static MaplePacket getHello(short mapleVersion, byte[] sendIv, byte[] recvIv, boolean testServer) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(0x0d);
        mplew.writeShort(mapleVersion);
        mplew.write(new byte[]{0, 0});
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(testServer ? 5 : 8);

        return mplew.getPacket();
    }

    /**
     * Sends a ping packet.
     *
     * @return The packet.
     */
    public static MaplePacket getPing() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.PING.getValue());
        return mplew.getPacket();
    }

    public static MaplePacket connectToServer(InetAddress inetAddr, int port) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.WORLD_INFORMATION.getValue());
        mplew.write(1);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);
        return mplew.getPacket();
    }

    /**
     * Banned reasons.
     *
     * 00 - Block user 01 - Hacking 02 - Botting 03 - Advertising 04 -
     * Harassment 05 - Profane language 06 - Scamming 07 - Misconduct 08 -
     * Illegal cash transaction 09 - Illegal charging/funding 10 - Temporary
     * request 11 - Impersonating GM 12 - Violating game policy 13 - Abusing
     * Megaphones
     *
     * @param reason
     * @return
     */
    public static MaplePacket getPermBan(int reason) {
        // [00 00] [02 00] [00 00 00 00] [01 00] [8C 65 3F 8A CB D1 01]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.write(SendPacketOpcode.CHECK_PASSWORD_RESULT.getValue());
        mplew.writeShort(2); // Account is banned

        mplew.writeInt(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("8C 65 3F 8A CB D1 01"));

        return mplew.getPacket();
    }

    public static MaplePacket getTempBan(long timestampTill, byte reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        mplew.write(SendPacketOpcode.CHECK_PASSWORD_RESULT.getValue());
        mplew.write(0x02);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00")); // Account
        // is
        // banned

        mplew.write(reason);
        mplew.writeLong(timestampTill); // Tempban date is handled as a 64-bit
        // long, number of 100NS intervals since
        // 1/1/1601. Lulz.

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client the IP of the channel server.
     *
     * @param inetAddr The InetAddress of the requested channel server.
     * @param port The port the channel is on.
     * @param clientId The ID of the client.
     * @return The server IP packet.
     */
    public static MaplePacket getServerIP(InetAddress inetAddr, int port, int clientId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SELECT_CHARACTER_RESULT.getValue());
        mplew.write(0);
        mplew.write(0);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);
        mplew.writeInt(clientId); // this gets repeated to the channel server
        mplew.write(0); // premium ? 1 : 0
        mplew.writeInt(1); // Minutes remaining in internet cafe
        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client the IP of the new channel.
     *
     * @param inetAddr The InetAddress of the requested channel server.
     * @param port The port the channel is on.
     * @return The server IP packet.
     */
    public static MaplePacket getChannelChange(InetAddress inetAddr, int port) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);

        return mplew.getPacket();
    }

    /**
     * Adds a quest info entry for a character to an existing
     * MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWrite instance to write the stats
     * to.
     * @param chr The character to add quest info about.
     */
    private static void addQuestInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
        List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
            mplew.writeInt(q.getQuest().getId());
        }
        List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            mplew.writeLong(KoreanDateUtil.getQuestTimestamp(q.getCompletionTime()));
        }
    }

    /**
     * Gets a packet telling the client to change maps.
     *
     * @param to The <code>MapleMap</code> to warp to.
     * @param spawnPoint The spawn portal number to spawn at.
     * @param chr The character warping to <code>to</code>
     * @return The map change packet.
     */
    public static MaplePacket getWarpToMap(MapleMap to, int spawnPoint, MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SET_FIELD.getValue());
        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.write(chr.getPortalCount());
        mplew.write(0); // Connecting = False
        mplew.writeInt(to.getId());
        mplew.write(spawnPoint);
        mplew.writeShort(chr.getHp()); // hp (???)
        return mplew.getPacket();
    }

    /**
     * Gets an update for specified stats.
     *
     * @param stats The list of stats to update.
     * @param itemReaction Result of an item reaction(?)
     * @return The stat update packet.
     */
    public static MaplePacket updatePlayerStats(List<Pair<MapleStat, Integer>> stats, boolean itemReaction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.STAT_CHANGED.getValue());
        if (itemReaction) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        int updateMask = 0;
        for (Pair<MapleStat, Integer> statupdate : stats) {
            updateMask |= statupdate.getLeft().getValue();
        }
        List<Pair<MapleStat, Integer>> mystats = stats;
        if (mystats.size() > 1) {
            Collections.sort(mystats, (Pair<MapleStat, Integer> o1, Pair<MapleStat, Integer> o2) -> {
                int val1 = o1.getLeft().getValue();
                int val2 = o2.getLeft().getValue();
                return (val1 < val2 ? -1 : (val1 == val2 ? 0 : 1));
            });
        }
        mplew.writeInt(updateMask);
        for (Pair<MapleStat, Integer> statupdate : mystats) {
            if (statupdate.getLeft().getValue() >= 1) {
                if (statupdate.getLeft().getValue() == 0x1) {
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else if (statupdate.getLeft().getValue() <= 0x4) {
                    mplew.writeInt(statupdate.getRight());
                } else if (statupdate.getLeft().getValue() < 0x20) {
                    mplew.write(statupdate.getRight().shortValue());
                } else if (statupdate.getLeft().getValue() < 0xFFFF) {
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else {
                    mplew.writeInt(statupdate.getRight());
                }
            }
        }

        return mplew.getPacket();
    }

    /**
     * Gets an empty stat update.
     *
     * @return The empy stat update packet.
     */
    public static MaplePacket enableActions() {
        return updatePlayerStats(EMPTY_STATUPDATE, true);
    }

    /**
     * Gets an update for specified stats.
     *
     * @param stats The stats to update.
     * @return The stat update packet.
     */
    public static MaplePacket updatePlayerStats(List<Pair<MapleStat, Integer>> stats) {
        return updatePlayerStats(stats, false);
    }

    /**
     * Gets a packet to spawn a portal.
     *
     * @param townId The ID of the town the portal goes to.
     * @param targetId The ID of the target.
     * @param pos Where to put the portal.
     * @return The portal spawn packet.
     */
    public static MaplePacket spawnPortal(int townId, int targetId, Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.TOWN_PORTAL.getValue());
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if (pos != null) {
            mplew.writeShort(pos.x);
            mplew.writeShort(pos.y);
        }
        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a door.
     *
     * @param oid The door's object ID.
     * @param pos The position of the door.
     * @param town
     * @return The remove door packet.
     */
    public static MaplePacket spawnDoor(int oid, Point pos, boolean town) {
        // [D3 00] [01] [93 AC 00 00] [6B 05] [37 03]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SPAWN_DOOR.getValue());
        mplew.write(town ? 1 : 0);
        mplew.writeInt(oid);
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);

        return mplew.getPacket();
    }

    /**
     * Gets a packet to remove a door.
     *
     * @param oid The door's ID.
     * @param town
     * @return The remove door packet.
     */
    public static MaplePacket removeDoor(int oid, boolean town) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (town) {
            mplew.write(SendPacketOpcode.TOWN_PORTAL.getValue());
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
        } else {
            mplew.write(SendPacketOpcode.REMOVE_DOOR.getValue());
            mplew.write(/* town ? 1 : */0);
            mplew.writeInt(oid);
        }

        return mplew.getPacket();
    }

    /**
     * Gets a packet to spawn a special map object.
     *
     * @param summon
     * @param skillLevel The level of the skill used.
     * @param animated Animated spawn?
     * @return The spawn packet for the map object.
     */
    public static MaplePacket spawnSpecialMapObject(MapleSummon summon, int skillLevel, boolean animated) {
        // 72 00 29 1D 02 00 FD FE 30 00 19 7D FF BA 00 04 01 00 03 01 00
        // 85 00 [6A 4D 27 00] [35 1F 00 00] [2D 5D 20 00] [0C] [8C 16] [CA 01]
        // [03] [00] [00] [01] [01] [00]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SPAWN_SPECIAL_MAPOBJECT.getValue());
        mplew.writeInt(summon.getOwner().getId());
        mplew.writeInt(summon.getObjectId()); // Supposed to be Object ID, but
        // this works too! <3

        mplew.writeInt(summon.getSkill());
        mplew.write(skillLevel);
        mplew.writeShort(summon.getPosition().x);
        mplew.writeShort(summon.getPosition().y);
        mplew.write(3); // test

        mplew.write(0); // test

        mplew.write(0); // test

        mplew.write(summon.getMovementType().getValue()); // 0 = don't move, 1 =
        // follow (4th mage
        // summons?), 2/4 =
        // only tele follow,
        // 3 = bird follow

        mplew.write(1); // 0 and the summon can't attack - but puppets don't
        // attack with 1 either ^.-

        mplew.write(animated ? 0 : 1);

        return mplew.getPacket();
    }

    /**
     * Gets a packet to remove a special map object.
     *
     * @param summon
     * @param animated Animated removal?
     * @return The packet removing the object.
     */
    public static MaplePacket removeSpecialMapObject(MapleSummon summon, boolean animated) {
        // [86 00] [6A 4D 27 00] 33 1F 00 00 02
        // 92 00 36 1F 00 00 0F 65 85 01 84 02 06 46 28 00 06 81 02 01 D9 00 BD
        // FB D9 00 BD FB 38 04 2F 21 00 00 10 C1 2A 00 06 00 06 01 00 01 BD FB
        // FC 00 BD FB 6A 04 88 1D 00 00 7D 01 AF FB
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.REMOVE_SPECIAL_MAPOBJECT.getValue());
        mplew.writeInt(summon.getOwner().getId());
        mplew.writeInt(summon.getObjectId());
        mplew.write(animated ? 4 : 1); // ?

        return mplew.getPacket();
    }
    
    public static MaplePacket addItem(MapleCharacter ch, byte inventory, IItem item, boolean isNew)
    {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(0x15);
        mplew.write(1);
        mplew.write(1);
        mplew.write(!isNew ? 1 : 0);
        mplew.write(inventory);
        if (isNew)
        {
              addItemInfo(mplew, item);
        }
        else
        {
            mplew.writeShort(item.getPosition());
            mplew.writeShort(item.getQuantity());
            mplew.writeShort(0);
        }
        mplew.writeLong(0x00);
        mplew.writeLong(0x00);
        mplew.writeLong(0x00);
        return mplew.getPacket();
    }

    /**
     * Adds info about an item to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param item The item to write info about.
     */
    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item) {
        addItemInfo(mplew, item, false, false);
    }

    /**
     * Adds item info to existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param item The item to add info about.
     * @param shortPos
     */
    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, boolean shortPos) {
        addItemInfo(mplew, item, shortPos, false);
    }

    /**
     * Adds expiration time info to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param time The expiration time.
     * @param showexpirationtime Show the expiration time?
     */
    private static void addExpirationTime(MaplePacketLittleEndianWriter mplew, long time, boolean showexpirationtime) {
        if (time != 0) {
            mplew.writeInt(KoreanDateUtil.getItemTimestamp(time));
        } else {
            mplew.writeInt(400967355);
        }
        mplew.write(showexpirationtime ? 1 : 2);
    }

    /**
     * Adds item info to existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to write to.
     * @param item The item to add info about.
     * @param shortPos
     * @param zeroPosition Is the position zero?
     * @param isCash Is it an Cash item? (means no stats)
     */
    public static void addItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, boolean shortPos, boolean zeroPosition) {
        Random rng = new Random();
        boolean isEquip = item.getItemId() / 1000000 == 1;
        boolean isPet = item.getItemId() / 1000000 == 5 && item.getItemId() % 1000000 < 100;
        boolean ring = false;
        boolean isCash = item.getItemId() / 1000000 == 5;

        IEquip equip = null;
        if (item.getType() == IItem.EQUIP) {
            equip = (IEquip) item;
            if (equip.getRingId() > -1) {
                ring = true;
            }
        }

        byte pos = zeroPosition ? 0 : item.getPosition();
        if (pos != 0) {
            if (shortPos) {
                mplew.writeShort(pos);
            } else {
                pos = (byte) Math.abs(pos);
                if (pos > 100 || (item.getType() == MapleInventoryType.EQUIPPED.getType() && ring)) {
                    isCash = true;
                    pos -= 100;
                }
                mplew.write(pos);
            }
        }

        // mplew.write(isPet ? 3 : item.getType());
        mplew.writeInt(item.getItemId());

        if (isCash) {
            mplew.write(1);
            mplew.writeLong(rng.nextLong());
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.2)));
        } else if (ring) {
            mplew.write(1);
            mplew.writeLong(equip.getRingId());
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.2)));
        } else if (isPet) {
            mplew.write(1); // we use unique id like rings here
            mplew.writeLong(item.getPetId());
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.2)));
            MaplePet pet = MaplePet.loadFromDb(item.getItemId(), item.getPosition(), item.getPetId());
            String petname = pet.getName();
            mplew.writeAsciiString(petname);
            for (int i = 0; i < 13 - petname.length(); i++) {
                mplew.write(0);
            }
            mplew.write(pet.getLevel());
            mplew.writeShort(pet.getCloseness());
            mplew.write(pet.getFullness());
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.2))); // TODO:
            // //
            // expirations
            return;
        } else {
            mplew.write(0);
            mplew.writeLong(rng.nextLong());
        }

        if (isEquip) {
            mplew.write(equip.getUpgradeSlots());
            mplew.write(equip.getLevel());
            mplew.writeShort(equip.getStr());
            mplew.writeShort(equip.getDex());
            mplew.writeShort(equip.getInt());
            mplew.writeShort(equip.getLuk());
            mplew.writeShort(equip.getHp());
            mplew.writeShort(equip.getMp());
            mplew.writeShort(equip.getWatk());
            mplew.writeShort(equip.getMatk());
            mplew.writeShort(equip.getWdef());
            mplew.writeShort(equip.getMdef());
            mplew.writeShort(equip.getAcc());
            mplew.writeShort(equip.getAvoid());
            mplew.writeShort(equip.getHands());
            mplew.writeShort(equip.getSpeed());
            mplew.writeShort(equip.getJump());
            // mplew.writeMapleAsciiString(equip.getOwner());
        } else {
            mplew.writeShort(item.getQuantity());
            // mplew.writeMapleAsciiString(item.getOwner());
        }
        // 1 = Sealed items cannot be sold, traded, or dropped.
        // mplew.writeShort(0);
    }

    public static MaplePacket serverNotice(int type, String message) {
        return serverMessage(type, 0, message, false);
    }

    public static MaplePacket serverNotice(int type, int channel, String message) {
        return serverMessage(type, channel, message, false);
    }
    /**
     * Gets a server message packet.
     *
     * Possible values for <code>type</code>:<br>
     * 0: [Notice] (BLUE)<br>
     * 1: Popup<br>
     * 2: Blue Text Light Blue BG<br>
     * 3: Super Megaphone<br>
     * 4: Scrolling message at top<br>
     *
     * @param type: The type of the notice.
     * @param channel: The channel this notice was sent on.
     * @param message: The message to convey.
     * @param servermessage: Is this a scrolling ticker?
     * @return The server notice packet.
     */
    private static MaplePacket serverMessage(int type, int channel, String message, boolean megaEar) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.BROADCAST_MSG.getValue());
        mplew.write(type);
        mplew.writeMapleAsciiString(message);
        if (type == 3) {
            mplew.write(channel - 1); // channel
            mplew.write(megaEar ? 1 : 0);
        }
        return mplew.getPacket();
    }

    /**
     * Gets an avatar megaphone packet.
     *
     * @param chr The character using the avatar megaphone.
     * @param channel The channel the character is on.
     * @param itemId The ID of the avatar-mega.
     * @param message The message that is sent.
     * @param ear
     * @return The avatar mega packet.
     */
    public static MaplePacket getAvatarMega(MapleCharacter chr, int channel, int itemId, List<String> message,
            boolean ear) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.AVATAR_MEGA.getValue());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        for (String s : message) {
            mplew.writeMapleAsciiString(s);
        }
        mplew.writeInt(channel - 1); // channel

        mplew.write(ear ? 1 : 0);
        CharacterData.addAvatarLook(mplew, chr, true);

        return mplew.getPacket();
    }

    /**
     * Gets a spawn monster packet.
     *
     * @param life The monster to spawn.
     * @param newSpawn Is it a new spawn?
     * @return The spawn monster packet.
     */
    public static MaplePacket spawnMonster(MapleMonster life, boolean newSpawn) {
        return spawnMonsterInternal(life, false, newSpawn, false, 0, false);
    }

    /**
     * Gets a spawn monster packet.
     *
     * @param life The monster to spawn.
     * @param newSpawn Is it a new spawn?
     * @param effect The spawn effect.
     * @return The spawn monster packet.
     */
    public static MaplePacket spawnMonster(MapleMonster life, boolean newSpawn, int effect) {
        return spawnMonsterInternal(life, false, newSpawn, false, effect, false);
    }

    /**
     * Gets a control monster packet.
     *
     * @param life The monster to give control to.
     * @param newSpawn Is it a new spawn?
     * @param aggro Aggressive monster?
     * @return The monster control packet.
     */
    public static MaplePacket controlMonster(MapleMonster life, boolean newSpawn, boolean aggro) {
        return spawnMonsterInternal(life, true, newSpawn, aggro, 0, false);
    }

    public static MaplePacket makeMonsterInvisible(MapleMonster life) {
        return spawnMonsterInternal(life, true, false, false, 0, true);
    }

    /**
     * Internal function to handler monster spawning and controlling.
     *
     * @param life The mob to perform operations with.
     * @param requestController Requesting control of mob?
     * @param newSpawn New spawn (fade in?)
     * @param aggro Aggressive mob?
     * @param effect The spawn effect to use.
     * @return The spawn/control packet.
     */
    private static MaplePacket spawnMonsterInternal(MapleMonster life, boolean requestController, boolean newSpawn,
            boolean aggro, int effect, boolean makeInvis) {
        //test
//        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
//        if (makeInvis) {
//            mplew.write(SendPacketOpcode.MOB_CHANGE_CONTROLLER.getValue());
//            mplew.write(0);
//            mplew.writeInt(life.getObjectId());
//            return mplew.getPacket();
//        }
//        if (requestController) {
//            mplew.write(SendPacketOpcode.MOB_CHANGE_CONTROLLER.getValue());
//            if (aggro) {
//                mplew.write(2);
//            } else {
//                mplew.write(1);
//            }
//        } else {
//            mplew.write(SendPacketOpcode.MOB_ENTER_FIELD.getValue());
//        }
//        mplew.writeInt(life.getObjectId());
//        mplew.writeInt(life.getId());
//        //Mob Position
//        mplew.writeShort(life.getPosition().x);
//        mplew.writeShort(life.getPosition().y);
//
//        if (requestController) {
//            mplew.write(2);
//        } else {
//            mplew.write(life.getController() != null ? 8 : 2);
//        }
//
//        mplew.writeShort(life.getFh());
//        mplew.writeShort(life.getStance());
//
//        if (effect > 0) {
//            mplew.write(effect);
//            mplew.writeInt(life.getObjectId());
//        }
//
//        if (newSpawn) {
//            mplew.write(-2);
//        } else {
//            mplew.write(-1);
//        }
//        mplew.write(0);
//        mplew.writeLong(0);
//
//        return mplew.getPacket();
	MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (requestController) {
            mplew.write(SendPacketOpcode.MOB_CHANGE_CONTROLLER.getValue());
            mplew.write(1);
        } else {
            mplew.write(SendPacketOpcode.MOB_ENTER_FIELD.getValue());
        }
        mplew.writeInt(life.getObjectId());
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        if (requestController) {
            mplew.write(2);
        } else {
            mplew.write((life.getController() != null ? 0x08 : 0x02));
        }
        mplew.writeShort(life.getFh()); // seems to be left and right
        mplew.writeShort(life.getStance());
        if (effect > 0) 
        {
            mplew.write(effect);
            mplew.writeInt(life.getObjectId());
        }
        if (newSpawn) {
            mplew.write(-2);
        } else {
            mplew.write(-1);
        }
        mplew.write(0);
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    /**
     * Handles monsters not being targettable, such as Zakum's first body.
     *
     * @param life The mob to spawn as non-targettable.
     * @param effect The effect to show when spawning.
     * @return The packet to spawn the mob as non-targettable.
     */
    public static MaplePacket spawnFakeMonster(MapleMonster life, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_CHANGE_CONTROLLER.getValue());
        mplew.write(1);
        mplew.writeInt(life.getObjectId());
        mplew.write(5);
        mplew.writeInt(life.getId());
        mplew.writeInt(0);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(life.getStartFh());
        mplew.writeShort(life.getFh());
        if (effect > 0) {
            mplew.write(effect);
            mplew.write(0);
            mplew.writeShort(0);
        }
        mplew.writeShort(-2);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Makes a monster previously spawned as non-targettable, targettable.
     *
     * @param life The mob to make targettable.
     * @return The packet to make the mob targettable.
     */
    public static MaplePacket makeMonsterReal(MapleMonster life) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_ENTER_FIELD.getValue());
        mplew.writeInt(life.getObjectId());
        mplew.write(5);
        mplew.writeInt(life.getId());
        mplew.writeInt(0);
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getPosition().y);
        mplew.write(life.getStance());
        mplew.writeShort(life.getStartFh());
        mplew.writeShort(life.getFh());
        mplew.writeShort(-1);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Gets a stop control monster packet.
     *
     * @param oid The ObjectID of the monster to stop controlling.
     * @return The stop control monster packet.
     */
    public static MaplePacket stopControllingMonster(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_CHANGE_CONTROLLER.getValue());
        mplew.write(0);
        //test
        //mplew.writeInt(oid);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    /**
     * Gets a response to a move monster packet.
     *
     * @param objectId The ObjectID of the monster being moved.
     * @param moveId The movement ID.
     * @param currentMp The current MP of the monster.
     * @param useSkills Can the monster use skills?
     * @return The move response packet.
     */
    public static MaplePacket moveMonsterResponse(int objectId, short moveId, int currentMp, boolean useSkills) {
        return moveMonsterResponse(objectId, moveId, currentMp, useSkills, 0, 0);
    }

    /**
     * Gets a response to a move monster packet.
     *
     * @param objectid The ObjectID of the monster being moved.
     * @param moveid The movement ID.
     * @param currentMp The current MP of the monster.
     * @param useSkills Can the monster use skills?
     * @param skillId The skill ID for the monster to use.
     * @param skillLevel The level of the skill to use.
     * @return The move response packet.
     */
    public static MaplePacket moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MOB_MOVE_RESPONSE.getValue());
        mplew.writeInt(objectid);
        mplew.writeShort(moveid);
        mplew.write(useSkills ? 1 : 0);
        mplew.writeShort(currentMp);
        mplew.write(skillId);
        mplew.write(skillLevel);
        return mplew.getPacket();
    }

    /**
     * For testing only! Gets a packet from a hexadecimal string.
     *
     * @param hex The hexadecimal packet to create.
     * @return The MaplePacket representing the hex string.
     */
    public static MaplePacket getPacketFromHexString(String hex) {
        byte[] b = HexTool.getByteArrayFromHexString(hex);
        return new ByteArrayMaplePacket(b);
    }

    /**
     * Gets a packet telling the client to show an EXP increase.
     *
     * @param gain The amount of EXP gained.
     * @param inChat In the chat box?
     * @param white White text or yellow?
     * @return The exp gained packet.
     */
    public static MaplePacket getShowExpGain(int gain, boolean inChat, boolean white) {
        // 20 00 03 01 0A 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(3); // 3 = exp, 4 = fame, 5 = mesos, 6 = guildpoints

        mplew.write(white ? 1 : 0);
        mplew.writeInt(gain);
        mplew.write(inChat ? 1 : 0);
        mplew.writeInt(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show a fame gain.
     *
     * @param gain How many fame gained.
     * @return The meso gain packet.
     */
    public static MaplePacket getShowFameGain(int gain) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(4);
        mplew.writeInt(gain);

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show a meso gain.
     *
     * @param gain How many mesos gained.
     * @return The meso gain packet.
     */
    public static MaplePacket getShowMesoGain(int gain) {
        return getShowMesoGain(gain, false);
    }

    /**
     * Gets a packet telling the client to show a meso gain.
     *
     * @param gain How many mesos gained.
     * @param inChat Show in the chat window?
     * @return The meso gain packet.
     */
    public static MaplePacket getShowMesoGain(int gain, boolean inChat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        if (!inChat) {
            mplew.write(0);
            mplew.write(1);
        } else {
            mplew.write(5);
        }
        mplew.writeInt(gain);
        mplew.writeShort(0); // inet cafe meso gain ?.o

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show a item gain.
     *
     * @param itemId The ID of the item gained.
     * @param quantity How many items gained.
     * @return The item gain packet.
     */
    public static MaplePacket getShowItemGain(int itemId, short quantity) {
        return getShowItemGain(itemId, quantity, false);
    }

    /**
     * Gets a packet telling the client to show an item gain.
     *
     * @param itemId The ID of the item gained.
     * @param quantity The number of items gained.
     * @param inChat Show in the chat window?
     * @return The item gain packet.
     */
    public static MaplePacket getShowItemGain(int itemId, short quantity, boolean inChat) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        if (inChat) {
            mplew.write(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(3);
            mplew.write(1);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
        } else {
            mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.writeShort(0);
            mplew.writeInt(itemId);
            mplew.writeInt(quantity);
            mplew.writeInt(0);
            mplew.writeInt(0);
        }

        return mplew.getPacket();
    }

    public static MaplePacket getShowItemLost(int itemId, short quantity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(3);
        mplew.write(1);
        mplew.writeInt(itemId);
        mplew.writeInt(quantity);
        return mplew.getPacket();
    }

    public static MaplePacket killMonster(int oid, boolean animation) {
        return killMonster(oid, animation ? 1 : 0);
    }

    /**
     * Gets a packet telling the client that a monster was killed.
     *
     * @param oid The objectID of the killed monster.
     * @param animation 0 = dissapear, 1 = fade out, 2+ = special
     * @return The kill monster packet.
     */
    public static MaplePacket killMonster(int oid, int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_LEAVE_FIELD.getValue());
        mplew.writeInt(oid);
        mplew.write(animation); // Not a boolean, really an int type

        return mplew.getPacket();
    }

    /**
     * Gets a packet telling the client to show mesos coming out of a map
     * object.
     *
     * @param amount The amount of mesos.
     * @param itemoid The ObjectID of the dropped mesos.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @return The drop mesos packet.
     */
    public static MaplePacket dropMesoFromMapObject(int amount, int itemoid, int dropperoid, int ownerid,
            Point dropfrom, Point dropto, byte mod) {
        return dropItemFromMapObjectInternal(amount, itemoid, dropperoid, ownerid, dropfrom, dropto, mod, true);
    }

    /**
     * Gets a packet telling the client to show an item coming out of a map
     * object.
     *
     * @param itemid The ID of the dropped item.
     * @param itemoid The ObjectID of the dropped item.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @return The drop mesos packet.
     */
    public static MaplePacket dropItemFromMapObject(int itemid, int itemoid, int dropperoid, int ownerid,
            Point dropfrom, Point dropto, byte mod) {
        return dropItemFromMapObjectInternal(itemid, itemoid, dropperoid, ownerid, dropfrom, dropto, mod, false);
    }

    /**
     * Internal function to get a packet to tell the client to drop an item onto
     * the map.
     *
     * @param itemid The ID of the item to drop.
     * @param itemoid The ObjectID of the dropped item.
     * @param dropperoid The OID of the dropper.
     * @param ownerid The ID of the drop owner.
     * @param dropfrom Where to drop from.
     * @param dropto Where the drop lands.
     * @param mod ?
     * @param mesos Is the drop mesos?
     * @return The item drop packet.
     */
    public static MaplePacket dropItemFromMapObjectInternal(int itemid, int itemoid, int dropperoid, int ownerid,
            Point dropfrom, Point dropto, byte mod, boolean mesos) {
        // dropping mesos
        // BF 00 01 01 00 00 00 01 0A 00 00 00 24 46 32 00 00 84 FF 70 00 00 00
        // 00 00 84 FF 70 00 00 00 00
        // dropping maple stars
        // BF 00 00 02 00 00 00 00 FB 95 1F 00 24 46 32 00 00 84 FF 70 00 00 00
        // 00 00 84 FF 70 00 00 00 00 80 05 BB 46 E6 17 02 00
        // killing monster (0F 2C 67 00)
        // BF 00 01 2C 03 00 00 00 6D 09 3D 00 24 46 32 00 00 A3 02 6C FF 0F 2C
        // 67 00 A3 02 94 FF 89 01 00 80 05 BB 46 E6 17 02 01

        // 4000109
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.DROP_ENTER_FIELD.getValue());
        // mplew.write(1); // 1 with animation, 2 without o.o
        mplew.write(mod);
        mplew.writeInt(itemoid);
        mplew.write(mesos ? 1 : 0); // 1 = mesos, 0 =item

        mplew.writeInt(itemid);
        mplew.writeInt(ownerid); // owner charid

        mplew.write(0);
        mplew.writeShort(dropto.x);
        mplew.writeShort(dropto.y);
               
        if (mod != 2) {
            mplew.writeInt(dropperoid); // 
            mplew.writeShort(dropfrom.x);
            mplew.writeShort(dropfrom.y);
            mplew.writeShort(0);
            //
        }
        
        if (!mesos) {
            // TODO getTheExpirationTimeFromSomewhere o.o
            //
            mplew.write(ITEM_MAGIC);//
                    //KoreanDateUtil.getItemTimestamp(System.currentTimeMillis()));

        }
        else
        {
            mplew.write(0);//           
        }
        //
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);

        return mplew.getPacket();
    }

    /*
	 * (non-javadoc) TODO: make MapleCharacter a mapobject, remove the need for
	 * passing oid here.
     */
    /**
     * Gets a packet spawning a player as a mapobject to other clients.
     *
     * @param chr The character to spawn to other clients.
     * @return The spawn player packet.
     */
    public static MaplePacket spawnPlayerMapobject(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.USER_ENTER_FIELD.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeMapleAsciiString(chr.getName());

        long buffmask = 0;
        Integer buffvalue = null;
        if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
            buffmask |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
            buffmask |= MapleBuffStat.COMBO.getValue();
            buffvalue = chr.getBuffedValue(MapleBuffStat.COMBO);
        }
        if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
            buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
            buffmask |= MapleBuffStat.SOULARROW.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            buffvalue = chr.getBuffedValue(MapleBuffStat.MORPH);
        }
        
        mplew.writeInt((int) ((buffmask >> 32) & 0xffffffffL));
        if (buffvalue != null) {
            mplew.write(buffvalue.byteValue());
        }

        CharacterData.addAvatarLook(mplew, chr, true);
        mplew.writeInt(0);
        mplew.writeInt(chr.getItemEffect());
        mplew.writeInt(chr.getChair());
        mplew.writeShort(chr.getPosition().x);
        System.out.println("char x: " + chr.getPosition().x);
        mplew.writeShort(chr.getPosition().y);
        System.out.println("char y: " + chr.getPosition().y);
        mplew.write(chr.getStance());
        mplew.writeShort(0);//FH

        if (chr.getPet(0) != null) {
            mplew.write(1);
            MaplePet pet = chr.getPet(0);
            mplew.writeInt(pet.getItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.writeLong(pet.getUniqueId());
            mplew.writeShort(pet.getPos().x);
            mplew.writeShort(pet.getPos().y);
            mplew.write(pet.getStance());
            mplew.writeShort(pet.getFh());
        } else {
            mplew.write(0);
        }
        
        mplew.write(0); //What's this?

        // Mini Game & Interaction Boxes
        if (chr.getPlayerShop() != null && chr.getPlayerShop().isOwner(chr)) {
            addShopBox(mplew, chr.getPlayerShop());
        } else if ((MapleMiniGame) chr.getInteraction() != null) {
            addAnnounceBox(mplew, chr.getInteraction());
        } else {
            mplew.write(0);
        }

        // Rings
        List<MapleRing> rings = getRing(chr);
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(ring.getRingId());
            mplew.writeInt(0);
            mplew.writeInt(ring.getPartnerRingId());
            mplew.writeInt(0);
            mplew.writeInt(ring.getItemId());
        }
        return mplew.getPacket();
    }

    public static List<MapleRing> getRing(MapleCharacter chr) {
        MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<Item>(equippedC.size());
        for (IItem item : equippedC) {
            equipped.add((Item) item);
        }
        Collections.sort(equipped);
        List<MapleRing> rings = new ArrayList<MapleRing>();
        for (Item item : equipped) {
            if (((IEquip) item).getRingId() > -1) {
                rings.add(MapleRing.loadFromDb(((IEquip) item).getRingId()));
            }
        }
        Collections.sort(rings);
        return rings;
    }

    private static void addShopBox(MaplePacketLittleEndianWriter mplew, MaplePlayerShop shop) {
        // 00: no game
        // 01: omok game
        // 02: card game
        // 04: shop
        mplew.write(4);
        mplew.writeInt(shop.getObjectId()); // gameid/shopid
        mplew.writeMapleAsciiString(shop.getDescription()); // desc
        // 00: public
        // 01: private
        mplew.write(0);
        // 00: red 4x3
        // 01: green 5x4
        // 02: blue 6x5
        // omok:
        // 00: normal
        mplew.write(0);
        // first slot: 1/2/3/4
        // second slot: 1/2/3/4
        mplew.write(1);
        mplew.write(4);
        // 0: open
        // 1: in progress
        mplew.write(0);
    }

    private static void serializeMovementList(LittleEndianWriter lew, List<LifeMovementFragment> moves) {
        lew.write(moves.size());
        for (LifeMovementFragment move : moves) {
            move.serialize(lew);
        }
    }

    public static MaplePacket movePlayer(int cid, SeekableLittleEndianAccessor slea) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        //;
        mplew.write(SendPacketOpcode.MOVE_PLAYER.getValue());
        //);
        mplew.writeInt(cid);
        //;
        mplew.write(slea.read((int) slea.available()));
//        byte[] bytes  = ((slea.read((int)(slea.available()))));
//        
//        for (byte b:bytes)
//        {
//            System.out.print(b);
//        }
        return mplew.getPacket();
    }

    public static MaplePacket moveSummon(int cid, int oid, Point startPos, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOVE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(oid);
        mplew.writeShort(startPos.x);
        mplew.writeShort(startPos.y);
        serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static MaplePacket moveMonster(int mobid, int controllerId, int moveId, boolean useSkill, byte skill, SeekableLittleEndianAccessor slea, MapleMonster monster) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MOB_MOVE.getValue());
        mplew.writeInt(mobid);
        mplew.write(useSkill ? 1 : 0);
        mplew.write(skill);
        mplew.writeInt(controllerId);
        mplew.write(slea.read((int) slea.available()));
        mplew.writeLong(0);
        mplew.writeLong(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket startKiteEffect(MapleCharacter chr, int itemid, String message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.write(139);
            mplew.writeInt(chr.getId());
            mplew.writeInt(itemid);
            mplew.writeMapleAsciiString(message);
            mplew.writeMapleAsciiString(chr.getName());
            mplew.writeShort(chr.getPosition().x);
            mplew.writeShort(chr.getPosition().y);
            return mplew.getPacket();
    }

    public static MaplePacket summonAttack(int cid, int summonSkillId,
            int newStance/* , List<SummonAttackEntry> allDamage */) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SUMMON_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);
        // mplew.write(allDamage.size());
        /*
		 * for (SummonAttackEntry attackEntry : allDamage) {
		 * mplew.writeInt(attackEntry.getMonsterOid()); // oid
		 * 
		 * mplew.write(6); // who knows
		 * 
		 * mplew.writeInt(attackEntry.getDamage()); // damage
		 * 
		 * }
         */
        return mplew.getPacket();
    }

    public static MaplePacket closeRangeAttack(int cid, int skill, int stance, int direction, int numAttackedAndDamage, List<Pair<Integer, List<Integer>>> damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CLOSE_RANGE_ATTACK.getValue());
        // mplew.writeShort(0x7F); // 47 7D
        if (skill == 4211006) { // meso explosion
            addMesoExplosion(mplew, cid, skill, stance, direction, numAttackedAndDamage, 0, damage);
        } else {
            addAttackBody(mplew, cid, skill, stance, direction, numAttackedAndDamage, 0, damage);
        }

        return mplew.getPacket();
    }

    public static MaplePacket rangedAttack(int cid, int skill, int stance, int direction, int numAttackedAndDamage, int projectile,
            List<Pair<Integer, List<Integer>>> damage) {
        // 7E 00 30 75 00 00 01 00 97 04 0A CB 72 1F 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.RANGED_ATTACK.getValue());
        // mplew.writeShort(0x80); // 47 7E
        addAttackBody(mplew, cid, skill, stance, direction, numAttackedAndDamage, projectile, damage);

        return mplew.getPacket();
    }

    public static MaplePacket magicAttack(int cid, int skill, int stance, int direction, int numAttackedAndDamage,
            List<Pair<Integer, List<Integer>>> damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MAGIC_ATTACK.getValue());
        addAttackBody(mplew, cid, skill, stance, direction, numAttackedAndDamage, 0, damage);
        return mplew.getPacket();
    }

    private static void addAttackBody(LittleEndianWriter lew, int cid, int skill, int stance, int direction, int numAttackedAndDamage,
            int projectile, List<Pair<Integer, List<Integer>>> damage) {
        lew.writeInt(cid);
        lew.write(numAttackedAndDamage);
        if (skill > 0) {
            lew.write(0xFF); // too low and some skills don't work (?)
            lew.writeInt(skill);
        } else {
            lew.write(0);
        }
        lew.write(stance);
        lew.write(direction);
        lew.write(0); //Mastery
        lew.writeInt(projectile); // 00

        for (Pair<Integer, List<Integer>> oned : damage) {
            if (oned.getRight() != null) {
                lew.writeInt(oned.getLeft());
                lew.write(0x06); //0xFF
                for (Integer eachd : oned.getRight()) { // highest bit set = crit
                    lew.writeInt(eachd);
                }
            }
        }
        lew.writeLong(0);
    }

    private static void addMesoExplosion(LittleEndianWriter lew, int cid, int skill, int stance, int direction,
            int numAttackedAndDamage, int projectile, List<Pair<Integer, List<Integer>>> damage) {
        lew.writeInt(cid);
        lew.write(numAttackedAndDamage);
        lew.write(0x1E);
        lew.writeInt(skill);
        lew.write(stance);
        lew.write(direction);
        lew.write(0); //Mastery
        lew.writeInt(projectile); // 00
        for (Pair<Integer, List<Integer>> oned : damage) {
            if (oned.getRight() != null) {
                lew.writeInt(oned.getLeft());
                lew.write(0xFF);
                lew.write(oned.getRight().size());
                for (Integer eachd : oned.getRight()) {
                    lew.writeInt(eachd);
                }
            }
        }

    }

    /*
	 * 19 reference 00 01 00 = new while adding 01 01 00 = add from drop 00 01
	 * 01 = update count 00 01 03 = clear slot 01 01 02 = move to empty slot 01
	 * 02 03 = move and merge 01 02 01 = move and merge with rest
     */
    public static MaplePacket scrollResult(boolean success)
    {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MEMO_RESULT.getValue());
        mplew.write(6);
        mplew.write(success ? 1 : 0);
        return mplew.getPacket();
    }
    
    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item) {
        return addInventorySlot(type, item, false);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        if (fromDrop) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.write(1); // Add Mode '01 00'
        mplew.write(0);
        mplew.write(type.getType()); // iv type
        // mplew.write(item.getPosition()); // slot id
        addItemInfo(mplew, item, true);
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item) {
        return updateInventorySlot(type, item, false);
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        if (fromDrop) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.write(HexTool.getByteArrayFromHexString("01 01")); // update
        mplew.write(type.getType()); // iv type
        mplew.write(item.getPosition()); // slot id
        mplew.write(0); // ?
        mplew.writeShort(item.getQuantity());
        return mplew.getPacket();
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, byte src, byte dst) {
        return moveInventoryItem(type, src, dst, (byte) -1);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, byte src, byte dst, byte equipIndicator) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 02"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(dst);
        if (equipIndicator != -1) {
            mplew.write(equipIndicator);
        }

        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, byte src, byte dst, short total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1); // merge mode?

        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);

        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, byte src, byte dst, short srcQ,
            short dstQ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 01"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(HexTool.getByteArrayFromHexString("01"));
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);

        return mplew.getPacket();
    }

    public static MaplePacket clearInventoryItem(MapleInventoryType type, byte slot, boolean fromDrop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(fromDrop ? 1 : 0);
        mplew.write(HexTool.getByteArrayFromHexString("01 03"));
        mplew.write(type.getType());
        mplew.writeShort(slot);

        return mplew.getPacket();
    }

    public static MaplePacket scrolledItem(IItem scroll, IItem item, boolean destroyed) {
        // 18 00 01 02 03 02 08 00 03 01 F7 FF 01
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(1); // fromdrop always true

        mplew.write(destroyed ? 2 : 3);
        mplew.write(scroll.getQuantity() > 0 ? 1 : 3);
        mplew.write(MapleInventoryType.USE.getType());
        mplew.writeShort(scroll.getPosition());
        if (scroll.getQuantity() > 0) {
            mplew.writeShort(scroll.getQuantity());
        }
        mplew.write(3);
        if (!destroyed) {
            mplew.write(MapleInventoryType.EQUIP.getType());
            mplew.writeShort(item.getPosition());
            mplew.write(0);
        }
        mplew.write(MapleInventoryType.EQUIP.getType());
        mplew.writeShort(item.getPosition());
        if (!destroyed) {
            addItemInfo(mplew, item, true, true);
        }
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket getScrollEffect(ScrollResult scrollSuccess) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(6);
        switch (scrollSuccess) {
            case SUCCESS:
                mplew.write(2); // 1: Nothing | 2: Nothing? wtf..
                break;
            case FAIL:
                mplew.write(0);
                break;
            case CURSE:
                mplew.write(1);
                break;
            default:
                throw new IllegalArgumentException("effects don't even work in v40 beta and we found a new one? wut");
        }
        return mplew.getPacket();
    }

    public static MaplePacket removePlayerFromMap(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.USER_LEAVE_FIELD.getValue());
        // mplew.writeShort(0x65); // 47 63
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    /**
     * animation: 0 - expire<br/>
     * 1 - without animation<br/>
     * 2 - pickup<br/>
     * 4 - explode<br/>
     * cid is ignored for 0 and 1
     *
     * @param oid
     * @param animation
     * @param cid
     * @return
     */
    public static MaplePacket removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, false, 0);
    }

    /**
     * animation: 0 - expire<br/>
     * 1 - without animation<br/>
     * 2 - pickup<br/>
     * 4 - explode<br/>
     * cid is ignored for 0 and 1.<br />
     * <br />
     * Flagging pet as true will make a pet pick up the item.
     *
     * @param oid
     * @param animation
     * @param cid
     * @param pet
     * @param slot
     * @return
     */
    public static MaplePacket removeItemFromMap(int oid, int animation, int cid, boolean pet, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.DROP_LEAVE_FIELD.getValue());
        mplew.write(animation); // expire

        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (pet) {
                mplew.write(slot);
            }
        }

        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItem(MapleInventoryType type, short src) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        // mplew.writeShort(0x19);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        if (src < 0) {
            mplew.write(1);
        }

        return mplew.getPacket();
    }

    public static MaplePacket dropInventoryItemUpdate(MapleInventoryType type, IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01"));
        mplew.write(type.getType());
        mplew.writeShort(item.getPosition());
        mplew.writeShort(item.getQuantity());

        return mplew.getPacket();
    }

    /**
     * @description - Gets a basic User::OnHit packet with the damage dealt.
     * @param monsterId - The monster attacking
     * @param cid - The Character being attacked
     * @param damage - The damage dealt to the Character
     * @return
     */
    public static MaplePacket damagePlayer(int monsterId, int cid, int damage) {
        return damagePlayer(0, monsterId, cid, damage, 0, 0, false, 0, false, 0, 0, 0);
    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage) {
        return damagePlayer(skill, monsteridfrom, cid, damage, 0, 0, false, 0, false, 0, 0, 0);
    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage, int fake, int direction,
            boolean pgmr, int pgmr_1, boolean is_pg, int oid, int pos_x, int pos_y) {
        // 82 00 30 C0 23 00 FF 00 00 00 00 B4 34 03 00 01 00 00 00 00 00 00
        ;
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.DAMAGE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.write(skill);
        mplew.writeInt(monsteridfrom);
        mplew.writeInt(damage);
        mplew.writeInt(1);
        //mplew.write(direction);
        //mplew.write(0);
        if (pgmr) {
            mplew.write(pgmr_1);
            mplew.write(is_pg ? 1 : 0);
            mplew.writeInt(oid);
            mplew.write(6);
            mplew.writeShort(pos_x);
            mplew.writeShort(pos_y);
            mplew.write(0);
        } else {
            mplew.writeShort(0);
        }

        mplew.writeInt(damage);

        if (fake > 0) {
            mplew.writeInt(fake);
        }

        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @return
     */
    public static MaplePacket startQuest(MapleCharacter c, short quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.writeShort(1);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket charInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.CHARACTER_INFO.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getJob().getId());
        mplew.writeShort(chr.getFame());
        mplew.writeMapleAsciiString("");		
        
        if (chr.getPet(0) != null) {
			MaplePet pet = chr.getPet(0);
			mplew.write(1);
			mplew.writeInt(pet.getItemId());
			mplew.writeMapleAsciiString(pet.getName());
			mplew.write(pet.getLevel());
			mplew.writeShort(pet.getCloseness());
			mplew.write(pet.getFullness());
			mplew.writeInt(0); // Pet Equip ID? I can't find the ID in v40 so
								// i'm not sure wtf this is
		} else {
			mplew.write(0);
		}

        mplew.write(0); //Wishlist
        mplew.writeLong(0); //??

        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @return
     */
    public static MaplePacket forfeitQuest(MapleCharacter c, short quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @return
     */
    public static MaplePacket completeQuest(MapleCharacter c, short quest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(quest);
        mplew.write(2);
        mplew.writeLong(getTime(System.currentTimeMillis()));

        return mplew.getPacket();
    }

    /**
     *
     * @param c
     * @param quest
     * @param npc
     * @param progress
     * @return
     */
    // frz note, 0.52 transition: this is only used when starting a quest and
    // seems to have no effect, is it needed?
    public static MaplePacket updateQuestInfo(MapleCharacter c, short quest, int npc, byte progress) {
        // [A5 00] [08] [69 08] [86 71 0F 00] [00 00 00 00]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.QUEST_CLEAR.getValue());
        mplew.write(progress);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    private static <E extends LongValueHolder> long getLongMask(List<Pair<E, Integer>> statups) {
        long mask = 0;
        for (Pair<E, Integer> statup : statups) {
            mask |= statup.getLeft().getValue();
        }
        return mask;
    }

    private static <E extends LongValueHolder> long getLongMaskFromList(List<E> statups) {
        long mask = 0;
        for (E statup : statups) {
            mask |= statup.getValue();
        }
        return mask;
    }

    /**
     * It is important that statups is in the correct order (see decleration
     * order in MapleBuffStat) since this method doesn't do automagical
     * reordering.
     *
     * @param buffid
     * @param bufflength
     * @param statups
     * @return
     */
    public static MaplePacket giveBuff(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.FORCED_STAT_SET.getValue());
        mplew.writeInt(getIntMask(statups));
        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeInt(buffid);
            mplew.writeShort(bufflength / 1000);
        }
        mplew.writeLong(0);
        mplew.writeLong(0);
        return mplew.getPacket();
    }
    
    public static MaplePacket cancelBuff(List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.FORCED_STAT_RESET.getValue());
        mplew.writeInt(getIntMaskFromList(statups));
        mplew.writeLong(0);
        return mplew.getPacket();
    }
    
    /**
     * @author Exile
     * ... God damn it Eric
     */
    
	private static <E extends LongValueHolder> int getIntMask(List<Pair<E, Integer>> statups) {
		int mask = 0x0;
		for (Pair<E, Integer> statup : statups) {
			mask |= statup.getLeft().getValue();
		}
		return mask;
	}
	
    /**
     * @author Eric
     * 
     */

	private static <E extends LongValueHolder> int getIntMaskFromList(List<E> statups) {
		int mask = 0x0;
		for (E statup : statups) {
			mask |= statup.getValue();
		}
		return mask;
	}

	

    public static MaplePacket giveDebuff(long mask, List<Pair<MapleDisease, Integer>> statups, MobSkill skill) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.FORCED_STAT_SET.getValue());
        mplew.writeLong(0);
        mplew.writeLong(mask);
        for (Pair<MapleDisease, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(skill.getSkillId());
            mplew.writeShort(skill.getSkillLevel());
            mplew.writeInt((int) skill.getDuration());
        }
        mplew.writeShort(0); // ??? wk charges have 600 here o.o

        mplew.writeShort(900);// Delay

        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket giveForeignDebuff(int cid, long mask, MobSkill skill) {
        // [99 00] [6A 4D 27 00] [00 00 00 00 00 00 00 00] [00 00 00 00 00 00 00
        // 40] [7A 00] [01 00] [00 00] [84 03]
        // [99 00] [7E 31 50 00] [00 00 00 00 00 00 00 00] [00 00 00 00 00 00 00
        // 80] [7C 00] [01 00] [00 00] [84 03]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(0);
        mplew.writeLong(mask);
        mplew.writeShort(skill.getSkillId());
        mplew.writeShort(skill.getSkillLevel());
        mplew.writeShort(0); // same as give_buff

        mplew.writeShort(900);// Delay

        return mplew.getPacket();
    }

    public static MaplePacket cancelForeignDebuff(int cid, long mask) {
        // 8A 00 24 46 32 00 80 04 00 00 00 00 00 00 F4 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(0);
        mplew.writeLong(mask);

        return mplew.getPacket();
    }

    public static MaplePacket giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups,
            MapleStatEffect effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        long mask = getLongMask(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);
        // mplew.writeShort(0);
        for (Pair<MapleBuffStat, Integer> statup : statups) {
        	mplew.writeShort(statup.getRight().shortValue()); 
        }
        mplew.writeShort(0); // same as give_buff
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        long mask = getLongMaskFromList(statups);
        mplew.writeLong(0);
        mplew.writeLong(mask);

        return mplew.getPacket();
    }

    public static MaplePacket cancelDebuff(long mask) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.FORCED_STAT_RESET.getValue());
        mplew.writeLong(0);
        mplew.writeLong(mask);
        mplew.write(0);

        return mplew.getPacket();
    }
   
    /**
     * Adds a announcement box to an existing MaplePacketLittleEndianWriter.
     *
     * @param mplew The MaplePacketLittleEndianWriter to add an announcement box
     * to.
     * @param shop The shop to announce.
     */
    private static void addAnnounceBox(MaplePacketLittleEndianWriter mplew, IPlayerInteractionManager interaction) {
        mplew.write(interaction.getShopType());
        if (interaction.getShopType() == 4) {
            mplew.writeInt(((MaplePlayerShop) interaction).getObjectId());
        } else {
            mplew.writeInt(((MapleMiniGame) interaction).getObjectId());
        }
        mplew.writeMapleAsciiString(interaction.getDescription()); // desc
        mplew.write(0);
        mplew.write(interaction.getItemType());
        mplew.write(1);
        mplew.write(interaction.getFreeSlot() > -1 ? 4 : 1);

        if (interaction.getShopType() == 4) {
            mplew.write(0);
        } else {
            mplew.write(((MapleMiniGame) interaction).getStarted() ? 1 : 0);
        }
    }

    public static MaplePacket showLevelup(int cid) {
        return showForeignEffect(cid, 0);
    }

    public static MaplePacket showJobChange(int cid) {
        return showForeignEffect(cid, 8);
    }

    public static MaplePacket showForeignEffect(int cid, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid); // ?

        mplew.write(effect);

        return mplew.getPacket();
    }

    public static MaplePacket showBuffeffect(int cid, int skillid, int effectid, byte direction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid); // ?
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1); // probably buff level but we don't know it and it
        // doesn't really matter
        if (direction != (byte) 3) {
            mplew.write(direction);
        }

        return mplew.getPacket();
    }

    public static MaplePacket showOwnBuffEffect(int skillid, int effectid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(effectid);
        mplew.writeInt(skillid);
        mplew.write(1); // probably buff level but we don't know it and it
        // doesn't really matter

        return mplew.getPacket();
    }

    public static MaplePacket showOwnBerserk(int skilllevel, boolean Berserk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(1);
        mplew.writeInt(1320006);
        mplew.write(skilllevel);
        mplew.write(Berserk ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket showBerserk(int cid, int skilllevel, boolean Berserk) {
        // [99 00] [5D 94 27 00] [01] [46 24 14 00] [14] [01]
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(1);
        mplew.writeInt(1320006);
        mplew.write(skilllevel);
        mplew.write(Berserk ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket beholderAnimation(int cid, int skillid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(skillid);
        mplew.writeShort(135);

        return mplew.getPacket();
    }

    public static MaplePacket updateSkill(int skillid, int level, int masterlevel) {
        // 1E 00 01 01 00 E9 03 00 00 01 00 00 00 00 00 00 00 01
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.CHANGE_SKILL_RECORD_RESULT.getValue());
        mplew.write(1);
        mplew.writeShort(1);
        mplew.writeInt(skillid);
        mplew.writeInt(level);
        mplew.writeInt(masterlevel);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket updateQuestMobKills(MapleQuestStatus status) {
        // 21 00 01 FB 03 01 03 00 30 30 31
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(1);
        mplew.writeShort(status.getQuest().getId());
        mplew.write(1);
        String killStr = "";
        for (int kills : status.getMobKills().values()) {
            killStr += StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3);
        }
        mplew.writeMapleAsciiString(killStr);
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static MaplePacket getShowQuestCompletion(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SET_QUEST_CLEAR.getValue());
        mplew.writeShort(id);

        return mplew.getPacket();
    }

    public static MaplePacket getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(18); // 18
        mplew.writeMapleAsciiString(sender); // Sender
        mplew.write(channel - 1); // Channel
        mplew.writeMapleAsciiString(text); // Text
        return mplew.getPacket();
    }

    public static MaplePacket playerIsInCs(MapleCharacter target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target.getName());
        mplew.write(2); // '%s' is at the Cash Shop.
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    /**
     *
     * @param target name of the target character
     * @param reply error code: 0x0 = cannot find char, 0x1 = success
     * @return the MaplePacket
     */
    public static MaplePacket getWhisperReply(MapleCharacter target, String message, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(18); // whisper?
        mplew.writeMapleAsciiString(target.getName());
        mplew.write(channel - 1);
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }
	
	public static MaplePacket getFind(MapleCharacter target, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target.getName());
        mplew.write(3);
        mplew.writeInt(channel - 1);
        return mplew.getPacket();
    }

    public static MaplePacket getFind(MapleCharacter target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target.getName());
        mplew.write(1);
        mplew.writeInt(target.getMapId());
        return mplew.getPacket();
    }

    public static MaplePacket getUnableToFind(String target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(4);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    /**
     *
     * @param target name of the target character
     * @param reply error code: 0x0 = cannot find char, 0x1 = success
     * @return the MaplePacket
     */
    public static MaplePacket getWhisperReply(String target, byte reply) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(10); // whisper?

        mplew.writeMapleAsciiString(target);
        mplew.write(reply);

        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithMap(String target, int mapid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        mplew.write(new byte[8]); // ?? official doesn't send zeros here but
        // whatever

        return mplew.getPacket();
    }

    public static MaplePacket getFindReply(String target, int channel) {
        // Received UNKNOWN (1205941596.79689): (25)
        // 54 00 09 07 00 64 61 76 74 73 61 69 01 86 7F 3D 36 D5 02 00 00 22 00
        // 00 00
        // T....davtsai..=6...."...
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);

        return mplew.getPacket();
    }

    public static MaplePacket getInventoryFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(1);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket getShowInventoryFull() {
        return getShowInventoryStatus(0xff);
    }

    public static MaplePacket showItemUnavailable() {
        return getShowInventoryStatus(0xfe);
    }

    public static MaplePacket getShowInventoryStatus(int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0);
        mplew.write(mode);
        mplew.write(0);
        mplew.write(0);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static MaplePacket getStorage(int npcId, byte slots, Collection<IItem> items, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.STORAGE.getValue());
        mplew.writeInt(npcId);
        mplew.write(slots);
        mplew.writeShort(126);
        mplew.writeInt(meso);
        getStorageContent(mplew, items);
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static void getStorageContent(MaplePacketLittleEndianWriter mplew, Collection<IItem> items) {
        byte size1 = 0, size2 = 0, size3 = 0, size4 = 0, size5 = 0;
        for (IItem item : items) {
            switch (item.getItemId() / 1000000) {
                case 1:
                    size1++;
                    break;
                case 2:
                    size2++;
                    break;
                case 3:
                    size3++;
                    break;
                case 4:
                    size4++;
                    break;
                case 5:
                    size5++;
                    break;
                default:
                    ;
                    break;
            }
        }
        mplew.write(size1);
        for (IItem item : items) { // Equip
            if (item.getItemId() / 1000000 == 1) {
                addItemInfo(mplew, item, false, true);
            }
        }
        mplew.write(size2);
        for (IItem item : items) { // Use
            if (item.getItemId() / 1000000 == 2) {
                addItemInfo(mplew, item, false, true);
            }
        }
        mplew.write(size3); // Setup
        for (IItem item : items) {
            if (item.getItemId() / 1000000 == 3) {
                addItemInfo(mplew, item, false, true);
            }
        }
        mplew.write(size4); // Etc
        for (IItem item : items) {
            if (item.getItemId() / 1000000 == 4) {
                addItemInfo(mplew, item, false, true);
            }
        }
        mplew.write(size5); // Pets
        for (IItem item : items) {
            if (item.getItemId() / 1000000 == 5) {
                addItemInfo(mplew, item, false, true);
            }
        }
    }

    /**
     * @param mode: 8 = Please check if your inventory is full or not 11 = You
     * have not enough mesos. 12 = The storage is full. 13 = Due to an error,
     * the trade did not happen. Other = Error 38
     * @return The storage error packet
     */
    public static MaplePacket getStorageError(int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.STORAGE_RESULT.getValue());
        mplew.write(mode); // Mode
        return mplew.getPacket();
    }

    public static MaplePacket mesoStorage(byte slots, int meso) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.STORAGE_RESULT.getValue());
        mplew.write(14); // Mode
        mplew.write(slots);
        mplew.writeShort(2); // If (v10 & 2), decode4
        mplew.writeInt(meso);
        return mplew.getPacket();
    }

    public static MaplePacket getStorageUpdate(byte slots, MapleInventoryType type, Collection<IItem> items,
            boolean withdraw) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.STORAGE_RESULT.getValue());
        mplew.write(withdraw ? 7 : 9); // Withdraw = 0x08, Deposit = 0x0A
        mplew.write(slots); // Storage Slots
        mplew.writeShort(type.getBitfieldEncoding()); // Item type;
        mplew.write(items.size()); // Size of storage items
        for (IItem item : items) {
            addItemInfo(mplew, item, false, true);
        }
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    /**
     *
     * @param oid
     * @param remhp in %
     * @return
     */
    public static MaplePacket showMonsterHP(int oid, int remhppercentage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_MONSTER_HP.getValue());
        mplew.writeInt(oid);
        mplew.write(remhppercentage);

        return mplew.getPacket();
    }

    public static MaplePacket showBossHP(int oid, int currHP, int maxHP, byte tagColor, byte tagBgColor) {
        // 53 00 05 21 B3 81 00 46 F2 5E 01 C0 F3 5E 01 04 01
        // 00 81 B3 21 = 8500001 = Pap monster ID
        // 01 5E F3 C0 = 23,000,000 = Pap max HP
        // 04, 01 - boss bar color/background color as provided in WZ
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.FIELD_EFFECT.getValue());
        mplew.write(5);
        mplew.writeInt(oid);
        mplew.writeInt(currHP);
        mplew.writeInt(maxHP);
        mplew.write(tagColor);
        mplew.write(tagBgColor);

        return mplew.getPacket();
    }

    private static void addPartyStatus(int forchannel, MapleParty party, LittleEndianWriter lew, boolean leaving) {
        List<MaplePartyCharacter> partymembers = new ArrayList<MaplePartyCharacter>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeAsciiString(StringUtil.getRightPaddedStr(partychar.getName(), '\0', 13));
        }
        /*
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
        }
        */
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                lew.writeInt(partychar.getChannel() - 1);
            } else {
                lew.writeInt(-2);
            }
        }
        lew.writeInt(party.getLeader().getId());
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                lew.writeInt(partychar.getMapid());
            } else {
                lew.writeInt(0);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                lew.writeInt(partychar.getDoorTown());
                lew.writeInt(partychar.getDoorTarget());
                lew.writeInt(partychar.getDoorPosition().x);
                lew.writeInt(partychar.getDoorPosition().y);
            } else {
                lew.writeInt(999999999);
                lew.writeInt(999999999);
                lew.writeInt(-1);
                lew.writeInt(-1);
            }
        }
    }

    public static MaplePacket updateParty(int forChannel, MapleParty party, PartyOperation op,
            MaplePartyCharacter target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.PARTY_RESULT.getValue());
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                mplew.write(11);
                mplew.writeInt(party.getId());
                mplew.writeInt(target.getId());

                if (op == PartyOperation.DISBAND) {
                    mplew.write(0);
                } else {
                    mplew.write(1);
                    if (op == PartyOperation.EXPEL) {
                        mplew.write(1);
                    } else {
                        mplew.write(0);
                    }
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, false);
                    // addLeavePartyTail(mplew);
                }

                break;
            case JOIN:
                mplew.write(14);
                mplew.writeInt(party.getId());
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, mplew, false);
                // addJoinPartyTail(mplew);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                mplew.write(6);
                mplew.writeInt(party.getId());
                addPartyStatus(forChannel, party, mplew, false);
                break;
        }

        return mplew.getPacket();
    }

    public static MaplePacket partyPortal(int townId, int targetId, Point position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.PARTY_RESULT.getValue());
        mplew.writeShort(0x22);
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        mplew.writeShort(position.x);
        mplew.writeShort(position.y);

        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stats, int skill,
            boolean monsterSkill, int delay) {
        return applyMonsterStatus(oid, stats, skill, monsterSkill, delay, null);
    }

    public static MaplePacket applyMonsterStatusTest(int oid, int mask, int delay, MobSkill mobskill, int value) {
        // 9B 00 67 40 6F 00 80 00 00 00 01 00 FD FE 30 00 08 00 64 00 01
        // 1D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 01 00 79 00 01
        // 00 B4 78 00 00 00 00 84 03
        // B4 00 A8 90 03 00 00 00 04 00 01 00 8C 00 03 00 14 00 4C 04 02
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_STAT_SET.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(mask);
        mplew.writeShort(1);
        mplew.writeShort(mobskill.getSkillId());
        mplew.writeShort(mobskill.getSkillLevel());
        mplew.writeShort(0); // as this looks similar to giveBuff this might
        // actually be the buffTime but it's not
        // displayed anywhere

        mplew.writeShort(delay); // delay in ms

        mplew.write(1); // ?

        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatusTest2(int oid, int mask, int skill, int value) {
        // 9B 00 67 40 6F 00 80 00 00 00 01 00 FD FE 30 00 08 00 64 00 01
        // 1D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 01 00 79 00 01
        // 00 B4 78 00 00 00 00 84 03
        // B4 00 A8 90 03 00 00 00 04 00 01 00 8C 00 03 00 14 00 4C 04 02
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_STAT_SET.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(mask);
        mplew.writeShort(value);
        mplew.writeInt(skill);
        mplew.writeShort(0); // as this looks similar to giveBuff this might
        // actually be the buffTime but it's not
        // displayed anywhere

        mplew.writeShort(0); // delay in ms

        mplew.write(1); // ?

        return mplew.getPacket();
    }

    public static MaplePacket applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stats, int skill,
            boolean monsterSkill, int delay, MobSkill mobskill) {
        // 9B 00 67 40 6F 00 80 00 00 00 01 00 FD FE 30 00 08 00 64 00 01
        // 1D 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 10 00 01 00 79 00 01
        // 00 B4 78 00 00 00 00 84 03
        // B4 00 A8 90 03 00 00 00 04 00 01 00 8C 00 03 00 14 00 4C 04 02
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_STAT_SET.getValue());
        mplew.writeInt(oid);
        int mask = 0;
        for (MonsterStatus stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        mplew.writeInt(mask);
        for (Integer val : stats.values()) {
            mplew.writeShort(val);
            if (monsterSkill) {
                mplew.writeShort(mobskill.getSkillId());
                mplew.writeShort(mobskill.getSkillLevel());
            } else {
                mplew.writeInt(skill);
            }
            mplew.writeShort(0); // as this looks similar to giveBuff this
            // might actually be the buffTime but it's not displayed anywhere

        }
        mplew.writeShort(delay); // delay in ms

        mplew.write(1); // ?

        return mplew.getPacket();
    }

    public static MaplePacket cancelMonsterStatus(int oid, Map<MonsterStatus, Integer> stats) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_STAT_RESET.getValue());
        mplew.writeInt(oid);
        int mask = 0;
        for (MonsterStatus stat : stats.keySet()) {
            mask |= stat.getValue();
        }
        mplew.writeInt(mask);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket getClock(int time) { // time in seconds
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.CLOCK.getValue());
        mplew.write(2); // clock type. if you send 3 here you have to send
        // another byte (which does not matter at all) before
        // the timestamp
        mplew.writeInt(time);
        return mplew.getPacket();
    }

    public static MaplePacket getClockTime(int hour, int min, int sec) { // Current
        // Time
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.CLOCK.getValue());
        mplew.write(1); // Clock-Type
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);
        return mplew.getPacket();
    }

    public static MaplePacket spawnMist(int oid, int ownerCid, int skillId, Rectangle mistPosition, int level) {
        /*
		 * D1 00 0E 00 00 00 // OID? 01 00 00 00 // Mist ID 6A 4D 27 00 // Char
		 * ID? 1B 36 20 00 // Skill ID 1E 08 00 3D FD FF FF 71 FC FF FF CD FE FF
		 * FF 9D FD FF FF 00 00 00 00
         */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.AFFECTED_AREA_CREATED.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(oid); // maybe this should actually be the "mistid" -
        // seems to always be 1 with only one mist in the map...
        mplew.writeInt(ownerCid); // probably only intresting for smokescreen
        mplew.writeInt(skillId);
        mplew.write(level); // who knows
        mplew.writeShort(8); // ???
        mplew.writeInt(mistPosition.x); // left position
        mplew.writeInt(mistPosition.y); // bottom position
        mplew.writeInt(mistPosition.x + mistPosition.width); // left position
        mplew.writeInt(mistPosition.y + mistPosition.height); // upper position
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket removeMist(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.AFFECTED_AREA_REMOVED.getValue());
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static MaplePacket damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
        // 77 00 29 1D 02 00 FA FE 30 00 00 10 00 00 00 BF 70 8F 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.DAMAGE_SUMMON.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(unkByte);
        mplew.writeInt(damage);
        mplew.writeInt(monsterIdFrom);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket damageMonster(int oid, int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MOB_DAMAGED.getValue());
        mplew.writeInt(oid);
        mplew.write(0);
        mplew.writeInt(damage);

        return mplew.getPacket();
    }

    public static MaplePacket healMonster(int oid, int heal) {
        return damageMonster(oid, heal * -1);
    }

    public static MaplePacket itemEffect(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_ITEM_EFFECT.getValue());

        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static MaplePacket showChair(int characterId, int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_CHAIR.getValue());
        mplew.writeInt(characterId);
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static MaplePacket showChair(short itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_CHAIR.getValue());
        mplew.write(itemid == -1 ? 0 : 1);
        if (itemid != -1) {
            mplew.writeShort(itemid);
        }
        return mplew.getPacket();
    }

    public static MaplePacket cancelChair() {
        return cancelChair(-1);
    }

    public static MaplePacket cancelChair(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SHOW_CHAIR.getValue());

        if (id == -1) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeShort(id);
        }

        return mplew.getPacket();
    }

    // is there a way to spawn reactors non-animated?
    public static MaplePacket spawnReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        Point pos = reactor.getPosition();

        mplew.write(SendPacketOpcode.REACTOR_ENTER_FIELD.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getId());
        mplew.write(reactor.getState());
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket triggerReactor(MapleReactor reactor, int stance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        Point pos = reactor.getPosition();

        mplew.write(SendPacketOpcode.REACTOR_CHANGE_STATE.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);
        mplew.writeShort(stance);
        mplew.write(0);
        mplew.write(5); // frame delay, set to 5 since there doesn't appear to
        // be a fixed formula for it

        return mplew.getPacket();
    }

    public static MaplePacket destroyReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        Point pos = reactor.getPosition();

        mplew.write(SendPacketOpcode.REACTOR_LEAVE_FIELD.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writeShort(pos.x);
        mplew.writeShort(pos.y);

        return mplew.getPacket();
    }

    public static MaplePacket musicChange(String song) {
        return environmentChange(song, 6);
    }

    public static MaplePacket showEffect(String effect) {
        return environmentChange(effect, 3);
    }

    public static MaplePacket playSound(String sound) {
        return environmentChange(sound, 4);
    }

    public static MaplePacket environmentChange(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.FIELD_EFFECT.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);
        return mplew.getPacket();
    }

    private static void getGuildInfo(MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        Collection<MapleGuildCharacter> members = guild.getMembers();
        mplew.write(members.size());
        // then it is the size of all the members
        for (MapleGuildCharacter mgc : members) { // and each of their character
            // ids o_O
            mplew.writeInt(mgc.getId());
        }
        for (MapleGuildCharacter mgc : members) {
            mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getGuildRank());
            mplew.writeInt(mgc.isOnline() ? 1 : 0);
            mplew.writeInt(guild.getSignature());
        }
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        // whatever functions calling this better make sure
        // that the character actually HAS a guild
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x1A); // signature for showing guild info

        if (c == null) { // show empty guild (used for leaving, expelled)

            mplew.write(0);
            return mplew.getPacket();
        }
        MapleGuildCharacter initiator = c.getMGC();
        MapleGuild g = c.getClient().getChannelServer().getGuild(initiator);
        if (g == null) { // failed to read from DB - don't show a guild

            mplew.write(0);
            log.warn(MapleClient.getLogMessage(c, "Couldn't load a guild"));
            return mplew.getPacket();
        } else {
            // MapleGuild holds the absolute correct value of guild rank
            // after it is initiated
            MapleGuildCharacter mgc = g.getMGC(c.getId());
            c.setGuildRank(mgc.getGuildRank());
        }
        mplew.write(1); // bInGuild
        getGuildInfo(mplew, g);
        // System.out.println("DEBUG: showGuildInfo packet:\n" +
        // mplew.toString());
        return mplew.getPacket();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x3d);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);

        return mplew.getPacket();
    }

    public static MaplePacket guildInvite(int gid, String charName) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x05);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);

        return mplew.getPacket();
    }

    /**
     * 'Char' has denied your guild invitation.
     *
     * @param charname
     * @return
     */
    public static MaplePacket denyGuildInvitation(String charname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x37);
        mplew.writeMapleAsciiString(charname);

        return mplew.getPacket();
    }

    public static MaplePacket genericGuildMessage(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(code);

        return mplew.getPacket();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x27);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\0', 13));
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getGuildRank()); // should be always 5 but whatevs
        mplew.writeInt(mgc.isOnline() ? 1 : 0); // should always be 1 too
        mplew.writeInt(1); // ? could be guild signature, but doesn't seem to
        // matter
        mplew.writeInt(3);

        return mplew.getPacket();
    }

    // someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(bExpelled ? 0x2f : 0x2c);

        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());

        return mplew.getPacket();
    }

    // rank change
    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x40);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());

        return mplew.getPacket();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x44);

        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);

        return mplew.getPacket();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x3C);

        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());

        return mplew.getPacket();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x3e);
        mplew.writeInt(gid);

        for (int i = 0; i < 5; i++) {
            mplew.writeMapleAsciiString(ranks[i]);
        }
        return mplew.getPacket();
    }

    public static MaplePacket guildDisband(int gid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x32);
        mplew.writeInt(gid);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x42);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);

        return mplew.getPacket();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x3a);
        mplew.writeInt(gid);
        mplew.write(capacity);

        return mplew.getPacket();
    }

    public static void addThread(MaplePacketLittleEndianWriter mplew, ResultSet rs) throws SQLException {
        mplew.writeInt(rs.getInt("localthreadid"));
        mplew.writeInt(rs.getInt("postercid"));
        mplew.writeMapleAsciiString(rs.getString("name"));
        mplew.writeLong(MaplePacketCreator.getKoreanTimestamp(rs.getLong("timestamp")));
        mplew.writeInt(rs.getInt("icon"));
        mplew.writeInt(rs.getInt("replycount"));
    }

    public static MaplePacket BBSThreadList(ResultSet rs, int start) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(0x06);
        if (!rs.last()) {
            // no result at all
            mplew.write(0);
            mplew.writeInt(0);
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        int threadCount = rs.getRow();
        if (rs.getInt("localthreadid") == 0) { // has a notice

            mplew.write(1);
            addThread(mplew, rs);
            threadCount--; // one thread didn't count (because it's a notice)

        } else {
            mplew.write(0);
        }
        if (!rs.absolute(start + 1)) { // seek to the thread before where we
            // start

            rs.first(); // uh, we're trying to start at a place past possible

            start = 0;
            // ;
        }
        mplew.writeInt(threadCount);
        mplew.writeInt(Math.min(10, threadCount - start));
        for (int i = 0; i < Math.min(10, threadCount - start); i++) {
            addThread(mplew, rs);
            rs.next();
        }

        return mplew.getPacket();
    }

    public static MaplePacket showThread(int localthreadid, ResultSet threadRS, ResultSet repliesRS)
            throws SQLException, RuntimeException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.BBS_OPERATION.getValue());
        mplew.write(0x07);
        mplew.writeInt(localthreadid);
        mplew.writeInt(threadRS.getInt("postercid"));
        mplew.writeLong(getKoreanTimestamp(threadRS.getLong("timestamp")));
        mplew.writeMapleAsciiString(threadRS.getString("name"));
        mplew.writeMapleAsciiString(threadRS.getString("startpost"));
        mplew.writeInt(threadRS.getInt("icon"));
        if (repliesRS != null) {
            int replyCount = threadRS.getInt("replycount");
            mplew.writeInt(replyCount);
            int i;
            for (i = 0; i < replyCount && repliesRS.next(); i++) {
                mplew.writeInt(repliesRS.getInt("replyid"));
                mplew.writeInt(repliesRS.getInt("postercid"));
                mplew.writeLong(getKoreanTimestamp(repliesRS.getLong("timestamp")));
                mplew.writeMapleAsciiString(repliesRS.getString("content"));
            }
            if (i != replyCount || repliesRS.next()) {
                // in the unlikely event that we lost count of replyid
                throw new RuntimeException(String.valueOf(threadRS.getInt("threadid")));
                // we need to fix the database and stop the packet sending
                // or else it'll probably error 38 whoever tries to read it

                // there is ONE case not checked, and that's when the thread
                // has a replycount of 0 and there is one or more replies to the
                // thread in bbs_replies
            }
        } else {
            mplew.writeInt(0); // 0 replies

        }
        return mplew.getPacket();
    }

    public static MaplePacket showGuildRanks(int npcid, ResultSet rs) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x49);
        mplew.writeInt(npcid);
        if (!rs.last()) { // no guilds o.o

            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow()); // number of entries

        rs.beforeFirst();
        while (rs.next()) {
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeInt(rs.getInt("GP"));
            mplew.writeInt(rs.getInt("logo"));
            mplew.writeInt(rs.getInt("logoColor"));
            mplew.writeInt(rs.getInt("logoBG"));
            mplew.writeInt(rs.getInt("logoBGColor"));
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.GUILD_RESULT.getValue());
        mplew.write(0x48);
        mplew.writeInt(gid);
        mplew.writeInt(GP);
        return mplew.getPacket();
    }

    public static MaplePacket skillEffect(MapleCharacter from, int skillId, int level, byte flags, int speed) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SKILL_PREPARE.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.write(flags);
        mplew.write(speed);
        return mplew.getPacket();
    }

    public static MaplePacket skillCancel(MapleCharacter from, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.SKILL_CANCEL.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        return mplew.getPacket();
    }

    /*
	 * /** ge a player hint.
	 *
	 * @param hint The hint it's going to send.
	 * 
	 * @param width How tall the box is going to be.
	 * 
	 * @param height How long the box is going to be.
	 * 
	 * @return The player hint packet.
	 * 
	 * public static MaplePacket sendHint(String hint, int width, int height) {
	 * if (width < 1) { width = hint.length() * 10; if (width < 40) { width =
	 * 40; } } if (height < 5) { height = 5; } MaplePacketLittleEndianWriter
	 * mplew = new MaplePacketLittleEndianWriter();
	 * mplew.write(SendPacketOpcode.PLAYER_HINT.getValue());
	 * mplew.writeMapleAsciiString(hint); mplew.writeShort(width);
	 * mplew.writeShort(height); mplew.write(1);
	 * 
	 * return mplew.getPacket(); }
     */

    public static MaplePacket addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0);
        mplew.write(position);
        CharacterData.addAvatarLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(7);
        mplew.write(position);
        CharacterData.addAvatarLook(mplew, chr, true);
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0);
        return mplew.getPacket();
    }

    /**
     * Gets a packet detailing why the field transfer request was ignored.
     *
     * @param reason 1: The portal is closed for now. 2: You cannot go to that
     * place 3: The cash shop is currently not available. Stay tuned...
     * @return
     */
    public static MaplePacket transferFieldReqIgnored(byte reason) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.TRANSFER_FIELD_REQ_IGNORED.getValue());
        mplew.write(reason);
        return mplew.getPacket();
    }

    public static MaplePacket cashShopDisabled() {
        return transferFieldReqIgnored((byte) 3);
    }

    /**
     * Gets a packet telling the user "Incorrect channel number."
     *
     * @return
     */
    public static MaplePacket transferChannelReqIgnored() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.TRANSFER_CHANNEL_REQ_IGNORED.getValue());
        return mplew.getPacket();
    }

    public static MaplePacket getFindReplyWithCSorMTS(String target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.WHISPER.getValue());
        mplew.write(9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static MaplePacket updatePet(MaplePet pet, boolean alive) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(0);
        mplew.write(2);
        mplew.write(3);
        mplew.write(5);
        mplew.write(pet.getPosition());
        mplew.writeShort(0);
        mplew.write(5);
        mplew.write(pet.getPosition());
        mplew.write(0);
        mplew.write(3);
        mplew.writeInt(pet.getItemId());
        mplew.write(1);
        mplew.writeInt(pet.getUniqueId());
        mplew.writeInt(0);
        mplew.write(HexTool.getByteArrayFromHexString("00 40 6f e5 0f e7 17 02"));
        String petname = pet.getName();
        if (petname.length() > 13) {
            petname = petname.substring(0, 13);
        }
        mplew.writeAsciiString(petname);
        for (int i = petname.length(); i < 13; i++) {
            mplew.write(0);
        }
        mplew.write(pet.getLevel());
        mplew.writeShort(pet.getCloseness());
        mplew.write(pet.getFullness());
        if (alive) {
            mplew.writeLong(getKoreanTimestamp((long) (System.currentTimeMillis() * 1.5)));
            mplew.writeInt(0);
        } else {
            mplew.write(0);
            mplew.write(ITEM_MAGIC);
            mplew.write(HexTool.getByteArrayFromHexString("bb 46 e6 17 02 00 00 00 00"));
        }

        return mplew.getPacket();
    }

    public static MaplePacket showPet(MapleCharacter chr, MaplePet pet, boolean remove) {
        return showPet(chr, pet, remove, false);
    }

    public static MaplePacket showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SPAWN_PET.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getPetIndex(pet));
        if (remove) {
            mplew.write(0);
            mplew.write(hunger ? 1 : 0);
        } else {
            mplew.write(1);
            mplew.write(0);
            mplew.writeInt(pet.getItemId());
            mplew.writeMapleAsciiString(pet.getName());
            mplew.writeInt(pet.getUniqueId());
            mplew.writeInt(0);
            mplew.writeShort(pet.getPos().x);
            mplew.writeShort(pet.getPos().y);
            mplew.write(pet.getStance());
            mplew.writeInt(pet.getFh());
        }

        return mplew.getPacket();
    }

    public static MaplePacket movePet(int cid, int pid, int slot, List<LifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.PET_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeInt(pid);
        serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static MaplePacket petChat(int cid, int un, String text, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.PET_ACTION.getValue());
        mplew.writeInt(cid);
        mplew.write(slot);
        mplew.writeShort(un);
        mplew.writeMapleAsciiString(text);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket commandResponse(int cid, byte command, int slot, boolean success, boolean food) {
        // 84 00 09 03 2C 00 00 00 19 00 00
        // 84 00 E6 DC 17 00 00 01 00 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.PET_COMMAND.getValue());
        mplew.writeInt(cid);
        mplew.write(slot);
        if (!food) {
            mplew.write(0);
        }
        mplew.write(command);
        if (success) {
            mplew.write(1);
        } else {
            mplew.write(0);
        }
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket showOwnPetLevelUp(int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(4);
        mplew.write(0);
        mplew.write(index); // Pet Index

        return mplew.getPacket();
    }

    public static MaplePacket showPetLevelUp(MapleCharacter chr, int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(4);
        mplew.write(0);
        mplew.write(index);

        return mplew.getPacket();
    }

    public static MaplePacket changePetName(MapleCharacter chr, String newname, int slot) {
        // 82 00 E6 DC 17 00 00 04 00 4A 65 66 66 00
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.PET_NAME_CHANGED.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(0);
        mplew.writeMapleAsciiString(newname);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket petStatUpdate(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.STAT_CHANGED.getValue());
        int mask = 0;
        mask |= MapleStat.PET.getValue();
        mplew.write(0);
        mplew.writeInt(mask);
        MaplePet[] pets = chr.getPets();
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null) {
                mplew.writeInt(pets[i].getUniqueId());
                mplew.writeInt(0);
            } else {
                mplew.writeLong(0);
            }
        }
        mplew.write(0);

        return mplew.getPacket();
    }

    public static MaplePacket weirdStatUpdate() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.STAT_CHANGED.getValue());
        mplew.write(0);
        mplew.write(8);
        mplew.write(0);
        mplew.write(0x18);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.writeLong(0);
        mplew.write(0);
        mplew.write(1);

        return mplew.getPacket();
    }

    public static MaplePacket showNotes(ResultSet notes, int count) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MEMO_RESULT.getValue());
        mplew.write(1);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(0);
            notes.next();
        }
        return mplew.getPacket();
    }

    public static void sendUnkwnNote(String to, String msg, String from) throws SQLException { // WTF
        // ?
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO notes (`to`, `from`, `message`, `timestamp`) VALUES (?, ?, ?, ?)");
        ps.setString(1, to);
        ps.setString(2, from);
        ps.setString(3, msg);
        ps.setLong(4, System.currentTimeMillis());
        ps.executeUpdate();
        ps.close();
        con.close();
    }

    /*
	 * public static MaplePacket showEventInstructions() {
	 * MaplePacketLittleEndianWriter mplew = new
	 * MaplePacketLittleEndianWriter();
	 * mplew.write(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
	 * mplew.write(0); return mplew.getPacket(); }
	 * 
     */
    public static MaplePacket showBoatEffect(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.BOAT_EFFECT.getValue());
        mplew.writeShort(effect); // 1034: balrog boat comes, 1548: boat comes
        // in ellinia station, 520: boat leaves
        // ellinia station

        return mplew.getPacket();
    }

    /*
	 * public static MaplePacket showZakumShrineTimeLeft(int timeleft) {
	 * MaplePacketLittleEndianWriter mplew = new
	 * MaplePacketLittleEndianWriter();
	 * 
	 * mplew.write(SendPacketOpcode.ZAKUM_SHRINE.getValue()); mplew.write(0);
	 * mplew.writeInt(timeleft);
	 * 
	 * return mplew.getPacket(); }
     */
    public static MaplePacket boatPacket(boolean arrival) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.BOAT_EFFECT.getValue());
        mplew.writeShort(arrival ? 1 : 2);
        return mplew.getPacket();
    }

    public static MaplePacket reportReply(byte type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.CLAIM_RESULT.getValue());
        mplew.write(type);
        return mplew.getPacket();
    }

    public static MaplePacket sendPacket(String packet) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(HexTool.getByteArrayFromHexString(packet));
        return mplew.getPacket();
    }

    /*
	 * public static MaplePacket refreshVIPRockMapList(List<Integer> maps, int
	 * type) { MaplePacketLittleEndianWriter mplew = new
	 * MaplePacketLittleEndianWriter();
	 * mplew.write(SendPacketOpcode.UPDATE_VIPMAPLIST.getValue());
	 * mplew.write(3); mplew.write(type); for (int map : maps) {
	 * mplew.writeInt(map); } for (int i = maps.size(); i <= 10; i++) {
	 * mplew.write(CHAR_INFO_MAGIC); } maps.clear(); return mplew.getPacket(); }
     */
    public static MaplePacket showDashEffecttoOthers(int cid, int x, int y, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
        mplew.writeInt(cid);
        mplew.writeLong(0);
        mplew.write(HexTool.getByteArrayFromHexString("00 00 00 30 00 00 00 00"));
        mplew.writeShort(0);
        mplew.writeInt(x);
        mplew.writeInt(5001005);
        mplew.write(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        mplew.writeShort(duration);
        mplew.writeInt(y);
        mplew.writeInt(5001005);
        mplew.write(HexTool.getByteArrayFromHexString("1A 7C 8D 35"));
        mplew.writeShort(duration);
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static MaplePacket sendGMPolice(int reason, String sReason, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.ANTI_MACRO_RESULT.getValue());
        mplew.writeInt(duration);
        mplew.write(4);
        mplew.write(reason);
        mplew.writeMapleAsciiString(sReason);
        return mplew.getPacket();
    }

    public static MaplePacket updateEquipSlot(IItem item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.INVENTORY_OPERATION.getValue());
        mplew.write(0); // any number,
        mplew.write(HexTool.getByteArrayFromHexString("02 03 01"));
        mplew.writeShort(item.getPosition()); // set item into this slot
        mplew.write(0);
        mplew.write(item.getType()); // 1 show / 0 disapear ? o________o
        mplew.writeShort(item.getPosition()); // update this slot ?
        addItemInfo(mplew, item, true, true);
        mplew.writeMapleAsciiString("XiuzSource");
        return mplew.getPacket();
    }

    public static MaplePacket MobDamageMob(MapleMonster mob, int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MOB_DAMAGED.getValue());
        mplew.writeInt(mob.getObjectId());
        mplew.write(1); // direction ?
        mplew.writeInt(damage);
        int remainingHp = mob.getHp() - damage;
        if (remainingHp < 0) {
            remainingHp = 0;
            mob.getMap().removeMonster(mob);
        }
        mob.setHp(remainingHp);
        mplew.writeInt(remainingHp);
        mplew.writeInt(mob.getMaxHp());
        return mplew.getPacket();
    }

    public static MaplePacket sendInteractionBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BALLOON.getValue());
        mplew.writeInt(c.getId());
        addAnnounceBox(mplew, c.getInteraction());
        return mplew.getPacket();
    }

    public static MaplePacket removeCharBox(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BALLOON.getValue());
        mplew.writeInt(c.getId());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket getInteraction(MapleCharacter chr, boolean firstTime) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue()); // header.

        IPlayerInteractionManager ips = chr.getInteraction();
        int type = ips.getShopType();
        if (type == 1) {
            mplew.write(HexTool.getByteArrayFromHexString("05 05 04"));
        } else if (type == 2) {
            mplew.write(HexTool.getByteArrayFromHexString("05 04 04"));
        } else if (type == 3) {
            mplew.write(HexTool.getByteArrayFromHexString("05 02 02"));
        } else if (type == 4) {
            mplew.write(HexTool.getByteArrayFromHexString("05 01 02"));
        }

        mplew.write(ips.isOwner(chr) ? 0 : 1);
        mplew.write(0);
        CharacterData.addAvatarLook(mplew, ((MaplePlayerShop) ips).getMCOwner(), true);
        mplew.writeMapleAsciiString(ips.getOwnerName());
        
        for (int i = 0; i < 3; i++) {
            if (ips.getVisitors()[i] != null) {
                mplew.write(i + 1);
                CharacterData.addAvatarLook(mplew, ips.getVisitors()[i], true);
                mplew.writeMapleAsciiString(ips.getVisitors()[i].getName());
            }
        }
        mplew.write(0xFF);
        if (type == 1) {
            mplew.writeShort(0);
            mplew.writeMapleAsciiString(ips.getOwnerName());
            if (ips.isOwner(chr)) {
                mplew.writeInt(Integer.MAX_VALUE); // contains timing, suck my
                // dick we dont need this
                mplew.write(firstTime ? 1 : 0);
                mplew.write(HexTool.getByteArrayFromHexString("00 00 00 00 00"));
            }
        } else if (type == 3 || type == 4) {
            MapleMiniGame minigame = (MapleMiniGame) ips;
            mplew.write(0);
            if (type == 4) {
                mplew.writeInt(1);
            } else {
                mplew.writeInt(2);
            }
            mplew.writeInt(minigame.getOmokPoints("wins", true));
            mplew.writeInt(minigame.getOmokPoints("ties", true));
            mplew.writeInt(minigame.getOmokPoints("losses", true));
            mplew.writeInt(2000);
            if (ips.getVisitors()[0] != null) {
                mplew.write(1);
                if (type == 4) {
                    mplew.writeInt(1);
                } else {
                    mplew.writeInt(2);
                }
                mplew.writeInt(minigame.getOmokPoints("wins", false));
                mplew.writeInt(minigame.getOmokPoints("ties", false));
                mplew.writeInt(minigame.getOmokPoints("losses", false));
                mplew.writeInt(2000);
            }
            mplew.write(0xFF);
        }
        mplew.writeMapleAsciiString(ips.getDescription());
        if (type == 3) {
            mplew.write(ips.getItemType());
            mplew.write(0);
        } else {
            mplew.write(0x10);
            if (type == 1) {
                mplew.writeInt(0);
            }
            mplew.write(ips.getItems().size());
            if (ips.getItems().isEmpty()) {
                if (type == 1) {
                    mplew.write(0);
                } else {
                    mplew.writeInt(0);
                }
            } else {
                for (MaplePlayerShopItem item : ips.getItems()) {
                    mplew.writeShort(item.getBundles());
                    mplew.writeShort(item.getItem().getQuantity());
                    mplew.writeInt(item.getPrice());
                    addItemInfo(mplew, item.getItem(), true, true);
                }
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket shopChat(String message, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("06 08"));
        mplew.write(slot);
        mplew.writeMapleAsciiString(message);
        return mplew.getPacket();
    }

    public static MaplePacket shopErrorMessage(int error, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x0A);
        mplew.write(type);
        mplew.write(error);
        return mplew.getPacket();
    }

    public static MaplePacket shopItemUpdate(IPlayerInteractionManager shop) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x17);
        if (shop.getShopType() == 1) {
            mplew.writeInt(0);
        }
        mplew.write(shop.getItems().size());
        shop.getItems().stream().map((item) -> {
            mplew.writeShort(item.getBundles());
            return item;
        }).map((item) -> {
            mplew.writeShort(item.getItem().getQuantity());
            return item;
        }).map((item) -> {
            mplew.writeInt(item.getPrice());
            return item;
        }).forEach((item) -> {
            addItemInfo(mplew, item.getItem(), true, true);
        });
        return mplew.getPacket();
    }

    public static MaplePacket shopVisitorAdd(MapleCharacter chr, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x04);
        mplew.write(slot);
        CharacterData.addAvatarLook(mplew, chr, true);
        mplew.writeMapleAsciiString(chr.getName());
        if (chr.getInteraction().getShopType() == 4 || chr.getInteraction().getShopType() == 3) {
            MapleMiniGame game = (MapleMiniGame) chr.getInteraction();
            mplew.writeInt(1);
            mplew.writeInt(game.getOmokPoints("wins", false));
            mplew.writeInt(game.getOmokPoints("ties", false));
            mplew.writeInt(game.getOmokPoints("losses", false));
            mplew.writeInt(2000);
        }
        ;
        return mplew.getPacket();
    }

    public static MaplePacket shopVisitorLeave(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x0A);
        mplew.write(slot);
        return mplew.getPacket();
    }


    public static MaplePacket getMiniBoxFull() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.writeShort(5);
        mplew.write(2);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameSkipTurn(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x39);
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameReady() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x34);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameUnReady() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x35);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameRequestTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x2C);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameDenyTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x2D);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameWin(MapleMiniGame game, int person) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("38 00"));
        mplew.write(person);
        mplew.writeInt(1); // start of owner; unknown
        mplew.writeInt(game.getOmokPoints("wins", true)); // wins
        mplew.writeInt(game.getOmokPoints("ties", true)); // ties
        mplew.writeInt(game.getOmokPoints("losses", true) + 1); // losses
        mplew.writeInt(2000); // points
        mplew.writeInt(1); // start of visitor; unknown
        mplew.writeInt(game.getOmokPoints("wins", false) + 1); // wins
        mplew.writeInt(game.getOmokPoints("ties", false)); // ties
        mplew.writeInt(game.getOmokPoints("losses", false)); // losses
        mplew.writeInt(2000); // points
        game.setOmokPoints(person + 1);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameTie(MapleMiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("38 01"));
        mplew.writeInt(1); // unknown
        mplew.writeInt(game.getOmokPoints("wins", true)); // wins
        mplew.writeInt(game.getOmokPoints("ties", true) + 1); // ties
        mplew.writeInt(game.getOmokPoints("losses", true)); // losses
        mplew.writeInt(2000); // points
        mplew.writeInt(1); // start of visitor; unknown
        mplew.writeInt(game.getOmokPoints("wins", false)); // wins
        mplew.writeInt(game.getOmokPoints("ties", false) + 1); // ties
        mplew.writeInt(game.getOmokPoints("losses", false)); // losses
        mplew.writeInt(2000); // points
        game.setMatchCardPoints(3);
        return mplew.getPacket();
    }

    public static MaplePacket getMiniGameForfeit(MapleMiniGame game, int person) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(HexTool.getByteArrayFromHexString("38 02"));
        mplew.write(person);
        mplew.writeInt(1); // start of owner; unknown
        mplew.writeInt(game.getOmokPoints("wins", true)); // wins
        mplew.writeInt(game.getOmokPoints("ties", true)); // ties
        mplew.writeInt(game.getOmokPoints("losses", true) + 1); // losses
        mplew.writeInt(2000); // points
        mplew.writeInt(1); // start of visitor; unknown
        mplew.writeInt(game.getOmokPoints("wins", false) + 1); // wins
        mplew.writeInt(game.getOmokPoints("ties", false)); // ties
        mplew.writeInt(game.getOmokPoints("losses", false)); // losses
        mplew.writeInt(2000); // points
        game.setOmokPoints(person + 1);
        return mplew.getPacket();
    }

    public static MaplePacket getMatchCardStart(MapleMiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x37);
        mplew.write(game.getLoser());
        int times;
        if (game.getMatchesToWin() > 10) {
            times = 30;
        } else if (game.getMatchesToWin() > 6) {
            times = 20;
        } else {
            times = 12;
        }
        mplew.write(times);
        for (int i = 1; i <= times; i++) {
            mplew.writeInt(game.getCardId(i));
        }
        return mplew.getPacket();
    }

    public static MaplePacket getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.write(SendPacketOpcode.MINI_ROOM_BASE.getValue());
        mplew.write(0x3E);
        mplew.write(turn);
        if (turn == 1) {
            mplew.write(slot);
        } else if (turn == 0) {
            mplew.write(slot);
            mplew.write(firstslot);
            mplew.write(type);
        }
        return mplew.getPacket();
    }

    /*
	 * public static MaplePacket showEquipEffect() {
	 * MaplePacketLittleEndianWriter mplew = new
	 * MaplePacketLittleEndianWriter();
	 * 
	 * mplew.write(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
	 * 
	 * return mplew.getPacket(); }
	 * 
	 * public static MaplePacket summonSkill(int cid, int summonSkillId, int
	 * newStance) { MaplePacketLittleEndianWriter mplew = new
	 * MaplePacketLittleEndianWriter();
	 * 
	 * mplew.write(SendPacketOpcode.SUMMON_SKILL.getValue());
	 * mplew.writeInt(cid); mplew.writeInt(summonSkillId);
	 * mplew.write(newStance);
	 * 
	 * return mplew.getPacket(); }
	 * 
	 * public static MaplePacket skillCooldown(int sid, int time) {
	 * MaplePacketLittleEndianWriter mplew = new
	 * MaplePacketLittleEndianWriter();
	 * 
	 * mplew.write(SendPacketOpcode.COOLDOWN.getValue()); mplew.writeInt(sid);
	 * mplew.writeShort(time);
	 * 
	 * return mplew.getPacket(); }
     */
}
