/* 
** NPC Name: Alex
** Location: Kerning City
** Purpose: Part of quest from Chief Stan (Alex the Runaway Kid)
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
	cm.sendOk("It's been a months since I ran away from home, and frankly I'm sick of wandering around strange places now. But I feel weird about going back home...");
    cm.dispose();
}