package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class SummonBagHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		MapleCharacter player = c.getPlayer();
		
		if (!player.isAlive()) {
			c.enableActions();
			return;
		}
		
		byte slot = (byte) slea.readShort();
		int itemId = slea.readInt();
		IItem item = player.getInventory(MapleInventoryType.USE).getItem(slot);
		
		if (item != null && item.getItemId() == itemId && item.getQuantity() > 0) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
            int[][] monsters = ii.getSummonMobs(itemId);
            for (int i = 0; i < monsters.length; i++) {
            	int monsterId = monsters[i][0];
            	int quantity = monsters[i][1];
            	if ((int) Math.ceil(Math.random() * 100) < quantity) {
            		MapleMonster spawn = MapleLifeFactory.getMonster(monsterId);
            		player.getMap().spawnMonsterOnGroudBelow(spawn, player.getPosition());
            	}	
            }
		} else { //Packet Editing, not in inventory.
			
		}
		c.enableActions();
	}

}
