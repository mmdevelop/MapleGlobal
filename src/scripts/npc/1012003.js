/* 
** NPC Name: Chief Stan
** Location: Henesys
** Purpose: For now all he does is say a small phrase. What's on my TODO list is to script that quest.
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
	cm.sendOk("My Son...! That ungrateful kid... He just didn't listen to me and ran out. I can never ever forgive him...! He can never come to this town again...!");
	cm.dispose();
}