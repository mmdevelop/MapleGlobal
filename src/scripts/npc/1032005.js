/* 
** NPC Name: VIP Cab
** Location: Ellinia
** Purpose: Travel NPC to Ant Tunnel Park
** Made by: wackyracer / Ixeb
** Fully GMS-Like Speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var status;

function start()
{
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection)
{
	if (mode == 1)
	{
		status++;
	}
	else if (mode == -1)
	{
		status = -1;
		cm.dispose();
		return;
    }
	else if (status == 1 && mode == 0)
	{
		cm.sendOk("This town also has a lot to offer. Find us if and when you feel the need to go to the Ant Tunnel Park.");
		cm.dispose();
		return;
	}
	else
	{
		status--;
	}
	
	if (status == 0)
	{
		cm.sendNext("Hi there! This cab is for VIP customers only. Instead of just taking you to different towns like the regular cabs, we offer a much better service worthy of VIP class. It's a bit pricey, but... for only 10,000 mesos, we'll take you safely to #bAnt Tunnel#k.");
	}
	
	else if (status == 1)
	{
		if (cm.getJob() == 0)
		{
			cm.sendYesNo("There's a special 90% discount for all beginners. Ant Tunnel is located deep inside in the dungeon that's at the center of the Victoria Island, where the 24 Hr Mobile Store is. Would you like to go there for #b1,000 mesos#k?");
		}
		else
		{
			cm.sendYesNo("Ant Tunnel is located deep inside in the dungeon that's at the center of the Victoria Island, where the 24 Hr Mobile Store is. Would you like to go there for #b10,000 mesos#k?");
		}
	}
	
	else if (status == 2)
	{
		if (mode == 1)
		{
			if (cm.getJob() == 0 && cm.getMeso() < 1000)
			{
				cm.sendOk("It looks like you don't have enough mesos. Sorry but you won't be able to use this without it.");
				cm.dispose();
			}
			else if (cm.getJob() == 0 && cm.getMeso() >= 1000)
			{
				cm.gainMeso(-1000);
                cm.getPlayer().changeMap(105070001);
				cm.dispose();
			}
			else if (cm.getJob() != 0 && cm.getMeso() < (10000))
			{
				cm.sendOk("It looks like you don't have enough mesos. Sorry but you won't be able to use this without it.");
				cm.dispose();
			}
			else if (cm.getJob() != 0 && cm.getMeso() >= (10000))
			{
				cm.gainMeso(-10000);
                cm.getPlayer().changeMap(105070001);
				cm.dispose();
			}
			else
			{
				cm.dispose();
			}
		}
		else
		{
			cm.sendOk("This town also has a lot to offer. Find us if and when you feel the need to go to the Ant Tunnel Park.");
			cm.dispose();
		}
	}
	else
	{
		cm.dispose();
	}
}