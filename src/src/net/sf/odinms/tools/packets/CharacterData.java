package net.sf.odinms.tools.packets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import net.sf.odinms.client.IEquip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.MapleRing;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.tools.KoreanDateUtil;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Novak
 */
public class CharacterData {

	/**
	 * Gets character info for a character.
	 *
	 * @param chr
	 *            The character to get info about.
	 * @return The character info packet.
	 */
	public static MaplePacket CharacterSetField(MapleCharacter chr) {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		mplew.write(SendPacketOpcode.SET_FIELD.getValue());
		// Start CStage::OnSetField
		mplew.writeInt(chr.getClient().getChannel() - 1);
		mplew.write(chr.getMap().getPortals().size()); // Portals
		mplew.write(1); // Is Connecting. connecting ? 1 : 0

		chr.CRand().connectData(mplew);
		mplew.writeInt(0); // Hmm?

		// Start CharacterData::Decode
		mplew.writeShort(-1); // v6, if (v6 & 1) -> GW_CharacterStat::Decode
		addCharacterStats(mplew, chr);
		addInventoryInfo(mplew, chr);

		// Start Skill Entries
		Map<ISkill, MapleCharacter.SkillEntry> skills = chr.getSkills();
		mplew.writeShort(skills.size());
		for (Map.Entry<ISkill, MapleCharacter.SkillEntry> skill : skills.entrySet()) {
			mplew.writeInt(skill.getKey().getId());
			mplew.writeInt((byte) skill.getValue().skillevel);
		}

		//Started Quests
        List<MapleQuestStatus> started = chr.getStartedQuests();
        mplew.writeShort(started.size());
        for (MapleQuestStatus q : started) {
        	mplew.writeInt(q.getQuest().getId()); //Quest ID
			mplew.writeMapleAsciiString(q.getData());
        }
		
        //Completed Quests
        List<MapleQuestStatus> completed = chr.getCompletedQuests();
        mplew.writeShort(completed.size());
        for (MapleQuestStatus q : completed) {
            mplew.writeShort(q.getQuest().getId());
            // maybe start time? no effect.
            mplew.writeInt((int) KoreanDateUtil.getQuestTimestamp(q.getCompletionTime()));
            // completion time - don't ask about the time format
            mplew.writeInt((int) KoreanDateUtil.getQuestTimestamp(q.getCompletionTime()));
        }

		mplew.writeShort(0); // Probably Quests
		mplew.writeShort(0); // Probably Minigame Record
		mplew.writeShort(0); // Probably Effect Rings

		for (int map : chr.getVIPRockMaps(0)) { // Teleport Rock Maps
			mplew.writeInt(map);
		}
		for (int i = 0; i < 40; i++) {
			mplew.writeLong(0);
		}
		return mplew.getPacket();
	}

	public static void addInventoryInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
		mplew.write(chr.getBuddylist().getCapacity()); // Buddylist Capacity
		mplew.writeInt(chr.getMeso()); // GW_MoneyInfo::Decode

		MapleInventory inventory = chr.getInventory(MapleInventoryType.EQUIPPED);
		Collection<IItem> equippedList = inventory.list();
		Item[] equipped = new Item[19];
		Item[] equippedCash = new Item[19];
		for (IItem item : equippedList) {
			byte pos = item.getPosition();
			if (pos < 0) {
				pos = (byte) Math.abs(pos);
				if (pos > 100) {
					equippedCash[(byte) (pos - 100)] = (Item) item;
				} else {
					equipped[(byte) pos] = (Item) item;
				}
			}
			if (pos < 0) {
				if (pos < -100) {
					pos += 100;
					pos = (byte) Math.abs(pos);
					equippedCash[(byte) (pos - 100)] = (Item) item;
				} else {
					pos = (byte) Math.abs(pos);
					equipped[(byte) pos] = (Item) item;
				}
			}
		}

		for (Item item : equipped) { // Start of regular equipment
			if (item != null) {
				MaplePacketCreator.addItemInfo(mplew, item, false);
			}
		}
		mplew.write(0); // End of regular equipment

		for (Item item : equippedCash) { // Start of cash equipment
			if (item != null) {
				MaplePacketCreator.addItemInfo(mplew, item, false);
			}
		}
		mplew.write(0); // End of cash equipment

