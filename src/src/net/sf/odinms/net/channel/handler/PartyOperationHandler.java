package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.CWvsContext;
import net.sf.odinms.tools.CWvsContext.PartyResult.Result;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import org.slf4j.LoggerFactory;

public class PartyOperationHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
		MapleCharacter player = c.getPlayer();
		MapleParty party = player.getParty();
		MaplePartyCharacter partyPlayer = new MaplePartyCharacter(player);
		
		byte mode = slea.readByte();
		try {
			if (mode == 1) { //Create Party
				if (player.getJob() == MapleJob.BEGINNER) {
					c.announce(CWvsContext.PartyResult.partyResult(Result.BEGINNER_CANT_CREATE)); // A beginner can't create a party.
					return;
				}
				if (party == null) {
					party = wci.createParty(partyPlayer);
					player.setParty(party);
					c.announce(CWvsContext.PartyResult.createParty(c.getPlayer()));
				}
			}  else if (mode == 2) { //Leave Party
				if (party == null) {
					c.announce(CWvsContext.PartyResult.partyResult(Result.YET_TO_JOIN_A_PARTY)); // You have yet to join a party.
					return;
				}
				if (partyPlayer.equals(party.getLeader())) { //Disband party
					wci.updateParty(party.getId(), PartyOperation.DISBAND, partyPlayer);
					if (player.getEventInstance() != null) {
						player.getEventInstance().disbandParty();
					}			
				} else {
					wci.updateParty(party.getId(), PartyOperation.LEAVE, partyPlayer);	
					if (player.getEventInstance() != null) {
						player.getEventInstance().leftParty(player);
					}		
				}			
				player.setParty(null);
			} else if (mode == 3) { //Join Party
				int id = slea.readInt();
				if (player.getParty() == null) {
					party = wci.getParty(id);
					if (party != null) {
						if (party.getMembers().size() < 6) {
							wci.updateParty(party.getId(), PartyOperation.JOIN, partyPlayer);
							player.receivePartyMemberHP();
							player.updatePartyMemberHP();
						} else {
							c.announce(CWvsContext.PartyResult.partyResult(Result.PARTY_FULL));
						}
					} else {
						//Party doesn't exist, error message?
					}
				} else {
					//Player already in party, error message?
				}
			} else if (mode == 4) { //Invite to Party
				int cid = slea.readInt();
				MapleCharacter invited = c.getChannelServer().getPlayerStorage().getCharacterById(cid);
				if (invited != null) {
					if (invited.getParty() == null) {
						if (party.getMembers().size() < 6) {
							invited.getClient().announce(CWvsContext.PartyResult.invite(player));
						}
					} else {
						c.announce(CWvsContext.PartyResult.partyResult(Result.ALREADY_JOINED));
					}
				}
			} else if (mode == 5) {
				int cid = slea.readInt();
				if (partyPlayer.equals(party.getLeader())) {
					MaplePartyCharacter expelled = party.getMemberById(cid);
					if (expelled != null) {
						wci.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
						if (player.getEventInstance() != null) {
							player.getEventInstance().disbandParty();
						}
					}
				}
			}
		} catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		}
	}

}
