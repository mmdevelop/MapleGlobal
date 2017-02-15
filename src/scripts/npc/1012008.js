/* 
** NPC Name: Casey
** Location: Henesys
** Purpose: Mini-Game Master
** Made by: anoob123 & wackyracer & Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

var status;
var selectionState;
var item1;
var item2;
var item3;

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
		cm.sendSimple("Hey, you look like you need a breather from all that hunting. You should be enjoying the life, just like I am. Well, if you have a couple of itmes, I can make a trade with you for an item you can play minigames with. Now... what can I do for you?\r\n\r\n#b#L0#Create a minigame item#l#k\r\n#b#L1#Explain to me what minigames are about#l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0)
		{
			cm.sendSimple("You want to make the minigame item? Minigames aren't something you can just go ahead and play right off the bat. You'll need a specific set of items for a specific minigame. Which minigame item do you want to make?\r\n\r\n#b#L2#Omok Set#l#k\r\n#b#L3#A Set of Match Cards#l#k");
		}
		else if (selection == 1)
		{
			cm.sendSimple("You want to learn more about the minigames? Awesome! Ask me anything. Which minigame do you want to know more about?\r\n\r\n#b#L4#Omok#l#k\r\n#b#L5#Match Cards#l#k");
		}
	}
	
	else if (status == 2)
	{
		if (selection == 2)
		{
			cm.sendNext("You want to play #bOmok#k, huh? To play it, you'll need the Omok Set. Only the ones with that item can open the room for a game of Omok, and you can play this game almost everywhere except for a few places at the market place.");
			selectionState = 1;
		}
		else if (selection == 3)
		{
			if (cm.haveItem(4030012, 99, false, true))
			{
				cm.gainItem(4030012, 99);
				cm.gainItem(4080100, 1);
				cm.sendOk("Bam!! Your #bSet of Match Cards#k is ready! If you have any questions about Match Cards, I can tell you all about it. Now go have fun and play some Match Cards!!!");
				cm.dispose();
			}
			else
			{
				cm.sendOk("You want #bA set of Match Cards#k? Hmmm... to make A set of Match Cards, you'll need some #bMonster Cards#k. Monster Card can be obtained by taking out the monsters all around the island. Collect 99 Monster Cards and you can make a set of A Set of Match Cards.");
				cm.dispose();
			}
		}
		else if (selection == 4)
		{
			cm.sendNext("Here are the rules to the game of Omok. Listen carefully. Omok is a game where, you and your opponent will take turns laying a piece on the table until someone finds a way to lay 5 consecutive pieces in a line, be it horizontal, diagonal or vertical. That person will be the winner. For starters, only the ones with #bOmok Set#k can open a game room.");
			selectionState = 2;
		}
		else if (selection == 5)
		{
			cm.sendNext("Here are the rules to the game of Match Cards. Listen carefully. Match Cards is just like the way it sounds, finding a matching pair among the number of cards laid on the table. When all the matching pairs are found, then the person with more matching pairs will win the game. Just like Omok, you'll need #bA Set of Match Cards#k to open the game room.");
			selectionState = 3;
		}
	}
	
	else if (status == 3)
	{
		if (selectionState == 1)
		{
			cm.sendSimple("The set also differs based on what kind of pieces you want to use for the game. Which set would you like to make?\r\n#b#L6#Slime & Mushroom Omok Set#l#k\r\n#b#L7#Slime & Octopus Omok Set#l#k\r\n#b#L8#Slime & Pig Omok Set#l#k\r\n#b#L9#Octopus & Mushroom Omok Set#l#k\r\n#b#L10#Pig & Octopus Omok Set#l#k\r\n#b#L11#Pig & Mushroom Omok Set#l#k");
		}
		else if (selectionState == 2)
		{
			cm.sendNextPrev("Every game of Omok will cost you #r100 mesos#k. Even if you don't have the #bOmok Set#k, you can enter the game room and play the game. If you don't have 100 mesos, however, then you won't be allowed in the room, period. The person opening the game room also needs 100 mesos to open the room, or there's no game. If you run out of mesos during the game, then you're automatically kicked out of the room!");
		}
		else if (selectionState == 3)
		{
			cm.sendNextPrev("Every game of Match Cards will cost you #r100 mesos#k. Even if you don't have #bA set of Match Cards#k, you can enter the game room and play the game. If you don't have 100 mesos, however, then you won't be allowed in the room, period. The person opening the game room also needs 100 mesos to open the room, or there's no game. If you run out of mesos during the game, then you're automatically kicked out of the room!");
		}
	}
	
	else if (status == 4)
	{
		if (selectionState == 2)
		{
			cm.sendNextPrev("Enter the room, and when you're ready to play, click on \r\n#bReady#k. Once the visitor clicks on #bReady#k, the owner of the room can press #bStart#k to start the game. If an unwanted visitor walks in, and you don't want to play with that person, the owner of the room has the right to kick the visitor out of the room. There will be a square box with #rx#k written on the right of that person. Click on that for a cold goodbye, ok?");
		}
		else if (selectionState == 3)
		{
			cm.sendNextPrev("Enter the room, and when you're ready to play, click on \r\n#bReady#k. Once the visitor clicks on #bReady#k, the owner of the room can press #bStart#k to start the game. If an unwanted visitor walks in, and you don't want to play with that person, the owner of the room has the right to kick the visitor out of the room. There will be a square box with #rx#k written on the right of that person. Click on that for a cold goodbye, ok?");
		}
		else
		{
			switch (selection)
			{
				case 6:
					item1 = 4030000;
					item2 = 4030001;
					item3 = 4080000;
					break;
				case 7:
					item1 = 4030000;
					item2 = 4030010;
					item3 = 4080001;
					break;
				case 8:
					item1 = 4030000;
					item2 = 4030011;
					item3 = 4080002;
					break;
				case 9:
					item1 = 4030010;
					item2 = 4030001;
					item3 = 4080003;
					break;
				case 10:
					item1 = 4030011;
					item2 = 4030010;
					item3 = 4080004;
					break;
				case 11:
					item1 = 4030011;
					item2 = 4030001;
					item3 = 4080005;
					break;
			}
			if (cm.haveItem(item1, 99, false, true) && cm.haveItem(item2, 99, false, true) && cm.haveItem(item3, 1, false, true))
			{
				cm.gainItem(item1, -99);
				cm.gainItem(item2, -99);
				cm.gainItem(4030009, 1); //Omok Table
				cm.gainItem(item3, 1);
				cm.sendOk("Bam!! Your #bOmok Set#k is ready! If you have any questions about Omok, I can tell you all about it. Now go have fun and play some Omok!!!");
				cm.dispose();
			}
			else
			{
				cm.sendOk("#bYou want to make #t" + pItem3 + "##k? Hmm ... get me the materials, and I can do just that. Listen carefully, the materials you need will be:   #r99 #t" + pItem1 + "#, 99 #t" + pItem2 + "#, and 1 #t4030009##k.");
				cm.dispose();
			}
		}
	}
	
	else if (status == 5)
	{
		if (selectionState == 2)
		{
			cm.sendNextPrev("When the first game starts, #bthe owner of the room goes \r\nfirst#k. Beware that you'll be given a time limit, and you may lose your turn if you don't make your move on time. Normally, 3 x 3 is not allowed, but if there comes a point that it's absolutely necessary to put your piece there or face a game over, then you can put it there. 3 x 3 is allowed as the last line of defense! Oh, and it won't count if it's #r6 or 7 straight#k. Only 5!");
		}
		else if (selectionState == 3)
		{
			cm.sendNextPrev("Oh, and unlike Omok, on Match Cards, when you create the game room, you'll need to set your game on the number of cards you'll use for the game. There are 3 modes available, 3x4, 4x5, and 5x6, which will require 12, 20, and 30 cards. Remember, though, you won't be able to change it up once the room is open, so if you really wish to change it up, you may have to close the room and open another one.");
		}
	}
	
	else if (status == 6)
	{
		if (selectionState == 2)
		{
			cm.sendNextPrev("If you know your back is against the wall, you can request a #bRedo#k. If the opponent accepts your request, then the opponent's last move, along with yours, will be canceled out. If you ever feel the need to go to the bathroom, or take an extended break, you can request a #btie#k. The game will end in a tie if the opponent accepts the request. This may be a good way to keep your friendship in tact with your buddy.");
		}
		else if (selectionState == 3)
		{
			cm.sendNextPrev("When the first game starts, #bthe owner of the room goes \r\nfirst#k. Beware that you'll be given a time limit, and you may lose your turn if you don't make your move on time. When you find a matching pair on your turn, you'll get to keep your turn, as long as you keep finding a pair of matching cards. Use your memorizing skills for a devastating combo of turns.");
		}
	}
	
	else if (status == 7)
	{
		if (selectionState == 2)
		{
			cm.sendOk("Once the game is over, and the next game starts, the loser will go first. Oh, and you can't leave in the middle of the game. If you do, you may need to request either a #bforfeit, or a tie#k. Of course, if you request a forfeit, you'll lose the game, so be careful of that. And if you click on \"Leave\" in the middle of the game and call to leave after the game, you'll leave the room right after the game is over, so this will be a much more useful way to leave.");
			cm.dispose();
		}
		if (selectionState == 3)
		{
			cm.sendNextPrev("If you and your opponent have the same number of matched pairs, then whoever had a longer streak of matched pairs will win. If you ever feel the need to go to the bathroom, or take an extended break, you can request a #btie#k. The game will end in a tie if the opponent accepts the request. This may be a good way to keep your friendship in tact with your buddy.");
		}
	}
	
	else if (status == 8)
	{
		if (selectionState == 3)
		{
			cm.sendOk("Once the game is over, and the next game starts, the loser will go fisrt. Oh, and you can't leave in the middle of the game. If you do, you may need to request either a #bforfeit, or a tie#k. Of course, if you request a forfeit, you'll lose the game, so be careful of that. And if you click on \"Leave\" in the middle of the game and call to leave after the game, you'll leave the room right after the game is over, so this will be a much more useful way to leave.");
			cm.dispose();
		}
	}
}