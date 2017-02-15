/* 
** NPC Name: Bari
** Location: Southperry
** Purpose: Simple phrase.
** Made by: wackyracer / Ixeb
** Somewhat GMS-like speech (because the new GMS has Bari in Amherst as a part of some quest, and doesn't have the original dialogue from long ago)
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
		cm.sendOk("The ship here takes you to #bVictoria Island#k, though I've never been there before... I wonder what it's like...");
		cm.dispose();
	}
	else
	{
		cm.dispose();
	}
}