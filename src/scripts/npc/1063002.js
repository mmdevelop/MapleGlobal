/* 
** NPC Name: a pile of white flowers
** Location: Sleepywood Forest of Patience 7
** Purpose: John's Last Present
** Made by: wackyracer
** FULLY GMS-like speech using poor English translation from Portugese
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var rnd = Math.floor(Math.random() * 3);
var selectedOreM = MixedOres[rnd];
var MixedOres = [ 4010006, 4020007, 4020008 ];
var Quest_John3 = 0005502;
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
		if (cm.checkQuestData(Quest_John3, "1") && cm.haveItem(4031028, 30, false, true))
		{
    		cm.sendOk("Many #b#t4031028##k are blooming, but you already have some, so you cannot take them for now. You need to get the flowers to John from #m104000000#.");
			cm.dispose();
		}
		// TODO: CODE INVENTORY SPACE CHECKER, AND ADD CASE FOR "Sorry, but your inventory of etc. Is full then you can not save flowers. Leave at least one empty slot for flowers."
		else if (cm.checkQuestData(Quest_John3, "1") && !cm.haveItem(4031028, 30, false, true))
		{
    		// TODO: ADD INVENTORY SPACE CHECKER HERE AS WELL
			cm.gainItem(4031028, 30);
			cm.getPlayer().changeMap(105040300);
			cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_John3, "2"))
		{
			// TODO: ADD INVENTORY SPACE CHECKER HERE AS WELL
    		cm.gainItem(selectedOreM, 2);
			cm.getPlayer().changeMap(105040300);
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("Many flowers are blooming here, except that of the #b#t4031028##k.");
    		cm.dispose();
    	}
    }
	
	else
	{
    	cm.dispose();
    }
}