/* 
** NPC Name: Dances With Balrog
** Location: Perion: Warriors Sanctuary
** Purpose: Warrior Job Giver / Advancer
** Made by: wackyracer / Joren McGrew & Diamondo25 / ????? ?????
** Incomplete GMS-like speech
*/

var status;
var jobName = "";
var jobId;
var Quest_War1 = 0002234;
var Quest_War2 = 0002235;
var started = 1;
var notStarted = 0;
var completed = 2;

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
		cm.sendNext("Really? Have to give more though to it, huh? Take your time, take your time. This is not something you should take lightly...come talk to me once you have made your decision.");
		cm.dispose();
		return;
	}
	else if (mode == 0 && status == 0)
	{
		cm.sendOk("Come back once you have thought about it some more.");
		cm.dispose();
		return;
	}
	else if (mode == 0 && status == 3)
	{
		cm.sendOk("You need to think about it a little more? Sure, take your time. This is not something you should take lightly. Let me know when you have made your decision.");
		cm.dispose();
		return;
	}
	else
	{
		status--; 
    } 
	
	if (cm.getJob() == 0 || (cm.getJob() == 100 && cm.checkQuestStatus(Quest_War1, started) && cm.getLevel() <= 29))
	{
		if (status == 0 && cm.checkQuestStatus(Quest_War1, started) && cm.getJob() == 100)
		{
			status += 5;
			cm.sendNext("I just gave you a little bit of #bSP#k. When you open up the #bSkill\r\nmenu#k on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can\r\nacquire only after having learned a couple of skills first.");
		}
		else if (status == 0)
		{
			cm.sendNext("Do you wish to become a Warrior? You need to meet some criteria in order to do so. #bYou should be at least Lv. 10 and have 30 STR.#k Let's see...");
		}
		else if (status == 1)
		{
			if (cm.getLevel() >= 10 && cm.getChar().getStr() >= 30)
			{
				cm.sendYesNo("You definitely have the look of a Warrior. You may not be there yet, but I can see the Warrior in you. What do you think? Do you want to become a Warrior?");
			}
			else
			{
				cm.sendOk("You need to train more. Don't think being a Warrior is a walk in the park... You'll need to be at least #bLevel 10#k and have #b30 STR#k.");
				cm.dispose();
			}
		}
		else if (status == 2)
		{
			if (cm.getLevel() < 10 && cm.getChar().getStr() < 30)
			{
				cm.sendOk("You need to train more. Don't think being a Warrior is a walk in the park... You'll need to be at least #bLevel 10#k and have #b30 STR#k.");
				cm.dispose();
				return;
			}
			else if (mode == 1)
			{
				cm.sendNext("Alright! You are a Warrior from here on out...Here's a little bit of my power to you...Haahhhh!");
			}
			else
			{
				cm.dispose();
			}
		}
		else if (status == 3)
		{
			if (cm.getLevel() >= 30)
			{
				cm.sendNextPrev("I think you've made the job advancement way too late. Usually, for beginners under Level 29 that were late in making job advancements, we compensate them with lost Skill Points, that weren't rewarded, but...I think you're a little too late for that. I am so sorry, but there's nothing I can do.");
			}
			else
			{
				// no available command to increase inventory space. help pls thx - wackyracer
				cm.startQuest(Quest_War1);
				cm.changeJob(Packages.net.sf.odinms.client.MapleJob.WARRIOR);
				cm.sendNext("I have added slots for your equipment and etc. inventory. You have also gotten much stronger. Train harder, and you may one day reach the very top of the Warrior. I'll be watching you from afar. Please work hard.");
			}
		}
		else if (status == 4)
		{
			if (cm.getLevel() >= 30)
			{
				cm.dispose();
			}
			else
			{
				cm.sendNext("I just gave you a little bit of #bSP#k. When you open up the #bSkill\r\nmenu#k on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can\r\nacquire only after having learned a couple of skills first.");
			}
		}
		else if (status == 5)
		{
			cm.sendNextPrev("One more warning. Once you have chosen your job, try to stay alive as much as you can. Once you reach that level, when you die, you will lose your experience level.\r\nYou wouldn't want to lose your hard-earned experience points, do you?");
		}
		else if (status == 6)
		{
			cm.sendNextPrev("OK! This is all I can teach you. Go to places, train and better yourself. Find me when you feel like you've done all you can, and need something interesting. I'll be waiting for you.");
		}
		else if (status == 7)
		{
			cm.completeQuest(Quest_War1);
			cm.sendPrev("Oh, and... if you have any other questions about being a Warrior, feel free to ask. I don't know every single thing about being a Warrior, but I'll answer as many questions as I can. Til then...");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (cm.getJob() == 100 && cm.getLevel() >= 30 && !cm.haveItem(4031008, 1, false, true) && !cm.haveItem(4031012, 1, false, true))
	{
		if (status == 0)
		{
			cm.sendYesNo("Whoa, you have definitely grown up! You don't look small and weak anymore...rather, now I can feel your presence as a Warrior! Impressive..so, what do you think? Do you want to get even stronger than you are right now? Pass a simple test and I'll do just that! Wanna do it?");
		}
		else if (status == 1)
		{
			if (mode == 1)
			{
				cm.sendNext("Good thinking. You look strong, don't get me wrong, but there's still a need to test your strength and see if your are for real. The test isn't too difficult, so you'll do just fine... Here, take this letter first. Make sure you don't lose it.");
			}
			else
			{
				cm.dispose();
			}
		}
		else if (status == 2)
		{
			//if (character.Inventory.GetOpenSlotsInInventory(4) > 0) <<< MAKE A VERSION OF THIS IN JAVASCRIPT PLS
			cm.gainItem(4031008, 1);
			cm.sendNextPrev("Please get this letter to #bWarrior Job Instructor#k who may be around the highlands here in Perion. He's the one being the instructor now in place of me, as I am busy here. Get him the letter and he'll give you the test in place of me. For more details, hear it straight from him. Best of luck to you.");
			cm.dispose();
		}
	}
	
	else if (cm.getJob() == 100 && cm.getLevel() >= 30 && cm.haveItem(4031008, 1, false, true))
	{
		cm.sendOk("Please get this letter to #bWarrior Job Instructor#k who may be around the highlands here in Perion. He's the one being the instructor now in place of me, as I am busy here. Get him the letter and he'll give you the test in place of me. For more details, hear it straight from him. Best of luck to you.");
		cm.dispose();
	}
	
	else if ((cm.getJob() == 100 && cm.getLevel() >= 30 && cm.haveItem(4031012, 1, false, true)) || (cm.getJob() == 110 && cm.checkQuestStatus(Quest_War2, started) && cm.getLevel() >= 30) || (cm.getJob() == 120 && cm.checkQuestStatus(Quest_War2, started) && cm.getLevel() >= 30) || (cm.getJob() == 130 && cm.checkQuestStatus(Quest_War2, started) && cm.getLevel() >= 30))
	{
		if (status == 0 && cm.checkQuestStatus(Quest_War2, started))
		{
			status += 6;
			cm.sendNext("Your #bUse#k and #bETC#k inventories have also been expanded with additional rows of slots now available. Your Max HP and MP has also increased...go check and see for it yourself!");
		}
		else if (status == 0)
		{
			cm.sendNext("Well look who's here!...you came back safe! I knew you'd breeze through...I'll admit you are a strong, formidable Warrior...alright, I'll make you an even stronger Warrior than you already are right now... Before THAT! you need to choose one of the three paths that you'll be given.. it isn't going to be easy, so if you have any questions, feel free to ask.");
		}
		else if (status == 1)
		{
			cm.sendSimple("Alright, when you have made your decision, click on '#bI'll choose my occupation!#k' at the very bottom.\r\n#b#L0#Please explain the role of the Fighter.#l\r\n#L1#Please explain the role of the Page.#l\r\n#L2#Please explain the role of the Spearman.#l\r\n#L3#I'll choose my occupation!#l");
		}
		else if (status == 2)
		{
			if (selection == 0)
			{
				cm.sendNext("Dialog not found/written yet. If this is still here in the release of the game, please report it to the staff right away.");
				cm.dispose();
			}
			else if (selection == 1)
			{
				cm.sendNext("Dialog not found/written yet. If this is still here in the release of the game, please report it to the staff right away.");
				cm.dispose();
			}
			else if (selection == 2)
			{
				cm.sendNext("Dialog not found/written yet. If this is still here in the release of the game, please report it to the staff right away.");
				cm.dispose();
			}
			else if (selection == 3)
			{
				cm.sendSimple("Hmmm, have you made up your mind? Choose the 2nd job advancement of your liking...\r\r#b#L0#Fighter#l\r\n#L1#Page#l\r\r#L2#Spearman#l");
			}
			else
			{
				cm.dispose();
			}
		}
		else if (status == 3)
		{
			if (selection == 0)
			{
				jobName = "Fighter";
				jobId = 110;
			}
			else if (selection == 1)
			{
				jobName = "Page";
				jobId = 120;
			}
			else if (selection == 2)
			{
				jobName = "Spearman";
				jobId = 130;
			}
			else
			{
				jobName = "ERROR 3595 PLEASE REPORT THIS BUG IMMEDIATELY BY THE FORUMS";
				jobId = 1337;
			}
			cm.sendYesNo("So you want to make the 2nd job advancement as the #b" + jobName + "#k? Once you make the decision, you won't be able to make a job advancement with any other job. Are you sure about this?");
		}
		else if (status == 4)
		{
			if (mode == 1)
			{
				if (jobId == 110)
				{
					cm.startQuest(Quest_War2);
					cm.gainItem(4031012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.FIGHTER);
					// inventory expansion code goes here
					cm.sendNext("Alright! You have now become the #bFighter#k! A fighter strives to become the strongest of the strong, and never stops fighting. Don't ever lose that will to fight, and push forward no matter what. I'll help you become even stronger than you already are.");
				}
				else if (jobId == 120)
				{
					cm.startQuest(Quest_War2);
					cm.gainItem(4031012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.PAGE);
					// inventory expansion code goes here
					cm.sendNext("Alright! You have now become the #bPage#k! A Page fights tactically, and uses special skills to strike each and every monster's weak spot! Always know your enemy's weakness, or else you will be weak!");
				}
				else if (jobId == 130)
				{
					cm.startQuest(Quest_War2);
					cm.gainItem(4031012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.SPEARMAN);
					// inventory expansion code goes here
					cm.sendNext("Alright! You have become the #bSpearman#k! A Spearman fights with all of his might and soul! Using long-range weapons, they can attack monsters from a farther distance than other Warriors! Not to mention Spearman have the most powerful weapons!");
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
		else if (status == 5)
		{
			cm.sendNext("Your use and etc. inventories have also been expanded with an additional row of slots. Your Max HP has also increased...go check and see for it yourself!");
		}
		else if (status == 6)
		{
			cm.sendNextPrev("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottom left corner. You'll be able to boost up the newly acquired 2nd level skills. A word of warning, though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
		}
		else if (status == 7)
		{
			if (cm.getJob() == 110)
			{
				jobName = "Fighter";
			}
			else if (cm.getJob() == 120)
			{
				jobName = "Page";
			}
			else if (cm.getJob() == 130)
			{
				jobName = "Spearman";
			}
			else
			{
				jobName = "ERROR 3591, PLEASE REPORT THIS TO THE FORUMS AS SOON AS POSSIBLE!";
			}
			cm.completeQuest(Quest_War2);
			cm.sendNextPrev("A " + jobName + " needs to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger. Find me after you have advanced much further. I'll be waiting for you.");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
	
	else
	{
		cm.sendOk("For those that want to become a warrior, come see me...");
		cm.dispose();
	}
}