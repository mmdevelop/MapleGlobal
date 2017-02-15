package net.sf.odinms.server.PlayerInteraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.maps.AbstractMapleMapObject;
import net.sf.odinms.tools.MaplePacketCreator;

public abstract class PlayerInteractionManager extends AbstractMapleMapObject implements IPlayerInteractionManager {

    private String ownerName;
    private int ownerId;
    private byte type;
    private String description = "";
    private short capacity;
    protected MapleCharacter[] visitors = new MapleCharacter[3];
    protected List<MaplePlayerShopItem> items = new LinkedList<MaplePlayerShopItem>();

    public PlayerInteractionManager(MapleCharacter owner, int type, String desc, int capacity) {
        this.setPosition(owner.getPosition());
        this.ownerName = owner.getName();
        this.ownerId = owner.getId();
        this.type = (byte) type;
        this.capacity = (short) capacity;
        this.description = desc;
    }

    @Override
    public void broadcast(MaplePacket packet, boolean toOwner) {
        for (MapleCharacter visitor : visitors) {
            if (visitor != null) {
                visitor.getClient().getSession().write(packet);
            }
        }
        if (toOwner) {
            MapleCharacter pOwner = null;
            if (getShopType() == 4) {
                pOwner = ((MaplePlayerShop) this).getMCOwner();
            }
            if (pOwner != null) {
                pOwner.getClient().getSession().write(packet);
            }
        }
    }

    @Override
    public void removeVisitor(MapleCharacter visitor) {
        int slot = getVisitorSlot(visitor);
        boolean shouldUpdate = getFreeSlot() == -1;
        if (slot > -1) {
            visitors[slot] = null;
            broadcast(MaplePacketCreator.shopVisitorLeave(slot + 1), true);
            if (shouldUpdate) {
                ((MaplePlayerShop) this).getMCOwner().getMap().broadcastMessage(MaplePacketCreator.sendInteractionBox(((MaplePlayerShop) this).getMCOwner()));
            }
        }
    }

    @Override
    public void addVisitor(MapleCharacter visitor) {
        int i = this.getFreeSlot();
        if (i > -1) {
            broadcast(MaplePacketCreator.shopVisitorAdd(visitor, i + 1), true);
            visitors[i] = visitor;
            if (getFreeSlot() == -1) {
                MapleCharacter pOwner = null;
                if (getShopType() == 4) {
                    pOwner = ((MaplePlayerShop) this).getMCOwner();
                } 
                if (pOwner != null) {
                    pOwner.getMap().broadcastMessage(MaplePacketCreator.sendInteractionBox(pOwner));
                }
            }
        }
    }

    @Override
    public int getVisitorSlot(MapleCharacter visitor) {
        for (int i = 0; i < capacity; i++) {
            if (visitors[i] == visitor) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void removeAllVisitors(int error, int type) {
        for (int i = 0; i < capacity; i++) {
            if (visitors[i] != null) {
                if (type != -1) {
                    visitors[i].getClient().getSession().write(MaplePacketCreator.shopErrorMessage(error, type));
                }
                visitors[i].setInteraction(null);
                visitors[i] = null;
            }
        }
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public int getOwnerId() {
        return ownerId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public MapleCharacter[] getVisitors() {
        return visitors;
    }

    @Override
    public List<MaplePlayerShopItem> getItems() {
        return items;
    }

    @Override
    public void addItem(MaplePlayerShopItem item) {
        items.add(item);
    }

    @Override
    public boolean removeItem(int item) {
        synchronized (items) {
            if (items.contains(item)) {
                items.remove(item);
                return true;
            }
            return false;
        }
    }

    @Override
    public void removeFromSlot(int slot) {
        items.remove(slot);
    }

    @Override
    public int getFreeSlot() {
        for (int i = 0; i < 3; i++) {
            if (visitors[i] == null) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public byte getItemType() {
        return type;
    }

    @Override
    public boolean isOwner(MapleCharacter chr) {
        return chr.getId() == ownerId && chr.getName().equals(ownerName);
    }

    public boolean returnItems(MapleClient c) {
        for (MaplePlayerShopItem item : items) {
            if (item.getBundles() > 0) {
                IItem nItem = item.getItem();
                nItem.setQuantity(item.getBundles());
                if (MapleInventoryManipulator.addFromDrop(c, nItem)) {
                    item.setBundles((short) 0);
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
