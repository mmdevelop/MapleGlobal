/* 
** NPC Name: Maya
** Location: 100000001 || Henesys Townstreet - Victoria Road
** Purpose: Maya and the Weird Medicine
** Made by: wackyracer
** FULLY GMS-like speech with very small missing part of dialog
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Maya = 1000200;
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
	 /*
	 // So here is the purpose of YNMode.
	 // Selecting Prev sets the mode to 0.
	 // Keep this in mind. There is a sendNextPrev and a sendYesNo on status 2.
	 // This else if statement of *mode* being *0* and *status* being *2* is being triggered in two different scenarios instead of just the one it serves (the sendYesNo scenario).
	 // The goal is for Prev to trigger the else case where status--; is called, not to trigger this case.
	 // So I made YNMode (standing for YesNoMode) to serve as a way for the system to better identify which if statement it should be running.
	 // Effectively, this makes it so that Prev reaches the else, and the No on YesNo leads to this.
	 // - wackyracer
	 */
	else if (mode == 0 && status == 2 && YNMode == 1)
	{
		YNMode = 0;
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, ""))
		{
    		cm.sendSimple("Cough... Cough... Ah... Headache... Can somebody help me?...\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bMaya of Henesys#k#l");
    	}
		else if (cm.getLevel() >= 15 && (cm.checkQuestData(Quest_Maya, "m1") || cm.checkQuestData(Quest_Maya, "m2") || cm.checkQuestData(Quest_Maya, "m3") || cm.checkQuestData(Quest_Maya, "m4") || cm.checkQuestData(Quest_Maya, "m5") || cm.checkQuestData(Quest_Maya, "m6")))
		{
    		cm.sendSimple("Cough... Cough... Ah... Headache... Can somebody help me?...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bMaya of Henesys (In Progress)#k#l");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m7"))
		{
			cm.sendSimple("Cough... Cough... Ah... Headache... Can somebody help me?...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bDelivering the Weird Medicine (Ready to complete.)#k#l");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "end"))
		{
			cm.sendOk("Thanks for the last time. Now I feel much better. Thanks for everything.");
			cm.dispose();
		}
		else
		{
    		cm.sendOk("Cough... Cough... Ah... Headache... Can somebody help me?...");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
		if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, ""))
		{
    		cm.sendNext("Cough ... cough ... ah ... oh, hello stranger. Sorry, but may I ask you for a favor? I've been suffering from sickness for a while, and the doctors can't do anything about it. Lately, it has gotten so bad I can't even take care of myself.");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m7") && cm.haveItem(4031006, 1, false, true))
		{
			cm.sendNext("Darn ... my whole body's aching ... what, oh my ... isn't that #b#t4031006##k?? How did you get it?? wow, you must be amazing.");
		}
		else
		{
			cm.sendOk("You haven't met up with #r#p1002001##k, yet? #p1002001# from #b#m104000000##k can definitely help you find some Weird Medicine. Please...talk\r\nto him for me...");
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, ""))
		{
			YNMode = 1;
    		cm.sendYesNo("Sorry to ask, but is there any way you can get me the #b#t4031006##k? I am not sure exactly how to get that medicine, but #r#p1002001##k from #b#m104000000##k may know a thing or two about it. Please help me out.");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m7") && cm.haveItem(4031006, 1, false, true))
		{
			cm.sendNextPrev("Um ... is it okay if I get that medicine? I'll give you something that I don't really need ... I urge you ... please ... I need that medicine ...");
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (mode == 1 && cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, ""))
		{
			cm.startQuest(Quest_Maya);
			cm.setQuestData(Quest_Maya, "m1");
    		cm.sendOk("#p1002001# from #b#m104000000##k can definitely help you find some Weird Medicine. Please...talk to him for me..."); // Little bit of missing dialog in here, but so little it is practically insignificant at this point.
			cm.dispose();
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m7") && cm.haveItem(4031006, 1, false, true))
		{
			cm.setQuestData(Quest_Maya, "end");
			cm.completeQuest(Quest_Maya);
			cm.gainItem(4031006, -1);
			cm.gainItem(1002026, 1);
			cm.gainExp(2000);
			cm.gainMeso(5000);
			cm.sendOk("Thank you so much ... this may cure my longtime sickness afterall ... here's something I don't really need ... hopefully it'll help you through your journey ... here are some mesos also as a sign of my appreciation ...\r\n\r\n#e#rREWARD:#k\r\n+5,000 mesos\r\n+2,000 exp\r\n+#i1002026# #t1002026#");
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