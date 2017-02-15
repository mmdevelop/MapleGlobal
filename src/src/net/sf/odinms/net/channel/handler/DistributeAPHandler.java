package net.sf.odinms.net.channel.handler;

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapleGlobal.org - 2016
 *
 * @Author - Ginseng
 */
public class DistributeAPHandler extends AbstractMaplePacketHandler {

    private static Logger log = LoggerFactory.getLogger(DistributeAPHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();

        List<Pair<MapleStat, Integer>> statupdate = new ArrayList<>(2);

        int dwInc = slea.readInt();
        if (player.getRemainingAp() > 0) {
            switch (dwInc) {
                case 64: //incSTR
                    int str = player.getStr();
                    if (str >= 999 && !player.isGM()) {
                        return;
                    }
                    player.setStr(str + 1);
                    statupdate.add(new Pair<>(MapleStat.STR, player.getStr()));
                    break;
                case 128: //incDEX
                    int dex = player.getDex();
                    if (dex >= 999 && !player.isGM()) {
                        return;
                    }
                    player.setDex(dex + 1);
                    statupdate.add(new Pair<>(MapleStat.DEX, player.getDex()));
                    break;
                case 256: //incINT
                    int int_ = player.getInt();
                    if (int_ >= 999 && !player.isGM()) {
                        return;
                    }
                    player.setInt(int_ + 1);
                    statupdate.add(new Pair<>(MapleStat.INT, player.getInt()));
                    break;
                case 512: //incLUK
                    int luk = player.getLuk();
                    if (luk >= 999 && !player.isGM()) {
                        return;
                    }
                    player.setLuk(luk + 1);
                    statupdate.add(new Pair<>(MapleStat.LUK, player.getLuk()));
                    break;
                case 2048: //incHP
                    int maxHP = player.getMaxHp();
                    if (maxHP == 30000) {
                        return;
                    }
                    if (player.getJob().isA(MapleJob.BEGINNER)) {
                        maxHP += rand(8, 12);
                    } else if (player.getJob().isA(MapleJob.WARRIOR)) {
                        ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                        int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                        if (improvingMaxHPLevel >= 1) {
                            maxHP += rand(20, 24) + improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        } else {
                            maxHP += rand(20, 24);
                        }
                    } else if (c.getPlayer().getJob().isA(MapleJob.MAGICIAN)) {
                        maxHP += rand(6, 10);
                    } else if (c.getPlayer().getJob().isA(MapleJob.BOWMAN)) {
                        maxHP += rand(16, 20);
                    } else if (c.getPlayer().getJob().isA(MapleJob.THIEF)) {
                        maxHP += rand(20, 24);
                    }
                    maxHP = Math.min(30000, maxHP);
                    c.getPlayer().setHpApUsed(c.getPlayer().getHpApUsed() + 1);
                    c.getPlayer().setMaxHp(maxHP);
                    statupdate.add(new Pair<>(MapleStat.MAXHP, maxHP));
                    break;
                case 8192: //incMP
                    int maxMP = player.getMaxMp();
                    if (maxMP == 30000) {
                        return;
                    }
                    if (player.getJob().isA(MapleJob.BEGINNER)) {
                        maxMP += rand(6, 8);
                    } else if (player.getJob().isA(MapleJob.WARRIOR)) {
                        maxMP += rand(2, 4);
                    } else if (player.getJob().isA(MapleJob.MAGICIAN)) {
                        ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                        int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                        if (improvingMaxMPLevel >= 1) {
                            maxMP += rand(18, 20) + improvingMaxMP.getEffect(improvingMaxMPLevel).getY();
                        } else {
                            maxMP += rand(18, 20);
                        }
                    } else if (player.getJob().isA(MapleJob.BOWMAN)) {
                        maxMP += rand(10, 12);
                    } else if (player.getJob().isA(MapleJob.THIEF)) {
                        maxMP += rand(10, 12);
                    }
                    maxMP = Math.min(30000, maxMP);
                    player.setMpApUsed(player.getMpApUsed() + 1);
                    player.setMaxMp(maxMP);
                    statupdate.add(new Pair<>(MapleStat.MAXMP, maxMP));
                    break;
                default:
                    c.announce(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true));
                    return;
            }
            player.setRemainingAp(player.getRemainingAp() - 1);
            statupdate.add(new Pair<>(MapleStat.AVAILABLEAP, player.getRemainingAp()));
            c.announce(MaplePacketCreator.updatePlayerStats(statupdate, true));
        } else {
            // Packet Edit
        }
    }

    private static int rand(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }
}
