/* 
** NPC Name: Sophia
** Location: Perion Department Store
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
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
    	if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m2"))
		{
    		cm.sendSimple("Man, I want to travel around and stuff. I don't want to be stuck here working. This stinks!! I'm stuck here making potions everyday thanks to my mom opening up a convenient store. This is definitely NOT fun.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bFinding Sophia (Ready to complete.)#k#l");
    	}
		else if (cm.getLevel() >= 15 && (cm.checkQuestData(Quest_Maya, "m3") || cm.checkQuestData(Quest_Maya, "m4")))
		{
    		cm.sendSimple("Man, I want to travel around and stuff. I don't want to be stuck here working. This stinks!! I'm stuck here making potions everyday thanks to my mom opening up a convenient store. This is definitely NOT fun.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bMaking a Sparkling Rock (In Progress)#k#l");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m5"))
		{
			cm.sendSimple("Man, I want to travel around and stuff. I don't want to be stuck here working. This stinks!! I'm stuck here making potions everyday thanks to my mom opening up a convenient store. This is definitely NOT fun.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bGetting Arcon's Blood (Ready to complete.)#k#l");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m6"))
		{
			cm.sendSimple("Man, I want to travel around and stuff. I don't want to be stuck here working. This stinks!! I'm stuck here making potions everyday thanks to my mom opening up a convenient store. This is definitely NOT fun.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bMaking Sparkling Rock (In Progress)#k#l");
		}
		else if (cm.getLevel() >= 15 && (cm.checkQuestData(Quest_Maya, "m7") || cm.checkQuestData(Quest_Maya, "end")))
		{
			cm.sendOk("What did you do with #b#t4031004##k? That's a coveted rock and all, but it requires so many items to make that you're the first one to actually gather them all up! Anyway, hope this is put to good use.");
			cm.dispose();
		}
		else
		{
    		cm.sendOk("Man, I want to travel around and stuff. I don't want to be stuck here working. This stinks!! I'm stuck here making potions everyday thanks to my mom opening up a convenient store. This is definitely NOT fun.");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
		if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m2"))
		{
    		cm.sendYesNo("Hmm... So you want to have #b#t4031004##k? That stone is very rare... Alright, get me the items, I tell you... Ready?");
    	}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m5") && cm.haveItem(4031005, 1, false, true) && cm.haveItem(4000004, 50, false, true) && cm.haveItem(4000005, 50, false, true) && cm.haveItem(4000006, 20, false, true))
		{
			cm.sendYesNo("How did you get all these items?? You must be really good! Especially #t4031005#. Wow... Anyway, good job! Now we can make a #b#t4031004##k");
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m6") && !cm.haveItem(4031004, 1, false, true))
		{
			cm.gainItem(4031004, 1);
			cm.sendOk("Here, take this, the #b#t4031004##k. By the way, what do you plan on doing with that stone? It is a special item, indeed, but ... unless you're collecting stones, this may be of no use...");
			cm.dispose();
		}
		else if (cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m6") && cm.haveItem(4031004, 1, false, true))
		{
			cm.sendOk("What did you do with #b#t4031004##k? That's a coveted rock and all, but it requires so many items to make that you're the first one to actually gather them all up! Anyway, hope this is put to good use.");
			cm.dispose();
		}
		else
		{
			// The phrase below had many grammatic and text-code errors directly from MapleStory's data. I modified it to show properly. Trust me, it's still GMS-like, it's just readable now. - wackyracer
			cm.sendOk("If you have 50#b#e#n #t4000004#s#k, 50#b#e#n #t4000005#s#k, 20#b#e#n #t4000006#s#k, and 1#b#e#n #t4031005##k, you can make 1 #b#e#n#t4031004##k... You should ask #r#p1022002##k about #t4031005#... I think he is somewhere around #m102000000#");
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (mode == 1 && cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m2"))
		{
			cm.setQuestData(Quest_Maya, "m3");
			// The phrase below had many grammatic and text-code errors directly from MapleStory's data. I modified it to show properly. Trust me, it's still GMS-like, it's just readable now. - wackyracer
    		cm.sendOk("50#b#e#n #t4000004#s#k, 50#b#e#n #t4000005#s#k, 20#b#e#n #t4000006#s#k, and 1#b#e#n \r\n#t4031005##k. Everything else should be easy to obtain. As for the #t4031005#...  you should ask #r#p1022002##k. He is somewhere around the town.");
			cm.dispose();
		}
		else if (mode == 1 && cm.getLevel() >= 15 && cm.checkQuestData(Quest_Maya, "m5") && cm.haveItem(4031005, 1, false, true) && cm.haveItem(4000004, 50, false, true) && cm.haveItem(4000005, 50, false, true) && cm.haveItem(4000006, 20, false, true))
		{
			cm.setQuestData(Quest_Maya, "m6");
			cm.gainItem(4000004, -50);
			cm.gainItem(4000005, -50);
			cm.gainItem(4000006, -20);
			cm.gainItem(4031005, -1);
			cm.gainItem(4031004, 1);
			cm.sendOk("Here, take this, the #b#t4031004##k. By the way, what do you plan on doing with that stone? It is a special item, indeed, but ... unless you're collecting stones, this may be of no use...");
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