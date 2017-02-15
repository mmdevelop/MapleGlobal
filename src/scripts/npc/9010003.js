/**
	*Npc: Ria (Kerning City)
	*ID: 9010003
	*Description: Kerning City NPC
	*Author: Straight Edgy
**/

function start()
{ 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection)
{
    cm.sendOk("Hi, I'm Ria.");
    cm.dispose();
}