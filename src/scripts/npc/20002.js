/* 
** NPC Name: Biggs
** Location: Southperry
** Purpose: Quest
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
** Extra notes: Write inventory checks on not only this NPC, but other NPCs that add items to an inventory
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Biggs = 100;
var daggers = [1332005, 1332007]; // Razor, Fruit Knife
var rnd = Math.floor(Math.random() * 2);
var selectedDagger = daggers[rnd];
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
	else if (mode == 0 && status == 1)
	{
    	cm.sendOk("Hmph.. I was right. You aren't up for the challenge!");
    	cm.dispose();
		return;
    }
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
		if (cm.checkQuestData(Quest_Biggs, "") || cm.checkQuestData(Quest_Biggs, "info"))
		{
    		cm.sendSimple("I can't stay in this town forever. Is there anyone who can help me?\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bBigg's Collection of Items#k#l");
    	}
		else if (cm.checkQuestData(Quest_Biggs, "f") && (!cm.haveItem(4000001, 10, false, true) || !cm.haveItem(4000000, 30, false, true)))
		{
    		cm.sendSimple("I can't stay in this town forever. Is there anyone who can help me?\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bBigg's Collection of Items (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Biggs, "f") && cm.haveItem(4000001, 10, false, true) && cm.haveItem(4000000, 30, false, true))
		{
    		cm.sendSimple("I can't stay in this town forever. Is there anyone who can help me?\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bBigg's Collection of Items (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Biggs, "end"))
		{
    		cm.sendOk("Thanks for the last time. Now, if I get just little more money, I probably can start the business. Are you still using the weapon, which I gave you?");
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("I can't stay in this town forever. Is there anyone who can help me?");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Biggs, "") || cm.checkQuestData(Quest_Biggs, "info"))
		{
    		cm.sendYesNo("I'll give you something nice if you get me #b#e10#n #t4000001#s#k and #b#e30#n #t4000000#s#k! You can get it by taking down the monsters, but ... looking at you, I'm not sure if you're up for the challenge...");
    	}
		else if (cm.checkQuestData(Quest_Biggs, "f"))
		{
    		if (cm.itemQuantity(4000001) >= 10 && cm.itemQuantity(4000000) >= 30)
			{
    			cm.setQuestData(Quest_Biggs, "end");
				cm.completeQuest(Quest_Biggs);
    			cm.gainItem(4000001, -10);
    			cm.gainItem(4000000, -30);
    			cm.gainExp(30);
    			cm.gainItem(selectedDagger, 1);
    			cm.sendOk("Oh wow! You brought them all!! Sweet! Here's an item like I promised. I don't really need it anyway, so take it!\r\n\r\n#e#rREWARD:#k\r\n+30 EXP\r\n+#i" + selectedDagger + "# #t" + selectedDagger + "#");
    			cm.dispose();
    		}
			else
			{
    			cm.sendOk("You don't have #e10#n #b#t4000001#s#k and #e30#n#k #b#t4000000#s#k, right? Don't worry too much about it. I'll be staying here for a while.");
    			cm.dispose();
    		}
    	}
    }
	
	else if (status == 2)
	{
    	if (cm.checkQuestData(Quest_Biggs, "") || cm.checkQuestData(Quest_Biggs, "info"))
		{
    		cm.startQuest(Quest_Biggs);
    		cm.setQuestData(Quest_Biggs, "f");
    		cm.sendOk("Thanks... I'll be waiting here!");
    		cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    }
}