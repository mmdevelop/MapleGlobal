package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Ginseng
 */
public class DistributeSPHandler extends AbstractMaplePacketHandler{

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter player = c.getPlayer();
        int remainingSP = player.getRemainingSp();
        
        int skillId = slea.readInt();
        boolean isBeginnerSkill = false;
        
        if (skillId == 1000 || skillId == 1001 || skillId == 1002) {
            remainingSP = Math.min((player.getLevel() - 1), 6);
            for (int i = 0; i < 3; i++) {
                remainingSP -= player.getSkillLevel(SkillFactory.getSkill(1000 + i));
            }
            isBeginnerSkill = true;
        }
        
        ISkill skill = SkillFactory.getSkill(skillId);
        int maxLevel = skill.getMaxLevel();
        int nextLevel = player.getSkillLevel(skill) + 1;
        
        if(remainingSP > 0 && nextLevel <= maxLevel && skill.canBeLearnedBy(player.getJob())) {
            if (!isBeginnerSkill) {
                player.setRemainingSp(player.getRemainingSp() - 1);
            }
            player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
            player.changeSkillLevel(skill, nextLevel, player.getMasterLevel(skill));
        } else if (!skill.canBeLearnedBy(player.getJob())) {
            //Packet Edit
        } else if(!(remainingSP > 0 && nextLevel <= maxLevel)) {
            //Packet Edit
        }
        
        
    }
    
}
