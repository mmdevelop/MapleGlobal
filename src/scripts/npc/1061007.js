/* 
** NPC Name: Crumbling Statue
** Location: 10504031(0-6) || Sleepywood - Forest of Patience 1-7
** Purpose: Warper to Sleepywood from JQ
** Made by: wackyracer
** FULLY GMS-like speech
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
	else if (mode == 0 && status == 0)
	{
		cm.sendOk("When I take my hand from the statue, it stands idle, as if nothing had happened.");
		cm.dispose();
		return;
	}
	else
	{ 
        status--;
    }
	
	if (status == 0)
	{
    	cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Is it okay to go back to #m105040300#?");
    }
	
	else if (status == 1)
	{
		if (mode == 1)
		{
			cm.getPlayer().changeMap(105040300);
			cm.dispose();
		}
    }
	
	else
	{
		cm.dispose();
	}
}