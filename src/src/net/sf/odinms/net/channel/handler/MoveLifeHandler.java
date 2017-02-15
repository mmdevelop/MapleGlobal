package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.handler.MovePathHandler;

/**
 * MapleGlobal.org - 2016
 * @Author - Straight Edgy, Exile
 */

public class MoveLifeHandler extends MovePathHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		int objectId = slea.readInt();
		
		MapleMapObject object = player.getMap().getMapObject(objectId);
		
		if (object == null || object.getType() != MapleMapObjectType.MONSTER) {
			return;
		}
		
		MapleMonster monster = (MapleMonster) object;
		
		List<LifeMovementFragment> res = null;
		short moveId = slea.readShort();
		boolean isSkill = slea.readByte() == 1;
		byte skill = slea.readByte();
		byte level = /*(byte)monster.getLevel()*/0;
		Point projectile = new Point(slea.readShort(), slea.readShort());
		Point endPos = new Point(slea.readShort(), slea.readShort());
		
		res = parseMovement(slea);
		                
		if (player != monster.getController()) {
			if (monster.isAttackedBy(player)) 
                        {
                            monster.switchController(player, true);
			}
                        else
                        {
                            player.stopControllingMonster(monster);
                            return;
                        }
		} else {
			if (skill == 255 && monster.isControllerKnowsAboutAggro() && !monster.isMobile()) {
				monster.setControllerHasAggro(false);
				monster.setControllerKnowsAboutAggro(false);
			}
 		}
		
		boolean aggro = monster.isControllerHasAggro();
		int mp = monster.getMp();
		c.announce(MaplePacketCreator.moveMonsterResponse(objectId, moveId, mp, isSkill, skill, level));
		
		if (aggro) {
			monster.setControllerKnowsAboutAggro(true);
		}
		
		if (res != null) {
			if (slea.available() != 1) {
				return;
			}
			slea.seek(13); //9, 14, 17, 18
                        MaplePacket packet = MaplePacketCreator.moveMonster(objectId, monster.getController().getId(), moveId, isSkill, skill, slea, monster);
			player.getMap().broadcastMessage(player, packet, monster.getPosition());
                        updatePosition(res, monster, -1);
			player.getMap().moveMonster(monster, monster.getPosition());
			player.getCheatTracker().checkMoveMonster(monster.getPosition());
		}
	}
}
