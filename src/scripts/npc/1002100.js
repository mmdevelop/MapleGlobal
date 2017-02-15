/* 
** NPC Name: Jane
** Location: Lith Harbor
** Purpose: Simple phrase for now but is really a quest. TODO
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
    cm.sendOk("My dream is to travel all over the world.... Just like you... But my father just doesn't let me...");
    cm.dispose();
}