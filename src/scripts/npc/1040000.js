/* 
** NPC Name: Luke
** Location: Henesys Dungeon Entrance
** Purpose: Quest
** Made by: Kyushen
** Luke dialogue
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var status;
var YNMode = 0;

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
	else if (mode == 0 && YNMode == 1)
	{
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
		cm.sendOk("Okay, who just woke me up?? I hate anyone that wakes me up from my nap … huh? What am I doing? What do you think I'm doing? Of course I'm guarding the entrance!! This is the entrance to the #bVictoria Island : Center Dungeon#k. You have to be careful in there; the monsters you've faced don't even compare to the ones you're about to face in here. I suggest you don't go in there unless you can protect yourself. Okay, nap time!");
		cm.dispose();
	}
	
	else
	{
		cm.dispose();
	}
}