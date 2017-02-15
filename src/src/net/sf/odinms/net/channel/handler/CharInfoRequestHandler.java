package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CharInfoRequestHandler extends AbstractMaplePacketHandler {
	
	/**
	 * @author Straight Edgy
	 */

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter target = (MapleCharacter) c.getPlayer().getMap().getMapObject(slea.readInt());
		if (target != null) {
			c.announce(MaplePacketCreator.charInfo(target));
		}	
	}

}
