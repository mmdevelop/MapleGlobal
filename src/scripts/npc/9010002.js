/**
	*Npc: Mia (Henesys)
	*ID: 9010002
	*Description: Henesys NPC
	*Author: Straight Edgy
**/


function start()
{ 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection)
{
    cm.sendOk("Hi, I'm Mia.");
    cm.dispose();
}
