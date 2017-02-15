/* 
** NPC Name: Rain
** Location: Amherst
** Purpose: Rain's Maple Quiz Quest
** Made by: wackyracer
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Rain_Quiz = 2000700;
var status;
/* the purpose of this YNMode variable is to help the system judge where it needs to take actions and where not to more accurately
   instead of selecting the wrong case for the wrong situation, which it will do without this variable */
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
	else if (YNMode == 1337 && cm.checkQuestData(Quest_Rain_Quiz, "") && mode == 0 && status == 1)
	{
		YNMode = 0;
    	cm.sendOk("Are you worried you're still a beginner in MapleStory? Hmmm ... even with that, you'll be learning a lot through my Maple Quiz. You should at least try it!!");
		cm.dispose();
		return;
    }
	else if (YNMode != 1337 && mode == 0 && status == 1)
	{
		YNMode = 0;
		cm.dispose();
		return;
    }
	else if (YNMode != 1337 && mode == 0 && status == 3)
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
    	if (cm.checkQuestData(Quest_Rain_Quiz, ""))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRain's Maple Quiz 1#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "1"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRain's Maple Quiz 1 (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "2"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRain's Maple Quiz 2#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "3"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRain's Maple Quiz 2 (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "4"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRain's Maple Quiz 3#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "5"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRain's Maple Quiz 3 (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "6"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRain's Maple Quiz 4#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "7"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRain's Maple Quiz 4 (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "8"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRain's Maple Quiz 5#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "9"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRain's Maple Quiz 5 (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "10"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRain's Maple Quiz 6#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "11"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRain's Maple Quiz 6 (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "12"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bRain's Maple Quiz 7#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "13"))
		{
    		cm.sendSimple("You want to try Maple Quiz? Please feel free to tell me, when you are ready.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bRain's Maple Quiz 7 (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "14"))
		{
    		cm.sendOk("Right~! You are the one who solved all the quiz. Anyway, How are you?");
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("You want to try Maple Quiz? Please feel free to tell me, when you are ready.");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Rain_Quiz, ""))
		{
			YNMode = 1337;
    		cm.sendYesNo("Hello there! I think you're still having trouble adjusting to life in Maple World.\nI'm going to be giving you a brief rundown on this place, then will give you a Maple Quiz. Once you answer them all correct, I'll be giving you a small present as a sign of appreciation, something that'll come in handy here.\nWhat do you think? Do you want to take a crack at The Maple Quiz?");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "1"))
		{
			cm.sendSimple("What do you press to open up the item inventory? \r\n#L0##b I   #l\r\n#L1# K   #l\r\n#L2# S   #l\r\n#L3# E   #k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "3"))
		{
			cm.sendSimple("Can you wear an item just by double-clicking it with your mouse? \r\n#L0##bOh yes    #l\r\n#L1#No way.    #k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "5"))
		{
			cm.sendSimple("What do you press to open up your equipment inventory? ?\r\n#L0##b E    #l\r\n#L1# S    #l\r\n#L2# I    #k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "7"))
		{
			cm.sendSimple("What do you press to pick up an item on the ground?\r\n#L0##b X    #l\r\n#L1# S    #l\r\n#L2# I    #l\r\n#L3# Z    #k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "9"))
		{
			cm.sendSimple("In order to make the job advancement as either a warrior, a bowman, or a thief, you'll have to be at least level 10 to do so. What level do you have to be in order to make the job adv. as a magician? \r\n#L0##bLevel 10    #l#l\r\n#L1#Level 8    #k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "11"))
		{
			cm.sendSimple("Every time you level up, you can raise your character's ability stats. How many ability points (AP) are you awarded after every level up? \r\n#L0##b2 points    #l\r\n#L1#10 points    #l\r\n#L2#5 points    #k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "13"))
		{
			cm.sendSimple("Last question, and this is also the most important one. You can only make the job advancement at Victoria Island, but you're currently at Maple Island. Where do you have to go in order to get on the ride to Victoria Island? \r\n#L0##bSouthperry    #l\r\n#L1#Amherst    #l\r\n#L2#No way I can get there    #k#l");
    	}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "2") || cm.checkQuestData(Quest_Rain_Quiz, "4") || cm.checkQuestData(Quest_Rain_Quiz, "6") || cm.checkQuestData(Quest_Rain_Quiz, "8") || cm.checkQuestData(Quest_Rain_Quiz, "10") || cm.checkQuestData(Quest_Rain_Quiz, "12"))
		{
			cm.sendYesNo("Alright, talk to me when you're ready to take on the next question.");
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
		if (YNMode == 1337 && mode == 1 && (cm.checkQuestData(Quest_Rain_Quiz, "") || cm.checkQuestData(Quest_Rain_Quiz, "1")))
		{
			cm.startQuest(Quest_Rain_Quiz);
			cm.setQuestData(Quest_Rain_Quiz, "1");
    		cm.sendOk("Hehe ... I'll have to warn you, don't underestimate the questions for the quiz. There's still a lot to learn about this place, you know. Well, I'll give you a quick rundown on the quiz. It'll be very quick, so you better listen carefully, because it'll all be on the quiz.");
    	}
		else if (YNMode != 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1") && selection == 0)
		{
			cm.setQuestData(Quest_Rain_Quiz, "2");
			cm.gainExp(2);
			cm.sendOk("That's right!! To open up the item inventory, you press #rI#k.");
		}
		else if (YNMode != 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1") && selection == 1)
		{
			cm.sendOk("K is for the Skill Window. You'll be able to use skills once you get yourself a job. Don't you remember the shortcut key for the i-tem inventory?");
			cm.dispose();
		}
		else if (YNMode != 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1") && selection == 2)
		{
			cm.sendOk("No, no, no. S is to check out your ability stats and the AP's. Come on, think!!");
			cm.dispose();
		}
		else if (YNMode != 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1") && selection == 3)
		{
			cm.sendOk("Eh? E is to check out the equipments you're donning and the ones you'd like to take off, so E is definitely not it.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "3") && selection == 0)
		{
			cm.setQuestData(Quest_Rain_Quiz, "4");
			cm.gainExp(2);
			cm.sendOk("Yup, you can wear an item just by double clicking it from your inventory. If you can't put it on, please check and see if your character matches or exceeds the level limit and the ability point requirements each item is assigned to.");
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "3") && selection == 1)
		{
			cm.sendOk("You'll have to try it for yourself. Press E, then take off an item by double-clicking it. Then, double-click the item from the item inventory (I) to put it back on.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "5") && selection == 0)
		{
			cm.setQuestData(Quest_Rain_Quiz, "6");
			cm.gainExp(6);
			cm.sendOk("That's right! To check out the equipments you're wearing, just press E.");
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "5") && selection == 1)
		{
			cm.sendOk("S is to check out the skill book and your skill points. Please remember that.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "5") && selection == 2)
		{
			cm.sendOk("I is to check out your item inventory.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "7") && selection == 0)
		{
			cm.sendOk("Once you see a chair or a bench you can sit on, you can do just that. Unfortunately, you won't find any chairs in Maple Island that you can sit on. Head over to Victoria Island to sit on some chairs, and when you find one, make sure to press X. You'll be able to tell that you're recovering much faster by sitting as opposed to just standing still.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "7") && selection == 1)
		{
			cm.sendOk("You can raise your ability stats using the AP's that you earn after every level-up. To check out the AP's, simply press S.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "7") && selection == 2)
		{
			cm.sendOk("I is used to check out your item inventory. It is one of the most useful functions in the game, one that'll enable you to check out the items you've collected.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "7") && selection == 3)
		{
			cm.setQuestData(Quest_Rain_Quiz, "8");
			cm.gainExp(10);
			cm.sendOk("That's correct. #bZ#k is used to pick up items on the ground dropped by the monsters. The other key you can use to pick up items would be #b0 on the number pad#k.\nAs for the seldom-used X, you can use that to sit on a chair. Once you head over to Victoria Island, you'll find some chairs you can sit on. You should try it, since you can recover much faster by sitting as opposed to just standing still! ");
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "9") && selection == 0)
		{
			cm.sendOk("You'll need to be at least level 10 in order to make the job advancement as either a warrior, a bowman, or a thief, but you can make the job adv. as a magician earlier than that.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "9") && selection == 1)
		{
			cm.setQuestData(Quest_Rain_Quiz, "10");
			cm.gainExp(10);
			cm.sendOk("That is correct!! You'll need to be at least level 10 in order to make the job advancement as either a warrior, a bowman, or a thief. To become a magician, however, you only need to be at level 8. Head over to Victoria Island, go to a magician town called Ellinia, and look for Grendel the Really Old, the chief magician that'll lead you towards becoming a magician yourself.");
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "11") && selection == 0)
		{
			cm.sendOk("Come on, you get more than 2 measly points~ you should check out the AP's you get after leveling up by pressing S.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "11") && selection == 1)
		{
			cm.sendOk("Nope, not 10 points. Press S to find out~");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "11") && selection == 2)
		{
			cm.setQuestData(Quest_Rain_Quiz, "12");
			cm.gainExp(10);
			cm.sendOk("That's right. Every time you level up, you'll be awarded #r5 AP's (Ability Point)#k, which can be used to raise your character's abilities. Press #bS#k after every level up to check out your character's abilities and the AP's. Make sure to assign those points well for maximum results.");
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "13") && selection == 0)
		{
			cm.setQuestData(Quest_Rain_Quiz, "14");
			cm.gainExp(20);
			cm.sendOk("That's absolutely correct. Head over to Southperry and get on the #rship#k that heads to Victoria Island.");
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "13") && selection == 1)
		{
			cm.sendOk("You won't find any ports at Amherst. Head over to the only place in Maple Island with a port to catch the ride to Victoria Island.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "13") && selection == 2)
		{
			cm.sendOk("You won't be able to come back here once you leave this place, but as for leaving ...");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "2"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "3");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "4"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "5");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "6"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "7");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "8"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "9");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "10"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "11");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "12"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "13");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
		if (YNMode == 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1"))
		{
    		cm.sendNext("Have you tried hunting there at the hunting ground? What key did you press to attack the monsters? Didn't you press #bCtrl for attack#k and #bAlt to jump#k? And you did press #bz to pick upk the items, right?\nYou can also use the #rKey Config#k, located on the bottom right corner of the game, to recalibrate the buttons to your liking. Phew, I guess I got too carried away with the explanations. You'll be picking up these in no time at the Maple Island.");
    	}
		else if (!cm.checkQuestData(Quest_Rain_Quiz, "1") && !cm.checkQuestData(Quest_Rain_Quiz, "14"))
		{
			cm.sendYesNo("Alright, talk to me when you're ready to take on the next question.");
		}
		else if (cm.checkQuestData(Quest_Rain_Quiz, "14"))
		{
			cm.sendOk("Once you get to Victoria Island, the first thing you'd want to do is make the job advancement, right? \nYou should set your level just right so you can make the job adv with the job of your choice. Head to Perion to become a warrior, Henesys for bowman, Kerning City for thief, and Ellinia for magician. If you're curious on where you are on the island, #bpress W for the world map#k. Always be aware of where you are at all times.\nAlright, your journey should get much more interesting now! Happy Mapling!!");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (status == 4)
	{
		if (YNMode == 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1"))
		{
    		cm.sendNextPrev("Now here's a brief description on the most important part of the game, the Job Advancement.\nTo become a more powerful traveler, you'll need to have a job. To obtain a job, you'll have to leave this place, the Maple Island, and head over to Victoria Island instead. \nYou'll need to be at least at level #b10#k to become either a warrior, a bowman, or a thief. To become a magician, however, you'll need to be only at level #b8#k. Each occupation has its plus's and minus's so choose carefully.");
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "2"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "3");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "4"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "5");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "6"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "7");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "8"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "9");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "10"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "11");
			cm.dispose();
		}
		else if (mode == 1 && cm.checkQuestData(Quest_Rain_Quiz, "12"))
		{
			cm.setQuestData(Quest_Rain_Quiz, "13");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (status == 5)
	{
		if (YNMode == 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1"))
		{
    		cm.sendPrev("I didn't tell you EVERYTHING you'll need to know for the quiz, so please make sure to focus for the quiz. Now, shall we get this started?");
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (status == 6)
	{
		if (YNMode == 1337 && cm.checkQuestData(Quest_Rain_Quiz, "1"))
		{
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