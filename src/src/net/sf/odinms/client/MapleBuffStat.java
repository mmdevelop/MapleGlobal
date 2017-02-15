package net.sf.odinms.client;

import java.io.Serializable;
import net.sf.odinms.net.LongValueHolder;

public enum MapleBuffStat implements LongValueHolder, Serializable {
	

    WATK(0x1),
    WDEF(0x2),
    MATK(0x4),
    MDEF(0x8),
    
    ACC(0x10),
    AVOID(0x20),
    HANDS(0x40),
    SPEED(0x80),
    JUMP(0x100),

    
    MAGIC_GUARD(0x200), //MagicGuard
    DARKSIGHT(0x400), //DarkSight
    BOOSTER(0x800), //Booster
    
    POWERGUARD(0x1000), //PowerGuard
    HYPERBODYHP(0x2000), //MaxHP
    HYPERBODYMP(0x4000), //MaxMP
    INVINCIBLE(0x8000), //Invincible
    
    SOULARROW(0x10000), //SoulArrow
    STUN(0x20000), //Stun
    POISON(0x40000), //Poison
    SEAL(0x80000), //Seal
    
    DARKNESS(0x100000), //Darkness
    COMBO(0x200000), //ComboCounter
    WK_CHARGE(0x400000), //WeaponCharge
    DRAGONBLOOD(0x800000), //DragonBlood
    
    HOLY_SYMBOL(0x01000000), //HolySymbol
    MESOUP(0x02000000), //MesoUp
    SHADOWPARTNER(0x04000000), //ShadowPartner
    PICKPOCKET(0x08000000), //PickPocket
    
    MESOGUARD(0x10000000), //MesoGuard
    THAW(0x20000000), //Wut
    //Mob Skills
    WEAKEN(0x40000000), //Weakness
    CURSE(0x80000000),
    
    
    //Most below not in v12.
    SUMMON(0x20000000000000L),
    PUPPET(0x800000000000000L),
    //SWITCH_CONTROLS(0x8000000000000L)
    
    MORPH(0x2),
    RECOVERY(0x4),
    MAPLE_WARRIOR(0x8),
    STANCE(0x10),
    SHARP_EYES(0x20),
    MANA_REFLECTION(0x40),
    SHADOW_CLAW(0x100),
    INFINITY(0x200),
    HOLY_SHIELD(0x400), 
    HAMSTRING(0x800),
    BLIND(0x1000), 
    CONCENTRATE(0x2000),
    ECHO_OF_HERO(0x8000),
    GHOST_MORPH(0x20000),
    DASH(0x30000000),
    MONSTER_RIDING(0x40000000),
    
    
    ;
    static final long serialVersionUID = 0L;
    private final long i;

    private MapleBuffStat(long i) {
        this.i = i;
    }

    @Override
    public long getValue() {
        return i;
    }
}
