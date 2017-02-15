package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * 
 * @Author - Straight Edgy
 */

public class EmoteHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		int emote = slea.readInt();
		if (emote < 8) {
			c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.UserPool.Emotion.getEmotion(c.getPlayer().getId(), emote), false);
		}
	}
}
