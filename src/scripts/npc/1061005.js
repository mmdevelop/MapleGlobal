/* 
** NPC Name: Sabitrama
** Location: Sleepywood
** Purpose: Quest
** Made by: Kyushen
** Sabitrama dialogue for "Sabitrama and the Diet Medicine" and "Sabitrama's Anti-Aging Medicine"
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_DietMed = 1000700; //req: lvl 25
var Quest_AgingMed = 1000701; //req: lvl 50
var rnd = Math.floor(Math.random() * 2);
var scrolls = [2040504, 2040505]; //Scroll for Overall Armor for Def. 60%, Scroll for Overall Armor for Def. 10%
var selectedScroll;
var status;
var YNMode = 0;

function start()
{
	selectedScroll = scrolls[rnd];
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
	else if (mode == 0 && (status == 2 || status == 1) && YNMode == 1)
	{
		cm.sendOk("Really. You look like you can just breeze through there ... please come back here when you have time. I'll be waiting for you.");
		cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
		if (cm.checkQuestData(Quest_DietMed, "") && cm.getLevel() >= 25)
		{
			cm.sendSimple("Lots of medicinal herbs in this forest. Nothing makes me happier than finding new herbs here!\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bSabitrama and the Diet Medicine#k#l");
		}
		else if ((cm.checkQuestData(Quest_DietMed, "1_01") || (cm.checkQuestData(Quest_DietMed, "1_11") && !cm.haveItem(4031020, 1, false, true))) && cm.getLevel() >= 25)
		{
			cm.sendSimple("Lots of medicinal herbs in this forest. Nothing makes me happier than finding new herbs here!\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bSabitrama and the Diet Medicine (In Progress)#k#l");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_11") && cm.getLevel() >= 25 && cm.haveItem(4031020, 1, false, true))
		{
			cm.sendSimple("Lots of medicinal herbs in this forest. Nothing makes me happier than finding new herbs here!\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bSabitrama and the Diet Medicine (Ready to complete.)#k#l");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel() >= 25)
		{
			if (cm.checkQuestData(Quest_AgingMed, "") && cm.getLevel() >= 50)
			{
				cm.sendSimple("It's you!! Thanks to the herbs you got me, the medicine is well on its way. It should be done pretty soon. Thanks again for your help.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bSabitrama's Anti-Aging Medicine#k#l");
			}
			else if ((cm.checkQuestData(Quest_AgingMed, "2_01") || (cm.checkQuestData(Quest_AgingMed, "2_11") && !cm.haveItem(4031032, 1, false, true))) && cm.getLevel() >= 50)
			{
				cm.sendSimple("It's you!! Thanks to the herbs you got me, the medicine is well on its way. It should be done pretty soon. Thanks again for your help.\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bSabitrama's Anti-Aging Medicine (In Progress)#k#l");
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_11") && cm.getLevel() >= 50 && cm.haveItem(4031032, 1, false, true))
			{
				cm.sendSimple("It's you!! Thanks to the herbs you got me, the medicine is well on its way. It should be done pretty soon. Thanks again for your help.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bSabitrama's Anti-Aging Medicine (Ready to complete.)#k#l");
			}
			else
			{
				cm.sendOk("It's you!! Thanks to the herbs you got me, the medicine is well on its way. It should be done pretty soon. Thanks again for your help.");
				cm.dispose();				
			}
		}
		else
		{
			cm.sendOk("Lots of medicinal herbs in this forest. Nothing makes me happier than finding new herbs here!");
			cm.dispose();
		}
	}
	
	else if (status == 1)
	{
		if (cm.checkQuestData(Quest_DietMed, "") && cm.getLevel() >= 25)
		{
			cm.sendNext("Wait, hold on one second. I am an herb-collector traveling around the world finding herbs. I'm looking for useful medicinal herbs around this area. It's been hard finding those these days ... so, hey, have found a place where the herbs run aplenty?");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_01") && cm.getLevel() >= 25)
		{
			cm.sendOk("You haven't gotten the herb yet. The herb you need to get is #b#t4031020##k. The roots look like this #i4031020#. Remember it and get it from #p1032003# in #m101000000#.");
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_11") && cm.getLevel() >= 25)
		{
			if (cm.itemQuantity(4031020) >= 1)
			{
				cm.sendNext("Ohhh ... this is IT! With #b#t4031020##k, I can finally make the diet medicine!! Hahaha, if you ever feel like you have gained weight, feel free to find me, because by then, I may have a special medicine in place for just that!");
			}
			else
			{
				cm.sendOk("You haven't gotten the herb yet. The herb you need to get is #b#t4031020##k. The roots look like this #i4031020#. Remember it and get it from #p1032003# in #m101000000#.");
				cm.dispose();
			}
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel() >= 50)
		{
			if (cm.checkQuestData(Quest_AgingMed, ""))
			{
				YNMode = 1;
				cm.sendYesNo("Ohhh, you're the traveler that helped me out a lot the other day! I made the diet medicine with the herbs you got me and made some money ... and this time, I'd like to make a different kind of a medicine. What do you think? Do you want to help me out one more time?");
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_01"))
			{
				cm.sendOk("You haven't gotten the herb yet. The herb you need to get is #b#t4031032##k. The roots look like this #i4031032#. Remember it and get it from #p1032003# in #m101000000#.");
				cm.dispose();
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_11"))
			{
				if (cm.itemQuantity(4031032) >= 1)
				{
					cm.sendNext("Ohhh ... this is IT! With #b#t4031032##k, I can finally make the anti-aging medicine!!! Hahaha, if you ever become old and weak, find me. By then I may have a special medicine for just that!");
				}
				else
				{
					cm.sendOk("You haven't gotten the herb yet. The herb you need to get is #b#t4031032##k. The roots look like this #i4031032#. Remember it and get it from #p1032003# in #m101000000#.");
					cm.dispose();
				}
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
		if (cm.checkQuestData(Quest_DietMed, "") && cm.getLevel() >= 25)
		{
			YNMode = 1;
			cm.sendYesNo("Actually I've found a place where you can find good medicinal herbs. It's at a forest not too far from here ... lots of obstacles around the area but I can tell in the end there will be goods available for us to use ... so what do you think? Do you want to go there in place of me?");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_11") && cm.getLevel() >= 25 && cm.haveItem(4031020, 1, false, true))
		{
			if (cm.canHold(selectedScroll))
			{
				cm.sendOk("Oh, I almost forgot. Since you helped me out, I should thank you for your hard work. Here, take this scroll. My brother made this for me a while back, and it adds to the guarding abilities of the armor. Hopefully you'll use it well. And from here on out,  #p1032003# will let you in free. Thanks for your help...\r\n\r\n#e#rREWARD:#k\r\n+1000 EXP\r\n+1 Fame\r\n+#i" + selectedScroll + "# #t" + selectedScroll + "#");
			}
			else
			{
				cm.sendOk("You don't have enough space in your inventory. Please make space and talk to me again.");
				cm.dispose();
			}
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel() >= 50)
		{
			if (cm.checkQuestData(Quest_AgingMed, ""))
			{
				YNMode = 0;
				cm.startQuest(Quest_AgingMed);
				cm.setQuestData(Quest_AgingMed, "2_01");
				cm.sendNext("Thank you. The place where you can find the mysterious herb is actually the place you've been to before, #m101000000#. I heard someone's accepting an entrance fee at the entrance ... you have the mesos to go in, right? This time you'll be going in much deeper than before so be prepared ...");
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_01"))
			{
				cm.sendNext("Thank you. The place where you can find the mysterious herb is actually the place you've been to before, #m101000000#. I heard someone's accepting an entrance fee at the entrance ... you have the mesos to go in, right? This time you'll be going in much deeper than before so be prepared ...");
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_11") && cm.haveItem(4031032, 1, false, true))
			{
				if (cm.canHold(4021009))
				{
					cm.sendOk("Oh, I almost forgot. Since you helped me out, I should thank you for your hard work ... #b#t4021009##k is something I found at the very bottom of a valley a lont time ago in the middle of a journey. It'll probably help you down the road. I also boosted up your fame level and from here on out, #p1032003# may let you in for free. Well, so long...\r\n\r\n#e#rREWARD:#k\r\n+3000 EXP\r\n+2 Fame\r\n+#i4021009# #t4021009#");
				}
				else
				{
					cm.sendOk("You don't have enough space in your etc. inventory. Please make space and talk to me again.");
					cm.dispose();
				}
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
	
	else if (status == 3)
	{
		if (cm.checkQuestData(Quest_DietMed, "") && cm.getLevel() >= 25)
		{
			YNMode = 0;
			cm.startQuest(Quest_DietMed);
			cm.setQuestData(Quest_DietMed, "1_01");
			cm.sendNext("Thank you. The place where you can find the mysterious herb is actually the place you've been to before, which is #m101000000#. I heard someone's accepting an entrance fee at the entrance ... you have the mesos to go in, right? Sorry but I've spent all my money traveling so I'm afraid I can't help you on that ...");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_01") && cm.getLevel() >= 25)
		{
			cm.sendNext("Thank you. The place where you can find the mysterious herb is actually the place you've been to before, which is #m101000000#. I heard someone's accepting an entrance fee at the entrance ... you have the mesos to go in, right? Sorry but I've spent all my money traveling so I'm afraid I can't help you on that ...");
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_11") && cm.getLevel() >= 25 && cm.haveItem(4031020, 1, false, true))
		{
			cm.completeQuest(Quest_DietMed);
			cm.setQuestData(Quest_DietMed, "1_00");
			cm.gainItem(4031020, -1);
			cm.gainExp(1000);
			cm.gainFame(1);
			cm.gainItem(selectedScroll, 1);
			cm.dispose();
		}
		else if (cm.checkQuestData(Quest_DietMed, "1_00") && cm.getLevel() >= 50)
		{
			if (cm.checkQuestData(Quest_AgingMed, "2_01"))
			{
				YNMode = 0;
				cm.sendPrev("Yes! I'll explain to you about the herb you'll be getting for me. Remember there are similar herbs around so make sure you know this. The herb you'll need to get is #b#t4031032##k, and the root looks like this #i4031032#. Look carefully and please get the same one as I described for you.");
			}
			else if (cm.checkQuestData(Quest_AgingMed, "2_11") && cm.haveItem(4031032, 1, false, true))
			{
				cm.completeQuest(Quest_AgingMed);
				cm.setQuestData(Quest_AgingMed, "2_00");
				cm.gainItem(4031032, -1);
				cm.gainExp(3000);
				cm.gainFame(2);
				cm.gainItem(4021009, 1);
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
	
	else if (status == 4)
	{
		if (cm.checkQuestData(Quest_DietMed, "1_01") && cm.getLevel() >= 25)
		{
			cm.sendPrev("Yes! I'll explain to you about the herb you'll be getting for me. Remember there are similar herbs around so make sure you know this. The herb you'll need to get is #b#t4031020##k, and the flower looks like this #i4031020#. Look carefully and please get the same one as I described for you.");
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