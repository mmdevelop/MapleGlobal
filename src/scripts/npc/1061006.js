/* 
** NPC Name: Mysterious Statue
** Location: 105040300 || Sleepywood - Dungeon
** Purpose: John's Quests
** Made by: wackyracer
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_John1 = 0005500;
var Quest_John2 = 0005501;
var Quest_John3 = 0005502;
var status;
var quest1 = 0;
var quest2 = 0;
var quest3 = 0;

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
		if (cm.checkQuestData(Quest_John3, "1") && !cm.haveItem(4031028, 30, false, true))
		{
			quest3 = 1;
    		cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Is it okay to be moved to somewhere else randomly just like that?");
    	}
		else if (cm.checkQuestData(Quest_John2, "1") && !cm.haveItem(4031026, 20, false, true))
		{
			quest2 = 1;
    		cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Is it okay to be moved to somewhere else randomly just like that?");
    	}
		else if (cm.checkQuestData(Quest_John1, "1") && !cm.haveItem(4031025, 10, false, true))
		{
			quest1 = 1;
    		cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Is it okay to be moved to somewhere else randomly just like that?");
    	}
		else if (cm.checkQuestData(Quest_John3, "2"))
		{
			quest3 = 1;
    		cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Is it okay to be moved to somewhere else randomly just like that?");
    	}
		else if (cm.checkQuestData(Quest_John2, "2"))
		{
			quest2 = 1;
    		cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Is it okay to be moved to somewhere else randomly just like that?");
    	}
		else if (cm.checkQuestData(Quest_John1, "2"))
		{
			quest1 = 1;
    		cm.sendYesNo("Once I lay my hand on the statue, a strange light covers me and it feels like I am being sucked into somewhere else. Is it okay to be moved to somewhere else randomly just like that?");
    	}
		else if (cm.checkQuestData(Quest_John3, "1") && cm.haveItem(4031028, 30, false, true))
		{
    		cm.sendOk("I put my hand on the statue, but nothing happened. It must be because of the #t4031028# that I have because it seems to only interfere with the power of the statue.");
			cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_John2, "1") && cm.haveItem(4031026, 20, false, true))
		{
    		cm.sendOk("I put my hand on the statue, but nothing happened. It must be because of the #t4031026# that I have because it seems to only interfere with the power of the statue.");
			cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_John1, "1") && cm.haveItem(4031025, 10, false, true))
		{
    		cm.sendOk("I put my hand on the statue, but nothing happened. It must be because of the #t4031025# that I have because it seems to only interfere with the power of the statue.");
			cm.dispose();
    	}
		else
		{
			cm.sendOk("I put my hand on the statue, but nothing happened.");
			cm.dispose();
		}
    }
	
	else if (status == 1)
	{
		if (mode == 1 && quest3 == 1)
		{
			cm.getPlayer().changeMap(105040314);
			cm.dispose();
		}
		else if (mode == 1 && quest2 == 1)
		{
			cm.getPlayer().changeMap(105040312);
			cm.dispose();
		}
		else if (mode == 1 && quest1 == 1)
		{
			cm.getPlayer().changeMap(105040310);
			cm.dispose();
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