package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CloseRangeAttackHandler extends AbstractDealDamageHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		AttackInfo attack = parseDamage(slea, (byte)0);
		
		MaplePacket attackPacket = MaplePacketCreator.closeRangeAttack(player.getId(), attack.skill, attack.stance, attack.direction, attack.numAttackedAndDamage, attack.allDamage);
		player.getMap().broadcastMessage(player, attackPacket, false, true);
		
		Integer comboBuff = player.getBuffedValue(MapleBuffStat.COMBO);
		int finisherOrbs = 0;
		
		if (isFinisher(attack.skill)) {
			if (comboBuff != null) {
				finisherOrbs = comboBuff.intValue() - 1;
			}
			player.handleOrbconsume();
		} else if (attack.numAttacked > 0 && comboBuff != null) {
			if (attack.skill != 1111008) {
				player.handleOrbgain();
			}
		}
		
		if (attack.numAttacked > 0) {
			if (attack.skill == 1311005) {
				int totalDamageToOneMonster = attack.allDamage.get(0).getRight().get(0).intValue();
				player.setHp(player.getHp() - totalDamageToOneMonster * attack.getAttackEffect(player).getX() / 100);
				player.updateSingleStat(MapleStat.HP, player.getHp());
			} else if (attack.skill == 1211002) {
				player.cancelEffectFromBuffStat(MapleBuffStat.WK_CHARGE);
			}
		}
		
		int maxDamage = player.getCurrentMaxBaseDamage();
		int attackCount = 1;
		
		if (attack.skill != 0) {
			MapleStatEffect effect = attack.getAttackEffect(player);
			attackCount = effect.getAttackCount();
			maxDamage *= effect.getDamage() / 100.0;
			maxDamage *= attackCount;
		}
		
		maxDamage = Math.min(maxDamage, 99999);
		
		if (attack.skill == 4211006) {
			maxDamage = 700000;
		} else if (finisherOrbs > 0) {
			maxDamage *= finisherOrbs;
		} else if (comboBuff != null) {
			ISkill combo = SkillFactory.getSkill(1111002);
			int comboLvl = player.getSkillLevel(combo);
			MapleStatEffect comboEffect = combo.getEffect(comboLvl);
			double comboMod = 1.0 + (comboEffect.getDamage() / 100.0 - 1.0) * (comboBuff.intValue() - 1);
			maxDamage *= comboMod;
		}
		
		if (finisherOrbs == 0 && isFinisher(attack.skill)) {
			return;
		}
		
		if (isFinisher(attack.skill)) {
			maxDamage = 99999;
		}
		
		applyAttack(attack, player, maxDamage, attackCount);
	}
	
	private boolean isFinisher(int skillId) {
        return skillId >= 1111003 && skillId <= 1111006;
	}
}
