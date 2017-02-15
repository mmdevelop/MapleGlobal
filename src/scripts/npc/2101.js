/**
 * NPC Name: Heena
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
var started = 1;
var notStarted = 0;
var completed = 2;
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
	else if (mode == 0 && status == 3)
    {
    	cm.sendOk("Don't want to? Hmmm... come back when you change your mind.");
		cm.dispose();
		return;
    }
	else
	{ 
        status--; 
    }
	
    if (status == 0) 
    {
		if (cm.checkQuestData(Quest_Heena_Sera, ""))
		{
			cm.sendSimple("You must be a new traveler. I will give some of the instruction, which will be very useful. If you want to speak with us, you can just simply double-click us. You can move by pressing #bLeft, Right Key#k and jump by pressing #bSpace Bar#k. Come on~ Try! Also, sometimes, you had to climb up the ladder or use the rope to get to the destination, where you want. You can do that by pressing #bup arrow#k. Please keep this in mind.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bBorrowing Sera's Mirror#k#l");
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, "1"))
		{
			cm.sendSimple("You must be a new traveler. I will give some of the instruction, which will be very useful. If you want to speak with us, you can just simply double-click us. You can move by pressing #bLeft, Right Key#k and jump by pressing #bSpace Bar#k. Come on~ Try! Also, sometimes, you had to climb up the ladder or use the rope to get to the destination, where you want. You can do that by pressing #bup arrow#k. Please keep this in mind.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bBorrowing Sera's Mirror (In Progress)#k#l");
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, "2"))
		{
			cm.sendSimple("You must be a new traveler. I will give some of the instruction, which will be very useful. If you want to speak with us, you can just simply double-click us. You can move by pressing #bLeft, Right Key#k and jump by pressing #bSpace Bar#k. Come on~ Try! Also, sometimes, you had to climb up the ladder or use the rope to get to the destination, where you want. You can do that by pressing #bup arrow#k. Please keep this in mind.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bBringing a Mirror to Heena (Ready to complete.)#k#l");
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, "3"))
		{
			cm.sendOk("Now I can take a look at my face with this mirror. It looks alright.");
			cm.dispose();
		}
		else
		{
			cm.sendOk("You must be a new traveler. I will give some of the instruction, which will be very useful. If you want to speak with us, you can just simply double-click us. You can move by pressing #bLeft, Right Key#k and jump by pressing #bSpace Bar#k. Come on~ Try! Also, sometimes, you had to climb up the ladder or use the rope to get to the destination, where you want. You can do that by pressing #bup arrow#k. Please keep this in mind.");
			cm.dispose();
		}
    }
	
    else if (status == 1)
    {
		if (cm.checkQuestData(Quest_Heena_Sera, "2") && cm.haveItem(4031003, 1, false, true))
		{
			cm.sendNext("Oh wow! You brought #p2100#'s mirror! Thank you so so much. Let's see... no skin damage whatsoever...\r\n\r\n#e#rREWARD:#k\r\n+1 EXP");
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, "2") && !cm.haveItem(4031003, 1, false, true))
		{
			cm.sendOk("Did you lose the mirror? Ask her for it once more.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, "1"))
		{
			cm.sendOk("Haven't met #r#p2100##k yet? She should be on a hill down on east side...it's pretty close from here so it will be easy to spot her...");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_Heena_Sera, ""))
		{
			cm.sendNext("You must be the new traveler. Still foreign to this, huh? I'll be giving you important information here and there so please listen carefully and follow along. First if you want to talk to us, #bdouble-click#k us with the mouse.");
		}
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 2)
    {
		if (cm.checkQuestData(Quest_Heena_Sera, "2") && cm.haveItem(4031003, 1, false, true))
		{
			cm.setQuestData(Quest_Heena_Sera, "3");
			cm.completeQuest(Quest_Heena_Sera);
			cm.gainItem(4031003, -1);
			cm.gainExp(1);
			cm.sendOk("If you go right, you will see the shiny spot. We call that a \"Portal\". If you press #bup-arrow#k, you will get to the next place. So long!");
			cm.dispose();
		}   
		else if (cm.checkQuestData(Quest_Heena_Sera, ""))
		{
			cm.sendNextPrev("#bLeft, right arrow#k will allow you to move. Press #bAlt#k to jump. Jump diagonally by combining it with the directional cursors. Try it later.");
		}	
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 3)
    {
		if (cm.checkQuestData(Quest_Heena_Sera, ""))
		{
			cm.sendYesNo("Man... the sun is literally burning my beautiful skin! It's a scorching day today. Can I ask you for a favor? Can you get me a #bmirror#k from #r#p2100##k, please?");
		}
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 4)
    {
    	if (cm.checkQuestData(Quest_Heena_Sera, ""))
    	{
    		cm.startQuest(Quest_Heena_Sera);
    		cm.setQuestData(Quest_Heena_Sera, "1");
    		cm.sendOk("Thank you... #r#p2100##k will be on the hill down on the east side hanging up the laundry. The mirror looks like this #i4031003#.");
    		cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    }
}