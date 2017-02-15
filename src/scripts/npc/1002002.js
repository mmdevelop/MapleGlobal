/* 
** NPC Name: Pason
** Location: Lith Harbor
** Purpose: Warper between Lith and Florina
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
	else
	{
		status--;
	}
	
	if (status == 0)
	{
		cm.sendSimple("Have you heard of the beach with a spectacular view of the ocean called #r#m110000000##k, located near #b#m104000000##k? I can take you there right now for #b1,500 mesos#k.\r\n#b#L0##m110000000#(1,500 mesos)#l");
	}
	
	else if (status == 1)
	{
		if (cm.getMeso < 1500)
		{
			cm.sendOk("I think you're lacking mesos. There are many ways to gather up some money, you know, like... selling your armor... defeating monsters... doing quests... you know what I'm talking about.");
			cm.dispose();
		}
		else
		{
			cm.gainMeso(-1500);
            cm.getPlayer().changeMap(110000000);
			cm.dispose();
		}
	}
}