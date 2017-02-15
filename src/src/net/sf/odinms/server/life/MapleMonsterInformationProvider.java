package net.sf.odinms.server.life;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleMonsterInformationProvider {
    public static class DropEntry {
        public DropEntry(int itemId, float chance) {
            this.itemId = itemId;
            this.chance = chance;
            isStackable = false;
            money = false;
        }
        
        public DropEntry(int itemId, float chance, int minAmount, int maxAmount)
        {
            this.itemId = itemId;
            this.chance = chance;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            isStackable = true;
            money = false;
        }
        
        public DropEntry(int meso, float chance, boolean money)
        {
            this.money = true;
            this.meso = meso;
            this.chance = chance;
            itemId = -1;
        }

        public int itemId;
        public float chance;
        public int assignedRangeStart;
        public int assignedRangeLength;
        public boolean isStackable;
        public int minAmount;
        public int maxAmount;
        public boolean money;
        public int meso;

        @Override
        public String toString() {
            return itemId + " chance: " + chance;
        }
    }

    public static final int APPROX_FADE_DELAY = 90;
    private static final String REWARD_WZ = "Reward_ori.img";
    
    private MapleData rewardData;
    private MapleDataProvider rewardDataProvider = MapleDataProviderFactory.getDataProvider(new File("wz" + "/Misc"));
    private static MapleMonsterInformationProvider instance = null;
    private Map<Integer,List<DropEntry>> drops = new HashMap<Integer, List<DropEntry>>();
    private static final Logger log = LoggerFactory.getLogger(MapleMonsterInformationProvider.class);

    private MapleMonsterInformationProvider() {
        rewardData = rewardDataProvider.getData(REWARD_WZ);
    }

    public static MapleMonsterInformationProvider getInstance() {
        if (instance == null) instance = new MapleMonsterInformationProvider();
        return instance;
    }

    public synchronized List<DropEntry> retrieveDropChances(int monsterId) {
        if (drops.containsKey(monsterId)) return drops.get(monsterId);
        System.out.println("initializing drop chances for: " + monsterId);
        List<DropEntry> ret = new LinkedList<DropEntry>();
        String mobDrop = "m" + monsterId;
        if (mobDrop.length() < 8)
        {
            mobDrop = "m0" + monsterId;
        }
        MapleData mobDropData = rewardData.getChildByPath(mobDrop);
        for (MapleData itemData : mobDropData)
        {
            try
            {
                MapleData money = itemData.getChildByPath("money");
                MapleData itemId = itemData.getChildByPath("item");
                MapleData chance = itemData.getChildByPath("prob");
                MapleData min = itemData.getChildByPath("min");
                MapleData max = itemData.getChildByPath("max");   
                DropEntry entry;
                if (money != null)
                {
                    entry = new DropEntry(
                            MapleDataTool.getInt(money),
                            Float.parseFloat(MapleDataTool.getString(chance).substring(4)),
                            true
                    );
                }
                else if (min != null && max != null)
                {
                    entry = new DropEntry(
                            MapleDataTool.getInt(itemId),
                            Float.parseFloat(MapleDataTool.getString(chance).substring(4)),
                            MapleDataTool.getInt(min),
                            MapleDataTool.getInt(max)
                    );
                }
                else
                {
                    entry = new DropEntry(
                            MapleDataTool.getInt(itemId),
                            Float.parseFloat(MapleDataTool.getString(chance).substring(4))
                    );
                }
                ret.add(entry);                
            }
            catch (Exception e)
            {
                System.out.println("Could not load drop data for mob: " + monsterId);
            }
        }
//        try {
//            Connection con = DatabaseConnection.getConnection();
//            PreparedStatement ps = con.prepareStatement("SELECT itemid, chance, monsterid FROM monsterdrops WHERE (monsterid = ? AND chance >= 0) OR (monsterid <= 0)");
//            ps.setInt(1, monsterId);
//            ResultSet rs = ps.executeQuery();
//            MapleMonster theMonster = null;
//            while (rs.next()) {
//                int rowMonsterId = rs.getInt("monsterid");
//                int chance = rs.getInt("chance");
//                if (rowMonsterId != monsterId && rowMonsterId != 0) {
//                    if (theMonster == null) {
//                        theMonster = MapleLifeFactory.getMonster(monsterId);
//                    }
//                    chance += theMonster.getLevel() * rowMonsterId;
//                }
//                ret.add(new DropEntry(rs.getInt("itemid"), chance));
//            }
//            rs.close();
//            ps.close();
//            con.close();
//        } catch (Exception e) {
//            log.error("Error retrieving drop", e);
//        }
        drops.put(monsterId, ret);
        return ret;
    }

    public synchronized void clearDrops() {
        drops.clear();
    }
}