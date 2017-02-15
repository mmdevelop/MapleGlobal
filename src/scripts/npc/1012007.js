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
	cm.sendOk("Hmmm ... did you really come here without your pet? These obstacles are for pets. What are you doing here without it? Are you kidding me!!!");
    cm.dispose();
}