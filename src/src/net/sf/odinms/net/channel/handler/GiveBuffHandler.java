package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Ginseng
 */
public class GiveBuffHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int skillId = slea.readInt();
        int skillLevel = slea.readByte();

        Point pos = null;
        if (slea.available() == 4) {
            pos = new Point(slea.readShort(), slea.readShort());
        }

        ISkill skill = SkillFactory.getSkill(skillId);
        MapleCharacter player = c.getPlayer();
        boolean correctSkillLevel = player.getSkillLevel(skill) == skillLevel;

        if (!correctSkillLevel || skillLevel <= 0 || !skill.canBeLearnedBy(player.getJob())) {
            if (!player.isGM()) {
                //Packet Editing
            }
            return;
        }

        if (player.isAlive() && skill.canBeLearnedBy(player.getJob()) || player.isGM()) {
            if (skill.getId() != 2311002 || c.getPlayer().canDoor()) {
                skill.getEffect(skillLevel).applyTo(player, pos);
            } else {
                new ServernoticeMapleClientMessageCallback(5, c).dropMessage("Please wait 5 seconds before casting Mystic Door again");
                c.enableActions();
            }
        } else {
            c.enableActions();
        }
    }

}
