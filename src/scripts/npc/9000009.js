/* 
** NPC Name: Vikin
** Location: Lith Harbor
** Purpose: Event NPC of some sort TODO
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
    cm.sendOk("Hey hey!!! Find the Treasure Scroll! I lost the map somewhere and I can't leave without it!");
    cm.dispose();
}
