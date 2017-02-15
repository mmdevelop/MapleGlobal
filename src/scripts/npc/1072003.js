/* 
** NPC Name: Thief Job Instructor
** Location: Construction Site North of Kerning City - Victoria Road
** Purpose: 2nd Job Advancer: Thief
** Made by: Rod Jalali / Editor417312 / etc & wackyracer / Joren McGrew
** Fully GMS-like speech
*/

Packages.net.sf.odinms.server
Packages.net.sf.odinms.client
Packages.net.sf.odinms.scripting
Packages.java.lang

var status

function start()
{
	status = -1
	action(1, 0, 0)
}

function action(mode, type, selection)
{//getJob() returns job ID
	
	if(mode == -1)
	{
		cm.dispose()
		return
	}

	if(status != 3 && mode == 0)
		status -= 2
	
	
	have29OrLess4031013 = false
	for(var i = 0; i < 30; ++i)//slows things down ever so slightly, unnoticeable to a human
	{
		have29OrLess4031013 = have29OrLess4031013 || cm.haveItem(4031013, i)
		if(have29OrLess4031013)
			break
	}
	
	if(cm.getJob() == 400 && cm.getLevel() >= 30 && cm.haveItem(4031011, 1, false, true) && have29OrLess4031013 && cm.haveItem(4031012, 0))
	{
		if(status == -1)
		{
			cm.sendNext("Hmmm...it is definitely the letter from #bDark Lord#k...so you came all the way here to take the test and make the 2nd job advancement as the Thief. Alright, I'll explain the test to you. Don't sweat it too much, it's not that complicated.")
			
			++status
		}
		else if(status == 0)
		{
			cm.sendNextPrev("I'll send you to a hidden map. You'll see monsters you don't normally see. They look the same like the regular ones, but with a totally different attitude. They neither boost your experience level nor provide you with item.")
			
			++status
		}
		else if(status == 1)
		{
			cm.sendNextPrev("You'll be able to acquire a marble called #b#t4031013##k while knocking down those monsters. It is a special marble made out of their sinister, evil minds. Collect 30 of those, and then go talk to a colleague of mine in there. That's how you pass the test.")
			
			++status
		}
		else if(status == 2)
		{
			cm.sendYesNo("Once you go inside, you can't leave until you take care of your mission. If you die, your experience level will decrease..so you better really buckle up and get ready...well, do you want to go for it now?")
		
			++status
		}
		else if(status == 3)
		{
			if(mode == 1)//i.e. they hit Yes
				if(cm.getPlayerCount(108000400) == 0)
				{
					cm.getPlayer().changeMap(108000400)
					cm.sendOk("Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you #bThe Proof of a Hero#k, the proof that you've passed the test. Best of luck to you.");
					cm.dispose()
				}
				else if(cm.getPlayerCount(108000401) == 0)
				{
					cm.getPlayer().changeMap(108000401)
					cm.sendOk("Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you #bThe Proof of a Hero#k, the proof that you've passed the test. Best of luck to you.")
					cm.dispose()
				}
				else if(cm.getPlayerCount(108000402) == 0)
				{
					cm.getPlayer().changeMap(108000402)
					cm.sendOk("Defeat the monsters inside, collect 30 Dark Marbles, then strike up a conversation with a colleague of mine inside. He'll give you #bThe Proof of a Hero#k, the proof that you've passed the test. Best of luck to you.")
					cm.dispose()
				}
				else
				{
					cm.sendOk("I am sorry, but all test chambers are currently taken, you will have to wait until the people inside them finish their job advancement.")
					cm.dispose()
				}
			else
			{
				cm.sendOk("Really? Have to give more though to it, huh? Take your time, take your time. This is not something you should take lightly...come talk to me once you have made your decision.")
				cm.dispose()
			}
		}
	}
	else
	{
		cm.sendOk("What?... Do you want something from me?...")
		cm.dispose()
	}
}