package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.status.MonsterStatus;
import net.sf.odinms.client.status.MonsterStatusEffect;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.Element;
import net.sf.odinms.server.life.ElementalEffectiveness;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.LittleEndianAccessor;

public abstract class AbstractDealDamageHandler extends AbstractMaplePacketHandler {

	public static class AttackInfo {
		public int numAttacked, numDamage, numAttackedAndDamage;
		public int skill, stance, direction;
		public List<Pair<Integer, List<Integer>>> allDamage = new ArrayList<>();
		public List<SummonAttackEntry> allSummonDamage = new ArrayList<>();

		private MapleStatEffect getAttackEffect(MapleCharacter chr, ISkill theSkill) {
			ISkill mySkill = theSkill;
			if (mySkill == null) {
				mySkill = SkillFactory.getSkill(skill);
			}
			int skillLevel = chr.getSkillLevel(mySkill);
			if (skillLevel == 0) {
				return null;
			}
			return mySkill.getEffect(skillLevel);
		}

		public MapleStatEffect getAttackEffect(MapleCharacter chr) {
			return getAttackEffect(chr, null);
		}
	}

	public class SummonAttackEntry {
		private int monsterOid;
		private int damage;

		public SummonAttackEntry(int monsterOid, int damage) {
			super();
			this.monsterOid = monsterOid;
			this.damage = damage;
		}

		public int getMonsterOid() {
			return monsterOid;
		}

		public int getDamage() {
			return damage;
		}
	}
	
	private void checkHighDamage(MapleCharacter player, MapleMonster monster, AttackInfo attack, ISkill skill, MapleStatEffect attackEffect, int damageDealt, int maxDamageDealt) {
		int elementalMaxDamagePerMonster;
		Element element = Element.NEUTRAL;
		if (skill != null) {
			element = skill.getElement();
		}
		
		if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
			int chargeSkillId = player.getBuffSource(MapleBuffStat.WK_CHARGE);
			switch (chargeSkillId) {
				case 1211003:
				case 1211004:
					element = Element.FIRE;
					break;
				case 1211005:
				case 1211006:
					element = Element.ICE;
					break;
				case 1211007:
				case 1211008:
					element = Element.LIGHTING;
					break;
				case 1221003:
				case 1221004:
					element = Element.HOLY;
					break;			
			}
			ISkill chargeSkill = SkillFactory.getSkill(chargeSkillId);
			maxDamageDealt *= chargeSkill.getEffect(player.getSkillLevel(chargeSkill)).getDamage() / 100.0;
		}
		
		if (element != Element.NEUTRAL) {
			double elementalEffect;
			if (attack.skill == 3211003 || attack.skill == 3111003) {
				elementalEffect = attackEffect.getX() / 200.0;
			} else {
				elementalEffect = 0.5;
			}
			switch (monster.getEffectiveness(element)) {
				case IMMUNE:
					elementalMaxDamagePerMonster = 1;
					break;
				case NORMAL:
					elementalMaxDamagePerMonster = maxDamageDealt;
					break;
				case WEAK:
					elementalMaxDamagePerMonster = (int) (maxDamageDealt * (1.0 + elementalEffect));
					break;
				case STRONG:
					elementalMaxDamagePerMonster = (int) (maxDamageDealt * (1.0 - elementalEffect));
					break;
				default:
					throw new RuntimeException("Unknown enum constant");
			}			
		} else {
			elementalMaxDamagePerMonster = maxDamageDealt;
		}
		
