/**
	*Npc: Tia (Perion)
	*ID: 9010001
	*Description: Perion NPC
	*Author: Straight Edgy & wackyracer
**/

function start()
{ 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection)
{
    cm.sendOk("Hi, I'm Tia.");
    cm.dispose();
}