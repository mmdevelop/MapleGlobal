/* 
** NPC Name: Teo
** Location: Lith Harbor
** Purpose: Part of Maya's Weird Medicine quest
** Made by: wackyracer / Joren McGrew
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Maya = 1000200;
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
	else if (mode == 0 && status == 1)
	{
		cm.dispose();
		return;
	}
	else if (mode == 0 && status == 3)
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
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m1"))
		{
    		cm.sendSimple("I heard that #p1012101# is sick again. Sad...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bMaya of Henesys (Ready to complete.)#k#l");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m2"))
		{
    		cm.sendSimple("I heard that #p1012101# is sick again. Sad...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bFinding Sophia (In Progress)#k#l");
    	}
		else if ((cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m6") && !cm.haveItem(4031004, 1, false, true)) || (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m3") || cm.checkQuestData(Quest_Maya, "m4") || cm.checkQuestData(Quest_Maya, "m5")))
		{
			cm.sendSimple("I heard that #p1012101# is sick again. Sad...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bMaking Sparkling Rock (In Progress)#k#l");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m6") && cm.haveItem(4031004, 1, false, true))
		{
			cm.sendSimple("I heard that #p1012101# is sick again. Sad...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bMaking Sparkling Rock (Ready to complete.)#k#l");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m7"))
		{
			cm.sendSimple("I heard that #p1012101# is sick again. Sad...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bDelivering the Weird Medicine (In Progress)#k#l");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "end"))
		{
			cm.sendOk("Oh~ So it seems that you have delievered #b#t4031006##k to #p1012101#...! Thanks. Now #p1012101# can get better now. huh? #t4031004# It is still very beautiful~");
			cm.dispose();
		}
		else
		{
    		cm.sendOk("I heard that #p1012101# is sick again. Sad...");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
		if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m1"))
		{
    		cm.sendNext("Well, I do have the #b#t4031006##k ... but I can't give it to you for free. If you get me the #b#t4031004##k, though, then I may reconsider ... meaning I am willing to trade with you...");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m6") && cm.haveItem(4031004, 1, false, true))
		{
			cm.sendYesNo("Ohhh ... this ... it's #b#t4031004##k!!! How did you get this?? That's just incredible!! How about trading that stone with me? I'll give you #b#t4031006##k in return!");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m7") && cm.haveItem(4031006, 1, false, true))
		{
			cm.sendOk("Hurry and give #p1012101# the #b#t4031006##k that I gave you. #m100000000# is very far from here so make sure you don't lose that medicine ...!");
			cm.dispose();
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m7") && !cm.haveItem(4031006, 1, false, true))
		{
			cm.gainItem(4031006, 1);
			cm.sendOk("Hurry and give #p1012101# the #b#t4031006##k that I gave you. #m100000000# is very far from here so make sure you don't lose that medicine ...!");
			cm.dispose();
		}
		else
		{
			cm.sendOk("Didn't get #b#t4031004##k yet? Oh well ... those two aren't the easiest things to acquire ... they look gorgeous when they shine like stars ... hurry and go see #r#p1022100##k from the department store at #b#m102000000##k.");
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m1"))
		{
    		cm.sendNextPrev("Do you want to know how to get that stone? I wouldn't be asking for help if I knew how ... here's the deal, how about going to the department store at #b#m102000000##k, ask for the daughter of the owner, #r#p1022100##k, and ask her about its whereabouts? She may have a clue ...");
    	}
		else if (mode == 1 && cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m6") && cm.haveItem(4031004, 1, false, true))
		{
			cm.setQuestData(Quest_Maya, "m7");
			cm.gainItem(4031004, -1);
			cm.gainItem(4031006, 1);
			cm.sendOk("Thank you!!! Please take this medicine instead. #r#p1012101##k from #b#m100000000##k is sick again. This will take care of the sickness a little bit...");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m1"))
		{
    		cm.sendYesNo("Please don't tell me you have no idea how to get to #m102000000#. Ok, take the exit on the right from the harbor, go past #m103000000# up northwest, and then keep going east, then you'll find #m102000000#. Or do you know all this??"); // Little bit of missing dialog in here, but so little it is practically insignificant at this point.
		}
		else 
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
		if (mode == 1 && cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m1"))
		{
			cm.setQuestData(Quest_Maya, "m2");
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