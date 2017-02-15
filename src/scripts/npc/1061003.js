/* 
** NPC Name: Mr. Wetbottom
** Location: VIP Sauna: Sleepywood
** Purpose: Sauna Robe Quest
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var Quest_Sauna_Robe = 1000600;
var no = false;
var status;
var robe;

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
    	if (cm.checkQuestData(Quest_Sauna_Robe, "") && cm.getLevel() >= 30)
		{
			cm.sendSimple("Welcome to the VIP sauna of the #m105040300# Hotel. Actually I need some help here ...\r\n\r\n#r#eQUEST AVAILABLE#k#n#l\r\n#L0##bA Clue to the Secret Book#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "1") || cm.checkQuestData(Quest_Sauna_Robe, "2") || cm.checkQuestData(Quest_Sauna_Robe, "3") && cm.getLevel() >= 30)
		{
    		cm.sendSimple("Welcome to the VIP sauna of the #m105040300# Hotel. Actually I need some help here ...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bA Clue to the Secret Book (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "4") && !cm.haveItem(4031016, 1, false, true) && cm.getLevel() >= 30)
		{
    		cm.sendSimple("Welcome to the VIP sauna of the #m105040300# Hotel. Actually I need some help here ...\r\n\r\n#r#eQUEST IN PROGRESS#k#n#l\r\n#L0##bA Clue to the Secret Book (In Progress)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "4") && cm.haveItem(4031016, 1, false, true) && cm.getLevel() >= 30)
		{
    		cm.sendSimple("Welcome to the VIP sauna of the #m105040300# Hotel. Actually I need some help here ...\r\n\r\n#r#eQUEST THAT CAN BE COMPLETED#k#n#l\r\n#L0##bA Clue to the Secret Book (Ready to complete.)#k#l");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "e") && cm.getLevel() >= 30)
		{
			cm.sendOk("I'm so glad I got this book back safely. It's my number one treasure, you know. Am I not worried about #p1061004#? The fairies are taking care of him alright, so I'm not worried one bit.");
    		cm.dispose();
		}
		else
		{
    		cm.sendOk("Welcome to the VIP sauna of the #m105040300# Hotel. Actually I need some help here ...");
    		cm.dispose();
    	}
    }
	
	else if (status == 1)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "")&& cm.getLevel() >= 30)
		{
    		cm.sendNext("Good job getting here. Actually I have a favor to ask you. If you accept it, I'll give you a piece of clothing that you'll need in return. I think you are more than capable of doing it. Even if you don't care, just please listen to my story first.");
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "1") || cm.checkQuestData(Quest_Sauna_Robe, "2") || cm.checkQuestData(Quest_Sauna_Robe, "3") && cm.getLevel() >= 30)
		{
    		cm.sendOk("You haven't found my book yet. Please come back when you have. My son is located in a passage made of trees near the forest at Ellinia, because that's his favorite spot ... best of luck to you!");
    		cm.dispose();
    	}
		else if (cm.checkQuestData(Quest_Sauna_Robe, "4") && cm.getLevel() >= 30)
		{
    		if (cm.haveItem(4031016, 1, false, true))
			{
    			cm.setQuestData(Quest_Sauna_Robe, "e");
    			cm.gainItem(4031016, -1);
    			if (cm.getPlayer().getGender() == 0)
				{
    				cm.gainItem(1050018, 1); // Sauna Robe (M)
                    robe = 1050018;
    			}
				else if (cm.getPlayer().getGender() == 1)
				{
    				cm.gainItem(1051017, 1); // Sauna Robe (F)
                    robe = 1051017;
    			}
				else
				{
    				cm.dispose();
    			}
    			cm.gainExp(500);
    			cm.gainMeso(10000);
    			cm.sendOk("This is the book! The book I was looking for! WHEW! Thank you so much! Here, the piece of clothing, like I promised.\r\n\r\n#v"+robe+"#");
    			cm.dispose();
    		}
			else
			{
    			cm.sendOk("You haven't found my book yet. Please come back when you have. My son is located in a passage made of trees near the forest at Ellinia, because that's his favorite spot ... best of luck to you!");
				cm.dispose();
    		}
    	}
    }
	
	else if (status == 2)
	{
    	if (no)
		{
    		cm.sendOk("I see ... I guess you're busy with things here and there ... but I'll definitely reward you handsomely for your work so if you ever change your mind, please let me know.");
    		cm.dispose();
    	}
    	else if (cm.checkQuestData(Quest_Sauna_Robe, "") && cm.getLevel() >= 30)
		{
    		cm.sendNextPrev("I have a son that can do me no wrong. But one day he took a book of mine that is very dear to me and left. That book ... hmmm ... I can't give you the full detail on it, but it is a very very important book to me...");
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 3)
	{
    	if (cm.checkQuestData(Quest_Sauna_Robe, "") && cm.getLevel() >= 30)
		{
    		cm.sendYesNo("If you get me that book back safely, I'll give you a comfortable article of clothing, perfect for saunas like this. What do you think? Will you find my son and take back that book?");
    		no = true;
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else if (status == 4)
	{
    	no = false;
    	if (cm.checkQuestData(Quest_Sauna_Robe, "") && cm.getLevel() >= 30)
		{
    		cm.startQuest(Quest_Sauna_Robe);
    		cm.setQuestData(Quest_Sauna_Robe, "1");
    		cm.sendOk("Ohhh ... thank you so much. It won't be easy locating my son in this humongous island, the Victoria Island. I'm guessing that he may be in a passage made of trees near the forest at #m101000000#, because that's his favorite spot ... best of luck to you!");
    		cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    }
}