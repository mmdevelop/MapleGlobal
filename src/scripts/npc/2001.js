/**
 * NPC Name: Sen
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
	else if (mode == 0 && status == 2)
	{
		cm.sendOk("Awww... You aren't going to tell her?");
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }

    if (status == 0)
    {
		if (cm.checkQuestData(Quest_Nina_Sen, "1"))
		{
			cm.sendSimple("There is nothing to eat in here~ oh...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bWhat Sen wants to eat (Ready to complete.)#k#l");
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "2"))
		{
			cm.sendSimple("There is nothing to eat in here~ oh...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bReturning to Nina (In Progress)#k#l");
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "3"))
		{
			cm.sendOk("What did my sister say? oh... I am so hungry...");
		}
		else
		{
			cm.sendOk("There is nothing to eat in here~ oh...");
			cm.dispose();
		}
    }
	
    else if (status == 1)
    {
		if (cm.checkQuestData(Quest_Nina_Sen, "1"))
		{
			cm.sendNext("Ahh, soooo hungry. Where's my sister?!! I was gonna ask her to make me a mushroom soup. Soooo hungry!!");
		}
		else if (cm.checkQuestData(Quest_Nina_Sen, "2"))
		{
			cm.sendOk("What did my sister say? oh... I am so hungry...");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 2)
    {
		if (cm.checkQuestData(Quest_Nina_Sen, "1"))
		{
			cm.sendYesNo("Please tell my sister I really really want #bmushroom soup#k for dinner!\r\n\r\n#e#rREWARD:#k\r\n+7 EXP");
		}
		else
		{
			cm.dispose();
		}
    }
    
	else if (status == 3)
    {
		if (mode == 1 && cm.checkQuestData(Quest_Nina_Sen, "1"))
		{
			cm.setQuestData(Quest_Nina_Sen, "2");
			cm.gainExp(7);
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}	
}