package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class ItemMoveHandler extends AbstractMaplePacketHandler{

	public ItemMoveHandler() {}
	
	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
		byte origin = (byte) slea.readShort();
		byte destination = (byte) slea.readShort();
		short quantity = slea.readShort();
		
		if (origin < 0 && destination > 0) {
			MapleInventoryManipulator.unequip(c, origin, destination);
		} else if (destination < 0) {
			MapleInventoryManipulator.equip(c, origin, destination);
		} else if (destination == 0) {
			MapleInventoryManipulator.drop(c, type, origin, quantity);
		} else {
			MapleInventoryManipulator.move(c, type, origin, destination);
		}
	}
}
