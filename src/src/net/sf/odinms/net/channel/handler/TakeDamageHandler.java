package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Straight Edgy
 */

public class TakeDamageHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		byte mode = slea.readByte();
		int damage = slea.readInt();
		int monsterId = 0;

		if (mode != -2) {
			monsterId = slea.readInt();
		}
		
		if (damage >= 30000 || damage <= 0) {
			return;
		}
		
		player.addHP(-damage);
		
		if (!player.isHidden()) {
			player.getMap().broadcastMessage(player, MaplePacketCreator.damagePlayer(monsterId, player.getId(), damage), false);
		}	
	}
}
