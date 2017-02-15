/* 
** NPC Name: a pile of herbs
** Location: The Forest of Patience <Step 5>
** Purpose: Quest
** Made by: Kyushen
** a pile of herbs dialogue for "Sabitrama's Anti-Aging Medicine"
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_AgingMed = 1000701;
var rewards = [1032013, 4010006, 4020007, 4020008]; //Red-Hearted Earrings, Gold Ore, Diamond Ore, Black Crystal Ore
var rnd;
var selectedReward;
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
	else if (mode == 0)
	{
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
		if ((cm.checkQuestData(Quest_AgingMed, "2_01") || (cm.checkQuestData(Quest_AgingMed, "2_11") && !cm.haveItem(4031032, 1, false, true))) && cm.getLevel() >= 50)
		{
			cm.sendYesNo("Are you sure you want to take #b#t4031032##k with you?");
		}
		else if (cm.checkQuestData(Quest_AgingMed, "2_00") && cm.getLevel() >= 50)
		{
			rnd = Math.floor(Math.random() * 4);
			selectedReward = rewards[rnd];
			if (cm.canHold(selectedReward) && cm.canHold(1032013)) //checks if player can hold the etc. reward AND the earrings. It might check for two different equipment slots if selectedReward = 1032013, though not sure yet. Maybe change to check all 4 rewards if thats the case.
			{
				cm.gainItem(selectedReward, 1);
				cm.getPlayer().changeMap(101000000);
				cm.dispose();
			}
			else 
			{
				cm.sendOk("You need to make room in your equipment and etc. inventory to put the items you found in the herbs. Please free up space and try again.");
				cm.dispose();
			}
		}
		else
		{
			cm.sendOk("In the midst of the herbs, you find roots with mysterious energy, but a strange aura surrounds them making it impossible to pick up.");
			cm.dispose();
		}
	}
	
	else if (status == 1)
	{
		if ((cm.checkQuestData(Quest_AgingMed, "2_01") || (cm.checkQuestData(Quest_AgingMed, "2_11") && !cm.haveItem(4031032, 1, false, true))) && cm.getLevel() >= 50)
		{
			if (cm.canHold(4031032))
			{
				cm.gainItem(4031032, 1);
				cm.setQuestData(Quest_AgingMed, "2_11");
				cm.getPlayer().changeMap(101000000);
				cm.dispose();
			}
			else 
			{
				cm.sendOk("Your etc. inventory seems to be full. Please free up space to get the item.");
				cm.dispose();
			}
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