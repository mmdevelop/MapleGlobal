/**
	*Npc: Ticketing Usher
	*ID: 2012001
	*Description: Orbis to Ellinia
	*Author: Straight Edgy
**/

var ticket = 4031047;

function start()
{
	var em = cm.getEventManager("Boats");
	
	if (!cm.haveItem(ticket, 1, false, true))
	{
		cm.sendNext("Make sure you have a #bTicket to Ellinia#k to travel on this ship.");
		cm.dispose();
	}
	
	else if (em.getProperty("entry") == "true" && cm.getPlayer().getMap().hasBoat() == 2)
	{
		cm.sendYesNo("It looks like there's plenty of room for the ship to Ellinia. Please have your ticket ready so I can let you in. This will not be a short flight, so if you need to take care of some things, I suggest you do that first before getting on board. Do you still wish to board the ship?");
	}
	
	else
	{
		if (em.getProperty("entry") == "false" && em.getProperty("docked") == "true" && cm.getPlayer().getMap().hasBoat() == 2)
		{
			cm.sendNext("This ship is getting ready for takeoff. I'm sorry, but you'll have to get on the next ride.The ride schedule is available through the guide at the ticketing booth.");
			cm.dispose();
		}
		else
		{
			cm.sendNext("We will begin boarding 5 minutes before the takeoff. Please be patient and wait for a few minutes. Be aware that the ship will take off right on time, and we stop boarding 1 minute before that, so please make sure to be here on time.")
			cm.dispose();
		}
	}
}

function action(mode)
{
	if (mode == 0)
	{
		cm.sendNext("You must have some business to take care of here, right?");
		cm.dispose();
	}
	
	else if (mode == 1)
	{
		cm.gainItem(ticket, -1);
		cm.getPlayer().changeMap(200000112);
		cm.dispose();
	}
}