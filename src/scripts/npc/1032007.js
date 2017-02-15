/**
	*Npc: Joel
	*ID: 1032007
	*Description: Ellinia Ticketing Usher
	*Author: Straight Edgy
**/

var ticket = 4031045;

function start()
{
	cm.sendYesNo("Hi there! I'm Joel, and I work in this station. Are you thinking of leaving Victoria Island for other places? This station is where you'll find the ship that heads to #bOrbis Station#k of Ossyria every 5 minutes. A ticket costs #b5,000 mesos#k. Would you like to purchase a #bTicket to Orbis#k?");
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
		if (cm.getPlayer().getMeso() >= 5000 && cm.canHold(ticket))
		{
			cm.sendNext("When you are ready to go to Orbis, please go talk to #bCherry#k on the right.");			
			cm.gainItem(4031045, 1);
			cm.gainMeso(-5000);
			cm.dispose();
		}
		else
		{
			cm.sendNext("Hmm... Are you sure you have #b5,000 mesos#k? If so, I urge you to check and see if you have an empty slot on your etc. inventory.");
			cm.dispose();
		}
	}
}