		if (damageDealt > elementalMaxDamagePerMonster) {
			player.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE);
			if (damageDealt > elementalMaxDamagePerMonster * 3) {
				AutobanManager.getInstance().autoban(player.getClient(), damageDealt +" damage (level: " + player.getLevel() + " watk: " + player.getTotalWatk() + " skill: " + attack.skill + ", monster: " + monster.getId() + " assumed max damage: " + elementalMaxDamagePerMonster + ")");				
			}
		}
	}

	/**
	 * @param type: 0 = Melee, 1 = Summon, 2 = Ranged, 3 = Magic
	 */
	
	public AttackInfo parseDamage(LittleEndianAccessor lea, byte type) {
		AttackInfo ret = new AttackInfo();
		boolean pickpocket = false;

		if (type != 1) {
			ret.numAttackedAndDamage = lea.readByte(); // tbyte
			ret.skill = lea.readInt(); // skillid
			if (ret.skill == 0) {
				byte skillLevel = 0; // we should be applying mastery and skill levels as if it theres a difference..
			}
			if (ret.skill == 4211006) {
				pickpocket = true;
			}
			ret.numAttacked = (byte) (ret.numAttackedAndDamage / 0x10); // targets
			ret.numDamage = (byte) (ret.numAttackedAndDamage % 0x10); // hits

			lea.skip(1);
			ret.stance = lea.readByte();
			ret.direction = lea.readByte();
		} else {
			ret.skill = lea.readInt();
			ret.stance = lea.readByte();
			ret.numAttacked = 1;
			ret.numDamage = 1;
		}

		if (type == 2) { // ranged
			lea.readShort(); // projectile
			lea.skip(1); // 02 (?)
		}

		ret.allDamage = new ArrayList<Pair<Integer, List<Integer>>>();
		for (byte i = 0; i < ret.numAttacked; i++) {
			int oid = lea.readInt();
			lea.skip(4); // skip 4 (int)
			lea.readShort(); // skip 2 (x - short)
			lea.readShort(); // skip 2 (y - short)
			lea.skip(4); // skip 4 (int)

			if (type == 1) { // summons
				lea.skip(1);
			} else if (!pickpocket) { // pickpocket
				lea.skip(2);
			}

			List<Integer> allDamageNumbers = new ArrayList<Integer>();
			for (byte j = 0; j < ret.numDamage; j++) {
				int damage = lea.readInt();
				allDamageNumbers.add(Integer.valueOf(damage));
				ret.allSummonDamage.add(new SummonAttackEntry(oid, damage));
			}

			ret.allDamage.add(new Pair<Integer, List<Integer>>(Integer.valueOf(oid), allDamageNumbers));
		}
		Point pos = new Point(lea.readShort(), lea.readShort()); //Point-less xDDD
		return ret;
	}
	
	private void handlePickPocket(MapleCharacter player, MapleMonster monster, Pair<Integer, List<Integer>> oned) {
		ISkill pickpocket = SkillFactory.getSkill(4211003);
		int delay = 0;
		int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET).intValue();
		int reqdamage = 20000;
		Point monsterPosition = monster.getPosition();
		
		for (Integer eachd : oned.getRight()) {
			if (pickpocket.getEffect(player.getSkillLevel(pickpocket)).makeChanceResult()) {
				double perc = (double) eachd / (double) reqdamage;

				final int todrop = Math.min((int) Math.max(perc * (double) maxmeso, (double) 1),
					maxmeso);
				final MapleMap tdmap = player.getMap();
				final Point tdpos = new Point((int) (monsterPosition.getX() + (Math.random() * 100) - 50),
											  (int) (monsterPosition.getY()));
				final MapleMonster tdmob = monster;
				final MapleCharacter tdchar = player;

				TimerManager.getInstance().schedule(new Runnable() {
					public void run() {
						tdmap.spawnMesoDrop(todrop, todrop, tdpos, tdmob, tdchar, false);
					}
				}, delay);

				delay += 200;
			}
		}
	}
	
	protected void applyAttack(AttackInfo attack, MapleCharacter player, int maxDamagePerMonster, int attackCount) {
		player.getCheatTracker().resetHPRegen();
		player.getCheatTracker().checkAttack(attack.skill);
		
		ISkill theSkill = null;
		MapleStatEffect attackEffect = null;
		if (attack.skill != 0) {
			theSkill = SkillFactory.getSkill(attack.skill);
			attackEffect = attack.getAttackEffect(player, theSkill);
			if (attackEffect == null) {
				AutobanManager.getInstance().autoban(player.getClient(),
					"Using a skill he doesn't have (" + attack.skill + ")");
			}
			if (attack.skill != 2301002) {
				// heal is both an attack and a special move (healing)
				// so we'll let the whole applying magic live in the special move part
				if (player.isAlive()) {
					attackEffect.applyTo(player);
				} else {
					player.getClient().getSession().write(MaplePacketCreator.enableActions());
				}
			}
		}
		if (!player.isAlive()) {
			player.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
			return;
		}
		// meso explosion has a variable bullet count
		if (attackCount != attack.numDamage && attack.skill != 4211006) {
			player.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT,
				attack.numDamage + "/" + attackCount);
		}
		int totDamage = 0;
		MapleMap map = player.getMap();
		
		if (attack.skill == 4211006) { // meso explosion
			for (Pair<Integer, List<Integer>> oned : attack.allDamage) {
				MapleMapObject mapobject = map.getMapObject(oned.getLeft().intValue());
				
				if (mapobject != null && mapobject.getType() == MapleMapObjectType.ITEM) {
					MapleMapItem mapitem = (MapleMapItem) mapobject;
					if (mapitem.getMeso() > 0) {
						synchronized (mapitem) {
							if (mapitem.isPickedUp())
								return;
							map.removeMapObject(mapitem);
							map.broadcastMessage(MaplePacketCreator.removeItemFromMap(mapitem.getObjectId(), 4, 0), mapitem.getPosition());
							mapitem.setPickedUp(true);
						}
					} else if (mapitem.getMeso() == 0) {
						player.getCheatTracker().registerOffense(CheatingOffense.ETC_EXPLOSION);
						return;
					}
				} else if (mapobject != null && mapobject.getType() != MapleMapObjectType.MONSTER) {
					player.getCheatTracker().registerOffense(CheatingOffense.EXPLODING_NONEXISTANT);
					return; // etc explosion, exploding nonexistant things, etc.
				}
			}
		}
		
		for (Pair<Integer, List<Integer>> oned : attack.allDamage) {
			MapleMonster monster = map.getMonsterByOid(oned.getLeft().intValue());

			if (monster != null) {
				int totDamageToOneMonster = 0;
				for (Integer eachd : oned.getRight()) {
					totDamageToOneMonster += eachd.intValue();
				}
				totDamage += totDamageToOneMonster;

				Point playerPos = player.getPosition();
				if (totDamageToOneMonster > attack.numDamage + 1) {
					int dmgCheck = player.getCheatTracker().checkDamage(totDamageToOneMonster);
					if (dmgCheck > 5) {
						player.getCheatTracker().registerOffense(CheatingOffense.SAME_DAMAGE, dmgCheck + " times: " +
							totDamageToOneMonster);
					}
				}
				checkHighDamage(player, monster, attack, theSkill, attackEffect, totDamageToOneMonster, maxDamagePerMonster);
				double distance = playerPos.distanceSq(monster.getPosition());
				if (distance > 360000.0) { // 600^2, 550 is approximatly the range of ultis
					player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, Double.toString(Math.sqrt(distance)));
					// if (distance > 1000000.0)
					// AutobanManager.getInstance().addPoints(player.getClient(), 50, 120000, "Exceeding attack
					// range");
				}
				if (!monster.isControllerHasAggro()) {
					if (monster.getController() == player) {
						monster.setControllerHasAggro(true);
					} else {
						monster.switchController(player, true);
					}
				}
				// only ds, sb, assaulter, normal (does it work for thieves, bs, or assasinate?)
				if ((attack.skill == 4001334 || attack.skill == 4201005 || attack.skill == 0 || attack.skill == 4211002 || attack.skill == 4211004) &&
					player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null) {
					handlePickPocket(player, monster, oned);
				}
				if (attack.skill == 4101005) { // drain
					ISkill drain = SkillFactory.getSkill(4101005);
					int gainhp = (int) ((double) totDamageToOneMonster *
						(double) drain.getEffect(player.getSkillLevel(drain)).getX() / 100.0);
					gainhp = Math.min(monster.getMaxHp(), Math.min(gainhp, player.getMaxHp() / 2));
					player.addHP(gainhp);
				}

				if (player.getJob().isA(MapleJob.WHITEKNIGHT)) {
					int[] charges = new int[] { 1211005, 1211006 };
					for (int charge : charges) {
						ISkill chargeSkill = SkillFactory.getSkill(charge);

						if (player.isBuffFrom(MapleBuffStat.WK_CHARGE, chargeSkill)) {
							final ElementalEffectiveness iceEffectiveness = monster.getEffectiveness(Element.ICE);
							if (totDamageToOneMonster > 0 && iceEffectiveness == ElementalEffectiveness.NORMAL || iceEffectiveness == ElementalEffectiveness.WEAK) {
								MapleStatEffect chargeEffect = chargeSkill.getEffect(player.getSkillLevel(chargeSkill));
								MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(Collections.singletonMap(MonsterStatus.FREEZE, 1), chargeSkill, false);
								monster.applyStatus(player, monsterStatusEffect, false, chargeEffect.getY() * 2000);
							}
							break;
						}
					}
				}

				if (totDamageToOneMonster > 0 && attackEffect != null && attackEffect.getMonsterStati().size() > 0) {
					if (attackEffect.makeChanceResult()) {
						MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(attackEffect.getMonsterStati(), theSkill, false);
						monster.applyStatus(player, monsterStatusEffect, attackEffect.isPoison(), attackEffect.getDuration());
					}
				}
				map.damageMonster(player, monster, totDamageToOneMonster);
			}
		}
		if (totDamage > 1) {
			player.getCheatTracker().setAttacksWithoutHit(player.getCheatTracker().getAttacksWithoutHit() + 1);
			final int offenseLimit;
			if (attack.skill != 3121004) {
				offenseLimit = 100;
			} else {
				offenseLimit = 300;				
			}
			if (player.getCheatTracker().getAttacksWithoutHit() > offenseLimit) {
				player.getCheatTracker().registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT,
					Integer.toString(player.getCheatTracker().getAttacksWithoutHit()));
			}
		}
	}
}
