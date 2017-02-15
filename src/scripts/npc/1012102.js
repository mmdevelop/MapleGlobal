/* 
** NPC Name: Pia
** Location: Henesys
** Purpose: Simple phrase, but is supposed to be a part of some sort of quest... or even a quest-giver itself... ugh I dunno lol. TODO
** Made by: wackyracer / Joren McGrew
** FULLY GMS-like speech
*/

function start()
{
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection)
{
	cm.sendOk("Ah~! It is really getting to me!!! #o2220100#... Oh... Are you a stranger?");
	cm.dispose();
}