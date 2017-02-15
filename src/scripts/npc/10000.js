/*
** NPC Name: Pio
** Location: Amherst
** Purpose: Pio's Collecting Recycled Goods
** Made by: wackyracer / Ixeb
*/

function start()
{ 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection)
{
	cm.sendOk("Hmmm... I guess these things must be very useful in some places...");
    cm.dispose();
}

/* // MISSING ITEMS IN V12 FOR THIS QUEST, THEREFORE THIS QUEST WAS NOT AVAILABLE EVEN UNTIL V12 SO NO RELAXER QUEST, CODE THIS QUEST LATER
Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Pio = 0000041;
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
	else if (mode == 0 && status == 2)
	{
    	cm.sendOk("Come on ... try it!! It's not hard at all! Just strike this box next to me and you'll know what I'm talking about.");
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
		if (cm.checkQuestData(Quest_Pio, ""))
		{
    		cm.sendSimple("Phew...work is hard. I need to take a break. I just ate a little bit ago... and yet I'm hungry already...!!\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bPio's Collecting Recycled Goods#k#l");
    	}
		else if (cm.checkQuestData(Quest_Pio, "1"))
		{
    		cm.sendSimple("Phew...work is hard. I need to take a break. I just ate a little bit ago... and yet I'm hungry already...!!\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bPio's Collecting Recycled Goods#k#l");
    	}
		else
		{
    		cm.sendOk("Phew...work is hard. I need to take a break. I just ate a little bit ago... and yet I'm hungry already...!!");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Pio, ""))
		{
    		cm.sendNext("My goodness. So many useful items are being thrown away ... I've been wandering around this town, and I see so many items that are thrown away that can be recycled! Hey, say ... can you help me collect those?\n Well, don't worry, I'll reward you well for your effort.");
    	}
		else if (cm.checkQuestData(Quest_Pio, "1"))
		{
			if (cm.itemQuantity(4031162) < 10 || cm.itemQuantity(4031161) < 10)
			{
				cm.sendOk("Well ... I don't know if you've seen the wooden boxes that have been left abandoned on your way here. Did you see them? Your job is to break those boxes and then bring them back as recyclable materials. When you break those boxes, you'll get #t4031162# and #t4031161# in return. Just collect 10 of those for me, okay? \n\n#i4031161# #b10#k #t4031161#s\n#i4031162# #b10#k #t4031162#s");
				cm.dispose();
			}
			else if (cm.itemQuantity(4031162) >= 10 || cm.itemQuantity(4031161) >= 10)
			{
				cm.sendNext("What? You brought them all? Okay, let's see ...");
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
	
	else if (status == 2)
	{
    	if (cm.checkQuestData(Quest_Pio, ""))
		{
			cm.sendYesNo("That's right!! Please get me all the supposedly useless items that are abandoned all over town. I'll reward you well for your effort. Hahahah!!!");
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
		if (cm.checkQuestData(Quest_Pio, ""))
		{
			cm.startQuest(Quest_Pio);
			cm.setQuestData(Quest_Pio, "1");
			cm.sendOk("Well ... I don't know if you've seen the wooden boxes that have been left abandoned on your way here. Did you see them? Your job is to break those boxes and then bring them back as recyclable materials. When you break those boxes, you'll get #t4031162# and #t4031161# in return. Just collect 10 of those for me, okay?"); //\n\n#i4031161# #b10#k #t4031161#s\n#i4031162# #b10#k #t4031162#s");
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
}*/