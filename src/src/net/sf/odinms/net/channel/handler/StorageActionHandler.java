package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleStorage;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 *
 * @Author - Ginseng
 */
public class StorageActionHandler extends AbstractMaplePacketHandler {

    private enum StorageError {
        INVENTORY_FULL(8), //Please check if your inventory is full or not
        NOT_ENOUGH_MESOS(11), //You have not enough mesos.
        STORAGE_FULL(12), //The storage is full.
        DUE_TO_AN_ERROR(13); //Due to an error, the trade did not happen.

        private int mode;
        
        private StorageError(int mode) {
            this.mode = mode;
        }
        
        public int getMode() {
            return mode;
        }
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleCharacter player = c.getPlayer();
        final MapleStorage storage = player.getStorage();
        byte action = slea.readByte();
        if (action == 3) { // Withdraw
            byte type = slea.readByte();
            byte slot = slea.readByte();
            ;
            slot = storage.getSlot(MapleInventoryType.getByType(type), slot);
            IItem item = storage.takeOut(slot);
            
            if (item != null) {
                if (MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                } else {
                    storage.store(item);
                    c.announce(MaplePacketCreator.getStorageError(StorageError.INVENTORY_FULL.getMode()));
                }
                storage.sendTakenOut(c, ii.getInventoryType(item.getItemId()));
            } else {
                AutobanManager.getInstance().autoban(c, "Trying to take out item from storage which does not exist.");
            }
            c.enableActions();
        } else if (action == 4) { //Store Items
            byte slot = (byte) slea.readShort();
            int itemId = slea.readInt();
            short quantity = slea.readShort();

            if (quantity < 1) {
                AutobanManager.getInstance().autoban(c, "Trying to store " + quantity + " of " + itemId);
                return;
            }

            if (storage.isFull()) {
                c.announce(MaplePacketCreator.getStorageError(StorageError.STORAGE_FULL.getMode()));
                return;
            }

            if (player.getMeso() < 100) {
                c.announce(MaplePacketCreator.getStorageError(StorageError.NOT_ENOUGH_MESOS.getMode()));
                return;
            }
            
            MapleInventoryType type = ii.getInventoryType(itemId);
            IItem item = player.getInventory(type).getItem(slot).copy();
            if (item.getItemId() == itemId) {
                if (item.getQuantity() >= quantity || ii.isThrowingStar(itemId)) {
                    if (ii.isThrowingStar(itemId)) {
                        quantity = item.getQuantity();
                    }
                    player.gainMeso(-100, false, true, false);
                    MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
                    item.setQuantity(quantity);
                    storage.store(item);
                } else { //Missmatching quantity
                    AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store non-matching quantity (" + quantity + "/" + item.getQuantity() + ").");
                }
            } else { //Missmatching itemId
                AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store non-matching itemid (" + itemId + "/" + item.getItemId() + ").");
            }
            storage.sendStored(c, type);
            c.enableActions();
        } else if (action == 5) { //Store Mesos
            int mesos = slea.readInt();

            if (mesos == 0) {
                return;
            }

            int storedMesos = storage.getMeso();
            int playerMesos = player.getMeso();
            boolean withdraw = mesos > 0;
            boolean success = false;

            if (withdraw) {
                if (storedMesos >= mesos) {
                    success = true;
                }
            } else {
                if (playerMesos >= Math.abs(mesos)) {
                    success = true;
                }
            }

            if (success) {
                if (mesos < 0 && (storedMesos - mesos) < 0) {
                    mesos = -(Integer.MAX_VALUE - storedMesos);
                    if (-mesos > playerMesos) {
                        return;
                    }
                } else if (mesos > 0 && (playerMesos + mesos) < 0) {
                    mesos = Integer.MAX_VALUE - playerMesos;
                    if (mesos > storedMesos) {
                        return;
                    }
                }
                storage.setMeso(storedMesos - mesos);
                player.gainMeso(mesos, false, true, false);
            } else {
                AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to store or take out unavailable amount of mesos (" + mesos + "/" + storage.getMeso() + "/" + player.getMeso() + ")");
            }
            storage.sendMeso(c);
            c.enableActions();
        } else if (action == 6) { //Exit Storage
        	player.setInStorage(false);
            storage.close();
        }
    }

}
