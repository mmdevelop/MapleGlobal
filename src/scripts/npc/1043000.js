/* 
** NPC Name: a pile of flowers
** Location: The Forest of Patience <Step 2>
** Purpose: Quest
** Made by: Kyushen
** a pile of flowers dialogue for "Sabitrama and the Diet Medicine"
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_DietMed = 1000700;
var ores = [4010000, 4010001, 4010002, 4010003, 4010004, 4010005, 4020000, 4020001, 4020002, 4020003, 4020004, 4020005, 4020006]; //Bronze, Steel, Mithril, Adamantium, Silver, Orihalcon, Garnet, Amethyst, Aquamarine, Emerald, Opal, Sapphire, Topaz (ORE)
var rnd;
var selectedOre;
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
		if ((cm.checkQuestData(Quest_DietMed, "1_01") || (cm.checkQuestData(Quest_DietMed, "1_11") && !cm.haveItem(4031020, 1, false, true))) && cm.getLevel() >= 25)
		{
			cm.sendYesNo("Are you sure you want to take #bPink Anthurium#k with you?");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel >= 25)
		{
			rnd = Math.floor(Math.random() * 13);
			selectedOre = ores[rnd];
			if (cm.canHold(selectedOre)) //maybe check every ore to prevent player from having full inventory of every ore except the one they want? change if having multiple cm.canHold(int) checks for the same inventory spot.
			{
				cm.gainItem(selectedOre, 2);
				cm.getPlayer().changeMap(101000000);
				cm.dispose();
			}
			else 
			{
				cm.sendOk("You need to have at least one empty slot in your etc. inventory to save the item you found in the middle of the flowers. Free up space and then try again.");
				cm.dispose();
			}
		}
		else
		{
			cm.sendOk("In the midst of the pile of flowers, I can feel some strange aura in there. Unfortunately, I feel something strange around the flowers and I don't think I can take them with me.");
			cm.dispose();
		}
	}
	
	else if (status == 1)
	{
		if ((cm.checkQuestData(Quest_DietMed, "1_01") || (cm.checkQuestData(Quest_DietMed, "1_11") && !cm.haveItem(4031020, 1, false, true))) && cm.getLevel() >= 25)
		{
			if (cm.canHold(4031020))
			{
				cm.gainItem(4031020, 1);
				cm.setQuestData(Quest_DietMed, "1_11");
				cm.getPlayer().changeMap(101000000);
				cm.dispose();
			}
			else 
			{
				cm.sendOk("You need to have at least one empty slot in your etc. inventory to save the item you found in the middle of the flowers. Free up space and then try again.");
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