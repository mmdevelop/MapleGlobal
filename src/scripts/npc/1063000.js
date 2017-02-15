/* 
** NPC Name: a pile of pink flowers
** Location: Sleepywood Forest of Patience 2
** Purpose: John's Pink Flower Basket
** Made by: wackyracer
** FULLY GMS-like speech using poor English translation from Portugese
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var rnd = Math.floor(Math.random() * 6);
var selectedOreM = MaterialOres[rnd];
var MaterialOres = [ 4010000, 4010001, 4010002, 4010003, 4010004, 4010005 ];
var Quest_John1 = 0005500;
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
		if (cm.checkQuestData(Quest_John1, "1") && cm.haveItem(4031025, 10, false, true))
		{
    		cm.sendOk("Many #b#t4031025##k are blooming, but you already have some, so you cannot take them for now. You need to get the flowers to John from #m104000000#.");
			cm.dispose();
		}
		// TODO: CODE INVENTORY SPACE CHECKER, AND ADD CASE FOR "Sorry, but your inventory of etc. Is full then you can not save flowers. Leave at least one empty slot for flowers."
		else if (cm.checkQuestData(Quest_John1, "1") && !cm.haveItem(4031025, 10, false, true))
		{
    		// TODO: ADD INVENTORY SPACE CHECKER HERE AS WELL
			cm.gainItem(4031025, 10);
			cm.getPlayer().changeMap(105040300);
			cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_John1, "2"))
		{
			// TODO: ADD INVENTORY SPACE CHECKER HERE AS WELL
    		cm.gainItem(selectedOreM, 2);
			cm.getPlayer().changeMap(105040300);
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("Many flowers are blooming here, except that of the #b#t4031025##k.");
    		cm.dispose();
    	}
    }
	
	else
	{
    	cm.dispose();
    }
}