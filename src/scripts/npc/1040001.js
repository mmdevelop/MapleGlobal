/* 
** NPC Name: Mike
** Location: ???
** Purpose: Quest ???
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
	cm.sendOk("Pass through here and you will find the Victoria Island Central Dungeon. Be careful...");
    cm.dispose();
}