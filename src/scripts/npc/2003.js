/**
 * NPC Name:	Robin
 * Location:	Maple Island
 * Purpose:		Dr. Robin Quest
 * Made by: Exile, cleaned up by wackyracer
 * FULLY GMS-like speech.
 */
 
Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Robin = 1000900;
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
	else if (mode == 0 && status == 1)
    {
        cm.sendOk("I guess you already know how if you don't want to take the quiz. If you change your mind, just talk to me again!");
        cm.dispose();
		return;
    }
	else
	{ 
        status--; 
    } 
	
    if (status == 0) 
    {
    	if (cm.checkQuestData(Quest_Robin, ""))
        {
            cm.sendSimple("Hello, I am Robin.\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bDr. Robin#k#l");
        }
        else if (cm.checkQuestData(Quest_Robin, "1"))
        {
            cm.sendSimple("Hello, I am Robin.\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bDr. Robin (Ready to complete.)#k#l");
        }
        else
        {
            cm.sendOk("Hello, I am Robin.");
            cm.dispose();
        }
    }
	
    else if (status == 1)
    {
        if (cm.checkQuestData(Quest_Robin, ""))
        {
            cm.sendYesNo("Hmm... you must be a new adventurer! If you have become Level 2, you have probably earned extra #bAP#k. Have you distributed your #bAP#k? Please ask if you don't know what an AP is. I will give you a quiz that will help you learn about Stats. Job Advancement and AP.");
        }
        else if (cm.checkQuestData(Quest_Robin, "1"))
        {
            cm.sendSimple("You must be ready for the quiz. I'll start then. First question! You can increase #bSTR#k, #bINT#k, #bDEX#k, #band LUK#k by using #bAbility Points#k, #bor AP#k, that you earn each time your level is increased. Is it possible to increase #bHP and MP#k using AP?\r\n\r\n#l#L0##bYes.#k#l\r\n\r\n#l#L1##bNo.#k#l");
        }
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 2)
    {
    	if (cm.checkQuestData(Quest_Robin, "1"))
        {
            switch (selection)
            {
                case 0:
                    cm.sendSimple("Correct! You can use AP to increase HP and MP instead of Stats. Let's try the second question! When you first start out, you're a beginner, but you can have a Job-Advancement once you reach a certain level. Normally you have to become a Level 10 for Job Advancement but this particular job can be acquired at Level 8. What is this job?\r\n\r\n#l#L2##bWarrior#k#l\r\n\r\n#l#L3##bMagician#k#l\r\n\r\n#l#L4##bBowman#k#l\r\n\r\n#l#L5##bThief#k#l");
                    break;
                case 1:
                    cm.sendOk("Incorrect! You can use AP to increase HP and MP instead of Stats.");
                    cm.dispose();
                    break;
				default:
					cm.dispose();
            }
        }
        else if (cm.checkQuestData(Quest_Robin, ""))
        {
            cm.startQuest(Quest_Robin);
            cm.setQuestData(Quest_Robin, "1");
            cm.sendOk("Okay, click me again to ask whatever you're curious about. I will explain everything step by step. If you feel you know enough, you can click the #bDr. Robin#k quest and start the quiz.");
            cm.dispose();
        }
		else
		{
			cm.dispose();
		}
    }
	
    else if (status == 3)
    {
        if (selection == 3 && cm.checkQuestData(Quest_Robin, "1"))
        {
            cm.sendSimple("Correct! Job Advancement for a Magician is possible at Level 8. Next is the third question! What does not match the Job and Job advancement town?\r\n\r\n#l#L6##bWarrior - Perion#k#l\r\n\r\n#l#L7##bMagician - Ellinia#k#l\r\n\r\n#l#L8##bBowman - Henesys#k#l\r\n\r\n#l#L9##bThief - Sleepywood#k#l");
        }
        else
        {
            cm.sendOk("Incorrect! Job Advancement for a Magician is possible at Level 8.");
            cm.dispose();
        }
    }
	
    else if (status == 4)
    {
        if (cm.checkQuestData(Quest_Robin, "1"))  
        {
            if (selection == 9)
            {
                cm.sendNext("Correct! A Thief can make the job advancement in the Kerning City. Sleepywood is a place for people who already have their job advancement.\r\n\r\n#e#rREWARD:#k\r\n#b+40 EXP#k");
            }
            else
            {
                cm.sendOk("Incorrect! A Thied can make the job advancement in the Kerning City. Sleepywood is a place for people who already have their job advancement.");
                cm.dispose();
            }
        }       
    }
    else if (status == 5)
    {
        if (cm.checkQuestData(Quest_Robin, "1"))
        {
			cm.setQuestData(Quest_Robin, "2");
            cm.completeQuest(Quest_Robin);
            cm.gainExp(40);
            cm.sendOk("You should start thinking about which job you want to choose while you're a beginner, or even before that. Once you have increased AP, you cannot undo it without a special method. I hope you make a wise decision, and grow into what you want to be.");
            cm.dispose();
        }
		else
		{
			cm.dispose();
		}
    }
}