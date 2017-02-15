/* 
** NPC Name: Blackbull
** Location: Perion
** Purpose: Two Quests
** Made by: wackyracer / Joren McGrew
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

 // Steel Shield, Red Triangular Shield
var shields = [ 1092001, 1092000 ];
// a bunch of 10% scrolls based on job, categorized properly below
var scrolls = [ /*beginner or warrior ->*/ 2044002, 2043002, 2043102, 2043202, 2044102, 2044202, 2044302, 2044402, /*mage ->*/ 2043702, 2043802, /*bowman ->*/ 2044502, 2044602, /*thief ->*/ 2043302, 2044702 ]
var shieldRnd = Math.floor(Math.random() * 10 % 2);
var selectedShield = shields[shieldRnd];
var selectedScroll;
var Quest_Blackbull1 = 1000100;
var Quest_Blackbull2 = 1000101;
var YNMode = 0;
var dialog = "Okay, now choose the scroll of your liking ... The odds of winning are 10% each.\r\n";
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
	else if (status == 1 && mode == 0 && YNMode == 1)
	{
		cm.dispose();
		return;
	}
	else if (status == 4 && mode == 0)
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
    	if (cm.checkQuestData(Quest_Blackbull1, ""))
		{
    		cm.sendSimple("Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so...\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bFixing \"Blackbull's\" House#k#l");
		} 
		else if (cm.checkQuestData(Quest_Blackbull1, "w") && (!cm.haveItem(4000003, 30, false, true) || !cm.haveItem(4000018, 50, false, true)))
		{
    		cm.sendSimple("Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bFixing \"Blackbull's\" House (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "w") && cm.haveItem(4000003, 30, false, true) && cm.haveItem(4000018, 50, false, true))
		{
    		cm.sendSimple("Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bFixing \"Blackbull's\" House (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "") && cm.getLevel() >= 30/* && cm.getPlayer().getFame() >= 10*/)
		{
			cm.sendSimple("Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so...\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bBuilding a New House For \"Blackbull\"#k#l");
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "p0") && cm.getLevel() >= 30/* && cm.getPlayer().getFame() >= 10*/ && (!cm.haveItem(4000022, 100, false, true) || !cm.haveItem(4003000, 30, false, true) || !cm.haveItem(4003001, 30, false, true) || !cm.haveItem(4001004, 1, false, true)))
		{
			cm.sendSimple("Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bBuilding a New House For \"Blackbull\" (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "p0") && cm.getLevel() >= 30/* && cm.getPlayer().getFame() >= 10*/ && cm.haveItem(4000022, 100, false, true) && cm.haveItem(4003000, 30, false, true) && cm.haveItem(4003001, 30, false, true) && cm.haveItem(4001004, 1, false, true))
		{
			cm.sendSimple("Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bBuilding a New House For \"Blackbull\" (Ready to complete.)#k#l");
    	}
		else if ((cm.checkQuestData(Quest_Blackbull1, "end")) || (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "pe")))
		{
			cm.sendOk("Hey, it's you! Thanks to you, the building of the house for my cousins are well on their way. You should come check it out when it's completed.");
			cm.dispose();
    	}
		else
		{
    		cm.sendOk("Our family grew, and I'll have to fix the house to make it bigger, but I need materials to do so...");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
		if (cm.checkQuestData(Quest_Blackbull1, "") && YNMode == 0)
		{
			YNMode = 1;
    		cm.sendYesNo("Can you get me #b#e30#n #b#t4000003#es#k and #b#e50#n #t4000018#s#k? I'm trying to remodel my house and make it bigger ... If you can do it, I'll hook you up with a nice #bshield#k that I don't really need ... You'll get plenty if you take down the ones that look like trees.");
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "w") && cm.haveItem(4000003, 30, false, true) && cm.haveItem(4000018, 50, false, true) && YNMode == 0)
		{
    		cm.sendOk("Incredible! You must be someone special to get that many. Hmm ... alright, the shield is yours. It's my favorite one. Please take good care of it.\r\n\r\n#e#rREWARD:#k\r\n+50 EXP\r\n+1 Random Warrior Shield (Lv. 15 or Lv. 25)");
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "w") && (!cm.haveItem(4000003, 30, false, true) || !cm.haveItem(4000018, 50, false, true)) && YNMode == 0)
		{
    		cm.sendOk("Wait, what happened??? Are you sure you have #b#e30#n #t4000003#es#k and #b#e50#n #t4000018#s#k?");
			cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && YNMode == 0)
		{
			YNMode = 1337;
			cm.sendNext("Hey, it's you! Got pretty famous since the last time I saw you, huh? Well thanks to you, I got my house fixed just fine. But hmm ... there's a problem ... all my relatives from #m100000000# want to move to this town. I need to build a new house for them, but I don't even have the materials to build with ...");
		}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && YNMode == 1337)
		{
			cm.sendNext("Hey, it's you! Got pretty famous since the last time I saw you, huh? Well thanks to you, I got my house fixed just fine. But hmm ... there's a problem ... all my relatives from #m100000000# want to move to this town. I need to build a new house for them, but I don't even have the materials to build with ...");
		}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "p0") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && cm.haveItem(4000022, 100, false, true) && cm.haveItem(4003000, 30, false, true) && cm.haveItem(4003001, 30, false, true) && cm.haveItem(4001004, 1, false, true) && YNMode == 0)
		{
			cm.sendNext("THIS is the deed to the land that my son lost! And you even brought all the necessary materials to build the house! Thank you so much ... my relatives can all move in and live in #m102000000#! As a sign of appreciation ...");
		}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "p0") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && (!cm.haveItem(4000022, 100, false, true) || !cm.haveItem(4003000, 30, false, true) || !cm.haveItem(4003001, 30, false, true) || !cm.haveItem(4001004, 1, false, true)))
		{
			cm.sendOk("Looks like you haven't gotten all the materials needed. Please get #b100 #t4000022#s, 30 #b#t4003000#s, 30 #b#t4003001#s and the lost deed to the land#k. Do it fast, before they eat it ...");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 2)
	{
    	if (mode == 1 && cm.checkQuestData(Quest_Blackbull1, ""))
		{
			cm.startQuest(Quest_Blackbull1);
    		cm.setQuestData(Quest_Blackbull1, "w");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "w") && cm.haveItem(4000003, 30, false, true) && cm.haveItem(4000018, 50, false, true))
		{
    		cm.setQuestData(Quest_Blackbull1, "end");
			cm.completeQuest(Quest_Blackbull1);
			cm.gainItem(4000003, -30);
			cm.gainItem(4000018, -50);
			cm.gainItem(selectedShield, 1);
			cm.gainExp(50);
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && YNMode == 1337)
		{
			cm.sendNextPrev("Well Hey! Sorry to ask you for a favor, but can you get the stuff necessary to build the house for my cousins? So many relatives are coming in that #t4000018# or #t4000003# that you got for me last time won't do. So can you do that for me? I'll tell you what to get.");
		}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "p0") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && cm.haveItem(4000022, 100, false, true) && cm.haveItem(4003000, 30, false, true) && cm.haveItem(4003001, 30, false, true) && cm.haveItem(4001004, 1, false, true))
		{
			if (cm.getJob() == 0 || cm.getJob() == 100 || cm.getJob() == 110 || cm.getJob() == 111 || cm.getJob() == 112 || cm.getJob() == 120 || cm.getJob() == 121 || cm.getJob() == 122 || cm.getJob() == 130 || cm.getJob() == 131 || cm.getJob() == 132)
			{
				dialog += "\r\n#L0##b#i2044002##t2044002##k#l\r\n#L1##b#i2043002##t2043002##k#l\r\n#L2##b#i2043102##t2043102##k#l\r\n#L3##b#i2043202##t2043202##k#l\r\n#L4##b#i2044102##t2044102##k#l\r\n#L5##b#i2044202##t2044202##k#l\r\n#L6##b#i2044302##t2044302##k#l\r\n#L7##b#i2044402##t2044402##k#l";
			}
			else if (cm.getJob() == 200 || cm.getJob() == 210 || cm.getJob() == 211 || cm.getJob() == 212 || cm.getJob() == 220 || cm.getJob() == 221 || cm.getJob() == 222 || cm.getJob() == 230 || cm.getJob() == 231 || cm.getJob() == 232)
			{
				dialog += "\r\n#L8##b#i2043702##t2043702##k#l\r\n#L9##b#i2043802##t2043802##k#l";
			}
			else if (cm.getJob() == 300 || cm.getJob() == 310 || cm.getJob() == 311 || cm.getJob() == 312 || cm.getJob() == 320 || cm.getJob() == 321 || cm.getJob() == 322)
			{
				dialog += "\r\n#L10##b#i2044502##t2044502##k#l\r\n#L11##b#i2044602##t2044602##k#l";
			}
			else if (cm.getJob() == 400 || cm.getJob() == 410 || cm.getJob() == 411 || cm.getJob() == 412 || cm.getJob() == 420 || cm.getJob() == 421 || cm.getJob() == 422)
			{
				dialog += "\r\n#L12##b#i2043302##t2043302##k#l\r\n#L13##b#i2044702##t2044702##k#l";
			}
			else
			{
				dialog += "\r\n#L99##bI'm a GM and I deserve nothing because I can just give any item to myself anyway#k#l";
				cm.sendSimple(dialog);
				cm.dispose();
			}
			cm.sendSimple(dialog);
		}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && YNMode == 1337)
		{
			cm.sendNextPrev("I'll need #b100 #t4000022#s#k, #b30 #t4003000#s#k, and #b30 #b#t4003001#s#k. But with only these ... well a couple of days ago, a deed to the land that I purchased disappeared ... my son had it on the way to #m105040300# when he got attacked by the monsters.");
		}
		else if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "p0") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/ && cm.haveItem(4000022, 100, false, true) && cm.haveItem(4003000, 30, false, true) && cm.haveItem(4003001, 30, false, true) && cm.haveItem(4001004, 1, false, true))
		{
			cm.setQuestData(Quest_Blackbull2, "pe");
			cm.completeQuest(Quest_Blackbull2);
			selectedScroll = scrolls[selection];
			cm.gainItem(selectedScroll);
			cm.gainExp(1000);
			cm.gainMeso(15000);
			cm.gainFame(2);
			cm.sendOk("Hopefully the scroll I gave you helped. Here's also a little bit of money if that helps. I'll never forget the fact that you helped me. For that, I'll be spreading the good news about your good deed all over the town. What do you think?? Anyway thank you so much for helping me out. We'll probably meet again ...\r\n\r\n#e#rREWARD:#k\r\n+1000 EXP\r\n+2 Fame\r\n#i" + selectedScroll + "##t" + selectedScroll + "#");
			cm.dispose();
		}
		else 
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
    	if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/)
		{
    		cm.sendYesNo("A group of reptiles that live in the forests called #r#o3230100##k took the deed to the land. Can you help me get that and the necessary materials to build the house? If so, then you'll be handsomely rewarded for your work ... good luck!");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 5)
	{
		if (cm.checkQuestData(Quest_Blackbull1, "end") && cm.checkQuestData(Quest_Blackbull2, "") && cm.getLevel() >= 30/* && cm.getFame() >= 10*/)
		{
			cm.startQuest(Quest_Blackbull2);
			cm.setQuestData(Quest_Blackbull2, "p0");
			cm.dispose();
		}
	}
	else
	{
    	cm.dispose();
    }
}