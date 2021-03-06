package net.sf.odinms.client.messages.commands;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.ExternalCodeTableGetter;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.net.RecvPacketOpcode;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.handler.ChangeChannelHandler;
import net.sf.odinms.scripting.portal.PortalScriptManager;
import net.sf.odinms.scripting.reactor.ReactorScriptManager;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.ShutdownServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.maps.MapleReactor;
import net.sf.odinms.server.maps.MapleMapItem;
import net.sf.odinms.server.maps.MapleReactorFactory;
import net.sf.odinms.server.maps.MapleReactorStats;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.tools.performance.CPUSampler;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.tools.NpcPacket;

public class Admins implements Command
{
    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception
	{
        splitted[0] = splitted[0].toLowerCase();
        MapleCharacter player = c.getPlayer();
        ChannelServer cserv = c.getChannelServer();
        if (splitted[0].equals("!speakall"))
		{
            String text = StringUtil.joinStringFrom(splitted, 1);
            for (MapleCharacter mch : player.getMap().getCharacters())
			{
                mch.getMap().broadcastMessage(CField.UserPool.Chat.sendMessage(mch.getId(), text));
            }
        }
		
		else if (splitted[0].equals("!dcall"))
		{
            for (ChannelServer channel : ChannelServer.getAllInstances())
			{
                for (MapleCharacter cplayer : channel.getPlayerStorage().getAllCharacters())
				{
                    if (cplayer != player)
					{
                        cplayer.getClient().disconnect();
                        cplayer.getClient().getSession().close();
                    }
                }
            }
        }
		
		else if (splitted[0].equals("!packet"))
		{
            if (splitted.length > 1)
			{
                c.getSession().write(MaplePacketCreator.sendPacket(StringUtil.joinStringFrom(splitted, 1)));
            }
			else
			{
                mc.dropMessage("Please enter packet data");
            }
        }
		
		else if (splitted[0].equals("!drop"))
		{
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int itemId = Integer.parseInt(splitted[1]);
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            IItem toDrop;
            if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP)
			{
                toDrop = ii.getEquipById(itemId);
            }
			else
			{
                toDrop = new Item(itemId, (byte) 0, quantity);
            }
            player.getMap().spawnItemDrop(player, player, toDrop, player.getPosition(), true, true);
        }
		
		else if (splitted[0].equals("!startprofiling"))
		{
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("net.sf.odinms");
            sampler.start();
        }
		
		else if (splitted[0].equals("!stopprofiling"))
		{
            CPUSampler sampler = CPUSampler.getInstance();
            try
			{
                String filename = "odinprofile.txt";
                if (splitted.length > 1)
				{
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists())
				{
                    file.delete();
                }
                sampler.stop();
                FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            }
			catch (IOException e)
			{
				// wat? do nothing?
            }
            sampler.reset();
        }
		
