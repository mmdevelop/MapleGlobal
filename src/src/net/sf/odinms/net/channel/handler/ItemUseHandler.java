package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class ItemUseHandler extends AbstractMaplePacketHandler {
	
	public ItemUseHandler() {}

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
		if (!player.isAlive()) {
			c.enableActions();
			return;
		}
		MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		
		byte slot = (byte) slea.readShort();
		int itemId = slea.readInt();
		
		IItem toUse = player.getInventory(MapleInventoryType.USE).getItem(slot);
		if (toUse != null && toUse.getQuantity() > 0) {	
			
			if (toUse.getItemId() != itemId) {
				player.ban("Attempting to use an item, not in the users inventory.", true);
				return;
			}
			
			if (ii.isTownScroll(itemId)) {
				if (ii.getItemEffect(itemId).applyTo(player)) {
					MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
				}
				c.enableActions();
				return;
			}
			
			MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
			ii.getItemEffect(itemId).applyTo(player);
		} else {
			player.ban("Attempting to use an item, not in the users inventory.", true);			
		}

	}

}
