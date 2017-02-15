package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleMessenger;
import net.sf.odinms.net.world.MapleMessengerCharacter;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MessengerHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
		MapleCharacter player = c.getPlayer();
		MapleMessenger messenger = player.getMessenger();
		String message;

		byte mode = slea.readByte();
		try {
			if (mode == 0) { // Open
				if (messenger == null) {
					int id = slea.readInt();
					if (id == 0) { // Create
						MapleMessengerCharacter chr = new MapleMessengerCharacter(player);
						messenger = wci.createMessenger(chr);
						player.setMessenger(messenger);
						player.setMessengerPosition(0);
					} else { // Join
						messenger = wci.getMessenger(id);
						if (messenger != null) {
							if (messenger.getMembers().size() < 3) {
								int position = messenger.getLowestPosition();
								MapleMessengerCharacter chr = new MapleMessengerCharacter(player, position);
								player.setMessenger(messenger);
								player.setMessengerPosition(position);
								wci.joinMessenger(messenger.getId(), chr, player.getName(), chr.getChannel());
							}
						}
					}
				}
			} else if (mode == 2) { // Exit
				if (messenger != null) {
					wci.leaveMessenger(player.getName(), messenger.getId());
					player.exitMessenger();
				}
			} else if (mode == 3) { // Invite
				if (messenger != null) {
					if (messenger.getMembers().size() < 3) {
						message = slea.readMapleAsciiString();
						MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(message);
						if (target != null) {
							if (target.getMessenger() == null) {
								target.getClient().announce(CField.Messenger.invite(player.getName(), messenger.getId()));
								c.announce(CField.Messenger.Note.inviteSent(target.getName()));
							} else {
								c.announce(CField.Messenger.chat(player.getName() + " : " + message + " is already using Maple Messenger"));
							}
						} else {
							if (ChannelServer.getInstance(c.getChannel()).getWorldInterface().isConnected(message)) {
								ChannelServer.getInstance(c.getChannel()).getWorldInterface().messengerInvite(c.getPlayer().getName(), messenger.getId(), message, c.getChannel());
							} else {
								c.announce(CField.Messenger.Note.inviteFailed(message));
							}
						}
					} else {
						c.announce(CField.Messenger.chat(player.getName() + " : You cannot have more than 3 people in the Maple Messenger"));
					}
				}
			} else if (mode == 5) { // Decline
				message = slea.readMapleAsciiString();
				MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(message);
				if (target != null) {
					if (target.getMessenger() != null) {
						target.getClient().announce(CField.Messenger.Note.inviteDeclined(player.getName()));
					}
				} else {
					wci.declineChat(message, player.getName());
				}
			} else if (mode == 6) { // Message
				if (messenger != null) {
					message = slea.readMapleAsciiString();
					MapleMessengerCharacter chr = new MapleMessengerCharacter(player);
					wci.messengerChat(messenger.getId(), message, chr.getName());
				}
			}
		} catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		}

	}

}
