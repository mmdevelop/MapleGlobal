/* 
** NPC Name: Dark Lord
** Location: Kerning City
** Purpose: Thief Job Giver / Advancer
** Made by: wackyracer / Joren McGrew & Diamondo25 / ????? ?????
** Incomplete GMS-like speech
*/

var status;
var jobName = "";
var jobId;
var Quest_Thief1 = 0004234;
var Quest_Thief2 = 0004235;
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
	
	if (cm.getJob() == 0 || (cm.getJob() == 400 && cm.checkQuestStatus(Quest_Thief1, started) && cm.getLevel() <= 29))
	{
		if (status == 0 && cm.checkQuestStatus(Quest_Thief1, started))
		{
			status += 5;
			cm.sendNext("I just gave you a little bit of #bSP#k. When you open up the #bSkill\r\nmenu#k on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can\r\nacquire only after having learned a couple of skills first.");
		}
		
		else if (status == 0)
		{
			cm.sendNext("Do you want to be a Thief? You need to meet some requirements in order to do so. You need to be at least at #bLevel 10#k and have #b25 DEX#k. Let's see if you have what it takes to become a Thief...");
		}
		
		else if (status == 1)
		{
			if (cm.getLevel() >= 10 && cm.getChar().getDex() >= 25)
			{
				cm.sendYesNo("You look qualified for this. With a great pair of eyes being able to spot the real monsters and have the coldhearted skills to accurately toss ninja stars through them...we needed someone like that. Do you want to become a Thief?");
			}
			else
			{
				cm.sendOk("Hmm... you are not quite ready yet. Come back when you're at least #bLevel 10#k and have #b25 DEX#k, okay?");
				cm.dispose();
			}
		}
		
		else if (status == 2)
		{
			if (cm.getLevel() < 10 && cm.getChar().getDex() < 25)
			{
				cm.sendOk("Hmm... you are not quite ready yet. Come back when you're at least #bLevel 10#k and have #b25 DEX#k, okay?");
				cm.dispose();
				return;
			}
			else if (mode == 1)
			{
				cm.sendNext("Alright! You are a Thief from here on out...Here's a little bit of my power to you...Haahhhh!");
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
				cm.changeJob(Packages.net.sf.odinms.client.MapleJob.THIEF);
				cm.startQuest(Quest_Thief1);
				cm.sendNext("I've just created more slots for your equipment and etc. storage. Not only that, but you've also gotten stronger as well. As you become part of us and learn to enjoy life in different angles, you may one day be on top of this world of darkness. I'll be watching your every move, so don't let me down.");
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
			cm.completeQuest(Quest_Thief1);
			cm.sendPrev("Oh, and... if you have any other questions about being a Thief, feel free to ask. I don't know every single thing about being a Thief, but I'll answer as many questions as I can. Til then...");
			cm.dispose();
		}
		
		else
		{
			cm.dispose();
		}
	}
	
	else if (cm.getJob() == 400 && cm.getLevel() >= 30 && !cm.haveItem(4041011, 1, false, true) && !cm.haveItem(4041012, 1, false, true))
	{
		if (status == 0)
		{
			cm.sendYesNo("Hmmm... you seem to have gotten a whole lot stronger. You got rid of the old, weak self and look much more like a thief now. Well, what do you think? Don't you want to get even more powerful than that? Pass a simple test and I'll do just that for you. Do you want to do it?");
		}
		
		else if (status == 1)
		{
			if (mode == 1)
			{
				cm.sendNext("Good thinking. But, I need to make sure that you are as strong as you look. It's not a hard test, one that should be easy for you to pass. First, take this letter...make sure you don't lose it.");
			}
			else
			{
				cm.dispose();
			}
		}
		
		else if (status == 2)
		{
			//if (character.Inventory.GetOpenSlotsInInventory(4) > 0) <<< MAKE A VERSION OF THIS IN JAVASCRIPT PLS
			cm.gainItem(4041011, 1);
			cm.sendNextPrev("Please get this letter to #bThief Job Instructor#k around #Construction Site North of Kerning City#k near Kerning City. He's doing the job of an instructor in place of me. Give him the letter and he'll give you the test for me. If you want more details, hear it straight from him. I'll be wishing you a good luck."); // yes these typos are GMS-like lmfao
			cm.dispose();
		}
	}
	
	else if (cm.getJob() == 400 && cm.getLevel() >= 30 && cm.haveItem(4041011, 1, false, true))
	{
		cm.sendOk("Please get this letter to #bThief Job Instructor#k around #Construction Site North of Kerning City#k near Kerning City. He's doing the job of an instructor in place of me. Give him the letter and he'll give you the test for me. If you want more details, hear it straight from him. I'll be wishing you a good luck."); // yes these typos are GMS-like lmfao
		cm.dispose();
	}
	
	else if ((cm.getJob() == 400 && cm.getLevel() >= 30 && cm.haveItem(4041012, 1, false, true)) || (cm.getJob() == 410 && cm.checkQuestStatus(Quest_Thief2, started) && cm.getLevel() >= 30) || (cm.getJob() == 420 && cm.checkQuestStatus(Quest_Thief2, started) && cm.getLevel() >= 30))
	{
		if (status == 0 && cm.checkQuestStatus(Quest_Thief2, started))
		{
			status += 6;
			cm.sendNext("Your #bUse#k and #bETC#k inventories have also been expanded with additional rows of slots now available. Your Max HP and MP has also increased...go check and see for it yourself!");
		}
		
		else if (status == 0)
		{
			cm.sendNext("Hmmm...so you got back here safely. I knew that test would be too easy for you. I admit, you are a great great thief. Now..I'll make you even more powerful than you already are. But, before all that...you need to choose one of two ways it'll be a difficult decision for you to make, but...if you have any questions, please ask.");
		}
		
		else if (status == 1)
		{
			cm.sendSimple("Alright, when you have made your decision, click on '#bI'll choose my occupation!#k' at the very bottom.\r\n#b#L0#Please explain the role of the Assassin.#l\r\n#L1#Please explain the role of the Bandit.#l\r\n#L2#I'll choose my occupation!#l");
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
				cm.sendSimple("Hmmm, have you made up your mind? Choose the 2nd job advancement of your liking...\r\r#b#L0#Assassin#l\r\n#L1#Bandit#l");
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
				jobName = "Assassin";
				jobId = 410;
			}
			else if (selection == 1)
			{
				jobName = "Bandit";
				jobId = 420;
			}
			else
			{
				jobName = "ERROR 3599 PLEASE REPORT THIS BUG IMMEDIATELY BY THE FORUMS";
				jobId = 1337;
			}
			cm.sendYesNo("So you want to make the 2nd job advancement as the #b" + jobName + "#k? Once you make the decision, you can't go back and change your mind. You ARE sure about this, right?");
		}
		
		else if (status == 4)
		{
			if (mode == 1)
			{
				if (jobId == 410)
				{
					cm.startQuest(Quest_Thief2);
					cm.gainItem(4041012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.ASSASSIN);
					// inventory expansion code goes here
					cm.sendNext("Alright, from here on out you are the Assassin. Assassins revel in shadows and darkness, waiting until the right time comes for them to stick a dagger through the enemy's heart, suddenly and swiftly...please keep training. I'll make you even more powerful than you are right now!");
				}
				else if (jobId == 420)
				{
					cm.startQuest(Quest_Thief2);
					cm.gainItem(4041012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.BANDIT);
					// inventory expansion code goes here
					cm.sendNext("Alright, from here on out you are the Bandit. Bandits revel in shadows and darkness, waiting until the right time comes for them to stick a dagger through the enemy's heart, suddenly and swiftly...please keep training. I'll make you even more powerful than you are right now!");
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
			cm.sendNext("Your use and etc. inventories have also been expanded with an additional row of slots. Your Max HP and MP has also increased...go check and see for it yourself!");
		}
		
		else if (status == 6)
		{
			cm.sendNextPrev("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottom left corner. You'll be able to boost up the newly acquired 2nd level skills. A word of warning, though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
		}
		
		else if (status == 7)
		{
			if (cm.getJob() == 410)
			{
				jobName = "Assassin";
			}
			else if (cm.getJob() == 420)
			{
				jobName = "Bandit";
			}
			else
			{
				jobName = "ERROR 3586, PLEASE REPORT THIS TO THE FORUMS AS SOON AS POSSIBLE!";
			}
			cm.completeQuest(Quest_Thief2);
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
		cm.sendOk("To those that want to be a thief, come...");
		cm.dispose();
	}
}