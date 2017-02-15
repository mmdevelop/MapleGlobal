package net.sf.odinms.client.messages.commands;

import java.rmi.RemoteException;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

public class PlayerCommands implements Command
{
    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception
	{
        splitted[0] = splitted[0].toLowerCase();
        MapleCharacter player = c.getPlayer();
		// These commands are staying here temporarily and soon will be removed. Their purpose is to help alleviate possible issues in CBT.
		// If you want to get rid of these commands (which arguably I find useful) go right ahead, I don't vote against it.
		if (splitted[0].equals("@save"))
		{
            if (!player.getCheatTracker().Spam(900000, 0))
			{ // 15 minutes
                player.saveToDB(true, true);
                mc.dropMessage("Your progress has been saved.");
            }
			else
			{
                mc.dropMessage("You cannot save more than once every 15 minutes.");
            }
        }
		
		else if (splitted[0].equals("@dispose"))
		{
            NPCScriptManager.getInstance().dispose(c);
            mc.dropMessage("You have successfully been disposed.");
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
            new CommandDefinition("save", 0),
            new CommandDefinition("dispose", 0),
        };
    }
}