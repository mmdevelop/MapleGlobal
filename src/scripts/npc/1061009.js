/* 
** NPC Name: Door of Dimension
** Location: 4 Different Places
** Purpose: NPC Dialogue
** Made by: Kyushen
** Not GMS-like speech
*/

function start()
{ 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection)
{
	cm.sendOk("The door seems to be closed...");
    cm.dispose();
}