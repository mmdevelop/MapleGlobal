/* 
** NPC Name: a pile of blue flowers
** Location: Sleepywood Forest of Patience 5
** Purpose: John's Present
** Made by: wackyracer
** FULLY GMS-like speech using poor English translation from Portugese
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var rnd = Math.floor(Math.random() * 7);
var selectedOreJ = JewelOres[rnd];
var JewelOres = [ 4020000, 4020001, 4020002, 4020003, 4020004, 4020005, 4020006 ];
var Quest_John2 = 0005501;
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
		if (cm.checkQuestData(Quest_John2, "1") && cm.haveItem(4031026, 20, false, true))
		{
    		cm.sendOk("Many #b#t4031026##k are blooming, but you already have some, so you cannot take them for now. You need to get the flowers to John from #m104000000#.");
			cm.dispose();
		}
		// TODO: CODE INVENTORY SPACE CHECKER, AND ADD CASE FOR "Sorry, but your inventory of etc. Is full then you can not save flowers. Leave at least one empty slot for flowers."
		else if (cm.checkQuestData(Quest_John2, "1") && !cm.haveItem(4031026, 20, false, true))
		{
    		// TODO: ADD INVENTORY SPACE CHECKER HERE AS WELL
			cm.gainItem(4031026, 20);
			cm.getPlayer().changeMap(105040300);
			cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_John2, "2"))
		{
			// TODO: ADD INVENTORY SPACE CHECKER HERE AS WELL
    		cm.gainItem(selectedOreJ, 2);
			cm.getPlayer().changeMap(105040300);
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("Many flowers are blooming here, except that of the #b#t4031026##k.");
    		cm.dispose();
    	}
    }
	
	else
	{
    	cm.dispose();
    }
}