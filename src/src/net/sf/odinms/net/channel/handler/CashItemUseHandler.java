package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.CWvsContext;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CashItemUseHandler extends AbstractMaplePacketHandler {

	/**
	 * @Author Straight Edgy
	 */

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		MapleCharacter player = c.getPlayer();
		
		byte slot = (byte) slea.readShort();
		int itemId = slea.readInt();
		
		IItem item = player.getInventory(MapleInventoryType.USE).getItem(slot);
		
		if (item == null || item.getItemId() != itemId) { //Packet Editting
			c.enableActions();
			return;
		}
		
		if (!player.isAlive()) {
			c.enableActions();
			return;
		}
		
		try {
			boolean sucess = true;
			switch (itemId) {
				//Megaphones
				case 2081000: //Megaphone
					if (player.getLevel() >= 10) {
						player.getMap().broadcastMessage(CWvsContext.BroadcastMsg.sendMegaphone(player.getName() + " : " + slea.readMapleAsciiString()));
					} else {
						c.announce(CField.WarnMessage.display("You may not use this until you're level 10."));
						sucess = false;
					}
					break;		
				case 2082000: //Super Megaphone
					c.getChannelServer().getWorldInterface().broadcastMessage(null, CWvsContext.BroadcastMsg.sendSuperMegaphone(c.getPlayer().getName() + " : " + slea.readMapleAsciiString(), c.getChannel(), slea.readByte() == 1).getBytes());
					break;				
				//Weather Effects
				case 2090000: //Snowy Snow
				case 2090001: //Sprinkled Flowers
				case 2090002: //Sprinkled Bubbles
				case 2090003: //Snow Flakes
				case 2090004: //Sprinkled Presents
				case 2090005: //Sprinkled Chocolate
				case 2090006: //Sprinkled Flower Petals
				case 2090007: //Sprinkled Candies
				case 2090008: //Sprinkled Maple Leaves
					String msg = slea.readMapleAsciiString();
					String itemMsg = ii.getMsg(itemId).replaceFirst("%s", player.getName());
					int length = itemMsg.length() - 2;
					if (msg.substring(0, length).equals(itemMsg.substring(0, length))) {
						player.getMap().startMapEffect(msg, itemId);
						c.enableActions();
					} else { //Packet Editing
						sucess = false;
					}
					break;
				//Pet Tag
				case 2110000: //Pet Tag
					break;
					
				//Kites
				case 2130000: //Korean Kite
				case 2130001: //Heart Balloon
				case 2130002: //Graduation Banner
				case 2130003: //Admission Banner
					break;
				//Meso Sacks
				case 2140000: //Bronze Sack of Mesos ("meso" value="60000")
				case 2140001: //Silver Sack of Mesos ("meso" value="130000")
				case 2140002: //Gold Sack Of Mesos ("meso" value="350000")
					break;
				//JukeBox
				case 2150000: //Congratulatory Song
					break;
				//Memo
				case 2160000: //Note
					break;
				//Teleport Rock
				case 2170000:
					break;
				//AP/SP Resets
				case 2180000: //Scroll for AP Reset
				case 2180001: //Scroll for 1st-level Skill SP
				case 2180002: //Scroll for 2nd-level Skill SP
				case 2180003: //Scroll for 3rd-level Skill SP
					break;
			}
			if (sucess) {
				MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
			}
			c.enableActions();
		} catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		}
	}

}
