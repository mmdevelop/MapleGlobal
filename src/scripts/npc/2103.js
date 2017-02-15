/* 
** NPC Name: Maria
** Location: Maple Island
** Purpose: Letter For Lucas / Lucas' Reply
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

 // Brown Skullcap, Green Skullcap, Red Headband, Green Headband, Yellow Headband, Blue Headband 
var hats = [1002008,1002053, 1002014, 1002070, 1002068, 1002071];
var rnd = Math.floor(Math.random() * 6);
var selectedHat = hats[rnd];
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
	else if (mode == -1)
	{
		status = -1;
		cm.dispose();
		return;
    }
	else if (mode == 0 && status == 2)
	{
    	cm.sendOk("Are you really busy? Ahhhh, this is not good. If I don't get this to the town chief... anyway if you have some free time please come back and talk to me.");
    	cm.dispose();
		return;
    }
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
    	if (cm.checkQuestData(Quest_Lucas_Letter, ""))
		{
    		cm.sendSimple("I should bring this to #m1010000#... What should I do... hmm...\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bLetter For Lucas#k#l");
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "1"))
		{
    		cm.sendSimple("I should bring this to #m1010000#... What should I do... hmm...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bLetter For Lucas (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "2"))
		{
    		cm.sendSimple("I should bring this to #m1010000#... What should I do... hmm...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bLucas' Reply (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "3"))
		{
    		cm.sendOk("Now I can deliver important news to the town. Thanks a lot. Are you using the hat, I gave you? How is that? Pretty good, huh?");
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("I should bring this to #m1010000#... What should I do... hmm...");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
		if (cm.checkQuestData(Quest_Lucas_Letter, "2") && cm.haveItem(4031001, 1, false, true))
		{
    		cm.sendNext("Here is a hat. It has a Lv. limitation, but I think you are strong enough to wear this. I hope this can help you. Thanks!!!\r\n\r\n#e#rREWARD:#k\r\n+10 EXP\r\n+1 Random Level 5 Hat");
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "2") && !cm.haveItem(4031001, 1, false, true))
		{
    		cm.sendOk("The letter from #r#p2103##k should be here by now. What happened...? Someone please let me know what's going on here...");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "1") && cm.haveItem(4031000, 1, false, true))
		{
    		cm.sendOk("Haven't met #r#p12000##k from #m1010000# yet? Please send him my letter. It's urgent. I need to get a reply from #p12000# quickly...");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, "1") && !cm.haveItem(4031000, 1, false, true))
		{
    		cm.sendOk("You lost my letter!! You should have been more careful. Here's the letter again. Please make sure you don't lose the letter, since there are a lot monsters around this area.");
    		cm.gainItem(4031000, 1);
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, ""))
		{
    		cm.sendNext("Ahh...I'm getting worried...because I need to get this letter to #p12000# fast. It's an urgent matter so I need to let him know of this ASAP. Too bad I have things to do here for a while so I won't be leaving this spot anytime soon...");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (cm.checkQuestData(Quest_Lucas_Letter, "2") && cm.haveItem(4031001, 1, false , true))
		{
    		cm.gainExp(10);
    		cm.gainItem(selectedHat, 1);
    		cm.gainItem(4031001, -1);
    		cm.setQuestData(Quest_Lucas_Letter, "3");
			cm.completeQuest(Quest_Lucas_Letter);
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Lucas_Letter, ""))
		{
    		cm.sendYesNo("I'm sorry but can you get this #bletter#k to #r#p12000##k from #b#m1010000##k? I have a lot of things to do here so I have to stay here for now. It'll only take a minute...");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (cm.checkQuestData(Quest_Lucas_Letter, ""))
		{
    		cm.startQuest(Quest_Lucas_Letter);
    		cm.setQuestData(Quest_Lucas_Letter, "1");
   			cm.gainItem(4031000, 1);
    		cm.sendNext("You're gonna do it? Thank goodness. Now I can breathe a sigh of relief. Here's my letter, and please get this to the town chief that's at #b#m1010000##k.");
    	}
		else 
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
    	if (cm.checkQuestData(Quest_Lucas_Letter, "1"))
		{
    		cm.sendOk("Head northeast and soon you'll find #b#m1010000##k. #p12000# is the town chief of #m1010000#. He should be in front of the department store probably taking a walk. Please get #p12000#'s reply letter fast!");
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