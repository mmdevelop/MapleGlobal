/* 
** NPC Name: Ronnie
** Location: The Tree Tunnel At The Forest Up North
** Purpose: Sauna Robe Quest
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Sauna_Robe = 1000600;
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
    	if (cm.checkQuestData(Quest_Sauna_Robe, "1") && cm.getLevel() >= 30)
		{
    		cm.sendSimple("Now what exactly is in this book that makes my dad take care of it so much? I want to know what's inside, but I don't think I'll understand it one bit...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bA Clue to the Secret Book (Ready to complete.)#k#l");
    	}
		else if ((cm.checkQuestData(Quest_Sauna_Robe, "2")) || (cm.checkQuestData(Quest_Sauna_Robe, "3") && !cm.haveItem(4031014, 1, false, true) || !cm.haveItem(4031015, 1, false, true) || !cm.haveItem(4000029, 50, false, true)) && cm.getLevel() >= 30)
		{
    		cm.sendSimple("Now what exactly is in this book that makes my dad take care of it so much? I want to know what's inside, but I don't think I'll understand it one bit...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bHungry Ronnie (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "3") && cm.haveItem(4031014, 1, false, true) && cm.haveItem(4031015, 1, false, true) && cm.haveItem(4000029, 50, false, true) && cm.getLevel() >= 30)
		{
    		cm.sendSimple("Now what exactly is in this book that makes my dad take care of it so much? I want to know what's inside, but I don't think I'll understand it one bit...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bHungry Ronnie (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "e") && cm.getLevel() >= 30)
		{
    		cm.sendOk("You got him the book, right? I still don't think he even cares about me. I should just go back to the fairy town and stay there.");
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("Now what exactly is in this book that makes my dad take care of it so much? I want to know what's inside, but I don't think I'll understand it one bit...");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "1"))
		{
    		cm.sendNext("Hey who are you? You know my dad well? Ah... you want this red book, huh?? I see...my daddy likes this book more than he likes me! This book ... NO, I can't give you this book!! (stomach growling...) Ahhhh!");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2"))
		{
    		cm.sendOk("You didn't get all my food yet?? Bring #b50 #t4000029#s#k, #p1010100# from #m100000000#'s #bUnagi Special#k, and #b#t4031015##k from the fairies of #m101000000#, and I'll give you back my dad's book. I promise~!!");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "3"))
		{
    		if (cm.haveItem(4031014, 1, false, true) && cm.haveItem(4031015, 1, false, true) && cm.haveItem(4000029, 50, false, true))
			{
    			cm.setQuestData(Quest_Sauna_Robe, "4");
    			cm.gainItem(4031014, -1);
    			cm.gainItem(4031015, -1);
    			cm.gainItem(4000029, -50);
    			cm.gainItem(4031016, 1);
    			cm.gainExp(300);
    			cm.sendOk("Wow...! You DID bring all that food!!!! Sweeeeeeet!!! Thank you so, so, soo much!! Oh yeah, a promise is a promise...here's my dad's book. I have no idea what this book is about, but...why is he so gaga over this anyway??");
    			cm.dispose();
    		}
			else
			{
    			cm.sendOk("You didn't get all my food yet?? Bring #b50 #t4000029#s#k, #p1010100# from #m100000000#'s #bUnagi Special#k, and #b#t4031015##k from the fairies of #m101000000#, and I'll give you back my dad's book. I promise~!!");
    			cm.dispose();
    		}
    	}
    }
	
	else if (status == 2)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "1"))
		{
    		cm.sendNextPrev("No... no... I'm NOT hungry ...! Dang, all, alright. I'll give you back the book. BUT! Not for free! I am really starving, and I need some food, so...if you get me something to eat, the book is yours. I promise!");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (no)
		{
    		cm.sendOk("Well... I guess you don't want the book then...");
    		cm.dispose();
    	}
    	else if (cm.checkQuestData(Quest_Sauna_Robe, "1"))
		{
    		cm.sendNextPrev("I want... #b50 #t4000029#s#k and #p1010100#'s #bUnagi Special#k, along with a #b#t4031015##k. #p1010100# is my awesome friend that lives in #m100000000#. Ask her for the Unagi Special and she'll make it for you.");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "1"))
		{
    		cm.sendYesNo("Oh yeah! The fairies from #m101000000# probably have #b#t4031015##k. I usually ate it at #m101000000#. If you ever get hungry on the way back and eat a couple of those... my dad's book is going to #o3230100#, so you better take care of that food!!!");
    		no = true;
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 5)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "1"))
		{
    		cm.setQuestData(Quest_Sauna_Robe, "2");
    		cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    }
}