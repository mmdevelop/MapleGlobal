package net.sf.odinms.client.messages.commands;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.Equip;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.CWvsContext;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.net.channel.ChannelServer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleDisease;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MapleRing;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.world.remote.CheaterData;
import net.sf.odinms.provider.MapleData;
import net.sf.odinms.provider.MapleDataProvider;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.provider.MapleDataTool;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleMonsterStats;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.net.channel.handler.ChangeChannelHandler;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.server.MapleTrade;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedDoubleArg;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import static net.sf.odinms.client.messages.CommandProcessor.joinAfterString;
import net.sf.odinms.tools.NpcPacket;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.packets.CharacterData;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;

public class GM implements Command {

    private static String getBannedReason(String name) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ResultSet rs;
            ps = con.prepareStatement("SELECT name, banned, banreason, macs FROM accounts WHERE name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("banned") > 0) {
                    String user, reason, mac;
                    user = rs.getString("name");
                    reason = rs.getString("banreason");
                    mac = rs.getString("macs");
                    rs.close();
                    ps.close();
                    con.close();
                    return "Username: " + user + " | BanReason: " + reason + " | Macs: " + mac;
                } else {
                    rs.close();
                    ps.close();
                    con.close();
                    return "Player is not banned.";
                }
            }
            rs.close();
            ps.close();
            int accid;
            ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                con.close();
                return "This character / account does not exist.";
            } else {
                accid = rs.getInt("accountid");
            }
            ps = con.prepareStatement("SELECT name, banned, banreason, macs FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (rs.getInt("banned") > 0) {
                String user, reason, mac;
                user = rs.getString("name");
                reason = rs.getString("banreason");
                mac = rs.getString("macs");
                rs.close();
                ps.close();
                con.close();
                return "Username: " + user + " | BanReason: " + reason + " | Macs: " + mac;
            } else {
                rs.close();
                ps.close();
                con.close();
                return "Player is not banned.";
            }
        } catch (SQLException exe) {
            // do nothing lolwat
        }
        return "Player is not banned.";
    }

    public void clearSlot(MapleClient c, int type) {
        MapleInventoryType invent;
        if (type == 1) {
            invent = MapleInventoryType.EQUIP;
        } else if (type == 2) {
            invent = MapleInventoryType.USE;
        } else if (type == 3) {
            invent = MapleInventoryType.ETC;
        } else if (type == 4) {
            invent = MapleInventoryType.SETUP;
        } else {
            invent = MapleInventoryType.CASH;
        }
        List<Integer> itemMap = new LinkedList<Integer>();
        for (IItem item : c.getPlayer().getInventory(invent).list()) {
            itemMap.add(item.getItemId());
        }
        for (int itemid : itemMap) {
            MapleInventoryManipulator.removeAllById(c, itemid, false);
        }
    }

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        splitted[0] = splitted[0].toLowerCase();
        ChannelServer cserv = c.getChannelServer();
        Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
        MapleCharacter player = c.getPlayer();

        if (splitted[0].equals("!go") || splitted[0].equals("!goto")) {
            if (splitted.length > 1) {
                String destination = splitted[1];
                int map = -1;
                switch (destination.toLowerCase()) {
                    case "start":
                        map = 0;
                        break;
                    case "southperry":
                        map = 60000;
                        break;
                    case "amherst":
                        map = 1010000;
                        break;
                    case "florina":
                        map = 110000000;
                        break;
                    case "gm":
                    case "gmmap":
                        map = 180000000;
                        break;
                    case "henesys":
                    case "hene":
                        map = 100000000;
                        break;
                    case "ellinia":
                    case "elli":
                        map = 101000000;
                        break;
                    case "perion":
                        map = 102000000;
                        break;
                    case "kerning":
                    case "kerning city":
                        map = 103000000;
                        break;
                    case "subway":
                        map = 103000100;
                        break;
                    case "sleepywood":
                    case "sleepy":
                        map = 105040300;
                        break;
                    case "ant tunnel":
                    case "ant":
                        map = 105070001;
                        break;
                    case "lith":
                    case "lith harbor":
                        map = 104000000;
                        break;
                    case "pig beach":
                    case "pig":
                        map = 104010001;
                        break;
                    case "orbis":
                        map = 200000000;
                        break;
                    case "ludi":
                    case "ludibrium":
                        map = 220000000;
                        break;
                    case "henefm":
                    case "henesysfm":
                        map = 100000110;
                        break;
                    case "perionfm":
                        map = 102000100;
                        break;
                    case "nathfm":
                    case "elnathfm":
                        map = 211000110;
                        break;
                    case "ludifm":
                        map = 220000200;
                        break;
                    case "ox":
                        map = 109020001;
                        break;
                    case "happyville":
                    case "happy":
                        map = 209000000;
                        break;
                    case "el":
                    case "nath":
                    case "el nath":
                    case "elnath":
                        map = 211000000;
                        break;
                    default:
                        map = -1;
                }
                if (map != -1) {
                    c.getPlayer().changeMap(map);
                }
            }
        } else if (splitted[0].equals("!portal")) {
            if (splitted.length == 2) {
                try {
                    int portal = Integer.parseInt(splitted[1]);
                    player.changeMap(player.getMapId(), portal);
                } catch (Exception e) {
                    // wat? do nothing?
                }
            }
        } else if (splitted[0].equals("!gmtext")) {
            if (splitted.length == 2) {
                try {
                    int mode = Integer.parseInt(splitted[1]);
                    player.setGMText(mode);
                } catch (Exception e) {
                }
            }
        } else if (splitted[0].equals("!sp")) {
            if (splitted.length != 2) {
                return;
            }
            int sp;
            try {
                sp = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            player.setRemainingSp(sp + player.getRemainingSp());
            player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
        } else if (splitted[0].equals("!ap")) {
            if (splitted.length != 2) {
                return;
            }
            int ap;
            try {
                ap = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            player.setRemainingAp(ap + player.getRemainingAp());
            player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
        } else if (splitted[0].equals("!job")) {
            if (splitted.length != 2) {
                return;
            }
            int job;
            try {
                job = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            player.setJob(job);
        } else if (splitted[0].equals("!whereami")) {
            mc.dropMessage("You are on map " + player.getMap().getId());
        } else if (splitted[0].equals("!openshop")) {
            if (splitted.length != 2) {
                return;
            }
            int shopid;
            try {
                shopid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            MapleShopFactory.getInstance().getShop(shopid).sendShop(c);
        } else if (splitted[0].equals("!opennpc")) {
            if (splitted.length != 2) {
                return;
            }
            int npcid;
            try {
                npcid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
                NPCScriptManager.getInstance().start(c, npcid);
            } else {
                mc.dropMessage("Invalid NPC ID entered.");
            }
        } else if (splitted[0].equals("!levelup")) {
            player.levelUp();
            player.setExp(0);
            player.updateSingleStat(MapleStat.EXP, 0);
        } else if (splitted[0].equals("!mp")) {
            if (splitted.length != 2) {
                return;
            }
            int amt;
            try {
                amt = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            player.setMaxMp(amt);
            player.updateSingleStat(MapleStat.MAXMP, player.getMaxMp());
        } else if (splitted[0].equals("!hp")) {
            if (splitted.length != 2) {
                return;
            }
            int amt;
            try {
                amt = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            player.setMaxHp(amt);
            player.updateSingleStat(MapleStat.MAXHP, player.getMaxHp());
        } else if (splitted[0].equals("!healall")) {
            for (MapleCharacter map : player.getMap().getCharacters()) {
                if (map != null) {
                    map.setHp(map.getCurrentMaxHp());
                    map.updateSingleStat(MapleStat.HP, map.getHp());
                    map.setMp(map.getCurrentMaxMp());
                    map.updateSingleStat(MapleStat.MP, map.getMp());
                }
            }
        } else if (splitted[0].equals("!item")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (splitted.length < 2) {
                return;
            }
            int item;
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            try {
                item = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException e) {
                mc.dropMessage("Error while making item.");
                return;
            }
            if (item >= 5000000 && item <= 5000100) {
                if (quantity > 1) {
                    quantity = 1;
                }
                int petId = MaplePet.createPet(item);
                MapleInventoryManipulator.addById(c, item, quantity, player.getName(), petId);
            } else if (ii.getInventoryType(item).equals(MapleInventoryType.EQUIP) && !ii.isThrowingStar(ii.getEquipById(item).getItemId())) {
                MapleInventoryManipulator.addFromDrop(c, ii.randomizeStats((Equip) ii.getEquipById(item)), true,
                        player.getName());
            } else {
                MapleInventoryManipulator.addById(c, item, quantity);
            }
        } else if (splitted[0].equals("!dropmesos")) {
            if (splitted.length < 2) {
                return;
            }
            int amt;
            try {
                amt = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            player.getMap().spawnMesoDrop(amt, amt, player.getPosition(), player, player, false);
        } else if (splitted[0].equals("!level")) {
            if (splitted.length != 2) {
                return;
            }
            int level;
            try {
                level = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            player.setLevel(level);
            player.setExp(0);
            player.updateSingleStat(MapleStat.EXP, 0);
        } else if (splitted[0].equals("!online")) {
            int i = 0;
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                if (cs.getPlayerStorage().getAllCharacters().size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    mc.dropMessage("Channel " + cs.getChannel());
                    for (MapleCharacter chr : cs.getPlayerStorage().getAllCharacters()) {
                        i++;
                        if (sb.length() > 150) { // Chars per line. Could be
                            // more or less
                            mc.dropMessage(sb.toString());
                            sb = new StringBuilder();
                        }
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName() + "   "));
                    }
                    mc.dropMessage(sb.toString());
                }
            }
        } else if (splitted[0].equals("!banreason")) {
            if (splitted.length != 2) {
                return;
            }
            mc.dropMessage(getBannedReason(splitted[1]));
        } else if (splitted[0].equals("!joinguild")) {
            if (splitted.length != 2) {
                return;
            }
            Connection con = DatabaseConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT guildid FROM guilds WHERE name = ?");
                ps.setString(1, splitted[1]);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if (player.getGuildId() > 0) {
                        try {
                            cserv.getWorldInterface().leaveGuild(player.getMGC());
                        } catch (java.rmi.RemoteException re) {
                            c.getSession().write(MaplePacketCreator.serverNotice(5, "Unable to connect to the World Server. Please try again later."));
                            return;
                        }
                        c.getSession().write(MaplePacketCreator.showGuildInfo(null));
                        player.setGuildId(0);
                        player.saveGuildStatus();
                    }
                    player.setGuildId(rs.getInt("guildid"));
                    player.setGuildRank(2); // Jr.master :D
                    try {
                        cserv.getWorldInterface().addGuildMember(player.getMGC());
                    } catch (RemoteException e) {
                        cserv.reconnectWorld();
                    }
                    c.getSession().write(MaplePacketCreator.showGuildInfo(player));
                    player.getMap().broadcastMessage(player, MaplePacketCreator.removePlayerFromMap(player.getId()), false);
                    player.getMap().broadcastMessage(player, MaplePacketCreator.spawnPlayerMapobject(player), false);
                    if (player.getNoPets() > 0) {
                        for (MaplePet pet : player.getPets()) {
                            player.getMap().broadcastMessage(player, MaplePacketCreator.showPet(player, pet, false, false), false);
                        }
                    }
                    player.saveGuildStatus();
                } else {
                    mc.dropMessage("Guild name does not exist.");
                }
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                return;
            }
        } else if (splitted[0].equals("!unbuffall")) {
            for (MapleCharacter map : player.getMap().getCharacters()) {
                if (map != null && map != player) {
                    map.cancelAllBuffs();
                }
            }
        } else if (splitted[0].equals("!mesos")) {
            if (splitted.length != 2) {
                return;
            }
            int meso;
            try {
                meso = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException ex) {
                return;
            }
            player.setMeso(meso);
        } else if (splitted[0].equals("!clearslot")) {
            if (splitted.length == 2) {
                if (splitted[1].equalsIgnoreCase("all")) {
                    clearSlot(c, 1);
                    clearSlot(c, 2);
                    clearSlot(c, 3);
                    clearSlot(c, 4);
                    clearSlot(c, 5);
                } else if (splitted[1].equalsIgnoreCase("equip")) {
                    clearSlot(c, 1);
                } else if (splitted[1].equalsIgnoreCase("use")) {
                    clearSlot(c, 2);
                } else if (splitted[1].equalsIgnoreCase("etc")) {
                    clearSlot(c, 3);
                } else if (splitted[1].equalsIgnoreCase("setup")) {
                    clearSlot(c, 4);
                } else if (splitted[1].equalsIgnoreCase("cash")) {
                    clearSlot(c, 5);
                } else {
                    mc.dropMessage("!clearslot " + splitted[1] + " does not exist!");
                }
            }
        } else if (splitted[0].equals("!resetreactors")) {
            player.getMap().resetReactors();
            mc.dropMessage("Reactors reset!");
        } else if (splitted[0].equals("!balrog")) {
            int[] balrog = {8130100, 8150000, 9400536};
            for (int amnt = getOptionalIntArg(splitted, 1, 1); amnt > 0; amnt--) {
                for (int i = 0; i < balrog.length; i++) {
                    player.getMap().spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(balrog[i]),
                            player.getPosition());
                }
            }
        } else if (splitted[0].equals("!killall")) {
            String mapMessage = "";
            MapleMap map = player.getMap();
            double range = Double.POSITIVE_INFINITY;
            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = cserv.getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    mapMessage = " in " + map.getStreetName() + " : " + map.getMapName();
                }
            }
            List<MapleMapObject> monsters = map.getMapObjectsInRange(player.getPosition(), range,
                    Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : monsters) {
                MapleMonster monster = (MapleMonster) monstermo;
                map.killMonster(monster, player, false);
            }
            mc.dropMessage("Killed " + monsters.size() + " monsters" + mapMessage + ".");
        } else if (splitted[0].equals("!help")) {
            int page = CommandProcessor.getOptionalIntArg(splitted, 1, 1);
            CommandProcessor.getInstance().dropHelp(c.getPlayer(), mc, page);
        } else if (splitted[0].equals("!say")) {
            if (splitted.length > 1) {
                try {
                    cserv.getWorldInterface().broadcastMessage(player.getName(), MaplePacketCreator.serverNotice(6, "[" + player.getName() + "] " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                } catch (RemoteException e) {
                    cserv.reconnectWorld();
                }
            } else {
                mc.dropMessage("Syntax: !say <message>");
            }
        } else if (splitted[0].equals("!spyon")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                mc.dropMessage("Players stats are:");
                mc.dropMessage("LVL: " + victim.getLevel());
                mc.dropMessage("Fame: " + victim.getFame());
                mc.dropMessage("STR: " + victim.getStr() + "  ||  DEX: " + victim.getDex() + "  ||  INT: " + victim.getInt() + "  ||  LUK: " + victim.getLuk());
                mc.dropMessage("Meso: " + victim.getMeso());
                mc.dropMessage("HP: " + victim.getHp() + "/" + victim.getCurrentMaxHp() + "  ||  MP: " + victim.getMp() + "/" + victim.getCurrentMaxMp());
                mc.dropMessage("NX Cash: " + victim.getCSPoints(0));
            } else {
                mc.dropMessage("Player not found.");
            }
        } else if (splitted[0].equals("!setstats")) {
            int max;
            try {
                max = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asda) {
                return;
            }
            player.setStr(max);
            player.setDex(max);
            player.setInt(max);
            player.setLuk(max);
            player.updateSingleStat(MapleStat.STR, max);
            player.updateSingleStat(MapleStat.DEX, max);
            player.updateSingleStat(MapleStat.INT, max);
            player.updateSingleStat(MapleStat.LUK, max);
        } else if (splitted[0].equals("!giftnx")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                int amount;
                try {
                    amount = Integer.parseInt(splitted[2]);
                } catch (NumberFormatException ex) {
                    return;
                }
                int type = getOptionalIntArg(splitted, 3, 1);
                victim.modifyCSPoints(type, amount);
                victim.dropMessage(5, player.getName() + " has gifted you " + amount + " NX points.");
                mc.dropMessage("NX recieved.");
            } else {
                mc.dropMessage("Player not found.");
            }
        } else if (splitted[0].equals("!maxskills")) {
            player.maxAllSkills();
            mc.dropMessage("All skills have been maxed.");
        } else if (splitted[0].equals("!fame")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.setFame(getOptionalIntArg(splitted, 2, 1));
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            } else {
                mc.dropMessage("Player not found");
            }
        } else if (splitted[0].equals("!unhide")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.dispelSkill(9101004);
            } else {
                mc.dropMessage("Player not found");
            }
        } else if (splitted[0].equals("!heal")) {
            MapleCharacter heal = null;
            if (splitted.length == 2) {
                heal = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
                if (heal == null) {
                    mc.dropMessage("Player was not found");
                }
            } else {
                heal = player;
            }
            heal.setHp(heal.getCurrentMaxHp());
            heal.setMp(heal.getCurrentMaxMp());
            heal.updateSingleStat(MapleStat.HP, heal.getCurrentMaxHp());
            heal.updateSingleStat(MapleStat.MP, heal.getCurrentMaxMp());
        } else if (splitted[0].equals("!unbuff")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.cancelAllBuffs();
            } else {
                mc.dropMessage("Player not found");
            }
        } else if (splitted[0].equals("!mute")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                victim.canTalk(!victim.getCanTalk());
                victim.dropMessage(5, "Your chatting ability is now " + (victim.getCanTalk() ? "on" : "off"));
                player.dropMessage(6, "Player's chatting ability is now set to " + victim.getCanTalk());
            } else {
                mc.dropMessage("Player not found");
            }
        } else if (splitted[0].equals("!givedisease")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            int type;
            if (splitted[2].equalsIgnoreCase("SEAL")) {
                type = 120;
            } else if (splitted[2].equalsIgnoreCase("DARKNESS")) {
                type = 121;
            } else if (splitted[2].equalsIgnoreCase("WEAKEN")) {
                type = 122;
            } else if (splitted[2].equalsIgnoreCase("STUN")) {
                type = 123;
            } else if (splitted[2].equalsIgnoreCase("POISON")) {
                type = 125;
            } else if (splitted[2].equalsIgnoreCase("SEDUCE")) {
                type = 128;
            } else {
                mc.dropMessage("ERROR.");
                return;
            }
            victim.giveDebuff(MapleDisease.getType(type), MobSkillFactory.getMobSkill(type, 1));
        } else if (splitted[0].equals("!dc")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            victim.getClient().disconnect();
            victim.getClient().getSession().close();
        } else if (splitted[0].equals("!charinfo")) {
            StringBuilder builder = new StringBuilder();
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                return;
            }
            builder.append(MapleClient.getLogMessage(victim, "")); // Could use
            // null i
            // think ?
            mc.dropMessage(builder.toString());

            builder = new StringBuilder();
            builder.append("Positions: X: ");
            builder.append(victim.getPosition().x);
            builder.append(" Y: ");
            builder.append(victim.getPosition().y);
            builder.append(" | RX0: ");
            builder.append(victim.getPosition().x + 50);
            builder.append(" | RX1: ");
            builder.append(victim.getPosition().x - 50);
            builder.append(" | FH: ");
            builder.append(victim.getMap().getFootholds().findBelow(player.getPosition()).getId());
            mc.dropMessage(builder.toString());
            builder = new StringBuilder();
            builder.append("HP: ");
            builder.append(victim.getHp());
            builder.append("/");
            builder.append(victim.getCurrentMaxHp());
            builder.append(" | MP: ");
            builder.append(victim.getMp());
            builder.append("/");
            builder.append(victim.getCurrentMaxMp());
            builder.append(" | EXP: ");
            builder.append(victim.getExp());
            builder.append(" | In a Party: ");
            builder.append(victim.getParty() != null);
            builder.append(" | In a Trade: ");
            builder.append(victim.getTrade() != null);
            mc.dropMessage(builder.toString());
            builder = new StringBuilder();
            builder.append("Remote Address: ");
            builder.append(victim.getClient().getSession().getRemoteAddress());
            mc.dropMessage(builder.toString());
            victim.getClient().dropDebugMessage(mc);
        } else if (splitted[0].equals("!connected")) {
            try {
                Map<Integer, Integer> connected = cserv.getWorldInterface().getConnected();
                StringBuilder conStr = new StringBuilder();
                mc.dropMessage("Connected Clients: ");

                for (int i : connected.keySet()) {
                    if (i == 0) {
                        conStr.append("Total: "); // I HAVE NO CLUE WHY.
                        conStr.append(connected.get(i));
                    } else {
                        conStr.append("Channel ");
                        conStr.append(i);
                        conStr.append(": ");
                        conStr.append(connected.get(i));
                    }
                }
                mc.dropMessage(conStr.toString());
            } catch (RemoteException e) {
                cserv.reconnectWorld();
            }
        } else if (splitted[0].equals("!warp")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    MapleMap target = victim.getMap();
                    player.changeMap(target, target.findClosestSpawnpoint(victim.getPosition()));
                } else {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory()
                            .getMap(Integer.parseInt(splitted[2]));
                    victim.changeMap(target, target.getPortal(0));
                }
            } else {
                try {
                    victim = player;
                    WorldLocation loc = cserv.getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        // mc.dropMessage("You will be cross-channel warped. This may take a few seconds.");
                        MapleMap target = cserv.getMapFactory().getMap(loc.map);
                        victim.cancelAllBuffs();
                        String ip = cserv.getIP(loc.channel);
                        victim.getMap().removePlayer(victim);
                        victim.setMap(target);
                        String[] socket = ip.split(":");
                        if (victim.getTrade() != null) {
                            MapleTrade.cancelTrade(player);
                        }
                        victim.saveToDB(true, true);
                        if (victim.getCheatTracker() != null) {
                            victim.getCheatTracker().dispose();
                        }
                        ChannelServer.getInstance(c.getChannel()).removePlayer(player);
                        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
                        try {
                            c.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        MapleMap target = cserv.getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        player.changeMap(target, target.getPortal(0));
                    }
                } catch (Exception e) {
                    // wat? do nothing?
                }
            }
        } else if (splitted[0].equals("!warphere")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            MapleMap pmap = player.getMap();
            if (victim != null) {
                victim.changeMap(pmap, player.getPosition());
            } else {
                try {
                    String name = splitted[1];
                    WorldChannelInterface wci = cserv.getWorldInterface();
                    int channel = wci.find(name);
                    if (channel > -1) {
                        ChannelServer pserv = ChannelServer.getInstance(channel);
                        MapleCharacter world_victim = pserv.getPlayerStorage().getCharacterByName(name);
                        if (world_victim != null) {
                            ChangeChannelHandler.changeChannel(c.getChannel(), world_victim.getClient());
                            world_victim.changeMap(pmap, player.getPosition());
                        }
                    } else {
                        mc.dropMessage("Player not online.");
                    }
                } catch (RemoteException e) {
                    cserv.reconnectWorld();
                }
            }
        } else if (splitted[0].equals("!map")) {
            int mapid;
            try {
                mapid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException mwa) {
                return;
            }
            player.changeMap(mapid, getOptionalIntArg(splitted, 2, 0));
        } else if (splitted[0].equals("!cheaters")) {
            try {
                List<CheaterData> cheaters = cserv.getWorldInterface().getCheaters();
                for (CheaterData cheater : cheaters) {
                    mc.dropMessage(cheater.getInfo());
                }
            } catch (RemoteException e) {
                cserv.reconnectWorld();
            }
        } else if (splitted[0].equals("!createring")) {
            Map<String, Integer> rings = new HashMap<String, Integer>();
            rings.put("crush", 1112001);
            if (rings.containsKey(splitted[3])) {
                MapleCharacter partner1 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
                MapleCharacter partner2 = cserv.getPlayerStorage().getCharacterByName(splitted[2]);
                int ret = MapleRing.createRing(rings.get(splitted[3]), partner1, partner2);
                switch (ret) {
                    case -2:
                        mc.dropMessage("Partner number 1 was not found.");
                        break;

                    case -1:
                        mc.dropMessage("Partner number 2 was not found.");
                        break;

                    case 0:
                        mc.dropMessage("One of the players already posesses a ring!");
                        break;

                    default:
                        mc.dropMessage("Success!");
                }
            } else {
                mc.dropMessage("Ring name was not found.");
            }
            rings.clear();
        } else if (splitted[0].equals("!removering")) {
            MapleCharacter victim = player;
            if (splitted.length == 2) {
                victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            }
            if (victim != null) {
                if (MapleRing.checkRingDB(victim)) {
                    MapleRing.removeRingFromDb(victim);
                } else {
                    victim.dropMessage("You have no ring..");
                }
            }
        } else if (splitted[0].equals("!nearestportal")) {
            final MaplePortal portal = player.getMap().findClosestSpawnpoint(player.getPosition());
            mc.dropMessage(portal.getName() + " id: " + portal.getId() + " script: " + portal.getScriptName());
        } else if (splitted[0].equals("!unban")) {
            if (MapleCharacter.unban(splitted[1])) {
                mc.dropMessage("Sucess!");
            } else {
                mc.dropMessage("Error while unbanning.");
            }
        } else if (splitted[0].equals("!spawn")) {
            int mid;
            int num = getOptionalIntArg(splitted, 2, 1);
            try {
                mid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            if (num > 20) {
                mc.dropMessage("Remember that we know what you're doing ;] please dont over summon");
            }
            Integer hp = getNamedIntArg(splitted, 1, "hp");
            Integer exp = getNamedIntArg(splitted, 1, "exp");
            Double php = getNamedDoubleArg(splitted, 1, "php");
            Double pexp = getNamedDoubleArg(splitted, 1, "pexp");
            MapleMonster onemob = MapleLifeFactory.getMonster(mid);
            int newhp = 0;
            int newexp = 0;
            if (hp != null) {
                newhp = hp.intValue();
            } else if (php != null) {
                newhp = (int) (onemob.getMaxHp() * (php.doubleValue() / 100));
            } else {
                newhp = onemob.getMaxHp();
            }
            if (exp != null) {
                newexp = exp.intValue();
            } else if (pexp != null) {
                newexp = (int) (onemob.getExp() * (pexp.doubleValue() / 100));
            } else {
                newexp = onemob.getExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }
            MapleMonsterStats overrideStats = new MapleMonsterStats();
            overrideStats.setHp(newhp);
            overrideStats.setExp(newexp);
            overrideStats.setMp(onemob.getMaxMp());
            if (num > 20) {
                num = 20;
            }
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                player.getMap().spawnMonsterOnGroudBelow(mob, player.getPosition());
            }
        } else if (splitted[0].equals("!ban")) {
            String originalReason = StringUtil.joinStringFrom(splitted, 2);
            String reason = player.getName() + " banned " + splitted[1] + ": " + originalReason;
            MapleCharacter target = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (!target.isGM() || player.getGMLevel() > 3) {
                    String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
                    String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    reason += "  IP: " + ip;
                    target.ban(reason, false);
                    try {
                        cserv.getWorldInterface().broadcastMessage(null, MaplePacketCreator.serverNotice(6, readableTargetName + " has been banned for " + originalReason).getBytes());
                    } catch (RemoteException e) {
                        cserv.reconnectWorld();
                    }
                } else {
                    mc.dropMessage("Please don't ban " + cserv.getServerName() + " GMs!");
                }
            } else {
                if (MapleCharacter.ban(splitted[1], reason, false)) {
                    String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
                    String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    reason += " (IP: " + ip + ")";
                    try {
                        cserv.getWorldInterface().broadcastMessage(null, MaplePacketCreator.serverNotice(6, readableTargetName + " has been banned for " + originalReason).getBytes());
                    } catch (RemoteException e) {
                        cserv.reconnectWorld();
                    }
                } else {
                    mc.dropMessage("Failed to ban " + splitted[1]);
                }
            }
        } else if (splitted[0].equals("!tempban")) {
            Calendar tempB = Calendar.getInstance();
            String originalReason = joinAfterString(splitted, ":");

            if (splitted.length < 4 || originalReason == null) {
                mc.dropMessage("Syntax helper: !tempban <name> [i / m / w / d / h] <amount> [r [reason id] : Text Reason");
                return;
            }

            int yChange = getNamedIntArg(splitted, 1, "y", 0);
            int mChange = getNamedIntArg(splitted, 1, "m", 0);
            int wChange = getNamedIntArg(splitted, 1, "w", 0);
            int dChange = getNamedIntArg(splitted, 1, "d", 0);
            int hChange = getNamedIntArg(splitted, 1, "h", 0);
            int iChange = getNamedIntArg(splitted, 1, "i", 0);
            int gReason = getNamedIntArg(splitted, 1, "r", 7);

            String reason = player.getName() + " tempbanned " + splitted[1] + ": " + originalReason;

            if (gReason > 14) {
                mc.dropMessage("You have entered an incorrect ban reason ID, please try again.");
                return;
            }

            DateFormat df = DateFormat.getInstance();
            tempB.set(tempB.get(Calendar.YEAR) + yChange, tempB.get(Calendar.MONTH) + mChange,
                    tempB.get(Calendar.DATE) + (wChange * 7) + dChange, tempB.get(Calendar.HOUR_OF_DAY) + hChange,
                    tempB.get(Calendar.MINUTE) + iChange);

            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);

            if (victim == null) {
                int accId = MapleClient.findAccIdForCharacterName(splitted[1]);
                if (accId >= 0 && MapleCharacter.tempban(reason, tempB, gReason, accId)) {
                    String readableTargetName = MapleCharacterUtil.makeMapleReadable(victim.getName());
                    cserv.broadcastPacket(MaplePacketCreator.serverNotice(6,
                            readableTargetName + " has been banned for " + originalReason));

                } else {
                    mc.dropMessage("There was a problem offline banning character " + splitted[1] + ".");
                }
            } else {
                victim.tempban(reason, tempB, gReason);
                mc.dropMessage("The character " + splitted[1] + " has been successfully tempbanned till "
                        + df.format(tempB.getTime()));
            }
        } else if (splitted[0].equals("!search")) {
            if (splitted.length > 2) {
                String type = splitted[1];
                String search = StringUtil.joinStringFrom(splitted, 2);
                MapleData data = null;
                MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String"));
                mc.dropMessage("<<Type: " + type + " | Search: " + search + ">>");
                if (type.equalsIgnoreCase("NPC") || type.equalsIgnoreCase("NPCS")) {
                    List<String> retNpcs = new ArrayList<String>();
                    data = dataProvider.getData("Npc.img");
                    List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData npcIdData : data.getChildren()) {
                        int npcIdFromData = Integer.parseInt(npcIdData.getName());
                        String npcNameFromData = MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME");
                        npcPairList.add(new Pair<Integer, String>(npcIdFromData, npcNameFromData));
                    }
                    for (Pair<Integer, String> npcPair : npcPairList) {
                        if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retNpcs.add(npcPair.getLeft() + " - " + npcPair.getRight());
                        }
                    }
                    if (retNpcs != null && retNpcs.size() > 0) {
                        for (String singleRetNpc : retNpcs) {
                            mc.dropMessage(singleRetNpc);
                        }
                    } else {
                        mc.dropMessage("No NPC's Found");
                    }
                } else if (type.equalsIgnoreCase("MAP") || type.equalsIgnoreCase("MAPS")) {
                    List<String> retMaps = new ArrayList<String>();
                    data = dataProvider.getData("Map.img");
                    List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mapAreaData : data.getChildren()) {
                        for (MapleData mapIdData : mapAreaData.getChildren()) {
                            int mapIdFromData = Integer.parseInt(mapIdData.getName());
                            String mapNameFromData = MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME");
                            mapPairList.add(new Pair<Integer, String>(mapIdFromData, mapNameFromData));
                        }
                    }
                    for (Pair<Integer, String> mapPair : mapPairList) {
                        if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMaps.add(mapPair.getLeft() + " - " + mapPair.getRight());
                        }
                    }
                    if (retMaps != null && retMaps.size() > 0) {
                        for (String singleRetMap : retMaps) {
                            mc.dropMessage(singleRetMap);
                        }
                    } else {
                        mc.dropMessage("No Maps Found");
                    }
                } else if (type.equalsIgnoreCase("MOB") || type.equalsIgnoreCase("MOBS") || type.equalsIgnoreCase("MONSTER") || type.equalsIgnoreCase("MONSTERS")) {
                    List<String> retMobs = new ArrayList<String>();
                    data = dataProvider.getData("Mob.img");
                    List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mobIdData : data.getChildren()) {
                        int mobIdFromData = Integer.parseInt(mobIdData.getName());
                        String mobNameFromData = MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME");
                        mobPairList.add(new Pair<Integer, String>(mobIdFromData, mobNameFromData));
                    }
                    for (Pair<Integer, String> mobPair : mobPairList) {
                        if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMobs.add(mobPair.getLeft() + " - " + mobPair.getRight());
                        }
                    }
                    if (retMobs != null && retMobs.size() > 0) {
                        for (String singleRetMob : retMobs) {
                            mc.dropMessage(singleRetMob);
                        }
                    } else {
                        mc.dropMessage("No Mob's Found");
                    }
                } else if (type.equalsIgnoreCase("REACTOR") || type.equalsIgnoreCase("REACTORS")) {
                    mc.dropMessage("NOT ADDED YET");
                } else if (type.equalsIgnoreCase("ITEM") || type.equalsIgnoreCase("ITEMS")) {
                    List<String> retItems = new ArrayList<String>();
                    for (Pair<Integer, String> itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (itemPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retItems.add(itemPair.getLeft() + " - " + itemPair.getRight());
                        }
                    }
                    if (retItems != null && retItems.size() > 0) {
                        for (String singleRetItem : retItems) {
                            mc.dropMessage(singleRetItem);
                        }
                    } else {
                        mc.dropMessage("No Item's Found");
                    }
                } else if (type.equalsIgnoreCase("SKILL") || type.equalsIgnoreCase("SKILLS")) {
                    List<String> retSkills = new ArrayList<String>();
                    data = dataProvider.getData("Skill.img");
                    List<Pair<Integer, String>> skillPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData skillIdData : data.getChildren()) {
                        int skillIdFromData = Integer.parseInt(skillIdData.getName());
                        String skillNameFromData = MapleDataTool.getString(skillIdData.getChildByPath("name"),
                                "NO-NAME");
                        skillPairList.add(new Pair<Integer, String>(skillIdFromData, skillNameFromData));
                    }
                    for (Pair<Integer, String> skillPair : skillPairList) {
                        if (skillPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retSkills.add(skillPair.getLeft() + " - " + skillPair.getRight());
                        }
                    }
                    if (retSkills != null && retSkills.size() > 0) {
                        for (String singleRetSkill : retSkills) {
                            mc.dropMessage(singleRetSkill);
                        }
                    } else {
                        mc.dropMessage("No Skills Found");
                    }
                } else {
                    mc.dropMessage("Sorry, that search call is unavailable");
                }
            } else {
                mc.dropMessage("Invalid search.  Proper usage: '!search <type> <search for>', where <type> is MAP, USE, ETC, CASH, EQUIP, MOB (or MONSTER), or SKILL.");
            }
        } else if (splitted[0].equals("!msearch")) {
            try {
                URL url;
                URLConnection urlConn;

                BufferedReader dis;

                String replaced;
                if (splitted.length > 1) {
                    replaced = StringUtil.joinStringFrom(splitted, 1).replace(' ', '%');
                } else {
                    mc.dropMessage("Syntax: !search item name/map name/monster name");
                    return;
                }

                url = new URL("http://www.mapletip.com/search_java.php?search_value=" + replaced + "&check=true");
                urlConn = url.openConnection();
                urlConn.setDoInput(true);
                urlConn.setUseCaches(false);
                dis = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String s;

                while ((s = dis.readLine()) != null) {
                    mc.dropMessage(s);
                }
                mc.dropMessage("Search for " + '"' + replaced.replace('%', ' ') + '"' + " was completed.");
                dis.close();
            } catch (MalformedURLException mue) {
                mc.dropMessage("Malformed URL Exception: " + mue.toString());
            } catch (IOException ioe) {
                mc.dropMessage("IO Exception: " + ioe.toString());
            } catch (Exception e) {
                mc.dropMessage("General Exception: " + e.toString());
            }
        } else if (splitted[0].equals("!npc")) {
            int npcId;
            try {
                npcId = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                return;
            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
                npc.setPosition(player.getPosition());
                npc.setCy(player.getPosition().y);
                npc.setRx0(player.getPosition().x + 50);
                npc.setRx1(player.getPosition().x - 50);
                npc.setFh(player.getMap().getFootholds().findBelow(player.getPosition()).getId());
                npc.setCustom(true);
                player.getMap().addMapObject(npc);
                player.getMap().broadcastMessage(NpcPacket.spawnNPC(npc));
            } else {
                mc.dropMessage("You have entered an invalid Npc-Id");
            }
        } else if (splitted[0].equals("!removenpcs")) {
            List<MapleMapObject> npcs = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.NPC));
            for (MapleMapObject npcmo : npcs) {
                MapleNPC npc = (MapleNPC) npcmo;
                if (npc.isCustom()) {
                    player.getMap().removeMapObject(npc.getObjectId());
                }
            }
        } else if (splitted[0].equals("!mynpcpos")) {
            Point pos = player.getPosition();
            mc.dropMessage("X: " + pos.x + " | Y: " + pos.y + "  | RX0: " + (pos.x + 50) + " | RX1: " + (pos.x - 50) + " | FH: " + player.getMap().getFootholds().findBelow(pos).getId());
        } else if (splitted[0].equals("!cleardrops")) {
            MapleMap map = player.getMap();
            double range = Double.POSITIVE_INFINITY;
            java.util.List<MapleMapObject> items = map.getMapObjectsInRange(player.getPosition(), range, Arrays.asList(MapleMapObjectType.ITEM));
            for (MapleMapObject itemmo : items) {
                map.removeMapObject(itemmo);
                map.broadcastMessage(MaplePacketCreator.removeItemFromMap(itemmo.getObjectId(), 0, player.getId()));
            }
            mc.dropMessage("You have destroyed " + items.size() + " items on the ground.");
        } else if (splitted[0].equals("!clearshops")) {
            MapleShopFactory.getInstance().clear();
        } else if (splitted[0].equals("!clearevents")) {
            for (ChannelServer instance : ChannelServer.getAllInstances()) {
                instance.reloadEvents();
            }
        } else if (splitted[0].equals("!permban")) {
            String name = splitted[1];
            String reason = StringUtil.joinStringFrom(splitted, 2);
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                if (!victim.isGM()) {
                    victim.ban(reason, true);
                    mc.dropMessage("Character permanently banned!");
                } else {
                    mc.dropMessage("You can't ban a GM. Sorry");
                }
            } else {
                if (MapleCharacter.ban(name, reason, false)) {
                    mc.dropMessage("Permanently banned sucessfully");
                } else {
                    mc.dropMessage("Error while banning.");
                }
            }
        } else if (splitted[0].equals("!emote")) {
            String name = splitted[1];
            int emote;
            try {
                emote = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException nfe) {
                return;
            }
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(name);
            if (victim != null) {
                victim.getMap().broadcastMessage(victim, CField.UserPool.Emotion.getEmotion(victim.getId(), emote),
                        victim.getPosition());
            } else {
                mc.dropMessage("Player was not found");
            }
        } else if (splitted[0].equals("!removeoid")) {
            if (splitted.length == 2) {
                MapleMap map = c.getPlayer().getMap();
                int oid = Integer.parseInt(splitted[1]);
                MapleMapObject obj = map.getMapObject(oid);
                if (obj == null) {
                    mc.dropMessage("This oid does not exist.");
                } else {
                    map.removeMapObject(obj);
                }
            }
        } else if (splitted[0].equals("!gmtext")) {
            int text;
            // RegularChat
            if (splitted[1].equalsIgnoreCase("normal")) {
                text = 0;
                // MultiChat
            } else if (splitted[1].equalsIgnoreCase("orange")) {
                text = 1;
            } else if (splitted[1].equalsIgnoreCase("pink")) {
                text = 2;
            } else if (splitted[1].equalsIgnoreCase("purple")) {
                text = 3;
            } else if (splitted[1].equalsIgnoreCase("green")) {
                text = 4;
                // ServerNotice
            } else if (splitted[1].equalsIgnoreCase("red")) {
                text = 5;
            } else if (splitted[1].equalsIgnoreCase("blue")) {
                text = 6;
                // RegularChat
            } else if (splitted[1].equalsIgnoreCase("whitebg")) {
                text = 7;
                // Whisper
            } else if (splitted[1].equalsIgnoreCase("lightinggreen")) {
                text = 8;
                // MapleTip
            } else if (splitted[1].equalsIgnoreCase("yellow")) {
                text = 9;
            } else {
                mc.dropMessage("Wrong syntax: use !gmtext normal/orange/pink/purple/green/blue/red/whitebg/lightinggreen/yellow");
                return;
            }

            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET gmtext = ? WHERE name = ?");
            ps.setString(2, player.getName());
            ps.setInt(1, text);
            ps.executeUpdate();
            ps.close();
            player.setGMText(text);
        } else if (splitted[0].equals("!maxmesos")) {
            player.gainMeso(Integer.MAX_VALUE - player.getMeso());
        } else if (splitted[0].equals("!youlose")) {
            for (MapleCharacter victim : player.getMap().getCharacters()) {
                if (victim != null) {
                    if (victim.getHp() <= 0) {
                        victim.dropMessage("You have lost the event.");
                        victim.changeMap(100000000);
                    } else {
                        victim.setHp(victim.getCurrentMaxHp());
                        victim.updateSingleStat(MapleStat.HP, victim.getHp());
                        victim.setMp(victim.getCurrentMaxMp());
                        victim.updateSingleStat(MapleStat.MP, victim.getMp());
                    }
                }
            }
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
            new CommandDefinition("go", 3),
            new CommandDefinition("goto", 3),
            new CommandDefinition("sp", 3),
            new CommandDefinition("map", 3),
            new CommandDefinition("ap", 3),
            new CommandDefinition("job", 3),
            new CommandDefinition("whereami", 3),
            new CommandDefinition("openshop", 3),
            new CommandDefinition("opennpc", 3),
            new CommandDefinition("levelup", 3),
            new CommandDefinition("mp", 3),
            new CommandDefinition("hp", 3),
            new CommandDefinition("healall", 3),
            new CommandDefinition("item", 3),
            new CommandDefinition("dropmesos", 3),
            new CommandDefinition("level", 3),
            new CommandDefinition("online", 3),
            new CommandDefinition("banreason", 3),
            new CommandDefinition("joinguild", 3),
            new CommandDefinition("unbuffall", 3),
            new CommandDefinition("mesos", 3),
            new CommandDefinition("clearslot", 3),
            new CommandDefinition("resetreactors", 3),
            new CommandDefinition("balrog", 3),
            new CommandDefinition("killall", 3),
            new CommandDefinition("help", 3),
            new CommandDefinition("say", 3),
            new CommandDefinition("spyon", 3),
            new CommandDefinition("setstats", 3),
            new CommandDefinition("giftnx", 3),
            new CommandDefinition("maxskills", 3),
            new CommandDefinition("fame", 3),
            new CommandDefinition("unhide", 3),
            new CommandDefinition("heal", 3),
            new CommandDefinition("unbuff", 3),
            new CommandDefinition("mute", 3),
            new CommandDefinition("givedisease", 3),
            new CommandDefinition("dc", 3),
            new CommandDefinition("charinfo", 3),
            new CommandDefinition("connected", 3),
            new CommandDefinition("warp", 3),
            new CommandDefinition("warphere", 3),
            new CommandDefinition("warpallhere", 3),
            new CommandDefinition("warpwholeworld", 3),
            new CommandDefinition("cheaters", 3),
            new CommandDefinition("createring", 3),
            new CommandDefinition("removering", 3),
            new CommandDefinition("nearestportal", 3),
            new CommandDefinition("portal", 3),
            new CommandDefinition("unban", 3),
            new CommandDefinition("spawn", 3),
            new CommandDefinition("ban", 3),
            new CommandDefinition("tempban", 3),
            new CommandDefinition("search", 3),
            new CommandDefinition("msearch", 3),
            new CommandDefinition("npc", 3),
            new CommandDefinition("removenpcs", 3),
            new CommandDefinition("mynpcpos", 3),
            new CommandDefinition("cleardrops", 3),
            new CommandDefinition("clearshops", 3),
            new CommandDefinition("clearevents", 3),
            new CommandDefinition("permban", 3),
            new CommandDefinition("emote", 3),
            new CommandDefinition("removeoid", 3),
            new CommandDefinition("gmtext", 3),
            new CommandDefinition("maxmesos", 3),
            new CommandDefinition("youlose", 3),};
    }
}
