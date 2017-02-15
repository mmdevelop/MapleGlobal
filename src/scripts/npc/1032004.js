/* 
** NPC Name: Louis
** Location: The Forest of Patience
** Purpose: Quest
** Made by: Kyushen
** Louis dialogue for The Forest of Patience
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
	else if (mode == 0)
	{
		cm.sendOk("Isn't it awful that you have to restart the whole thing? Keep trying... the more you explore, the better you will know this whole place. Soon you'll be able to walk around here with your eyes closed hehe.");
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
		cm.sendYesNo("Do you want to get out of here? Well... this place can really wear you down... I'm used to it, I'm fine. Anyway, remember that if you leave here through me, you will have to start over again. Still want to go?");
	}
	
	else if (status == 1)
	{
		cm.getPlayer().changeMap(101000000);
		cm.dispose();
	}
	
	else
	{
		cm.dispose();
	}
}