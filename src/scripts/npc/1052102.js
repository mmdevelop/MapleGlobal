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
	cm.sendOk("Oh my gosh ... I lost the coin that my friend lent me ... I can't find it!!");
    cm.dispose();
}