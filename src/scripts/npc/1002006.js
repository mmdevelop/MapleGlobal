/* 
** NPC Name: Chef
** Location: Lith Harbor
** Purpose: Event Reward Giver
** Made by: wackyracer / Joren McGrew
** Fully GMS-like speech
*/

// TODO: Change this NPC when we start hosting this event

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
		cm.sendSimple("What are you here for? Are you here because of my ravishing good looks? \r\n#L0##bI have the Secret Scroll#k#l\r\n#L1##bNevermind...#k#l");
	}
	
	else if (status == 1)
	{
		if (selection == 0)
		{
			if (cm.haveItem(4031048, 1, false, true))
			{
				cm.sendOk("Huh? You actually have the Secret Scroll?... Interesting. You either got it from a GM or you're hacking/glitching/exploiting the system, because this feature hasn't been scripted yet, due to it's lack of importance compared to other scripts.\r\n#rSincerely, wackyracer :)#k.")
				cm.dispose();
			}
			else
			{
				cm.sendOk("Hey! You don't have the #bSecret Scroll#k. Don't lie to me!")
				cm.dispose();
			}
		}
		else
		{
			cm.dispose();
		}
	}
	
	else
	{
		cm.dispose();
	}
}