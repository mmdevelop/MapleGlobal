package net.sf.odinms.server;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.server.PlayerInteraction.IPlayerInteractionManager;
import net.sf.odinms.server.PlayerInteraction.MapleMiniGame;
import net.sf.odinms.server.PlayerInteraction.MaplePlayerShop;
import net.sf.odinms.server.PlayerInteraction.MaplePlayerShopItem;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MapleTrade {

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MapleTrade.class);
	private MapleTrade partner = null;
	private List<IItem> items = new LinkedList<IItem>();
	private List<IItem> exchangeItems;
	private int meso = 0;
	private int exchangeMeso;
	boolean locked = false;
	private MapleCharacter chr;
	private byte pos;

	public MapleTrade(byte number, MapleCharacter c) {
		chr = c;
		this.pos = pos;
	}

	private int getFee(int meso) {
		int fee = 0;
		if (meso >= 10000000) {
			fee = (int) Math.round(0.04 * meso);
		} else if (meso >= 5000000) {
			fee = (int) Math.round(0.03 * meso);
		} else if (meso >= 1000000) {
			fee = (int) Math.round(0.02 * meso);
		} else if (meso >= 100000) {
			fee = (int) Math.round(0.01 * meso);
		} else if (meso >= 50000) {
			fee = (int) Math.round(0.005 * meso);
		}
		return fee;
	}

	public void lock() {
		locked = true;
		// chr.getClient().announce(MaplePacketCreator.getTradeConfirmation());
		// // own side shouldn't see other side whited
		partner.getChr().getClient().announce(CField.MiniRoomBase.Trade.confirmTrade());
	}

	public void complete1() {
		exchangeItems = partner.getItems();
		exchangeMeso = partner.getMeso();
	}

	public void complete2() {
		items.clear();
		meso = 0;
		for (IItem item : exchangeItems) {
			MapleInventoryManipulator.addFromDrop(chr.getClient(), item, false);
		}
		if (exchangeMeso > 0) {
			chr.gainMeso(exchangeMeso - getFee(exchangeMeso), false, true, false);
		}
		exchangeMeso = 0;
		if (exchangeItems != null) {
			exchangeItems.clear();
		}
		chr.getClient().announce(CField.MiniRoomBase.Trade.completeTrade(pos, true));
	}

	public void cancel() {
		for (IItem item : items) {
			MapleInventoryManipulator.addFromDrop(chr.getClient(), item, false);
		}
		if (meso > 0) {
			chr.gainMeso(meso, false, true, false);
		}
		// just to be on the safe side...
		meso = 0;
		if (items != null) {
			items.clear();
		}
		exchangeMeso = 0;
		if (exchangeItems != null) {
			exchangeItems.clear();
		}
		chr.getClient().announce(CField.MiniRoomBase.Trade.cancelTrade(pos));
	}

	public boolean isLocked() {
		return locked;
	}

	public int getMeso() {
		return meso;
	}

	public void setMeso(int meso) {
		if (locked) {
			throw new RuntimeException("Trade is locked.");
		}
		if (meso < 0) {
			log.info("[h4x] {} Trying to trade < 0 meso", chr.getName());
			return;
		}
		if (chr.getMeso() >= meso) {
			chr.gainMeso(-meso, false, true, false);
			this.meso += meso;
			chr.getClient().announce(CField.MiniRoomBase.Trade.setMesos((byte) 0, this.meso));
			if (partner != null) {
				partner.getChr().getClient().announce(CField.MiniRoomBase.Trade.setMesos((byte) 1, this.meso));
			}
		} else {
			AutobanManager.getInstance().addPoints(chr.getClient(), 1000, 0,
					"Trying to trade more mesos than in possession");
		}
	}

	public void addItem(IItem item) {
		items.add(item);
		chr.getClient().announce(CField.MiniRoomBase.Trade.addItem((byte) 0, item));
		if (partner != null) {
			partner.getChr().getClient().announce(CField.MiniRoomBase.Trade.addItem((byte) 1, item));
		}
	}

	public void chat(String message) {
		chr.getClient().announce(MaplePacketCreator.shopChat(chr.getName() + " : " + message, 0));
		if (partner != null) {
			partner.getChr().getClient().announce(MaplePacketCreator.shopChat(chr.getName() + " : " + message, 1));
		}
	}

	public MapleTrade getPartner() {
		return partner;
	}

	public void setPartner(MapleTrade partner) {
		if (locked) {
			throw new RuntimeException("Trade is locked.");
		}
		this.partner = partner;
	}

	// private void broadcast(MaplePacket packet) {
	// chr.getClient().announce(packet);
	// if (partner != null)
	// partner.getChr().getClient().announce(packet);
	// }
	public MapleCharacter getChr() {
		return chr;
	}

	public List<IItem> getItems() {
		return new LinkedList<IItem>(items);
	}

	public boolean fitsInInventory() {
		MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
		Map<MapleInventoryType, Integer> neededSlots = new LinkedHashMap<MapleInventoryType, Integer>();
		for (IItem item : exchangeItems) {
			MapleInventoryType type = mii.getInventoryType(item.getItemId());
			if (neededSlots.get(type) == null) {
				neededSlots.put(type, 1);
			} else {
				neededSlots.put(type, neededSlots.get(type) + 1);
			}
		}
		for (Map.Entry<MapleInventoryType, Integer> entry : neededSlots.entrySet()) {
			if (chr.getInventory(entry.getKey()).isFull(entry.getValue() - 1)) {
				return false;
			}
		}
		return true;
	}

	public static void completeTrade(MapleCharacter c) {
		c.getTrade().lock();
		MapleTrade local = c.getTrade();
		MapleTrade partner = local.getPartner();
		if (partner.isLocked()) {
			local.complete1();
			partner.complete1();
			// check for full inventories
			if (!local.fitsInInventory() || !partner.fitsInInventory()) {
				cancelTrade(c);
				c.getClient().announce(CField.MiniRoomBase.Trade.completeTrade((byte) 0, false));
				partner.getChr().getClient().announce(CField.MiniRoomBase.Trade.completeTrade((byte) 1, false));
				return;
			}
			local.complete2();
			partner.complete2();
			partner.getChr().setTrade(null);
			c.setTrade(null);
		}
	}

	public static void cancelTrade(MapleCharacter c) {
		c.getTrade().cancel();
		if (c.getTrade().getPartner() != null) {
			c.getTrade().getPartner().cancel();
			c.getTrade().getPartner().getChr().setTrade(null);
		}
		c.setTrade(null);
	}

	public static void startTrade(MapleCharacter c) {
		if (c.getTrade() == null) {
			c.setTrade(new MapleTrade((byte) 0, c));
			c.getClient().announce(CField.MiniRoomBase.Trade.start(c.getClient(), c.getTrade(), false));
		} else {
			c.getClient().announce(MaplePacketCreator.serverNotice(5, "You are already in a trade"));
		}
	}

	public static void inviteTrade(MapleCharacter c1, MapleCharacter c2) {
		if (c2.getTrade() == null) {
			c2.setTrade(new MapleTrade((byte) 1, c2));
			c2.getTrade().setPartner(c1.getTrade());
			c1.getTrade().setPartner(c2.getTrade());
			c2.getClient().announce(CField.MiniRoomBase.Trade.invite(c1));
		} else {
			c1.getClient().announce(
					MaplePacketCreator.serverNotice(5, "The other player is already trading with someone else."));
			cancelTrade(c1);
		}
	}

	public static void visitTrade(MapleCharacter c1, MapleCharacter c2) {
		if (c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null
				&& c2.getTrade().getPartner() == c1.getTrade()) {
			c2.getClient().announce(CField.MiniRoomBase.Trade.addPartner(c1));
			c1.getClient().announce(CField.MiniRoomBase.Trade.start(c1.getClient(), c1.getTrade(), true));
		} else {
			c1.getClient()
					.announce(MaplePacketCreator.serverNotice(5, "The other player has already closed the trade"));
		}
	}

	public static void declineTrade(MapleCharacter c) {
		MapleTrade trade = c.getTrade();
		if (trade != null) {
			if (trade.getPartner() != null) {
				MapleCharacter other = trade.getPartner().getChr();
				other.getTrade().cancel();
				other.setTrade(null);
				other.getClient()
						.announce(MaplePacketCreator.serverNotice(5, c.getName() + " has declined your trade request"));

			}
			trade.cancel();
			c.setTrade(null);
		}
	}

	public static void visit(SeekableLittleEndianAccessor slea, MapleClient c) {
		if (c.getPlayer().getTrade() != null && c.getPlayer().getTrade().getPartner() != null) {
			MapleTrade.visitTrade(c.getPlayer(), c.getPlayer().getTrade().getPartner().getChr());
		} else {
			int oid = slea.readInt();
			MapleMapObject ob = c.getPlayer().getMap().getMapObject(oid);
			
			if (ob instanceof MapleMiniGame && c.getPlayer().getMiniGame() == null) {
				MapleMiniGame game = (MapleMiniGame) ob;
				if (game.hasFreeSlot()) {
					game.addVisitor(c.getPlayer());
				}
			} else if (ob instanceof IPlayerInteractionManager && c.getPlayer().getInteraction() == null) {
				IPlayerInteractionManager ips = (IPlayerInteractionManager) ob;
				if (ips.getShopType() == 2) {
					if (((MaplePlayerShop) ips).isBanned(c.getPlayer().getName())) {
						c.getPlayer().dropMessage(1, "You have been banned from this store.");
						return;
					}
				}
				if (ips.getFreeSlot() == -1) {
					c.announce(MaplePacketCreator.getMiniBoxFull());
					return;
				}
				c.getPlayer().setInteraction(ips);
				ips.addVisitor(c.getPlayer());
				c.announce(MaplePacketCreator.getInteraction(c.getPlayer(), false));
			}
		}
	}

	public static void exit(SeekableLittleEndianAccessor slea, MapleClient c) {
		if (c.getPlayer().getTrade() != null) {
			MapleTrade.cancelTrade(c.getPlayer());
		} else {
			IPlayerInteractionManager ips = c.getPlayer().getInteraction();
			c.getPlayer().setInteraction(null);
			if (ips != null) {
				if (ips.isOwner(c.getPlayer())) {
					if (ips.getShopType() == ips.PLAYER_SHOP) {
						boolean save = false;
						for (MaplePlayerShopItem items : ips.getItems()) {
							if (items.getBundles() > 0) {
								IItem item = items.getItem();
								item.setQuantity(items.getBundles());
								if (MapleInventoryManipulator.addFromDrop(c, item)) {
									items.setBundles((short) 0);
								} else {
									save = true;
									break;
								}
							}
						}
						ips.removeAllVisitors(3, 1);
						ips.closeShop(save);
					}
				} else {
					ips.removeVisitor(c.getPlayer());
				}
			}
		}
	}
}
