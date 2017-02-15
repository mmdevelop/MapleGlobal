/* 
** NPC Name: John
** Location: 104000000 || Lith Harbor - Victoria Road
** Purpose: John's Pink Flower Basket
** Made by: wackyracer
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_John1 = 0005500;
var Quest_John2 = 0005501;
var Quest_John3 = 0005502;
var status;
var YNMode1 = 0;
var YNMode2 = 0;
var YNMode3 = 0;
var text = "Is there anyone who can help me? Well... I would like to go by myself, but as you can see, I have lots of stuff to do...";

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
	 /*
	 // So here is the purpose of YNMode.
	 // Selecting Prev sets the mode to 0.
	 // Keep this in mind. There is a sendNextPrev and a sendYesNo on status 2.
	 // This else if statement of *mode* being *0* and *status* being *2* is being triggered in two different scenarios instead of just the one it serves (the sendYesNo scenario).
	 // The goal is for Prev to trigger the else case where status--; is called, not to trigger this case.
	 // So I made YNMode (standing for YesNoMode) to serve as a way for the system to better identify which if statement it should be running.
	 // Effectively, this makes it so that Prev reaches the else, and the No on YesNo leads to this.
	 // Also, specifically for John, this variable helps the script navigate to the correct if statements depending on each individual case (and there are a lot of cases).
	 // I even made multiple YNMode variables just for John because he has so much different cases. Probably unnecessary, could have done it in one variable, but screw it.
	 // - wackyracer
	 */
	else if (mode == 0 && status == 1 && YNMode1 == 1337)
	{
		YNMode1 = 0;
		cm.sendOk("I understand ... my wedding anniversary is coming up and I am screwed! Please come back if you have some spare time.");
		cm.dispose();
		return;
	}
	else if (mode == 0 && status == 1 && YNMode2 == 1337)
	{
		YNMode2 = 0;
		cm.sendOk("I understand ... my wife's birthday is coming up and I am screwed! Please come back if you have some spare time.");
		cm.dispose();
		return;
	}
	else if (mode == 0 && status == 1 && YNMode3 == 1337)
	{
		YNMode2 = 0;
		cm.sendOk("I see ... my mother loved looking at those flowers while she was alive ... I wished that you could get them for me and for her ... I understand ...");
		cm.dispose();
		return;
	}
	else
	{ 
        status--;
    }
	
	if (status == 0)
	{
		if (cm.getLevel() >= 15)
		{
			if (cm.checkQuestData(Quest_John1, "1"))
			{
				if (cm.haveItem(4031025, 10, false, true))
				{
					text += "\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bJohn's Pink Flower Basket (Ready to complete.)#k#l";
				}
				else
				{
					text += "\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bJohn's Pink Flower Basket (In Progress)#k#l";
				}
			}
			else if (cm.checkQuestData(Quest_John1, ""))
			{
				text += "\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bJohn's Pink Flower Basket#k#l";
			}
			else if (cm.checkQuestData(Quest_John1, "2") && cm.getLevel() <= 29)
			{
				text = "You are the one, who brought the flower to me. Again, thanks a lot.. Feel free to stay in this town.";
			}
		}
		if (cm.getLevel() >= 30)
		{
			if (cm.checkQuestData(Quest_John2, "1"))
			{
				if (cm.haveItem(4031026, 20, false, true))
				{
					text += "\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L1##bJohn's Present (Ready to complete.)#k#l";
				}
				else
				{
					text += "\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L1##bJohn's Present (In Progress)#k#l";
				}
			}
			else if (cm.checkQuestData(Quest_John2, ""))
			{
				text += "\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L1##bJohn's Present#k#l";
			}
			else if (cm.checkQuestData(Quest_John1, "2") && cm.checkQuestData(Quest_John2, "2") && cm.getLevel() <= 59)
			{
				text = "You are the one, who brought the flower to me. Again, thanks a lot.. Feel free to stay in this town.";
			}
		}
		if (cm.getLevel() >= 60)
		{
			if (cm.checkQuestData(Quest_John3, "1"))
			{
				if (cm.haveItem(4031028, 30, false, true))
				{
					text += "\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L2##bJohn's Last Present (Ready to complete.)#k#l";
				}
				else
				{
					text += "\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L2##bJohn's Last Present (In Progress)#k#l";
				}
			}
			else if (cm.checkQuestData(Quest_John3, ""))
			{
				text += "\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L2##bJohn's Last Present#k#l";
			}
			else if (cm.checkQuestData(Quest_John1, "2") && cm.checkQuestData(Quest_John2, "2") && cm.checkQuestData(Quest_John3, "2"))
			{
				text = "You are the one, who brought the flower to me. Again, thanks a lot.. Feel free to stay in this town.";
			}
		}
		cm.sendSimple(text);
    }
	
	else if (status == 1)
	{
		if (cm.checkQuestData(Quest_John1, "") && selection == 0 && YNMode1 == 0)
		{
			YNMode1 = 1337;
    		cm.sendYesNo("How's traveling these days? I actually have a favor to ask you ... this time, my wedding anniversary is coming up and I need flowers. Can you get them for me?");
    	}
		else if (cm.checkQuestData(Quest_John1, "1") && !cm.haveItem(4031025, 10, false, true) && selection == 0 && YNMode1 == 0)
		{
			cm.sendOk("You haven't gotten #b#t4031025##k yet. There's #p1061006# at #m105040300# and I heard that with that you can go to the place where #t4031025##k is. Please go into the forest and collect #t4031025##k for me. I need 10 to make my wedding anniversary");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_John1, "1") && cm.haveItem(4031025, 10, false, true) && selection == 0 && YNMode1 == 0)
		{
			YNMode1 = 13337;
			YNMode2 = 777;
			YNMode3 = 777;
			cm.sendNext("Ohhh ... you got me #b10 #b#t4031025#s#k~! This is awesome ... I can't believe you went deep into the forest and got these flowers ... there's a story about this flower where it supposedly doesn't die for 100 years. With this, I can make my wife happy.");
		}
		else if (cm.checkQuestData(Quest_John1, "1") && cm.haveItem(4031025, 10, false, true) && YNMode1 == 13337)
		{
			cm.sendNext("Ohhh ... you got me #b10 #b#t4031025#s#k~! This is awesome ... I can't believe you went deep into the forest and got these flowers ... there's a story about this flower where it supposedly doesn't die for 100 years. With this, I can make my wife happy.");
		}
		else if (cm.checkQuestData(Quest_John2, "") && selection == 1 && YNMode2 == 0)
		{
			YNMode2 = 1337;
			cm.sendYesNo("Ohhhh, you're the one that helped me out the other day. You look much stronger now. How's traveling these days? I actually have another favor to ask you ... this time, my wife's birthday is coming up and I need more flowers. Can you get them for me?");
		}
		else if (cm.checkQuestData(Quest_John2, "1") && !cm.haveItem(4031026, 20, false, true) && selection == 1 && YNMode2 == 0)
		{
			cm.sendOk("You haven't gotten the #b#t4031026##k yet. There's #p1061006# at #m105040300# and I heard that with that you can go to the place where #t4031026##k is. Please go into the forest and collect #t4031026##k for me. I need 20 to make my wife's birthday present.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_John2, "1") && cm.haveItem(4031026, 20, false, true) && selection == 1 && YNMode2 == 0)
		{
			YNMode1 = 777;
			YNMode2 = 13337;
			YNMode3 = 777;
			cm.sendNext("Ohhh ... you got me #b20 #b#t4031026#s#k~! This is awesome ... I can't believe you went deep into the forest and got these flowers ... there's a story about this flower where it supposedly doesn't die for 500 years. With this, I can make the whole house smell like flowers.");
		}
		else if (cm.checkQuestData(Quest_John2, "1") && cm.haveItem(4031026, 20, false, true) && YNMode2 == 13337)
		{
			cm.sendNext("Ohhh ... you got me #b20 #b#t4031026#s#k~! This is awesome ... I can't believe you went deep into the forest and got these flowers ... there's a story about this flower where it supposedly doesn't die for 500 years. With this, I can make the whole house smell like flowers.");
		}
		else if (cm.checkQuestData(Quest_John3, "") && selection == 2 && YNMode3 == 0)
		{
			YNMode3 = 1337;
			cm.sendYesNo("Ohhh...you're the person that did me huge favors a while ago. You look so much stronger now that I can't even recognize you anymore. By now it looks like you have gone pretty much everywhere. I have one last favor to ask you. Well, my mother passed away a few days ago of old age. I need a special kind of flower for her on her grave ... can you get them for me?");
		}
		else if (cm.checkQuestData(Quest_John3, "1") && !cm.haveItem(4031028, 30, false, true) && selection == 2 && YNMode3 == 0)
		{
			cm.sendOk("You haven't gotten #b#t4031028##k yet. There's #p1061006# at #m105040300# and I heard that with that you can go to the place where #t4031028##k is. Please go into the forest and collect #t4031028##k for me. I need 30 to make a wreath");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_John3, "1") && cm.haveItem(4031028, 30, false, true) && selection == 2 && YNMode3 == 0)
		{
			YNMode1 = 777;
			YNMode2 = 777;
			YNMode3 = 13337;
			cm.sendNext("Ohhh ... you got me all #b30 #t4031028#s#k! This is awesome ... I can't believe you went deep into the forest and got these flowers... there's a story about this flower where it supposedly doesn't die for 1000 years and it glows on its own. I can make a nice wreath out of this and bring it to my mother's grave...");
		}
		else if (cm.checkQuestData(Quest_John3, "1") && cm.haveItem(4031028, 30, false, true) && YNMode3 == 13337)
		{
			cm.sendNext("Ohhh ... you got me all #b30 #t4031028#s#k! This is awesome ... I can't believe you went deep into the forest and got these flowers... there's a story about this flower where it supposedly doesn't die for 1000 years and it glows on its own. I can make a nice wreath out of this and bring it to my mother's grave...");
		}
		else
		{
			cm.sendOk("ERROR 4593: CASE NOT HANDLED. AUTO-DISPOSING...");
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (mode == 1 && cm.checkQuestData(Quest_John1, "") && YNMode1 == 1337)
		{
			cm.startQuest(Quest_John1);
			cm.setQuestData(Quest_John1, "1");
    		cm.sendNext("Thank you! This time I'd like to give my wife #b#t4031025##k ... It has a very pleasant scent, and I heard it's found deep in the forest ... I heard the place where it exists doesn't let everyone in; only a select few, I think. Something about #p1061006# at #m105040300# and something something ...");
    	}
		else if (cm.checkQuestData(Quest_John1, "1") && YNMode1 == 1337)
		{
			cm.sendNext("Thank you! This time I'd like to give my wife #b#t4031025##k ... It has a very pleasant scent, and I heard it's found deep in the forest ... I heard the place where it exists doesn't let everyone in; only a select few, I think. Something about #p1061006# at #m105040300# and something something ...");
		}
		else if (cm.checkQuestData(Quest_John1, "1") && cm.haveItem(4031025, 10, false, true) && YNMode1 == 13337)
		{
			cm.sendNextPrev("Oh, and ... since you have worked hard for me, I should reward you well. I found some #t4003000# in the ship. Some travelers leave things here and there. It looks like something you may need, so take it.");
		}
		else if (mode == 1 && cm.checkQuestData(Quest_John2, "") && YNMode2 == 1337)
		{
			cm.startQuest(Quest_John2);
			cm.setQuestData(Quest_John2, "1");
    		cm.sendNext("Thank you! This time I'd like to give my wife #b#t4031026##k ... It has a very pleasant scent, and I heard it's found deep in the forest ... I heard the place where it exists doesn't let everyone in; only a selected few, I think. Something about #p1061006# at #m105040300# and something something ...");
    	}
		else if (cm.checkQuestData(Quest_John2, "1") && YNMode2 == 1337)
		{
			cm.sendNext("Thank you! This time I'd like to give my wife #b#t4031026##k ... It has a very pleasant scent, and I heard it's found deep in the forest ... I heard the place where it exists doesn't let everyone in; only a selected few, I think. Something about #p1061006# at #m105040300# and something something ...");
		}
		else if (cm.checkQuestData(Quest_John2, "1") && cm.haveItem(4031026, 20, false, true) && YNMode2 == 13337)
		{
			cm.sendNextPrev("Oh, and ... since you have worked hard for me, I should reward you well. I found this glove in the ship. Some travelers leave things here and there. It looks like something you may need, so take it.");
		}
		else if (mode == 1 && cm.checkQuestData(Quest_John3, "") && YNMode3 == 1337)
		{
			cm.startQuest(Quest_John3);
			cm.setQuestData(Quest_John3, "1");
    		cm.sendNext("Thank you so much! The flowers I want on her grave are called #b#t4031028##k and it's a very rare kind. I heard it's found deep in the forest ... I heard the place where it exists doesn't let everyone in; only a select few, I think. Something about #p1061006# at #m105040300# and something something ...");
    	}
		else if (cm.checkQuestData(Quest_John3, "1") && YNMode3 == 1337)
		{
			cm.sendNext("Thank you so much! The flowers I want on her grave are called #b#t4031028##k and it's a very rare kind. I heard it's found deep in the forest ... I heard the place where it exists doesn't let everyone in; only a select few, I think. Something about #p1061006# at #m105040300# and something something ...");
		}
		else if (cm.checkQuestData(Quest_John3, "1") && cm.haveItem(4031028, 30, false, true) && YNMode3 == 13337)
		{
			cm.sendNextPrev("Oh, and... since you have worked hard for me, I should reward you well. My late mother left me this earing before passing away. Apparently she had it since she was young. I don't know, but it seems to have some kind of a special power hidden in it ... anyway please take it.");
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (cm.checkQuestData(Quest_John1, "1") && YNMode1 == 1337)
		{
    		cm.sendPrev("Please get me #b10 #t4031025#s#k. I think 10 will cover the house with that pleasant scent. Please hurry!");
		}
		else if (cm.checkQuestData(Quest_John1, "1") && cm.haveItem(4031025, 10, false, true) && YNMode1 == 13337)
		{
			YNMode1 = 0;
			cm.setQuestData(Quest_John1, "2");
			cm.gainItem(4031025, -10);
			cm.gainItem(4003000, 30);
			cm.gainExp(300);
			cm.sendOk("If you have time, why not try going back into the forest? You may find an important item in there. I can't guarantee it since obviously I've never been there before, so please don't come back complaining if all you can find is trash.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_John2, "1") && YNMode2 == 1337)
		{
			cm.sendPrev("Please get me #b20 #t4031026#s#k. I think 20 will cover the house with that pleasant scent. Please hurry!");
		}
		else if (cm.checkQuestData(Quest_John2, "1") && cm.haveItem(4031026, 20, false, true) && YNMode2 == 13337)
		{
			YNMode2 = 0;
			cm.setQuestData(Quest_John2, "2");
			cm.gainItem(4031026, -20);
			cm.gainExp(2000);
			if (cm.getJob() == 0)
			{
				cm.gainItem(1082002, 1);
			}
			else if (cm.getJob() == 100 || cm.getJob() == 110 || cm.getJob() == 111 || cm.getJob() == 112 || cm.getJob() == 120 || cm.getJob() == 121 || cm.getJob() == 122 || cm.getJob() == 130 || cm.getJob() == 131 || cm.getJob() == 132)
			{
				cm.gainItem(1082036, 1);
			}
			else if (cm.getJob() == 200 || cm.getJob() == 210 || cm.getJob() == 211 || cm.getJob() == 212 || cm.getJob() == 220 || cm.getJob() == 221 || cm.getJob() == 222 || cm.getJob() == 230 || cm.getJob() == 231 || cm.getJob() == 232)
			{
				cm.gainItem(1082056, 1);
			}
			else if (cm.getJob() == 300 || cm.getJob() == 310 || cm.getJob() == 311 || cm.getJob() == 312 || cm.getJob() == 320 || cm.getJob() == 321 || cm.getJob() == 322)
			{
				cm.gainItem(1082070, 1);
			}
			else if (cm.getJob() == 400 || cm.getJob() == 410 || cm.getJob() == 411 || cm.getJob() == 412 || cm.getJob() == 420 || cm.getJob() == 421 || cm.getJob() == 422)
			{
				cm.gainItem(1082045, 1);
			}
			cm.sendOk("If you have time, why not try going back into the forest? You may find an important item in there. I can't guarantee it since obviously I've never been there before, so please don't come back complaining if all you can find is trash.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_John3, "1") && YNMode3 == 1337)
		{
    		cm.sendPrev("Please get me #b30 #t4031028#s#k. I want to get a whole bunch for my mother's grave. Please hurry!");
		}
		else if (cm.checkQuestData(Quest_John3, "1") && cm.haveItem(4031028, 30, false, true) && YNMode3 == 13337)
		{
			YNMode3 = 0;
			cm.setQuestData(Quest_John3, "2");
			cm.gainItem(4031028, -30);
			cm.gainItem(1032014, 1);
			cm.gainExp(4000);
			cm.sendOk("If you have time, why not try going back into the forest? You may find an important item in there. I can't guarantee it since obviously I've never been there before, so please don't come back complaining if all you can find is trash.");
			cm.dispose();
		}
		else 
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
		if (cm.checkQuestData(Quest_John1, "1") && YNMode2 == 1337)
		{
			YNMode1 = 0;
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_John2, "1") && YNMode2 == 1337)
		{
			YNMode2 = 0;
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_John3, "1") && YNMode3 == 1337)
		{
			YNMode3 = 0;
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