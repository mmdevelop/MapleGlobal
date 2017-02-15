/* 
** NPC Name: Riel
** Location: Florina Beach
** Purpose: NPC Dialogue
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
	cm.sendOk("Welcome to Florina Beach! How can I help you?");
    cm.dispose();
}