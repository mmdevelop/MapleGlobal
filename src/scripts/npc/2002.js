/**
 * NPC Name: Peter
 * Location: Maple Island
 * Purpose: Phrase
 * Made by: Exile, cleaned up by wackyracer
 * FULLY GMS-like speech.
 */

function start()
{ 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection)
{
    cm.sendOk("Are you having a pleasant journey so far?");
    cm.dispose();
}