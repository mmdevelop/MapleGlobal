/* 
** NPC Name: Mai
** Location: Amherst
** Purpose: Simple phrase for now, but supposed to be a quest but is missing data for it to be able to work
** Made by: wackyracer / Ixeb
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
	else
	{
		status--;
	}
	
	if (status == 0)
	{
		cm.sendOk("You can be much stronger, if you get to be trained by me. Come to me, whenever you are ready.");
		cm.dispose();
	}
	else
	{
		cm.dispose();
	}
}