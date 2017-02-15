/* 
** NPC Name: Manji
** Location: Perion
** Purpose: Part of Maya's Weird Medicine quest
** Made by: wackyracer / Joren McGrew
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Maya = 1000200;
var YNMode = 0;
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
	else if (YNMode == 1337 && mode == 0 && status == 2)
	{
		YNMode = 0;
		cm.dispose();
		return;
	}
	else if (YNMode == 1337 && mode == 0 && status == 3)
	{
		YNMode = 0;
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m3"))
		{
    		cm.sendSimple("Anyone who dares to stand in my path will be punished dearly...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bMaking a Sparkling Rock (Ready to complete.)#k#l");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m4") && !cm.haveItem(4000008, 40, false, true))
		{
    		cm.sendSimple("Anyone who dares to stand in my path will be punished dearly...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bArcon's Blood? (In Progress)#k#l");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m4") && cm.haveItem(4000008, 40, false, true))
		{
    		cm.sendSimple("Anyone who dares to stand in my path will be punished dearly...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bArcon's Blood? (Ready to complete.)#k#l");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m5"))
		{
    		cm.sendSimple("Anyone who dares to stand in my path will be punished dearly...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bGetting Arcon's Blood (In Progress)#k#l");
    	}
		else if (cm.getLevel() >= 15 && (cm.checkQuestData(Quest_Maya, "m6") || cm.checkQuestData(Quest_Maya, "m7") || cm.checkQuestData(Quest_Maya, "end")))
		{
			cm.sendOk("Hah... I was wondering who's bothering me, and it's you. Alright, I won't call you a nobody anymore...");
			cm.dispose();
		}
		else
		{
    		cm.sendOk("Anyone who dares to stand in my path will be punished dearly...");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
		if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m3"))
		{
    		cm.sendNext("So you want to acquire #b#t4031005##k? Hah ... that's not for a nobody like you. Can't have #t4031005# in your hand unless you're the strong one ... If you really want it, prove me wrong and show me that you really are strong enough and worthy of it...");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m4") && cm.haveItem(4000008, 40, false, true))
		{
			cm.sendNext("Hmmm... I may have underestimated you... alright, here's #t4031005#, since a promise is definitely a promise. Take it.");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m5") && !cm.haveItem(4031005, 1, false, true))
		{
			cm.gainItem(4031005, 1);
			cm.sendOk("By the way what are you going to do with #b#t4031005##k? It's used as a material for precious important items, so ... are you going to take it to #p1022100#? Well, it's none of my business anyway ...");
			cm.dispose();
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m5") && cm.haveItem(4031005, 1, false, true))
		{
			cm.sendOk("By the way what are you going to do with #b#t4031005##k? It's used as a material for precious important items, so ... are you going to take it to #p1022100#? Well, it's none of my business anyway ...");
			cm.dispose();
		}
		else
		{
			cm.sendOk("If you want to get #t4031005# from me, you need to defeat the monster in the underground tunnel and collect #b#e40 pieces of #t4000008##k#n. Before all this happens, I can't give #t4031005# to a nobody like you...");
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m3"))
		{
			YNMode = 1337;
    		cm.sendYesNo("There's a huge cave underground in the middle of this island ... and in the deepest part live the mutants of Arcon. Take down those monsters in the cave and collect #b#e40 pieces of #t4000008##k#n. Are you brave enough to put yourself in there?");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m4") && cm.haveItem(4000008, 40, false, true))
		{
			cm.sendNextPrev("It's the bottle that contains the blood of Arcon that I have slain before ... Hopefully you'll use this wisely, because it's a rare, precious item...");
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
		if (mode == 1 && cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m3"))
		{
			cm.setQuestData(Quest_Maya, "m4");
			cm.sendOk("That's right ... #b#t4000008##k looks like this #i4000008#. I'm doing this for a first-timer like you, so be thankful.");
			cm.dispose();
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m4") && cm.haveItem(4000008, 40, false, true))
		{
			YNMode = 1337;
			cm.sendYesNo("By the way what are you going to do with #b#t4031005##k? It's used as a material for precious important items, so ... are you going to take it to #p1022100#? Anyway it's none of my business.");
		}
	}
	
	else if (status == 4)
	{
		if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m4") && cm.haveItem(4000008, 40, false, true))
		{
			cm.setQuestData(Quest_Maya, "m5");
			cm.gainItem(4000008, -40);
			cm.gainItem(4031005, 1);
			cm.dispose();
		}
	}
	
	else
	{
    	cm.dispose();
    }
}