		else if (splitted[0].equals("!reloadops"))
		{
            try
			{
                ExternalCodeTableGetter.populateValues(SendPacketOpcode.getDefaultProperties(), SendPacketOpcode.values());
                ExternalCodeTableGetter.populateValues(RecvPacketOpcode.getDefaultProperties(), RecvPacketOpcode.values());
            }
			catch (Exception e)
			{
				// do nothing? wat?
            }
            PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER).reset(PacketProcessor.Mode.CHANNELSERVER);
            PacketProcessor.getProcessor(PacketProcessor.Mode.CHANNELSERVER).reset(PacketProcessor.Mode.CHANNELSERVER);
        }
		
	else if (splitted[0].equals("!shutdown"))
	{
            int time = 60000;
            if (splitted.length > 1)
            {
                time = Integer.parseInt(splitted[1]) * 60000;
            }
            CommandProcessor.forcePersisting();
            c.getChannelServer().shutdown(time);
        }
		
		else if (splitted[0].equals("!shutdownworld"))
		{
            int time = 60000;
            if (splitted.length > 1)
			{
                time = Integer.parseInt(splitted[1]) * 60000;
            }
            CommandProcessor.forcePersisting();
            c.getChannelServer().shutdownWorld(time);
        }
		
		else if (splitted[0].equals("!shutdownnow"))
		{
            CommandProcessor.forcePersisting();
            new ShutdownServer(c.getChannel()).run();
        }
		
		else if (splitted[0].equals("!mesoperson"))
		{
            int mesos;
            try
			{
                mesos = Integer.parseInt(splitted[2]);
            }
			catch (NumberFormatException blackness)
			{
                return;
            }
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null)
			{
                victim.gainMeso(mesos, true, true, true);
            }
			else
			{
                mc.dropMessage("Player was not found");
            }
        }
		
		else if (splitted[0].equals("!gmperson"))
		{
            if (splitted.length == 3)
			{
                MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                if (victim != null)
				{
                    int level;
                    try
					{
                        level = Integer.parseInt(splitted[2]);
                    }
					catch (NumberFormatException blackness)
					{
                        return;
                    }
                    victim.setGM(level);
                    if (victim.isGM())
					{
                        victim.dropMessage(5, "You now have level " + level + " GM powers.");
                    }
                }
				else
				{
                    mc.dropMessage("The player " + splitted[1] + " is either offline or not in this channel");
                }
            }
        }
		
		else if (splitted[0].equals("!kill"))
		{
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null)
			{
                victim.setHp(0);
                victim.setMp(0);
                victim.updateSingleStat(MapleStat.HP, 0);
                victim.updateSingleStat(MapleStat.MP, 0);
            }
			else
			{
                mc.dropMessage("Player not found");
            }
        }
		
		else if (splitted[0].equals("!jobperson"))
		{
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            int job;
            try
			{
                job = Integer.parseInt(splitted[2]);
            }
			catch (NumberFormatException blackness)
			{
                return;
            }
            if (victim != null)
			{
                victim.setJob(job);
            }
			else
			{
                mc.dropMessage("Player not found");
            }
        }
		
		else if (splitted[0].equals("!spawndebug"))
		{
            player.getMap().spawnDebug(mc);
        }
		
		else if (splitted[0].equals("!timerdebug"))
		{
            TimerManager.getInstance().dropDebugInfo(mc);
        }
		
		else if (splitted[0].equals("!threads"))
		{
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            String filter = "";
            if (splitted.length > 1)
			{
                filter = splitted[1];
            }
            for (int i = 0; i < threads.length; i++)
			{
                String tstring = threads[i].toString();
                if (tstring.toLowerCase().indexOf(filter.toLowerCase()) > -1)
				{
                    mc.dropMessage(i + ": " + tstring);
                }
            }
        }
		
		else if (splitted[0].equals("!showtrace"))
		{
            Thread[] threads = new Thread[Thread.activeCount()];
            Thread.enumerate(threads);
            Thread t = threads[Integer.parseInt(splitted[1])];
            mc.dropMessage(t.toString() + ":");
            for (StackTraceElement elem : t.getStackTrace())
			{
                mc.dropMessage(elem.toString());
            }

        }
		
		else if (splitted[0].equals("!shopitem"))
		{
            if (splitted.length < 5)
			{
                mc.dropMessage("!shopitem <shopid> <itemid> <price> <position>");
            }
			else
			{
                try
				{
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO shopitems (shopid, itemid, price, position) VALUES (" + Integer.parseInt(splitted[1]) + ", " + Integer.parseInt(splitted[2]) + ", " + Integer.parseInt(splitted[3]) + ", " + Integer.parseInt(splitted[4]) + ");");
                    ps.executeUpdate();
                    ps.close();
                    con.close();
                    MapleShopFactory.getInstance().clear();
                    mc.dropMessage("Done adding shop item.");
                }
				catch (SQLException e)
				{
                    mc.dropMessage("Something wrong happened.");
                }
            }

        }
		
		else if (splitted[0].equals("!pnpc"))
		{
            int npcId = Integer.parseInt(splitted[1]);
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            int xpos = player.getPosition().x;
            int ypos = player.getPosition().y;
            int fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();
            if (npc != null && !npc.getName().equals("MISSINGNO"))
			{
                npc.setPosition(player.getPosition());
                npc.setCy(ypos);
                npc.setRx0(xpos + 50);
                npc.setRx1(xpos - 50);
                npc.setFh(fh);
                npc.setCustom(true);
                try
				{
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    ps.setInt(1, npcId);
                    ps.setInt(2, 0);
                    ps.setInt(3, fh);
                    ps.setInt(4, ypos);
                    ps.setInt(4, ypos);
                    ps.setInt(5, xpos + 50);
                    ps.setInt(6, xpos - 50);
                    ps.setString(7, "n");
                    ps.setInt(8, xpos);
                    ps.setInt(9, ypos);
                    ps.setInt(10, player.getMapId());
                    ps.executeUpdate();
                    con.close();
                }
				catch (SQLException e)
				{
                    mc.dropMessage("Failed to save NPC to the database");
                }
                player.getMap().addMapObject(npc);
                player.getMap().broadcastMessage(NpcPacket.spawnNPC(npc));
            }
			else
			{
                mc.dropMessage("You have entered an invalid Npc-Id");
            }
        }
		
		else if (splitted[0].equals("!toggleoffense"))
		{
            try
			{
                CheatingOffense co = CheatingOffense.valueOf(splitted[1]);
                co.setEnabled(!co.isEnabled());
            }
			catch (IllegalArgumentException iae)
			{
                mc.dropMessage("Offense " + splitted[1] + " not found");
            }
        }
		
		else if (splitted[0].equals("!toggledrops"))
		{
            player.getMap().toggleDrops();
        }
		
		else if (splitted[0].equals("!givemonsbuff"))
		{
            int mask = 0;
            mask |= Integer.decode(splitted[1]);
            MobSkill skill = MobSkillFactory.getMobSkill(128, 1);
            c.getSession().write(MaplePacketCreator.applyMonsterStatusTest(Integer.valueOf(splitted[2]), mask, 0, skill, Integer.valueOf(splitted[3])));
        }
		
		else if (splitted[0].equals("!givemonstatus"))
		{
            int mask = 0;
            mask |= Integer.decode(splitted[1]);
            c.getSession().write(MaplePacketCreator.applyMonsterStatusTest2(Integer.valueOf(splitted[2]), mask, 1000, Integer.valueOf(splitted[3])));
        }
		
		else if (splitted[0].equals("!spawnreactor"))
		{
            MapleReactorStats reactorSt = MapleReactorFactory.getReactor(Integer.parseInt(splitted[1]));
            MapleReactor reactor = new MapleReactor(reactorSt, Integer.parseInt(splitted[1]));
            reactor.setDelay(-1);
            reactor.setPosition(player.getPosition());
            player.getMap().spawnReactor(reactor);
        }
		
		else if (splitted[0].equals("!hitreactor"))
		{
            player.getMap().getReactorByOid(Integer.parseInt(splitted[1])).hitReactor(c);
        }
		
		else if (splitted[0].equals("!loadreactor"))
		{
            MapleMap map = player.getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            for (MapleMapObject reactorL : reactors)
			{
                MapleReactor reactor2l = (MapleReactor) reactorL;
                mc.dropMessage("Reactor: oID: " + reactor2l.getObjectId() + " reactorID: " + reactor2l.getId() + " Position: " + reactor2l.getPosition().toString() + " State: " + reactor2l.getState());
            }
        }
		
		else if (splitted[0].equals("!destroyreactor"))
		{
            MapleMap map = player.getMap();
            List<MapleMapObject> reactors = map.getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.REACTOR));
            if (splitted[1].equalsIgnoreCase("all"))
			{
                for (MapleMapObject reactorL : reactors)
				{
                    MapleReactor reactor2l = (MapleReactor) reactorL;
                    player.getMap().destroyReactor(reactor2l.getObjectId());
                }
            }
			else
			{
                player.getMap().destroyReactor(Integer.parseInt(splitted[1]));
            }
        }
		
		else if (splitted[0].equals("!writecmds"))
		{
            CommandProcessor.getInstance().writeCommandList();
        }
		
		else if (splitted[0].equals("!saveall"))
		{
            for (ChannelServer chan : ChannelServer.getAllInstances())
			{
                for (MapleCharacter chr : chan.getPlayerStorage().getAllCharacters())
				{
                    chr.saveToDB(true, false);
                }
            }
            mc.dropMessage("Everyone has been successfully saved!");
        }
		
		else if (splitted[0].equals("!notice"))
		{
            int joinmod = 1;
            int range = -1;
            if (splitted[1].equalsIgnoreCase("m"))
			{
                range = 0;
            }
			else if (splitted[1].equalsIgnoreCase("c"))
			{
                range = 1;
            }
			else if (splitted[1].equalsIgnoreCase("w"))
			{
                range = 2;
            }
            int tfrom = 2;
            int type;
            if (range == -1)
			{
                range = 2;
                tfrom = 1;
            }
            if (splitted[tfrom].equalsIgnoreCase("n"))
			{
                type = 0;
            }
			else if (splitted[tfrom].equalsIgnoreCase("p"))
			{
                type = 1;
            }
			else if (splitted[tfrom].equalsIgnoreCase("l"))
			{
                type = 2;
            }
			else if (splitted[tfrom].equalsIgnoreCase("nv"))
			{
                type = 5;
            }
			else if (splitted[tfrom].equalsIgnoreCase("v"))
			{
                type = 5;
            }
			else if (splitted[tfrom].equalsIgnoreCase("b"))
			{
                type = 6;
            }
			else
			{
                type = 0;
                joinmod = 0;
            }
            String prefix = "";
            if (splitted[tfrom].equalsIgnoreCase("nv"))
			{
                prefix = "[Notice] ";
            }
            joinmod += tfrom;
            String outputMessage = StringUtil.joinStringFrom(splitted, joinmod);
            MaplePacket packet = MaplePacketCreator.serverNotice(type, prefix + outputMessage);
            if (range == 0)
			{
                player.getMap().broadcastMessage(packet);
            }
			else if (range == 1)
			{
                ChannelServer.getInstance(c.getChannel()).broadcastPacket(packet);
            }
			else if (range == 2)
			{
                try
				{
                    ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(player.getName(), packet.getBytes());
                }
				catch (RemoteException e)
				{
                    c.getChannelServer().reconnectWorld();
                }
            }
        }
		
		else if (splitted[0].equals("!speak"))
		{
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null)
			{
                String text = StringUtil.joinStringFrom(splitted, 2);
                victim.getMap().broadcastMessage(CField.UserPool.Chat.sendMessage(victim.getId(), text));
            }
			else
			{
                mc.dropMessage("Player not found");
            }
        }
		
		else if (splitted[0].equals("!cc"))
		{
            int channel;

            if (splitted.length == 3)
			{
                try
				{
                    channel = Integer.parseInt(splitted[2]);
                }
				catch (NumberFormatException blackness)
				{
                    return;
                }
                if (channel <= ChannelServer.getAllInstances().size() || channel < 0)
				{
                    String name = splitted[1];
                    try
					{
                        int vchannel = c.getChannelServer().getWorldInterface().find(name);
                        if (vchannel > -1)
						{
                            ChannelServer pserv = ChannelServer.getInstance(vchannel);
                            MapleCharacter victim = pserv.getPlayerStorage().getCharacterByName(name);
                            ChangeChannelHandler.changeChannel(channel, victim.getClient());
                        }
						else
						{
                            mc.dropMessage("Player not found");
                        }
                    }
					catch (RemoteException rawr)
					{
                        c.getChannelServer().reconnectWorld();
                    }
                }
				else
				{
                    mc.dropMessage("Channel not found.");
                }
            }
			else
			{
                try
				{
                    channel = Integer.parseInt(splitted[1]);
                }
				catch (NumberFormatException blackness)
				{
                    return;
                }
                if (channel <= ChannelServer.getAllInstances().size() || channel < 0)
				{
                    ChangeChannelHandler.changeChannel(channel, c);
                }
            }
        }
		
		else if (splitted[0].equals("!reloadguilds"))
		{
            try
			{
                mc.dropMessage("Attempting to reload all guilds... this may take a while...");
                cserv.getWorldInterface().clearGuilds();
                mc.dropMessage("Completed.");
            }
			catch (RemoteException re)
			{
                mc.dropMessage("RemoteException occurred while attempting to reload guilds.");
            }
        }
		
		else if (splitted[0].equals("!clearportalscripts"))
		{
            PortalScriptManager.getInstance().clearScripts();
        }
		
		else if (splitted[0].equals("!clearreactordrops"))
		{
            ReactorScriptManager.getInstance().clearDrops();
        }
		
		else if (splitted[0].equals("!monsterdebug"))
		{
            MapleMap map = player.getMap();
            double range = Double.POSITIVE_INFINITY;
            if (splitted.length > 1)
			{
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2)
				{
                    range = irange * irange;
                }
				else
				{
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            List<MapleMapObject> monsters = map.getMapObjectsInRange(player.getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            for (MapleMapObject monstermo : monsters)
			{
                MapleMonster monster = (MapleMonster) monstermo;
                mc.dropMessage("Monster " + monster.toString());
            }
        }
		
		else if (splitted[0].equals("!itemperson"))
		{
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            int item;
            try
			{
                item = Integer.parseInt(splitted[2]);
            }
			catch (NumberFormatException blackness)
			{
                return;
            }
            short quantity = (short) getOptionalIntArg(splitted, 3, 1);
            if (victim != null)
			{
                MapleInventoryManipulator.addById(victim.getClient(), item, quantity);
            }
			else
			{
                mc.dropMessage("Player not found");
            }
        }
		
		else if (splitted[0].equals("!setaccgm"))
		{
            int accountid;
            Connection con = DatabaseConnection.getConnection();
            try
			{
                PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
                ps.setString(1, splitted[1]);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
				{
                    accountid = rs.getInt("accountid");
                    ps.close();
                    ps = con.prepareStatement("UPDATE accounts SET gm = ? WHERE id = ?");
                    ps.setInt(1, 1);
                    ps.setInt(2, accountid);
                    ps.executeUpdate();
                }
				else
				{
                    mc.dropMessage("Player was not found in the database.");
                }
                ps.close();
                rs.close();
                con.close();
            }
			catch (SQLException se)
			{
				// do nothing lol
            }
        }
		
		else if (splitted[0].equals("!servercheck"))
		{
            try
			{
                cserv.getWorldInterface().broadcastMessage(null, MaplePacketCreator.serverNotice(1, "Server check will commence soon. Please log off to prevent any save-related issues!").getBytes());
            }
			catch (RemoteException asd)
			{
                cserv.reconnectWorld();
            }
        }
		
		else if (splitted[0].equals("!itemvac"))
		{
            List<MapleMapObject> items = player.getMap().getMapObjectsInRange(player.getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapleMapObjectType.ITEM));
            for (MapleMapObject item : items)
			{
                MapleMapItem mapItem = (MapleMapItem) item;
                if (mapItem.getMeso() > 0)
				{
                    player.gainMeso(mapItem.getMeso(), true);
                }
				else if (mapItem.getItem().getItemId() >= 5000000 && mapItem.getItem().getItemId() <= 5000100)
				{
                    int petId = MaplePet.createPet(mapItem.getItem().getItemId());
                    if (petId == -1)
					{
                        return;
                    }
                    MapleInventoryManipulator.addById(c, mapItem.getItem().getItemId(), mapItem.getItem().getQuantity(), null, petId);
                }
				else
				{
                    MapleInventoryManipulator.addFromDrop(c, mapItem.getItem(), true);
                }
                mapItem.setPickedUp(true);
                player.getMap().removeMapObject(item); // just incase ?
                player.getMap().broadcastMessage(MaplePacketCreator.removeItemFromMap(mapItem.getObjectId(), 2, player.getId()), mapItem.getPosition());
            }
        }
		
		else if (splitted[0].equals("!pmob"))
		{
            int npcId = Integer.parseInt(splitted[1]);
            int mobTime = Integer.parseInt(splitted[2]);
            int xpos = player.getPosition().x;
            int ypos = player.getPosition().y;
            int fh = player.getMap().getFootholds().findBelow(player.getPosition()).getId();
            if (splitted[2] == null)
			{
                mobTime = 0;
            }
            MapleMonster mob = MapleLifeFactory.getMonster(npcId);
            if (mob != null && !mob.getName().equals("MISSINGNO"))
			{
                mob.setPosition(player.getPosition());
                mob.setCy(ypos);
                mob.setRx0(xpos + 50);
                mob.setRx1(xpos - 50);
                mob.setFh(fh);
                try
				{
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("INSERT INTO spawns ( idd, f, fh, cy, rx0, rx1, type, x, y, mid, mobtime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    ps.setInt(1, npcId);
                    ps.setInt(2, 0);
                    ps.setInt(3, fh);
                    ps.setInt(4, ypos);
                    ps.setInt(5, xpos + 50);
                    ps.setInt(6, xpos - 50);
                    ps.setString(7, "m");
                    ps.setInt(8, xpos);
                    ps.setInt(9, ypos);
                    ps.setInt(10, player.getMapId());
                    ps.setInt(11, mobTime);
                    ps.executeUpdate();
                    ps.close();
                    con.close();
                }
				catch (SQLException e)
				{
                    mc.dropMessage("Failed to save MOB to the database");
                }
                player.getMap().addMonsterSpawn(mob, mobTime);
            }
			else
			{
                mc.dropMessage("You have entered an invalid Npc-Id");
            }
        }
    }

    @Override
    public CommandDefinition[] getDefinition()
	{
        return new CommandDefinition[]
		{
            new CommandDefinition("speakall", 4),
            new CommandDefinition("dcall", 4),
            new CommandDefinition("packet", 4),
            new CommandDefinition("drop", 4),
            new CommandDefinition("startprofiling", 4),
            new CommandDefinition("stopprofiling", 4),
            new CommandDefinition("reloadops", 4),
            new CommandDefinition("shutdown", 4),
            new CommandDefinition("shutdownworld", 4),
            new CommandDefinition("shutdownnow", 4),
            new CommandDefinition("mesoperson", 4),
            new CommandDefinition("gmperson", 4),
            new CommandDefinition("kill", 4),
            new CommandDefinition("jobperson", 4),
            new CommandDefinition("spawndebug", 4),
            new CommandDefinition("timerdebug", 4),
            new CommandDefinition("threads", 4),
            new CommandDefinition("showtrace", 4),
            new CommandDefinition("toggleoffense", 4),
            new CommandDefinition("toggledrops", 4),
            new CommandDefinition("givemonsbuff", 4),
            new CommandDefinition("givemonstatus", 4),
            new CommandDefinition("spawnreactor", 4),
            new CommandDefinition("hitreactor", 4),
			new CommandDefinition("loadreactor", 4),
            new CommandDefinition("destroyreactor", 4),
            new CommandDefinition("writecmds", 4),
            new CommandDefinition("saveall", 4),
            new CommandDefinition("notice", 4),
            new CommandDefinition("speak", 4),
            new CommandDefinition("cc", 4),
            new CommandDefinition("reloadguilds", 4),
            new CommandDefinition("clearportalscripts", 4),
            new CommandDefinition("shopitem", 4),
            new CommandDefinition("clearreactordrops", 4),
            new CommandDefinition("monsterdebug", 4),
            new CommandDefinition("itemperson", 4),
            new CommandDefinition("setaccgm", 4),
            new CommandDefinition("pnpc", 4),
            new CommandDefinition("servercheck", 4),
            new CommandDefinition("itemvac", 4),
            new CommandDefinition("pmob", 4)
        };
    }
}