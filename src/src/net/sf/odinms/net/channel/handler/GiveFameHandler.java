package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CWvsContext;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class GiveFameHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		int id = slea.readInt();
		int mode = slea.readByte();
		int fame = mode == 0 ? -1 : 1;

		MapleCharacter target = (MapleCharacter) player.getMap().getMapObject(id);

		if (target == player) { // Self faming, Packet Editing.
			player.getCheatTracker().registerOffense(CheatingOffense.FAMING_SELF);
			return;
		} else if (player.getLevel() < 15) {
			c.announce(CWvsContext.GivePopularityResult.getError(2));
			return;
		} else if (target == null) {
			c.announce(CWvsContext.GivePopularityResult.getError(1));
			return;
		}

		switch (player.canGiveFame(target)) {
		case OK:
			player.hasGivenFame(target);
			target.addFame(fame);
			target.updateSingleStat(MapleStat.FAME, target.getFame());
			target.getClient().announce(CWvsContext.GivePopularityResult.receivePopularity(player, mode));
			c.announce(CWvsContext.GivePopularityResult.givePopularity(target, mode, target.getFame()));
			break;
		case NOT_TODAY:
			c.announce(CWvsContext.GivePopularityResult.getError(3));
			break;
		case NOT_THIS_MONTH:
			c.announce(CWvsContext.GivePopularityResult.getError(4));
			break;
		}

	}

}
