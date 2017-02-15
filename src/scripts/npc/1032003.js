/* 
** NPC Name: Shane
** Location: Ellinia
** Purpose: Quest
** Made by: Kyushen
** Shane dialogue for "Sabitrama and the Diet Medicine" and "Sabitrama's Anti-Aging Medicine"
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_DietMed = 1000700;
var Quest_AgingMed = 1000701;
var status;
var YNMode = 0;

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
	else if (mode == 0 && status == 1 && YNMode == 1)
	{
		cm.sendOk("I understand... but understand my side too, I can't let you in for free.");
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
		if ((cm.checkQuestData(Quest_DietMed, "1_01") || cm.checkQuestData(Quest_DietMed, "1_11")) && cm.getLevel() >= 25)
		{
			cm.sendSimple("Do you want to enter this place? I'm sure you've heard of the fact that there are some rare herbs in here, but I can't let some stranger like you enter my property. I'm sorry, but you'll have to leave.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bSabitrama and the Diet Medicine (In Progress)#k#l");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel() >= 25)
		{
			if ((cm.checkQuestData(Quest_AgingMed, "2_01") || cm.checkQuestData(Quest_AgingMed, "2_11")) && cm.getLevel() >= 50)
			{
				cm.sendSimple("It's you from the other day... is #b#p1061005##k working hard on the diet medicine? Anyway, I was kind of surprised you walked through this place without difficulty. As a reward, I'll let you stay a while without paying. You can even find some cool items out there along the way.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bSabitrama's Anti-Aging Medicine (In Progress)#k#l");
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_00") && cm.getLevel() >= 50)
			{
				cm.sendNext("It's you from the other day... is #b#p1061005##k working hard on the anti-aging medicine? Anyway, I was kind of surprised you walked through this place without difficulty. As a reward, I'll let you stay a while without paying. You can even find some cool items out there along the way.");
			}
			else
			{
				cm.sendNext("It's you from the other day... is #b#p1061005##k working hard on the diet medicine? Anyway, I was kind of surprised you walked through this place without difficulty. As a reward, I'll let you stay a while without paying. You can even find some cool items out there along the way.");
			}
		}
		else
		{
			cm.sendOk("Do you want to enter this place? I'm sure you've heard of the fact that there are some rare herbs in here, but I can't let some stranger like you enter my property. I'm sorry, but you'll have to leave.");
		}
	}
	
	else if (status == 1)
	{
		if ((cm.checkQuestData(Quest_DietMed, "1_01") || cm.checkQuestData(Quest_DietMed, "1_11")) && cm.getLevel() >= 25)
		{
			YNMode = 1;
			cm.sendYesNo("So you came here at the request of #b#p1061005##k to take the medicinal herb? Well...I inherited this land from my father and I can't let some stranger in just like that ... But, with #r3400#k mesos, it's a whole different story...So, do you want to pay your way in?");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel() >= 25)
		{
			if ((cm.checkQuestData(Quest_AgingMed, "2_01") || cm.checkQuestData(Quest_AgingMed, "2_11")) && cm.getLevel() >= 50)
			{
				YNMode = 1;
				cm.sendYesNo("It's you from the other day... Did #b#p1061005##k give you another request? What? You need to go further? Hmmmm... it's very dangerous there, but... okay. With #r10000#k mesos, I'll let you search through everything. So, do you want to pay your way in?");
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_00") && cm.getLevel() >= 50)
			{
				cm.sendNextPrev("Oh yeah ... #b#p1032100##k, from this same town, tried to sneak in. I stopped her, but #b#p1032100##k dropped something inside. I tried to look for it, but I have no idea where it is. What do you think about searching for it?");
			}
			else
			{
				cm.getPlayer().changeMap(101000100);
				cm.dispose();
			}
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (status == 2)
	{
		if ((cm.checkQuestData(Quest_DietMed, "1_01") || cm.checkQuestData(Quest_DietMed, "1_11")) && cm.getLevel() >= 25)
		{
			YNMode = 0;
			if (cm.getMeso() >= 3400)
			{
				cm.gainMeso(-3400);
				cm.getPlayer().changeMap(101000100);
				cm.dispose();
			}
			else
			{
				cm.sendOk("Are you missing money? See if you have more than #r3400#k mesos in hand. Do not expect me to give any discounts.");
				cm.dispose();
			}
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel() >= 25)
		{
			if ((cm.checkQuestData(Quest_AgingMed, "2_01") || cm.checkQuestData(Quest_AgingMed, "2_11")) && cm.getLevel() >= 50)
			{
				YNMode = 0;
				if (cm.getMeso() >= 10000)
				{
					cm.gainMeso(-10000);
					cm.getPlayer().changeMap(101000102);
					cm.dispose();
				}
				else
				{
					cm.sendOk("Are you missing money? See if you have more than #r10000#k mesos in hand. Do not expect me to give any discounts.");
					cm.dispose();
				}
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_00") && cm.getLevel() >= 50)
			{
				cm.getPlayer().changeMap(101000102);
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
	
	else
	{
		cm.dispose();
	}
}