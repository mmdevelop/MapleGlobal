package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class WhisperHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		byte mode = slea.readByte();
		
		if (mode == 5) { // /find %s
			String name = slea.readMapleAsciiString();
			MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(name);
			if (target != null) {	
				if (target.isGM() && !player.isGM() || target.getName().equalsIgnoreCase(player.getName())) {
					c.announce(MaplePacketCreator.getUnableToFind(name));				
				} else if (target.inCS()) {
					c.announce(MaplePacketCreator.playerIsInCs(target));
				} else {
					c.announce(MaplePacketCreator.getFind(target));
				}
			} else {
				try {
					int channel = ChannelServer.getInstance(c.getChannel()).getWorldInterface().find(name);
					if (channel > -1) {
						c.announce(MaplePacketCreator.getFind(target, channel));
					} else {
						c.announce(MaplePacketCreator.getUnableToFind(name));
					}
				} catch (RemoteException e) {
					c.announce(MaplePacketCreator.getUnableToFind(name));
					c.getChannelServer().reconnectWorld();
				}
			}
		} else if (mode == 6) { // Whisper
			String target = slea.readMapleAsciiString();
			String text = slea.readMapleAsciiString();
			
			MapleCharacter recipient = c.getChannelServer().getPlayerStorage().getCharacterByName(target);
			if (recipient != null) {
				if (!recipient.isGM()) {
					if (recipient.inCS()) {
						c.announce(MaplePacketCreator.playerIsInCs(recipient));
					} else {
						recipient.getClient().announce(MaplePacketCreator.getWhisper(player.getName(), c.getChannel(), text));
						c.announce(MaplePacketCreator.getWhisperReply(recipient, text, recipient.getClient().getChannel()));
					}
				} else {
					c.announce(MaplePacketCreator.getUnableToFind(target));
				}
			} else {
				try {
					if (ChannelServer.getInstance(c.getChannel()).getWorldInterface().isConnected(target)) {
						ChannelServer.getInstance(c.getChannel()).getWorldInterface().whisper(c.getPlayer().getName(), target, c.getChannel(), text);
						c.announce(MaplePacketCreator.getWhisperReply(recipient, text, 1));
					} else {
						c.announce(MaplePacketCreator.getUnableToFind(target));
					}
				} catch (RemoteException e) {
					c.announce(MaplePacketCreator.getUnableToFind(target));
					c.getChannelServer().reconnectWorld();
				}
				c.announce(MaplePacketCreator.getUnableToFind(target));
			}
			
		}
		
	}

	
}
