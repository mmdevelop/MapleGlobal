/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

 /*
 * MapleItemInformationProvider.java
 * 
 * Created on 26. November 2007, 21:58
 * 
 * To change this template, choose Tools | Template Manager and open the template in the editor.
 */
package net.sf.odinms.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleWeaponType;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataDirectoryEntry;
import net.sf.odinms.provider.MapleDataFileEntry;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.tools.Pair;

/*
 * @author Novak
 *
 */
public class MapleItemInformationProvider {

    private static MapleItemInformationProvider instance = null;
    protected MapleDataProvider itemData;
    protected MapleDataProvider equipData;
    protected MapleData stringData;
    protected MapleDataProvider stringDataa;
    protected Map<Integer, MapleInventoryType> inventoryTypeCache = new HashMap<Integer, MapleInventoryType>();
    protected Map<Integer, Short> slotMaxCache = new HashMap<Integer, Short>();
    protected Map<Integer, MapleStatEffect> itemEffects = new HashMap<Integer, MapleStatEffect>();
    protected Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<Integer, Map<String, Integer>>();
    protected Map<Integer, Equip> equipCache = new HashMap<Integer, Equip>();
    protected Map<Integer, Double> priceCache = new HashMap<Integer, Double>();
    protected Map<Integer, Integer> wholePriceCache = new HashMap<Integer, Integer>();
    protected Map<Integer, Integer> projectileWatkCache = new HashMap<Integer, Integer>();
    protected Map<Integer, String> nameCache = new HashMap<Integer, String>();
    protected Map<Integer, String> descCache = new HashMap<Integer, String>();
    protected Map<Integer, String> msgCache = new HashMap<Integer, String>();
    protected Map<Integer, Integer> mesoCache = new HashMap<Integer, Integer>();
    protected Map<Integer, Boolean> dropRestrictionCache = new HashMap<Integer, Boolean>();
    protected Map<Integer, Boolean> pickupRestrictionCache = new HashMap<Integer, Boolean>();
    protected Map<Integer, Boolean> isQuestItemCache = new HashMap<Integer, Boolean>();
    protected List<Pair<Integer, String>> itemNameCache = new ArrayList<Pair<Integer, String>>();
    private static Random rand = new Random();

    /**
     * Creates a new instance of MapleItemInformationProvider
     */
    protected MapleItemInformationProvider() {
        itemData = MapleDataProviderFactory.getDataProvider(new File("wz" + "/Item"));
        equipData = MapleDataProviderFactory.getDataProvider(new File("wz" + "/Character"));
        stringData = MapleDataProviderFactory.getDataProvider(new File("wz" + "/String")).getData("Item.img");
        stringDataa = MapleDataProviderFactory.getDataProvider(new File("wz" + "/String"));
    }

    public static MapleItemInformationProvider getInstance() {
        if (instance == null) {
            instance = new MapleItemInformationProvider();
        }
        return instance;
    }

