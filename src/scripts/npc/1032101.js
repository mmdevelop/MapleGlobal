/* 
** NPC Name: Rowen the Fairy
** Location: Ellinia
** Purpose: Sauna Robe Quest
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Sauna_Robe = 1000600;
var Quest_Milk = 1090902;
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
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, ""))
		{
    		cm.sendSimple("Do you need me for something? Please don't bother me unless you need me right this minute.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bHungry Ronnie (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, "1") && !cm.haveItem(4021007, 1, false, true))
		{
    		cm.sendSimple("Do you need me for something? Please don't bother me unless you need me right this minute.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bCold Milk (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, "1") && cm.haveItem(4021007, 1, false, true))
		{
    		cm.sendSimple("Do you need me for something? Please don't bother me unless you need me right this minute.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bCold Milk (Ready to complete.)#k#l");
    	}
		else if ((cm.checkQuestData(Quest_Sauna_Robe, "2") || cm.checkQuestData(Quest_Sauna_Robe, "3")) && cm.checkQuestData(Quest_Milk, "2"))
		{
    		cm.sendSimple("Do you need me for something? Please don't bother me unless you need me right this minute.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bHungry Ronnie (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "e"))
		{
			cm.sendOk("It's you, my savior. #o4230101# hasn't been attacking our town ever since you helped us out the other day. How are you doing these days?");
    		cm.dispose();
		}
		else
		{
    		cm.sendOk("Do you need me for something? Please don't bother me unless you need me right this minute.");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, ""))
		{
    		cm.sendNext("Hmmm... I'm sure you're aware of the fact that we fairies aren't exactly friends with humans ... but if you're bothering us, there must be a valid reason for it ... so how can I help you?");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, "1") && !cm.haveItem(4021007, 1, false, true))
		{
    		cm.sendOk("Haven't gotten the refined #b#t4021007##k yet? Come to me once you have gotten #t4021007#. I'll give you the #t4031015# from Ellinia.");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, "1") && cm.haveItem(4021007, 1, false, true))
		{
			cm.sendNext("Wow...! This is one very well-refined #t4021007#! If you give it to me, I'll give you something that #p1061004# likes, which is #b#t4031015##k. Will you give the #t4021007# to me?");
		}
		else if ((cm.checkQuestData(Quest_Sauna_Robe, "2") || cm.checkQuestData(Quest_Sauna_Robe, "3")) && cm.checkQuestData(Quest_Milk, "2") && !cm.haveItem(4031015, 1, false, true))
		{
    		cm.gainItem(4031015, 1);
			cm.sendOk("Please get #b#t4031015##k to #p1061004#. I'll take good care of Diamond that you got me.");
			cm.dispose();
    	}
		else
		{
			cm.sendOk("Please get #b#t4031015##k to #p1061004#. I'll take good care of Diamond that you got me.");
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, ""))
		{
    		cm.sendNextPrev("So you're here as a favor from #p1061004# to get back #bFresh\r\nMilk#k? #p1061004# is the one boy that we have opened up to ... hmm, please hold on one second, I'll get you the Fresh Milk.");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, "1") && cm.haveItem(4021007, 1, false, true))
		{
			if (cm.haveItem(4031014, 1, false, true))
			{
				cm.setQuestData(Quest_Sauna_Robe, "3");
			}
			cm.setQuestData(Quest_Milk, "2");
	    	cm.gainItem(4021007, -1);
			cm.gainExp(500);
			cm.gainItem(4031015, 1);
			cm.sendOk("Thank you very much. Please get #b#t4031015##k to #p1061004#. I'll take good care of #t4021007# that you got me.");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, ""))
		{
    		cm.sendYesNo("But then again, I can't give you this just like that. You're also aware that we fairies love fancy, spectacular items, right? If you get me a refined #b#t4021007##k, then #t4031015# is yours.");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
		if (mode == 1 && cm.checkQuestData(Quest_Sauna_Robe, "2") && cm.checkQuestData(Quest_Milk, ""))
		{
			cm.startQuest(Quest_Milk);
    		cm.setQuestData(Quest_Milk, "1");
			cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    } 
}