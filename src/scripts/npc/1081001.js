/* 
** NPC Name: Pison
** Location: Florina Beach
** Purpose: Warper between Lith and Florina
** Made by: wackyracer / Ixeb
** Fully GMS-Like Speech
*/

var status;

function start()
{
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection)
{
    if (mode == 1)
	{ 
        status++; 
    }
	else if (mode == -1)
	{
		status = -1;
		cm.dispose();
		return;
    }
	else if (mode == 0 && status ==1)
	{
		cm.sendOk("You must have some business to take care of here. It's not a bad idea to take some rest at #b#m110000000##k! Look at me; I love it here so much that I wound up living here. Hahaha anyway, talk to me when you feel like going back.");
        cm.dispose();
		return;
	}
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
        cm.sendNext("So you want to leave #b#m110000000##k? If you want, I can take you back to #bLith Harbor#k.");
    }
	
	else if (status == 1)
	{
        cm.sendYesNo("Are you sure you want to return to #b#m104000000##k?");
    }
	
	else if (status == 2)
	{
		cm.getPlayer().changeMap(104000000);
        cm.dispose();
    }
}