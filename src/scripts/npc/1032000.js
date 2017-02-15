/**
	*Npc: Regular Cab (Ellinia)
	*ID: 1032000
	*Description: Ellinia Cab
	*Author: Straight Edgy
**/

var maps = Array(104000000, 102000000, 103000000, 100000000);
var prices = Array(1200, 1000, 1200, 1000);

function start()
{
	cm.sendNext("Hi! I drive the Regular Cab. If you want to go from town to town safely and fast, then ride our cab. We'll gladly take you to your destination with an affordable price.");
	status = 0;
}

function action(mode, type, selection)
{
	var isBeginner = cm.getJob() == 0;
	if (mode == 1)
	{
		status++;
	}
	else
	{
		if (status == 2 && mode == 0)
		{
			cm.sendNext("There's a lot to see in this town, too. Come back and find us when you need to go to a different town.");
		} 
		cm.dispose();
		return;
	}
	
	if (status == 1)
	{
		var text = "";
		if (isBeginner)
		{
			text += "We have a special 90% discount for beginners. ";
		}
		text += "Choose your destination, for fees will change from place to place.#b";
		for (var i = 0; i < maps.length; i++)
		{
			price = isBeginner ? prices[i] / 10 : prices[i]; 
			text += "\r\n#L" + i + "##m" + maps[i] + "# (" + price + " mesos)#l"; 
		}
		cm.sendSimple(text);
	}
	
	else if (status == 2)
	{
		if (selection < 0 || selection >= maps.length)
		{
			sel = 0;
		}
		else
		{
			sel = selection;
		}
		map = maps[sel];
		price = isBeginner ? prices[sel] / 10 : prices[sel];
		cm.sendYesNo("You don't have anything else to do here, huh? Do you really want to go to #b#m" + map + "##k? It'll cost you #b" + price + " mesos#k.");
	}
	
	else if (status == 3)
	{
		if (cm.getMeso() >= price)
		{
			cm.gainMeso(-price);
			cm.getPlayer().changeMap(map);
		}
		else
		{
			cm.sendNext("You don't have enough mesos. Sorry to say this, but without them, you won't be able to ride the cab.");
		}
		cm.dispose();
	}
}