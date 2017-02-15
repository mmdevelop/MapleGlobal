/**
 * NPC Name: Sera
 * Location: Maple Island
 * Purpose: Heena/Sera Quest
 * Made by: Exile, cleaned up by wackyracer
 * FULLY GMS-like speech.
 */
 
Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Heena_Sera = 0000001;
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
	else
	{ 
        status--; 
    } 
	
    if (status == 0) 
    {
    	if (cm.checkQuestData(Quest_Heena_Sera, "1"))
    	{
    		cm.sendSimple("It is a fine day to do the laundry~ Don't you think so?\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bBorrowing Sera's Mirror (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Heena_Sera, "2"))
    	{
    		cm.sendSimple("Did you give my mirror to Sarah? When will she help me to do this...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bBringing a Mirror to Heena (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Heena_Sera, "3"))
    	{
    		cm.sendOk("Did you give my mirror to Sarah? When will she help me to do this...");
			cm.dispose();
    	}
    	else
    	{
    		cm.sendOk("It is a fine day to do the laundry~ Don't you think so?");
    		cm.dispose();
    	}
    }
	
    else if (status == 1)
    {
		if (cm.checkQuestData(Quest_Heena_Sera, "2") && cm.haveItem(4031003, 1, false, true))
		{
			cm.sendOk("Haven't given #r#p2101##k the mirror yet? She should be on sitting somewhere on the west side...it's pretty close from here so it will be easy to spot her...");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, "2") && !cm.haveItem(4031003, 1, false, true))
		{
			cm.sendNext("How am I going to hang all these up? Sigh... what? My mirror? Please don't tell me #p2101# asked you for this ...");
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, "1"))
		{
			cm.sendNext("How am I going to hang all these up? Sigh... what? My mirror? Please don't tell me #p2101# asked you for this ...");
		}
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 2)
    {
    	if (cm.checkQuestData(Quest_Heena_Sera, "1"))
    	{
			cm.setQuestData(Quest_Heena_Sera, "2");
    		cm.gainItem(4031003, 1);
    		cm.sendOk("Aye...she should have come and get it herself. Seriously, she is SOOO lazy. Here's the mirror you're looking for.");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Heena_Sera, "2") && !cm.haveItem(4031003, 1, false, true))
		{
			cm.gainItem(4031003, 1);
			cm.sendOk("Aye...she should have come and get it herself. Seriously, she is SOOO lazy. Here's the mirror you're looking for.");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
}