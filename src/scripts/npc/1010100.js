/* 
** NPC Name: Rina
** Location: Henesys / 100000000
** Purpose: Sauna Robe Quest
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Sauna_Robe = 1000600;
var Quest_Unagi = 1090901;
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
	else if (status == 3 && mode == 0)
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
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, ""))
		{
    		cm.sendSimple("This town is made by the group of bowmen. If you want to become a bowman, please meet with #r#p1012100##k... She will help you. What? You don't know #r#p1012100##k? She saved our town long time ago from the monsters. Of course, it is safe now. She is the hero of our town.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bHungry Ronnie (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, "1") && (!cm.haveItem(4000013, 50, false, true) || !cm.haveItem(4000017, 5, false, true)))
		{
    		cm.sendSimple("This town is made by the group of bowmen. If you want to become a bowman, please meet with #r#p1012100##k... She will help you. What? You don't know #r#p1012100##k? She saved our town long time ago from the monsters. Of course, it is safe now. She is the hero of our town.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bSecret to Unagi Special (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, "1") && cm.haveItem(4000013, 50, false, true) && cm.haveItem(4000017, 5, false, true))
		{
    		cm.sendSimple("This town is made by the group of bowmen. If you want to become a bowman, please meet with #r#p1012100##k... She will help you. What? You don't know #r#p1012100##k? She saved our town long time ago from the monsters. Of course, it is safe now. She is the hero of our town.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bSecret to Unagi Special (Ready to complete.)#k#l");
    	}
		else if ((cm.checkQuestData(Quest_Sauna_Robe, "2") || cm.checkQuestData(Quest_Sauna_Robe, "3")) && cm.checkQuestData(Quest_Unagi, "2"))
		{
    		cm.sendSimple("This town is made by the group of bowmen. If you want to become a bowman, please meet with #r#p1012100##k... She will help you. What? You don't know #r#p1012100##k? She saved our town long time ago from the monsters. Of course, it is safe now. She is the hero of our town.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bHungry Ronnie (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "e"))
		{
			cm.sendOk("Oh... You are the one who gave the stuff back? So what's up? Is #p1012102# doing fine? If you get to #m100000000# someday, please say hello to #p1012102# for me.");
    		cm.dispose();
		}
		else
		{
    		cm.sendOk("This town is made by the group of bowmen. If you want to become a bowman, please meet with #r#p1012100##k... She will help you. What? You don't know #r#p1012100##k? She saved our town long time ago from the monsters. Of course, it is safe now. She is the hero of our town.");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, ""))
		{
    		cm.sendNext("So you came here through a favor by #p1061004#? Hahaha ... hopefully #p1061004# is not in any trouble this time around. Anyway, he wants the #bUnagi special#k, huh? It's pretty easy, so why don't you just sit around and wait for a little bit as I make this dish...");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, "1") && (!cm.haveItem(4000013, 50, false, true) || !cm.haveItem(4000017, 5, false, true)))
		{
    		cm.sendOk("Please get me #b50 #b#t4000013#s and #b5 #t4000017#s#k. Then I'll get you #p1061004#'s favorite, the Unagi Special.");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, "1") && cm.haveItem(4000013, 50, false, true) && cm.haveItem(4000017, 5, false, true))
		{
			cm.sendNext("You got all the ingredients!! I knew you'd be the one to do it... alright, now just wait oooone second. I, Rina, proudly present the #bUnagi special#k!");
		}
		else if ((cm.checkQuestData(Quest_Sauna_Robe, "2") || cm.checkQuestData(Quest_Sauna_Robe, "3")) && cm.checkQuestData(Quest_Unagi, "2") && !cm.haveItem(4031014, 1, false, true))
		{
    		cm.gainItem(4031014, 1);
			cm.sendOk("Ok, here it is, the #bUnagi Special#k! You should take this to \r\n#p1061004# before it gets cold. It's #p1061004#'s favorite.");
			cm.dispose();
    	}
		else
		{
			cm.sendOk("You should take the #bUnagi Special#k to #p1061004# before it gets cold. It's #p1061004#'s favorite.");
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, ""))
		{
    		cm.sendNextPrev("Oh shoot. I'm lacking #b#t4000013##k and #b#t4000017##k, the most important ingredients for Unagi. Do these really go in the dish? oh of course~~just please make this a secret from #p1061004#, ok?");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, "1") && cm.haveItem(4000013, 50, false, true) || cm.haveItem(4000017, 5, false, true))
		{
			if (cm.haveItem(4031015, 1, false, true))
			{
				cm.setQuestData(Quest_Sauna_Robe, "3");
			}
			cm.setQuestData(Quest_Unagi, "2");
			cm.gainItem(4000013, -50);
	    	cm.gainItem(4000017, -5);
			cm.gainExp(500);
			cm.gainItem(4031014, 1);
			cm.sendOk("Ok, here it is, the #bUnagi Special#k! You should take this to \r\n#p1061004# before it gets cold. It's #p1061004#'s favorite.");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, ""))
		{
    		cm.sendYesNo("Anyway I don't have enough ingredients for Unagi. Sorry but can you gather up the ingredients for me? #b50 #t4000013#s and 5 #t4000017#s#k and then the #bUnagi Special#k will be made.");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
		if (mode == 1 && cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Unagi, ""))
		{
			cm.startQuest(Quest_Unagi);
    		cm.setQuestData(Quest_Unagi, "1");
			cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    } 
}