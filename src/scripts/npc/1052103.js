/* 
** NPC Name: Trainer Frod
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
	cm.sendOk("I got so many requests from the townspeople today! I need help here...");
    cm.dispose();
}