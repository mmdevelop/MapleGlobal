/* 
** NPC Name: Cloy
** Location: Henesys
** Purpose: Pet Information NPC
** Made by: wackyracer / Joren McGrew
** GMS-like speech I think... x_x
*/

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
		cm.sendNext("Hmm... are you raising one of my kids by any chance? I perfected a spell that uses Water of Life to blow life into a doll. People call it the #bPet#k. If you have one with you, feel free to ask me questions.");
	}
	
	else if (status == 1)
	{
		cm.sendSimple("What do you want to know more of?#b\r\n#L0#Tell me more about Pets.#l\r\n#L1#How do I raise Pets?#l\r\n#L2#Do Pets die too?#l\r\n#L3#What are the commands for Brown and Black Kitty?#l\r\n#L4#What are the commands for Brown Puppy?#l\r\n#L5#What are the commands for Pink and White Bunny?#l\r\n#L6#What are the commands for Mini Kargo?#l");
	}
	
	else if (status == 2)
	{
		if (selection == 0)
		{
			cm.sendOk("So you want to know more about Pets. Long ago I made a doll, sprayed Water of Life on it, and cast spell on it to create a magical animal. I know it sounds unbelievable, but it's a doll that became an actual living thing. They understand and follow people very well.\r\n\r\nBut Water of Life only comes out little at the very bottom of the World Tree, so I can't give him too much time in life... I know, it's very unfortunate... but even if it becomes a doll again I can always bring life back into it so be good to it while you're with it.\r\n\r\nOh yeah, they'll react when you give them special commands. You can scold them, love them... it all\r\ndepends on how you take care of them. They are afraid to leave their masters so be nice to them, show them love. They can get sad and lonely fast...");
			cm.dispose();
		}
		else if (selection == 1)
		{
			cm.sendOk("Depending on the command you give, pets can love it, hate, and display other kinds of reactions to it. If you give the pet a command and it follows you well, your intimacy goes up. Double click on the pet and you can check the intimacy, level, fullness and etc...\r\n\r\nTalk to the pet, pay attention to it and its intimacy level will go up and eventually his overall level will go up too. As the intimacy level rises, the pet's overall level will rise soon after. As the overall level rises, one day the pet may even talk like a person a little bit, so try hard raising it. Of course it won't be easy doing so...\r\n\r\nIt may be a live doll but they also have life so they can feel the hunger too. #bFullness#k shows the level of hunger the pet's in. 100 is the max, and the lower it gets, it means that the pet is getting hungrier. After a while, it won't even follow your command and be on the offensive, so watch out over that.");
			cm.dispose();
		}
		else if (selection == 2)
		{
			cm.sendOk("Dying... well, they aren't technically ALIVE per say, so I don't know if dying is the right term to use. They are dolls with my magical power and the power of Water of Life to become a live object. Of course while it's alive, it's just like a live animal...\r\n\r\nAfter some time... that's correct, they stop moving. They just turn back to being a doll, after the effect of magic dies down and Water of Life dries out. But that doesn't mean it's stopped forever, because once you pour Water of Life over, it's going to be back alive.\r\n\r\nEven if it someday moves again, it's sad to see them stop altogether. Please be nice to them while they are alive and moving. Feed them well, too. Isn't it nice to know that there's something alive that follows and listens to only you?");
			cm.dispose();
		}
		else if (selection == 3)
		{
			cm.sendOk("These are the commands for #rBrown Kitty and Black Kitty#k. The level mentioned next to the command shows the pet level required for it to respond.\r\n#bsit#k (Level 1 ~ 30)\r\n#bbad, no, badgirl, badboy#k (Level 1 ~ 30)\r\n#bstupid, ihateyou, dummy#k (Level 1 ~ 30)\r\n#biloveyou#k (Level 1~30)\r\n#bpoop#k (Level 1 ~ 30)\r\n#btalk, say, chat#k (Level 10 ~ 30)\r\n#bcutie#k (Level 10 ~ 30)\r\n#bup, stand, rise#k (Level 20 ~ 30)");
			cm.dispose();
		}
		else if (selection == 4)
		{
			cm.sendOk("These are the commands for #rBrown Puppy#k. The level mentioned next to the command shows the pet level required for it to respond.\r\n#bsit#k (Level 1 ~ 30)\r\n#bbad, no, badgirl, badboy#k (Level 1 ~ 30)\r\n#bstupid, ihateyou, baddog, dummy#k (Level 1 ~ 30)\r\n#biloveyou#k (Level 1~30)\r\n#bpee#k (Level 1 ~ 30)\r\n#btalk, say, chat#k (Level 10 ~ 30)\r\n#bdown#k (Level 10 ~ 30)\r\n#bup, stand, rise#k (Level 20 ~ 30)");
			cm.dispose();
		}
		else if (selection == 5)
		{
			cm.sendOk("These are the commands for #rPink Bunny and White Bunny#k. The level mentioned next to the command shows the pet level required for it to respond.\r\n#bsit#k (Level 1 ~ 30)\r\n#bbad, no, badgirl, badboy#k (Level 1 ~ 30)\r\n#bup, stand, rise#k (Level 1 ~ 30)\r\n#biloveyou#k (Level 1~30)\r\n#bpoop#k (Level 1 ~ 30)\r\n#btalk, say, chat#k (Level 10 ~ 30)\r\n#bhug#k (Level 10 ~ 30)\r\n#bsleep, sleepy, gotobed#k (Level 20 ~ 30)");
			cm.dispose();
		}
		else if (selection == 6)
		{
			cm.sendOk("These are the commands for #rMini Kargo#k. The level mentioned next to the command shows the pet level required for it to respond.\r\n#bsit#k (Level 1 ~ 30)\r\n#bbad, no, badgirl, badboy#k (Level 1 ~ 30)\r\n#bup, stand, rise#k (Level 1 ~ 30)\r\n#biloveyou#k (Level 1~30)\r\n#bpee#k (Level 1 ~ 30)\r\n#btalk, say, chat#k (Level 10 ~ 30)\r\n#bthelook, charisma#k (Level 10 ~ 30)\r\n#bdown#k (Level 10 ~ 30)\r\n#bgoodboy, goodgirl#k (Level 20 ~ 30)");
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