    /* returns the inventory type for the specified item id */
    public MapleInventoryType getInventoryType(int itemId) {
        if (inventoryTypeCache.containsKey(itemId)) {
            return inventoryTypeCache.get(itemId);
        }
        MapleInventoryType ret;
        String idStr = "0" + String.valueOf(itemId);
        // first look in items...
        MapleDataDirectoryEntry root = itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            // we should have .img files here beginning with the first 4 IID
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = MapleInventoryType.getByWZName(topDir.getName());
                    inventoryTypeCache.put(itemId, ret);
                    return ret;
                } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    ret = MapleInventoryType.getByWZName(topDir.getName());
                    inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
            }
        }
        // not found? maybe its equip...
        root = equipData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    ret = MapleInventoryType.EQUIP;
                    inventoryTypeCache.put(itemId, ret);
                    return ret;
                }
            }
        }
        ret = MapleInventoryType.UNDEFINED;
        inventoryTypeCache.put(itemId, ret);
        return ret;
    }

    protected MapleData getStringData(int itemId) {
        String cat;
        if (itemId >= 5010000) {
            cat = "Cash";
        } else if (itemId >= 2000000 && itemId < 3000000) {
            cat = "Con";
        } else if (itemId >= 1010000 && itemId < 1040000 || itemId >= 1122000 && itemId < 1123000) {
            cat = "Eqp/Accessory";
        } else if (itemId >= 1000000 && itemId < 1010000) {
            cat = "Eqp/Cap";
        } else if (itemId >= 1102000 && itemId < 1103000) {
            cat = "Eqp/Cape";
        } else if (itemId >= 1040000 && itemId < 1050000) {
            cat = "Eqp/Coat";
        } else if (itemId >= 20000 && itemId < 22000) {
            cat = "Eqp/Face";
        } else if (itemId >= 1080000 && itemId < 1090000) {
            cat = "Eqp/Glove";
        } else if (itemId >= 30000 && itemId < 32000) {
            cat = "Eqp/Hair";
        } else if (itemId >= 1050000 && itemId < 1060000) {
            cat = "Eqp/Longcoat";
        } else if (itemId >= 1060000 && itemId < 1070000) {
            cat = "Eqp/Pants";
        } else if (itemId >= 1802000 && itemId < 1810000) {
            cat = "Eqp/PetEquip";
        } else if (itemId >= 1112000 && itemId < 1120000) {
            cat = "Eqp/Ring";
        } else if (itemId >= 1092000 && itemId < 1100000) {
            cat = "Eqp/Shield";
        } else if (itemId >= 1070000 && itemId < 1080000) {
            cat = "Eqp/Shoes";
        } else if (itemId >= 1900000 && itemId < 2000000) {
            cat = "Eqp/Taming";
        } else if (itemId >= 1300000 && itemId < 1800000) {
            cat = "Eqp/Weapon";
        } else if (itemId >= 4000000 && itemId < 5000000) {
            cat = "Etc";
        } else if (itemId >= 3000000 && itemId < 4000000) {
            cat = "Ins";
        } else if (itemId >= 5000000 && itemId < 5010000) {
            cat = "Pet";
        } else {
            return null;
        }
        return stringData.getChildByPath(cat + "/" + itemId);
    }

    protected MapleData getItemData(int itemId) {
        MapleData ret = null;
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            // we should have .img files here beginning with the first 4 IID
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    return itemData.getData(topDir.getName() + "/" + iFile.getName());
                }
            }
        }
        root = equipData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    return equipData.getData(topDir.getName() + "/" + iFile.getName());
                }
            }
        }
        return ret;
    }

    public List<Pair<Integer, String>> getAllItems() {
        if (!itemNameCache.isEmpty()) {
            return itemNameCache;
        }
        List<Pair<Integer, String>> itemPairs = new ArrayList<Pair<Integer, String>>();
        MapleData itemsData;

        itemsData = stringDataa.getData("Item.img").getChildByPath("Eqp");
        for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
                int itemId = Integer.parseInt(itemFolder.getName());
                if ((itemId >= 20000 && itemId <= 21007) || (itemId >= 30000 && itemId <= 31277)) {
                    continue;
                }
                String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
                itemPairs.add(new Pair<Integer, String>(itemId, itemName));
            }
        }

        itemsData = stringDataa.getData("Item.img").getChildByPath("Con");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId, itemName));
        }

        itemsData = stringDataa.getData("Item.img").getChildByPath("Ins");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId, itemName));
        }

        itemsData = stringDataa.getData("Item.img").getChildByPath("Etc");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId, itemName));
        }

        itemsData = stringDataa.getData("Item.img").getChildByPath("Pet");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair<Integer, String>(itemId, itemName));
        }
        return itemPairs;
    }

    /**
     * returns the maximum of items in one slot
     * @param itemId
     * @return 
     */
    public short getSlotMax(int itemId) {
        if (slotMaxCache.containsKey(itemId)) {
            return slotMaxCache.get(itemId);
        }
        short ret = 0;
        MapleData item = getItemData(itemId);
        if (item != null) {
            MapleData smEntry = item.getChildByPath("info/slotMax");
            if (smEntry == null) {
                if (getInventoryType(itemId).getType() == MapleInventoryType.EQUIP.getType()) {
                    ret = 1;
                } else {
                    ret = 100;
                }
            } else {
                if (isThrowingStar(itemId)) {
                    ret = 1;
                } else if (MapleDataTool.getInt(smEntry) == 0) {
                    ret = 1;
                } else {
                    ret = (short) MapleDataTool.getInt(smEntry);
                }
            }
        }
        slotMaxCache.put(itemId, ret);
        return ret;
    }

    public int getMeso(int itemId) {
        if (mesoCache.containsKey(itemId)) {
            return mesoCache.get(itemId);
        }
        MapleData item = getItemData(itemId);
        if (item == null) {
            return -1;
        }
        int pEntry;
        MapleData pData = item.getChildByPath("info/meso");
        if (pData == null) {
            return -1;
        }
        pEntry = MapleDataTool.getInt(pData);

        mesoCache.put(itemId, pEntry);
        return pEntry;
    }

    public int getWholePrice(int itemId) {
        if (wholePriceCache.containsKey(itemId)) {
            return wholePriceCache.get(itemId);
        }
        MapleData item = getItemData(itemId);
        if (item == null) {
            return -1;
        }

        int pEntry;
        MapleData pData = item.getChildByPath("info/price");
        if (pData == null) {
            return -1;
        }
        pEntry = MapleDataTool.getInt(pData);

        wholePriceCache.put(itemId, pEntry);
        return pEntry;
    }

    public double getPrice(int itemId) {
        if (priceCache.containsKey(itemId)) {
            return priceCache.get(itemId);
        }
        MapleData item = getItemData(itemId);
        if (item == null) {
            return -1;
        }

        //TODO ULTRAHACK - prevent players gaining miriads of mesars with orbis/eos scrolls
        if (itemId == 4001019 || itemId == 4001020) {
            return 0;
        }

        double pEntry;
        MapleData pData = item.getChildByPath("info/unitPrice");
        if (pData != null) {
            try {
                pEntry = MapleDataTool.getDouble(pData);
            } catch (Exception e) {
                pEntry = (double) MapleDataTool.getInt(pData);
            }
        } else {
            pData = item.getChildByPath("info/price");
            if (pData == null) {
                return -1;
            }
            pEntry = (double) MapleDataTool.getInt(pData);
        }

        priceCache.put(itemId, pEntry);
        return pEntry;
    }

    public Map<String, Integer> getEquipStats(int itemId) {
        if (equipStatsCache.containsKey(itemId)) {
            return equipStatsCache.get(itemId);
        }
        Map<String, Integer> ret = new LinkedHashMap<String, Integer>();
        MapleData item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        MapleData info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        for (MapleData data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                ret.put(data.getName().substring(3), MapleDataTool.getIntConvert(data));
            }
        }
        ret.put("tuc", MapleDataTool.getInt("tuc", info, 0));
        ret.put("reqLevel", MapleDataTool.getInt("reqLevel", info, 0));
        ret.put("cursed", MapleDataTool.getInt("cursed", info, 0));
        ret.put("success", MapleDataTool.getInt("success", info, 0));
        equipStatsCache.put(itemId, ret);
        return ret;
    }

    public int getReqLevel(int itemId) {
        final Integer req = getEquipStats(itemId).get("reqLevel");
        return req == null ? 0 : req;
    }

    public int[][] getSummonMobs(int itemId) {
        MapleData data = getItemData(itemId);
        int theInt = data.getChildByPath("mob").getChildren().size();
        int[][] mobs2spawn = new int[theInt][2];
        for (int x = 0; x < theInt; x++) {
            mobs2spawn[x][0] = MapleDataTool.getIntConvert("mob/" + x + "/id", data);
            mobs2spawn[x][1] = MapleDataTool.getIntConvert("mob/" + x + "/prob", data);
        }
        return mobs2spawn;
    }

    public MapleWeaponType getWeaponType(int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) {
            case 30:
                return MapleWeaponType.SWORD1H;
            case 31:
                return MapleWeaponType.AXE1H;
            case 32:
                return MapleWeaponType.BLUNT1H;
            case 33:
                return MapleWeaponType.DAGGER;
            case 37:
                return MapleWeaponType.WAND;
            case 38:
                return MapleWeaponType.STAFF;
            case 40:
                return MapleWeaponType.SWORD2H;
            case 41:
                return MapleWeaponType.AXE2H;
            case 42:
                return MapleWeaponType.BLUNT2H;
            case 43:
                return MapleWeaponType.SPEAR;
            case 44:
                return MapleWeaponType.POLE_ARM;
            case 45:
                return MapleWeaponType.BOW;
            case 47:
                return MapleWeaponType.CLAW;
            case 46:
                return MapleWeaponType.CROSSBOW;

        }
        return MapleWeaponType.NOT_A_WEAPON;
    }

    public boolean isShield(int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        return cat == 9;
    }

    public boolean isEquip(int itemId) {
        return itemId / 1000000 == 1;
    }

    public boolean isTownScroll(int itemId) {
        return (itemId >= 2030000 && itemId < 2030020);
    }

    public IItem scrollEquipWithId(IItem equip, int scrollId, boolean usingWhiteScroll) {
        if (equip instanceof Equip) {
            Equip nEquip = (Equip) equip;
            Map<String, Integer> stats = this.getEquipStats(scrollId);
            if (nEquip.getUpgradeSlots() > 0 && Math.ceil(Math.random() * 100.0) <= stats.get("success")) {
                for (Entry<String, Integer> stat : stats.entrySet()) {
                    switch (stat.getKey()) {
                        case "STR":
                            nEquip.setStr((short) (nEquip.getStr() + stat.getValue()));
                            break;
                        case "DEX":
                            nEquip.setDex((short) (nEquip.getDex() + stat.getValue()));
                            break;
                        case "INT":
                            nEquip.setInt((short) (nEquip.getInt() + stat.getValue()));
                            break;
                        case "LUK":
                            nEquip.setLuk((short) (nEquip.getLuk() + stat.getValue()));
                            break;
                        case "PAD":
                            nEquip.setWatk((short) (nEquip.getWatk() + stat.getValue()));
                            break;
                        case "PDD":
                            nEquip.setWdef((short) (nEquip.getWdef() + stat.getValue()));
                            break;
                        case "MAD":
                            nEquip.setMatk((short) (nEquip.getMatk() + stat.getValue()));
                            break;
                        case "MDD":
                            nEquip.setMdef((short) (nEquip.getMdef() + stat.getValue()));
                            break;
                        case "ACC":
                            nEquip.setAcc((short) (nEquip.getAcc() + stat.getValue()));
                            break;
                        case "EVA":
                            nEquip.setAvoid((short) (nEquip.getAvoid() + stat.getValue()));
                            break;
                        case "Speed":
                            nEquip.setSpeed((short) (nEquip.getSpeed() + stat.getValue()));
                            break;
                        case "Jump":
                            nEquip.setJump((short) (nEquip.getJump() + stat.getValue()));
                            break;
                        case "MHP":
                            nEquip.setHp((short) (nEquip.getHp() + stat.getValue()));
                            break;
                        case "MMP":
                            nEquip.setMp((short) (nEquip.getMp() + stat.getValue()));
                            break;
                        case "afterImage":
                            break;
                        default:
                            break;
                    }
                }
                nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                nEquip.setLevel((byte) (nEquip.getLevel() + 1));
            } else {
                if (!usingWhiteScroll) {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                }
                if (Math.ceil(1.0 + Math.random() * 100.0) < stats.get("cursed")) {
                    // DESTROY :) (O.O!)
                    return null;
                }
            }
        }
        return equip;
    }

    public List<Integer> petsCanConsume(int itemId) {
        List<Integer> ret = new ArrayList<Integer>();
        MapleData data = getItemData(itemId);
        int curPetId;
        int size = data.getChildren().size();
        for (int i = 0; i < size; i++) {
            curPetId = MapleDataTool.getInt("spec/" + Integer.toString(i), data, 0);
            if (curPetId == 0) {
                break;
            }
            ret.add(curPetId);
        }
        return ret;
    }

    public IItem getEquipById(int equipId) {
        return getEquipById(equipId, -1);
    }

    public IItem getEquipById(int equipId, int ringId) {
        Equip nEquip;
        nEquip = new Equip(equipId, (byte) 0, ringId);
        nEquip.setQuantity((short) 1);
        Map<String, Integer> stats = this.getEquipStats(equipId);
        if (stats != null) {
            for (Entry<String, Integer> stat : stats.entrySet()) {
                if (stat.getKey().equals("STR")) {
                    nEquip.setStr((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("DEX")) {
                    nEquip.setDex((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("INT")) {
                    nEquip.setInt((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("LUK")) {
                    nEquip.setLuk((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("PAD")) {
                    nEquip.setWatk((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("PDD")) {
                    nEquip.setWdef((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("MAD")) {
                    nEquip.setMatk((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("MDD")) {
                    nEquip.setMdef((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("ACC")) {
                    nEquip.setAcc((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("EVA")) {
                    nEquip.setAvoid((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("Speed")) {
                    nEquip.setSpeed((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("Jump")) {
                    nEquip.setJump((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("MHP")) {
                    nEquip.setHp((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("MMP")) {
                    nEquip.setMp((short) stat.getValue().intValue());
                } else if (stat.getKey().equals("tuc")) {
                    nEquip.setUpgradeSlots((byte) stat.getValue().intValue());
                } else if (stat.getKey().equals("afterImage")) {
                }
            }
        }
        equipCache.put(equipId, nEquip);
        return nEquip.copy();
    }

    private short getRandStat(short defaultValue, int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }

        // vary no more than ceil of 10% of stat
        int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);
        return (short) ((defaultValue - lMaxRange) + Math.floor(rand.nextDouble() * (lMaxRange * 2 + 1)));
    }

    public Equip randomizeStats(Equip equip) {
        equip.setStr(getRandStat(equip.getStr(), 5));
        equip.setDex(getRandStat(equip.getDex(), 5));
        equip.setInt(getRandStat(equip.getInt(), 5));
        equip.setLuk(getRandStat(equip.getLuk(), 5));
        equip.setMatk(getRandStat(equip.getMatk(), 5));
        equip.setWatk(getRandStat(equip.getWatk(), 5));
        equip.setAcc(getRandStat(equip.getAcc(), 5));
        equip.setAvoid(getRandStat(equip.getAvoid(), 5));
        equip.setJump(getRandStat(equip.getJump(), 5));
        equip.setSpeed(getRandStat(equip.getSpeed(), 5));
        equip.setWdef(getRandStat(equip.getWdef(), 10));
        equip.setMdef(getRandStat(equip.getMdef(), 10));
        equip.setHp(getRandStat(equip.getHp(), 10));
        equip.setMp(getRandStat(equip.getMp(), 10));
        return equip;
    }

    public Equip hardcoreItem(Equip equip, short stat) {
        equip.setStr(stat);
        equip.setDex(stat);
        equip.setInt(stat);
        equip.setLuk(stat);
        equip.setMatk(stat);
        equip.setWatk(stat);
        equip.setAcc(stat);
        equip.setAvoid(stat);
        equip.setJump(stat);
        equip.setSpeed(stat);
        equip.setWdef(stat);
        equip.setMdef(stat);
        equip.setHp(stat);
        equip.setMp(stat);
        return equip;
    }

    public MapleStatEffect getItemEffect(int itemId) {
        MapleStatEffect ret = itemEffects.get(itemId);
        if (ret == null) {
            MapleData item = getItemData(itemId);
            if (item == null) {
                return null;
            }
            MapleData spec = item.getChildByPath("spec");
            ret = MapleStatEffect.loadItemEffectFromData(spec, itemId);
            itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public boolean isThrowingStar(int itemId) {
        return itemId >= 2070000 && itemId < 2080000;
    }

    public boolean isOverall(int itemId) {
        return itemId >= 1050000 && itemId < 1060000;
    }

    public boolean isArrowForCrossBow(int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }

    public boolean isArrowForBow(int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }

    public boolean isRing(int itemId) {
        return itemId >= 1112000 && itemId < 1120000;
    }

    public boolean isEffectRing(int itemid) {
        if (itemid < 1112000 || itemid > 1120000) {
            return false;
        } else if (itemid > 1112006 && itemid < 1112800) {
            return false;
        } else return itemid != 1112808;
    }

    public boolean isTwoHanded(int itemId) {
        switch (getWeaponType(itemId)) {
            case AXE2H:
                return true;
            case BLUNT2H:
                return true;
            case BOW:
                return true;
            case CLAW:
                return true;
            case CROSSBOW:
                return true;
            case POLE_ARM:
                return true;
            case SPEAR:
                return true;
            case SWORD2H:
                return true;
            default:
                return false;
        }
    }

    public int getWatkForProjectile(int itemId) {
        Integer atk = projectileWatkCache.get(itemId);
        if (atk != null) {
            return atk;
        }
        MapleData data = getItemData(itemId);
        atk = MapleDataTool.getInt("info/incPAD", data, 0);
        projectileWatkCache.put(itemId, atk);
        return atk;
    }

    public boolean canScroll(int scrollid, int itemid) {
        int scrollCategoryQualifier = (scrollid / 100) % 100;
        int itemCategoryQualifier = (itemid / 10000) % 100;
        return scrollCategoryQualifier == itemCategoryQualifier;
    }

    public String getName(int itemId) {
        if (nameCache.containsKey(itemId)) {
            return nameCache.get(itemId);
        }
        MapleData strings = getStringData(itemId);
        if (strings == null) {
            return null;
        }
        String ret = MapleDataTool.getString("name", strings, null);
        nameCache.put(itemId, ret);
        return ret;
    }

    public String getDesc(int itemId) {
        if (descCache.containsKey(itemId)) {
            return descCache.get(itemId);
        }
        MapleData strings = getStringData(itemId);
        if (strings == null) {
            return null;
        }
        String ret = MapleDataTool.getString("desc", strings, null);
        descCache.put(itemId, ret);
        return ret;
    }

    public String getMsg(int itemId) {
        if (msgCache.containsKey(itemId)) {
            return msgCache.get(itemId);
        }
        MapleData strings = getStringData(itemId);
        if (strings == null) {
            return null;
        }
        String ret = MapleDataTool.getString("msg", strings, null);
        msgCache.put(itemId, ret);
        return ret;
    }

    public boolean isWeapon(int itemId) {
        return itemId >= 1302000 && itemId < 1492024;
    }

    public boolean isDropRestricted(int itemId) {
        if (dropRestrictionCache.containsKey(itemId)) {
            return dropRestrictionCache.get(itemId);
        }

        MapleData data = getItemData(itemId);

        boolean bRestricted = MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1;
        if (!bRestricted) {
            bRestricted = MapleDataTool.getIntConvert("info/quest", data, 0) == 1;
        }
        dropRestrictionCache.put(itemId, bRestricted);

        return bRestricted;
    }

    public boolean isPickupRestricted(int itemId) {
        if (pickupRestrictionCache.containsKey(itemId)) {
            return pickupRestrictionCache.get(itemId);
        }

        MapleData data = getItemData(itemId);
        boolean bRestricted = MapleDataTool.getIntConvert("info/only", data, 0) == 1;

        pickupRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public boolean isQuestItem(int itemId) {
        if (isQuestItemCache.containsKey(itemId)) {
            return isQuestItemCache.get(itemId);
        }
        MapleData data = getItemData(itemId);
        boolean questItem = MapleDataTool.getIntConvert("info/quest", data, 0) == 1;
        isQuestItemCache.put(itemId, questItem);
        return questItem;
    }
}
