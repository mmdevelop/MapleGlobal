/* 
** NPC Name: Grendel The Really Old
** Location: Ellinia
** Purpose: Magician Job Giver / Advancer
** Made by: wackyracer / Joren McGrew & Diamondo25 / ????? ?????
** Incomplete GMS-like speech
*/

var status;
var jobName = "";
var jobSpeech = "";
var jobId;
var Quest_Mage1 = 0003234;
var Quest_Mage2 = 0003235;
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
	
	if (cm.getJob() == 0 || (cm.getJob() == 200 && cm.checkQuestStatus(Quest_Mage1, started) && cm.getLevel() <= 29))
	{
		if (status == 0 && cm.checkQuestStatus(Quest_Mage1, started) && cm.getJob() == 200)
		{
			status += 5;
			cm.sendNext("I just gave you a little bit of #bSP#k. When you open up the #bSkill\r\nmenu#k on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can\r\nacquire only after having learned a couple of skills first.");
		}
		else if (status == 0)
		{
			cm.sendNext("Do you want to be a Magician? You need to meet some requirements in order to do so. You need to be at least at #bLevel 8#k, and #bINT 20#k. Let's see if you have what it takes to become a Magician...");
		}
		else if (status == 1)
		{
			if (cm.getLevel() >= 8 && cm.getChar().getInt() >= 20)
			{
				cm.sendYesNo("You definitely have the look of a Magician. You may not be there yet, but I can see the Magician in you...what do you think? Do you want to become a Magician?");
			}
			else
			{
				cm.sendOk("You need to train more. Don't think being a Magician is a walk in the park... You'll need to be at least #bLevel 10#k and have #b30 STR#k.");
				cm.dispose();
			}
		}
		else if (status == 2)
		{
			if (cm.getLevel() < 8 && cm.getChar().getInt() < 20)
			{
				cm.sendOk("You need to train more. Don't think being a Magician is a walk in the park... You'll need to be at least #bLevel 10#k and have #b30 STR#k.");
				cm.dispose();
				return;
			}
			else if (mode == 1)
			{
				cm.sendNext("Alright, you're a Magician from here on out, since I, Grendel the Really Old, the head Magician, allow you so. It isn't much, but I'll give you a little bit of what I have...");
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
				cm.startQuest(Quest_Mage1);
				cm.changeJob(Packages.net.sf.odinms.client.MapleJob.MAGICIAN);
				cm.sendNext("You have just equipped yourself with much more magical power. Please keep training and make yourself much better...I'll be watching you from here and there...");
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
				cm.sendNext("I just gave you a little bit of #bSP#k. When you open up the #bSkill\r\nmenu#k on the lower left corner of the screen, there are skills you can learn by using SP's. One warning, though: You can't raise it all together all at once. There are also skills you can acquire only after having learned a couple of skills first.");
			}
		}
		else if (status == 5)
		{
			cm.sendNextPrev("One more warning. Once you have chosen your job, try to stay alive as much as you can. Once you reach that level, when you die, you will lose your experience level. You wouldn't want to lose your hard-earned experience points, do you?");
		}
		else if (status == 6)
		{
			cm.sendNextPrev("OK! This is all I can teach you. Go to places, train and better yourself. Find me when you feel like you've done all you can, and need something interesting. I'll be waiting for you.");
		}
		else if (status == 7)
		{
			cm.completeQuest(Quest_Mage1);
			cm.sendPrev("Oh, and... if you have any other questions about being a Magician, feel free to ask. I don't know every single thing about being a Magician, but I'll answer as many questions as I can. Til then...");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (cm.getJob() == 200 && cm.getLevel() >= 30 && !cm.haveItem(4031009, 1, false, true) && !cm.haveItem(4031012, 1, false, true))
	{
		if (status == 0)
		{
			cm.sendYesNo("Whoa, you have definitely grown up! You don't look small and weak anymore...rather, now I can feel your presence as a Magician! Impressive..so, what do you think? Do you want to get even stronger than you are right now? Pass a simple test and I'll do just that! Wanna do it?");
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
			cm.gainItem(4031009, 1);
			cm.sendNextPrev("Please get this letter to #bMagician Job Instructor #kwho may be around #bThe Forest North of Ellinia #kthat's near Ellinia. He's the one being the instructor now in place of me, as I am busy here. Get him the letter and he'll give you the test in place of me. For more details, hear it straight from him. Best of luck to you.");
			cm.dispose();
		}
	}
	
	else if (cm.getJob() == 200 && cm.getLevel() >= 30 && cm.haveItem(4031009, 1, false, true))
	{
		cm.sendOk("Please get this letter to #bMagician Job Instructor #kwho may be around #bThe Forest North of Ellinia #kthat's near Ellinia. He's the one being the instructor now in place of me, as I am busy here. Get him the letter and he'll give you the test in place of me. For more details, hear it straight from him. Best of luck to you.");
		cm.dispose();
	}
	
	else if ((cm.getJob() == 200 && cm.getLevel() >= 30 && cm.haveItem(4031012, 1, false, true)) || (cm.getJob() == 210 && cm.checkQuestStatus(Quest_Mage2, started) && cm.getLevel() >= 30) || (cm.getJob() == 220 && cm.checkQuestStatus(Quest_Mage2, started) && cm.getLevel() >= 30) || (cm.getJob() == 230 && cm.checkQuestStatus(Quest_Mage2, started) && cm.getLevel() >= 30))
	{
		if (status == 0 && cm.checkQuestStatus(Quest_Mage2, started))
		{
			status += 6;
			cm.sendNext("Your #bUse#k and #bETC#k inventories have also been expanded with additional rows of slots now available. Your Max HP and MP has also increased...go check and see for it yourself!");
		}
		else if (status == 0)
		{
			cm.sendNext("You got back here safely. Well done. I knew you'd pass the tests very easily...alright, I'll make you much stronger now. Before that, though...you need to choose one of the three paths that will given to you. It will be a tough decision for you to make, but...if you have any questions, feel free to ask.");
		}
		else if (status == 1)
		{
			cm.sendSimple("Alright, when you have made your decision, click on '#bI'll choose my occupation!#k' at the very bottom.\r\n#b#L0#Please explain the role of the The Wizard of Fire and Poison.#l\r\n#L1#Please explain the role of the The Wizard of Ice and Lightening.#l\r\n#L2#Please explain the role of the Cleric.#l\r\n#L3#I'll choose my occupation!#l");
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
				cm.sendSimple("Now, have you made up your mind? Please select your occupation for your 2nd job advancement.\r\r#b#L0#The Wizard of Fire and Poison#l\r\n#L1#The Wizard of Ice and Lightening#l\r\r#L2#Cleric#l");
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
				jobName = "The Wizard of Fire and Poison";
				jobId = 210;
			}
			else if (selection == 1)
			{
				jobName = "The Wizard of Ice and Lightening";
				jobId = 220;
			}
			else if (selection == 2)
			{
				jobName = "Cleric";
				jobId = 230;
			}
			else
			{
				jobName = "ERROR 3597 PLEASE REPORT THIS BUG IMMEDIATELY BY THE FORUMS";
				jobId = 1337;
			}
			cm.sendYesNo("So you want to make the 2nd job advancement as the #b" + jobName + "#k? Once you make the decision, you won't be able to make a job advancement with any other job. Are you sure about this?");
		}
		else if (status == 4)
		{
			if (mode == 1)
			{
				if (jobId == 210)
				{
					cm.startQuest(Quest_Mage2);
					cm.gainItem(4031012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.FP_WIZARD);
					// inventory expansion code goes here
					cm.sendNext("From here on out, you have become the #bWizard of Fire and Poison#k... Wizards use high intelligence and the power of nature all around us to take down the enemies...please continue with your studies, for one day I may make you much more powerful with my own power...");
				}
				else if (jobId == 220)
				{
					cm.startQuest(Quest_Mage2);
					cm.gainItem(4031012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.IL_WIZARD);
					// inventory expansion code goes here
					cm.sendNext("From here on out, you have become the #bWizard of Ice and Lightening#k... Wizards use high intelligence and the power of nature all around us to take down the enemies...please continue with your studies, for one day I may make you much more powerful with my own power...");
				}
				else if (jobId == 230)
				{
					cm.startQuest(Quest_Mage2);
					cm.gainItem(4031012, -1);
					cm.changeJob(Packages.net.sf.odinms.client.MapleJob.CLERIC);
					// inventory expansion code goes here
					cm.sendNext("Alright, you're a #bCleric#k from here on out. Clerics blow life into every living organism here with their undying faith in God. Never stop working on your faith...then one day, I'll help you become much more powerful...");
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
			cm.sendNext("I've also expanded your etc. inventory by a whole row, along with your maximum MP... go see for it yourself...");
		}
		else if (status == 6)
		{
			cm.sendNextPrev("I have also given you a little bit of #bSP#k. Open the #bSkill Menu#k located at the bottom left corner. You'll be able to boost up the newly acquired 2nd level skills. A word of warning, though: You can't boost them up all at once. Some of the skills are only available after you have learned other skills. Make sure to remember that.");
		}
		else if (status == 7)
		{
			if (cm.getJob() == 210)
			{
				jobName = "Wizards";
				jobSpeech = "have to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger. Find me after you have advanced much further ...";
			}
			else if (cm.getJob() == 220)
			{
				jobName = "Wizards";
				jobSpeech = "have to be strong. But remember that you can't abuse that power and use it on a weakling. Please use your enormous power the right way, because...for you to use that the right way, that is much harder than just getting stronger. Find me after you have advanced much further ...";
			}
			else if (cm.getJob() == 230)
			{
				jobName = "Cleric";
				jobSpeech = "needs faith more than anything else. Keep your strong faith in God and treat everyone with respect and dignity they deserve. Keep working hard and you may one day earn more religious magic power...alright...please find me after you have made more strides. I'll be waiting for you.";
			}
			else
			{
				jobName = "ERROR 3598, PLEASE REPORT THIS TO THE FORUMS AS SOON AS POSSIBLE!";
			}
			cm.completeQuest(Quest_Mage2);
			cm.sendNextPrev("The " + jobName + " " + jobSpeech);
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
	
	else
	{
		cm.sendOk("To all that desire to become a magician...talk to me...");
		cm.dispose();
	}
}