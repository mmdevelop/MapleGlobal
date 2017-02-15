/* 
** NPC Name: Lucas
** Location: Maple Island
** Purpose: Letter For Lucas / Lucas' Reply
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Lucas_Letter = 0000004;
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
	else if (mode == 0 && status == 1)
	{
    	cm.sendOk("Are you really busy? Ahhhh, this is not good. If I don't get this to Maria... anyway if you have some free time please come back and talk to me.");
    	cm.dispose();
		return;
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
		if (cm.checkQuestData(Quest_Lucas_Letter, "1"))
		{
    		cm.sendSimple("A letter from #r#p2103##k should be here, any minute. Is there something wrong? Hmm... \r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bLetter For Lucas (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "2"))
		{
    		cm.sendSimple("A letter from #r#p2103##k should be here, any minute. Is there something wrong? Hmm... \r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bLucas' Reply (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "3"))
		{
    		cm.sendOk("You safely delivered my reply to #r#p2103##k? Thanks~ so #r#p2103##k gave you a present? Haha... She is one of the best for making a hat....");
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("A letter from #r#p2103##k should be here, any minute. Is there something wrong? Hmm... ");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Lucas_Letter, "2") && cm.haveItem(4031001, 1, false, true))
		{
    		cm.sendOk("You didn't meet up with #r#p2103##k yet? Please get her my reply letter... if you lose the letter by any chance, come find me again... I can always write a new one.");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "2") && !cm.haveItem(4031001, 1, false, true))
		{
    		cm.sendOk("You lost my reply letter!! Should have been more careful. Oh well, there are lots of monsters around this area, so it's understandable. Anyway, here's the reply letter. Please be careful this time around.");
    		cm.gainItem(4031001, 1);
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "1") && cm.haveItem(4031000, 1, false, true))
		{
    		cm.sendYesNo("This is definitely the letter from #p2103#! Ohhh, thank you. I was beginning to get worried because the letter didn't get here. Ok! here's the #breply letter#k. Please get this to her. Just head back to #p2103# and you'll be fine.");
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "1") && !cm.haveItem(4031000, 1, false, true))
		{
    		cm.sendOk("Do you have the letter for me?... No? You must have lost it somewhere along the way here... I don't blame you, there are a lot of monsters around here. Go back and talk to #p2103# and she will write you another letter! Please hurry!");
    		cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (cm.checkQuestData(Quest_Lucas_Letter, "1") && cm.haveItem(4031000, 1, false, true))
		{
			cm.setQuestData(Quest_Lucas_Letter, "2");
			cm.gainExp(10);
			cm.gainItem(4031000, -1);
			cm.gainItem(4031001, 1);
			cm.sendOk("So you have gotten the reply from #p2103#. Thanks! and I want to give you something to show my appreciation.\r\n\r\n#e#rREWARD:#k\r\n+10 EXP\r\n+Lucas' Reply");
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