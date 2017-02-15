/**
 * NPC Name: Nina
 * Location: Maple Island
 * Purpose: Nina and Sen Quest (3rd maple island quest)
 * Made by: Exile, cleaned up by wackyracer
 * FULLY GMS-like speech.
 */
 
Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Nina_Sen = 0000003;
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
		cm.sendOk("Oh, you must be busy. Wouldn't it be fun to get to know some others, though?");
		cm.dispose();
		return;
	}
	else
	{ 
        status--;
    } 

    if (status == 0)
    {
		if (cm.checkQuestData(Quest_Nina_Sen, ""))
		{
			cm.sendSimple("What is #p2001# doing, now?...\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bWhat Sen wants to eat#k#l");
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "1"))
		{
			cm.sendSimple("What is #p2001# doing, now?...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bWhat Sen wants to eat (In Progress)#k#l");
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "2"))
		{
			cm.sendSimple("What is #p2001# doing, now?...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bReturning to Nina (Ready to complete.)#k#l");
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "3"))
		{
			cm.sendOk("I will make a mushroom soup for #p2001#. Is there a fresh mushroom?");
			cm.dispose();
		}
		else
		{
			cm.sendOk("What is #p2001# doing, now?...");
			cm.dispose();
		}
    }
    
	else if (status == 1)
    {
		if (cm.checkQuestData(Quest_Nina_Sen, ""))
		{
			cm.sendYesNo("Oh, a traveler!! Nice, right on time... I have a favor to ask, will you do it for me? Go a little more to the right and you'll find a #bhouse with the orange roof#k.");
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "1"))
		{
			cm.sendOk("Haven't met #p2001# yet? Press the up arrow in front of the door!");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "2"))
		{
			cm.sendOk("He wants the mushroom soup? I guess that's our dinner right there then. Thanks for doing me a favor.\r\n\r\n#e#rREWARD:#k\r\n+20 EXP");
		}
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 2)
    {
		if (mode == 1 && cm.checkQuestData(Quest_Nina_Sen, ""))
		{
			cm.startQuest(Quest_Nina_Sen);
			cm.setQuestData(Quest_Nina_Sen, "1");
			cm.sendOk("That's my house. I have a little brother #r#p2001##k that's at home, so can you please ask him what he wants for dinner? Stand in front of the door, press the #bup arrow#k and then you'll be able to enter the house.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "2"))
		{
			cm.setQuestData(Quest_Nina_Sen, "3");
			cm.completeQuest(Quest_Nina_Sen);
			cm.gainExp(20);
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
}