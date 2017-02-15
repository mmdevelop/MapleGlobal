/**
 * NPC Name: Roger
 * Location: Maple Island
 * Purpose: Roger's Apple Quest
 * Made by: wackyracer / snopboy / Exile
 * FULLY GMS-like speech.
 */
 
Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Roger_Apple = 0000002;
var started = 1;
var notStarted = 0;
var completed = 2;
var no = false;
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
		if (cm.checkQuestStatus(Quest_Roger_Apple, notStarted))
		{
			cm.sendSimple("Hey! Nice weather today, huh?\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRoger's Apple#k#l");
		}
		else if (cm.checkQuestStatus(Quest_Roger_Apple, started) && cm.haveItem(2010000, 1, false, true))
		{
			cm.sendSimple("Hey! Nice weather today, huh?\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bRoger's Apple (In Progress)#k#l");
		}
		else if (cm.checkQuestStatus(Quest_Roger_Apple, started) && !cm.haveItem(2010000, 1, false, true))
		{
			cm.sendSimple("Hey! Nice weather today, huh?\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRoger's Apple (Ready to complete.)#k#l");
		}
		else
		{
			cm.sendOk("Hey! Nice weather today, huh?");
			cm.dispose();
		}
    }
	
    else if (status == 1)
    {
		if (cm.checkQuestStatus(Quest_Roger_Apple, started) && cm.haveItem(2010000, 1, false, true))
		{
			cm.sendOk("You haven't eaten the #bApple#k that I gave you yet. Talk to me once you have!");
			cm.dispose();
		}
		else if (cm.checkQuestStatus(Quest_Roger_Apple, started) && !cm.haveItem(2010000, 1, false, true))
		{
			cm.sendNext("Easy, right? You can set up a #bhotkey#k in the quickslots to the lower right of the screen to make it even easier. Oh, and your HP will automatically recover if you stand still, though it takes time.");
		}
		else if (cm.checkQuestStatus(Quest_Roger_Apple, notStarted))
		{
			cm.sendNext("Hey there, what's up! The name's Roger, and I'm here to teach you new, wide-eyed Maplers lots of cool things to help you get started.");
		}
		else
		{
			cm.dispose();
		}
	}
	
    else if (status == 2)
	{
		if (cm.checkQuestStatus(Quest_Roger_Apple, started))
		{
			cm.sendNextPrev("Alright! I suppose after all that learning, you should receive a reward. This gift is a must for your travel in Maple World, so thank me! Use this for emergencies!\r\n\r\n#e#rREWARD:#k\r\n#b3 Apples\r\n+10 EXP#k");
		}
		else if (cm.checkQuestStatus(Quest_Roger_Apple, notStarted) && !no)
		{
			cm.sendNextPrev("You are asking who made me do this? No one! It's just all out of the overflowing kindness of my heart. Haha!");
		}
		else if (cm.checkQuestStatus(Quest_Roger_Apple, notStarted) && no)
		{
			no = false;
			cm.sendOk("I can't believe I just got turned down!");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 3)
    {
		if (cm.checkQuestStatus(Quest_Roger_Apple, started))
		{
			cm.gainItem(2010000, 3);
			cm.gainExp(10);
			cm.completeQuest(Quest_Roger_Apple);
			cm.setQuestData(Quest_Roger_Apple, "end");
			cm.sendOk("Well, that's about all I can teach you. I know it's sad, but it is time to say goodbye. Take good care of yourself and do well, my friend!");
			cm.dispose();
		}
		else if (cm.checkQuestStatus(Quest_Roger_Apple, notStarted))
		{
			cm.sendYesNo("So... Let me just do this for fun! Abracadabra!");
			no = true;
		}
		else
		{
			cm.dispose();
		}
    }
    
	else if (status == 4)
    {
		if (cm.checkQuestStatus(Quest_Roger_Apple, notStarted))
		{
			cm.startQuest(Quest_Roger_Apple);
			cm.setQuestData(Quest_Roger_Apple, "1");
			cm.addHP(-25);
			cm.gainItem(2010000);
			cm.sendNext("Ha! Your HP bar almost emptied! If your HP ever gets to 0, you're in trouble. To prevent that, consume food and potions. Here, take this #rRoger's Apple#k. Open your inventory (press #bI#k) and double-click the apple to eat it.");
		}
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 5)
    {
		if (cm.checkQuestStatus(Quest_Roger_Apple, started))
		{
			cm.sendOk("Eat the Roger's Apples to get back HP. Talk to me after you do.");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
}