		for (byte i = 1; i < 6; i++) { // Start Inventory slots
			inventory = chr.getInventory(MapleInventoryType.getByType((byte) i));
			mplew.write(inventory.getSlotLimit());
			for (IItem item : inventory.list()) {
				if (item != null && item.getPosition() > 0) {
					MaplePacketCreator.addItemInfo(mplew, item, false);
				}
			}
			mplew.write(0);
		}
	}

	public static void addCharacterLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {

		MapleInventory equip = chr.getInventory(MapleInventoryType.EQUIPPED);
		Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
		Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
		for (IItem item : equip.list()) {
			byte pos = (byte) (item.getPosition() * -1);
			if (pos < 100 && myEquip.get(pos) == null) {
				myEquip.put(pos, item.getItemId());
			} else if (pos > 100 && pos != 111) { // don't ask. o.o
				pos -= 100;
				if (myEquip.get(pos) != null) {
					maskedEquip.put(pos, myEquip.get(pos));
				}
				myEquip.put(pos, item.getItemId());
			} else if (myEquip.get(pos) != null) {
				maskedEquip.put(pos, item.getItemId());
			}
		}
		for (Map.Entry<Byte, Integer> entry : myEquip.entrySet()) {
			mplew.write(entry.getKey());
			mplew.writeInt(entry.getValue());
		}
		mplew.write(0);
		for (Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
			mplew.write(entry.getKey());
			mplew.writeInt(entry.getValue());
		}
		mplew.write(0);
		if ((chr.getJob().getId() / 100) != 5) {
			mplew.write(1);
			mplew.writeInt(chr.getRank());
			mplew.writeInt(chr.getRankMove()); // might be reverted
			mplew.writeInt(chr.getJobRank());
			mplew.writeInt(chr.getJobRankMove());
		} else {
			mplew.write(0);
		}
	}

	public static void addAvatarLook(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, boolean announceGender) {
		if (announceGender) {
			mplew.write(chr.getGender());
		}
		mplew.write(chr.getSkinColor().getId()); // skin color
		mplew.writeInt(chr.getFace()); // face
		mplew.write(0); // OdinMS: mega ? 1 : 0
		mplew.writeInt(chr.getHair()); // hair

		MapleInventory iv = chr.getInventory(MapleInventoryType.EQUIPPED);
		Collection<IItem> equippedList = iv.list();
		Item[] equipped = new Item[19];
		Item[] equippedCash = new Item[19];
		for (IItem item : equippedList) {
			byte pos = item.getPosition();
			if (pos < 0) {
				pos = (byte) Math.abs(pos);
				if (pos > 100) {
					equippedCash[(byte) (pos - 100)] = (Item) item;
				} else {
					equipped[(byte) pos] = (Item) item;
				}
			}
			if (pos < 0) {
				if (pos < -100) {
					pos += 100;
					pos = (byte) Math.abs(pos);
					equippedCash[(byte) (pos - 100)] = (Item) item;
				} else {
					pos = (byte) Math.abs(pos);
					equipped[(byte) pos] = (Item) item;
				}
			}
		}
		Map<Byte, Integer> items = new LinkedHashMap<>();
		for (Item item : equippedCash) {
			if (item != null) {
				byte slotuse = (byte) Math.abs(item.getPosition());
				if (slotuse > 100)
					slotuse -= 100;
				items.put(slotuse, item.getItemId());
			}
		}
		for (Item item : equipped) {
			if (item != null && !items.containsKey((byte) Math.abs(item.getPosition()))) {
				items.put((byte) Math.abs(item.getPosition()), item.getItemId());
			}
		}
		for (Entry<Byte, Integer> entry : items.entrySet()) {
			mplew.write(entry.getKey());
			mplew.writeInt(entry.getValue());
		}
		mplew.write(0xFF);

		if (chr.getPet(0) != null) {
			mplew.writeInt(chr.getPet(0).getItemId());
		} else {
			mplew.writeInt(0); // Pet
		}
	}

	public static MaplePacket updateCharacterLook(MapleCharacter chr) {
		MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
		mplew.write(SendPacketOpcode.UPDATE_CHAR_LOOK.getValue());
		mplew.writeInt(chr.getId());
		mplew.writeInt(0);
		mplew.write(1);

		addAvatarLook(mplew, chr, false);

		mplew.writeInt(0); // ??

		// What's this?
		mplew.write(0); // if > 0
		// mplew.writeInt(0); //??
		// mplew.writeInt(0); //??

		// What's this?
		mplew.write(0); // if > 0
		// mplew.write(0); //??

		List<MapleRing> rings = MaplePacketCreator.getRing(chr);
		mplew.write(rings.size()); // Rings: 16 bytes (long, long)
		for (MapleRing ring : rings) {
			mplew.writeInt(ring.getRingId());
			mplew.writeInt(0);
			mplew.writeInt(ring.getPartnerRingId());
			mplew.writeInt(0);
			mplew.writeInt(ring.getItemId());
		}

		return mplew.getPacket();
	}

	public static void addCharacterStats(MaplePacketLittleEndianWriter mplew, MapleCharacter chr) {
		// Start GW_CharacterStat::Decode
		mplew.writeInt(chr.getId()); // character id
		mplew.writeAsciiString(chr.getName());
		for (int x = chr.getName().length(); x < 13; x++) { // fill to maximum
			// name length
			mplew.write(0);
		}
		mplew.write(chr.getGender()); // gender (0 = male, 1 = female)
		mplew.write(chr.getSkinColor().getId()); // skin color
		mplew.writeInt(chr.getFace()); // face
		mplew.writeInt(chr.getHair()); // hair
		mplew.writeLong(0); // Pet Cash ID
		mplew.write(chr.getLevel()); // level
		mplew.writeShort(chr.getJob().getId()); // job
		mplew.writeShort(chr.getStr()); // str
		mplew.writeShort(chr.getDex()); // dex
		mplew.writeShort(chr.getInt()); // int
		mplew.writeShort(chr.getLuk()); // luk
		mplew.writeShort(chr.getHp()); // hp (?)
		mplew.writeShort(chr.getMaxHp()); // maxhp
		mplew.writeShort(chr.getMp()); // mp (?)
		mplew.writeShort(chr.getMaxMp()); // maxmp
		mplew.writeShort(chr.getRemainingAp()); // remaining ap
		mplew.writeShort(chr.getRemainingSp()); // remaining sp
		mplew.writeInt(chr.getExp()); // current exp
		mplew.writeShort(chr.getFame()); // fame
		mplew.writeInt(chr.getMapId()); // current map id
		mplew.write(chr.getInitialSpawnpoint()); // spawnpoint

		mplew.writeLong(0); // ??
		mplew.writeInt(0); // ??
		mplew.writeInt(0); // ??
		// End GW_CharacterStat::Decode
	}
}
