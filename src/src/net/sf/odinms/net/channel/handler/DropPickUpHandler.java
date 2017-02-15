package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class DropPickUpHandler extends AbstractMaplePacketHandler {

	@Override
	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
		MapleCharacter player = c.getPlayer();
                
		slea.readInt(); //foothold? is this even used though?
		int objectId = slea.readInt();
		MapleMapObject object = player.getMap().getMapObject(objectId);
		
		if (object == null) {
			c.announce(MaplePacketCreator.getInventoryFull());
			c.announce(MaplePacketCreator.getShowInventoryFull());
			return;
		}
		
		if (object instanceof MapleMapItem) {
                    
			MapleMapItem mapItem = (MapleMapItem) object;
			
                        if (!mapItem.isPickupTimeElapsed() && mapItem.getOwner().getId() != player.getId())
                        {
                            c.enableActions();
                            return;
                        }
                        
			if (mapItem.isPickedUp()) {
				c.announce(MaplePacketCreator.getInventoryFull());
				c.announce(MaplePacketCreator.getShowInventoryFull());
				return;				
			}
			
			double distance = player.getPosition().distanceSq(mapItem.getPosition());
			player.getCheatTracker().checkPickupAgain();
			if (distance > 90000.0) {
				player.getCheatTracker().registerOffense(CheatingOffense.ITEMVAC);
			} else if (distance > 22500.0) {
				player.getCheatTracker().registerOffense(CheatingOffense.SHORT_ITEMVAC);
			}
			
			if (mapItem.getMeso() > 0) {
				player.gainMeso(mapItem.getMeso(), true, true);
				player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapItem.getObjectId(), 2, player.getId()), mapItem.getPosition());
				player.getCheatTracker().pickupComplete();
				player.getMap().removeMapObject(object);				
			} else {
				if (MapleInventoryManipulator.addFromDrop(c, mapItem.getItem())) {
					player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapItem.getObjectId(), 2, c.getPlayer().getId()), mapItem.getPosition());
					player.getCheatTracker().pickupComplete();
					player.getMap().removeMapObject(object);
				} else {
					c.getPlayer().getCheatTracker().pickupComplete();
					return;					
				}
			}
			mapItem.setPickedUp(true);
		}
	}

}
