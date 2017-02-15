/* 
** NPC Name: Mar the Fairy
** Location: Ellinia
** Purpose: NPC involving pets and revival of expired pets
** Made by: wackyracer / Joren McGrew
** Not GMS-like speech
*/

function start()
{ 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection)
{
	cm.sendOk("I'm Mar, the fairy. I can revive pets or transfer existing EXP to another.");
    cm.dispose();
}