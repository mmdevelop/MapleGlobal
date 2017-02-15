package net.sf.odinms.net.channel.handler;

import java.util.List;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MagicAttackHandler extends AbstractDealDamageHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		AttackInfo attack = parseDamage(slea, (byte) 3);
		
		MaplePacket attackPacket = MaplePacketCreator.magicAttack(player.getId(), attack.skill, attack.stance, attack.direction, attack.numAttackedAndDamage, attack.allDamage);
		player.getMap().broadcastMessage(player, attackPacket, false, true);
		
		MapleStatEffect effect = attack.getAttackEffect(player);
		int maxDamage = 40000; //To-do damage calc
		applyAttack(attack, player, maxDamage, effect.getAttackCount());
		
		for (int i = 1; i <= 3; i++) {
			ISkill skill = SkillFactory.getSkill(2000000 + i * 100000);
			int skillLvl = player.getSkillLevel(skill);
			if (skillLvl > 0) {
				for (Pair<Integer, List<Integer>> singleDamage : attack.allDamage) {
					skill.getEffect(skillLvl).applyPassive(player, player.getMap().getMapObject(singleDamage.getLeft()), 0);
				}
				break;
			}
		}
		
	}	
}
