package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class GroupMessageHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		boolean party = slea.readByte() == 1;
		int nMessages = slea.readByte();
		int recipients[] = new int[nMessages];
		
		for (int i = 0; i < nMessages; i++) {
			recipients[i] = slea.readInt();
		}
		
		String message = slea.readMapleAsciiString();
		try {
			if (party && player.getParty() != null) {
				c.getChannelServer().getWorldInterface().partyChat(player.getPartyId(), message, player.getName());
			} else {
				c.getChannelServer().getWorldInterface().buddyChat(recipients, player.getId(), player.getName(), message);
			}
		} catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		}
		
	}

}
