/* 
** NPC Name: Trainer Bartos
** Location: Henesys
** Purpose: Trainer Bartos' Letter Quest for pets
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
	cm.sendOk("My brother told me to be careful with the obstacle course, but ... since I am so far from him, I can't resist, I wanna make a mess ... hehe. I don't personally see him, I think I can relax for a few minutes.");
    cm.dispose();
}