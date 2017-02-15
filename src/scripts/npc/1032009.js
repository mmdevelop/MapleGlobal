/**
	*Npc: Ticketing Usher
	*ID: 1032009
	*Description: Ellinia to Orbis
	*Author: Straight Edgy
**/

function start()
{
	cm.sendYesNo("We're just about to take off. Are you sure you want to get off the ship? You may do so, but then you'll have to wait until the next available flight, also, the ticket is NOT refundable. Do you still wish to get off board?");
}

function action(mode)
{
	if (mode == 0)
	{
		cm.sendNext("You'll get to your destination in a short while. Talk to other passengers and share your stories to them, and you'll be there before you know it.");
		cm.dispose();
	}
	else if (mode == 1)
	{
		cm.getPlayer().changeMap(101000300);
		cm.dispose();
	}
}