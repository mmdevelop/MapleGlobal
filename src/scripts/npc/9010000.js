/* 
** NPC Name: Maple Administrator
** Location: Town Zones
** Purpose: Simple phrase for now, is usually in charge of certain events.
** Made by: wackyracer / Joren McGrew
** FULLY GMS-like speech
*/

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
	else if (mode == 0 && status == 0)
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
		if (cm.getLevel() >= 15 && cm.getLevel() <= 25)
		{
			cm.sendYesNo("Hi, welcome to MapleGlobal's Closed Beta test server! Congratulations on achieving level 15 here! To help you test other stuff out faster, I can level you up once (#bup to level 25#k) for free if you wish. Would you like that?");
		}
		else
		{
			cm.sendOk("Only users of level 15~25 can level up through this NPC.");
			cm.dispose();
		}
	}
	
	else if (status == 1)
	{
		if (cm.getLevel() >= 15 && cm.getLevel() <= 25 && mode == 1)
		{
			cm.gainExp(999999999);
			cm.setExp(0);
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
}