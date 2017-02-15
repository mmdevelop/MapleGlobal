/* 
** NPC Name: Shanks
** Location: Southperry
** Purpose: Ship director to Victoria Island
** Made by: wackyracer / Ixeb
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server;
Packages.net.sf.odinms.client;
Packages.net.sf.odinms.scripting;
Packages.java.lang;

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
	else if (mode == 0 && status == 0)
	{
    	cm.sendOk("Hmmm...I guess you still have things to do here.");
    	cm.dispose();
		return;
    }
	else
	{ 
        status--; 
    }
	
	if (status == 0)
	{
    	cm.sendYesNo("Take this ship and you'll head off to a bigger continent. For 150 mesos, I'll take you to #bVictoria Island#k. The thing is, once you leave thie place, you will never be able to return. So, choice is yours. Do you want to go to Victoria Island?");
    }
	
	else if (status == 1)
	{
    	if (cm.getMeso() >= 150)
		{
    		cm.sendNext("Bored of this place? Here... Give me #e150 mesos#k first...");
    	}
		else
		{
    		cm.sendOk("What? You're telling me you wanted to go without any money? You're one weirdo...");
    		cm.dispose();
    	}
    }
	
	else if (status == 2)
	{
    	if (cm.getLevel() >= 7 && cm.getMeso() >= 150)
		{
    		cm.sendNext("Awesome! #e150#n mesos accepted! Alright, off to Victoria Island!");
    	}
		else
		{
    		cm.sendOk("You are not level 7 yet. I'm sorry, but you need to be at least level 7 to go to Victoria Island.");
    		cm.dispose();
    	}
    }
	
	else if (status == 3)
	{
    	if (cm.getLevel() >= 7 && cm.getMeso() >= 150)
		{
    		cm.gainMeso(-150);
			cm.getPlayer().changeMap(104000000);
			cm.dispose();
    	}
		else
		{
			cm.dispose();
		}
    }
	
	else
	{
    	cm.sendOk("Hmmm...I guess you still have things to do here.");
    	cm.dispose();
    }
}