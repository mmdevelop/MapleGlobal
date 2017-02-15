/* 
** NPC Name: Jake
** Location: Kerning City Subway
** Purpose: Ticket Vendor for Subway Levels B1 ~ B3
** Made by: Rod Jalali / Editor417312 / etc & wackyracer / Joren McGrew
** FULLY GMS-like speech
*/

Packages.net.sf.odinms.server
Packages.net.sf.odinms.client
Packages.net.sf.odinms.scripting
Packages.java.lang

var status

var NPCVerbalResponses = ["Area 1", "Area 2", "Area 3"]
var constructionSites = ["construction site B1", "construction site B2", "construction site B3"]
var ticketPrices = [500, 1200, 2000]
var items = [4031036, 4031037, 4031038]
var siteChoice

function start()
{
	status = -1
	action(1, 0, 0)
}

function action(mode, type, selection)
{
	if(mode == 0 || mode == -1)
	{
		if(status == 1 && mode != -1)
			cm.sendOk("You can enter the premise once you have bought the ticket. I heard there are strange devices in there everywhere but in the end, rare precious items await you. So let me know if you ever decide to change your mind.")
		cm.dispose()
		return
	}
	
	if(status == -1)
	{
		mustPurchaseTicketGreeting = "You must purchase the ticket to enter. Once you have made the purchase, you can enter through The Ticket Gate on the right."
		
		if(cm.getLevel() >= 40)
		{
			cm.sendSimple(mustPurchaseTicketGreeting + " What would you like to buy?\r\n" +
			"#L0##b" + "Construction Site B1" +//selection = 0
			"#k#l\r\n#L1##b" + "Construction Site B2" +//selection = 1
			"#k#l\r\n#L2##b" + "Construction Site B3" +//selection = 2
			"#k#l")
			
			++status
		}
		else if(cm.getLevel() >= 30)
		{
			cm.sendSimple(mustPurchaseTicketGreeting + " What would you like to buy?\r\n" +
			"#L0##b" + "Construction Site B1" +
			"#k#l\r\n#L1##b" + "Construction Site B2" +
			"#k#l")
			
			++status
		}
		else if(cm.getLevel() >= 20)
		{
			cm.sendSimple(mustPurchaseTicketGreeting + " What would you like to buy?\r\n" +
			"#L0##b" + "Construction Site B1" +
			"#k#l")
			
			++status
		}
		else//if(cm.getLevel() < 20)
		{
			cm.sendNext(mustPurchaseTicketGreeting)
			
			cm.dispose()
		}
	}
	else if(status == 0)
	{
		siteChoice = selection
		cm.sendYesNo("Will you purchase the ticket to #b" + constructionSites[selection] + "#k? It'll cost you " + ticketPrices[selection] + " mesos. Before making the purchase, please make sure you have an empty slot on your etc. inventory.")
		
		++status
	}
	else if(status == 1)
	{
		if(cm.getMeso() > ticketPrices[siteChoice] /*&& cm.getInventory(MapleInventoryType type).getOpenSlots()??? >= 1 TODO*/)
		{
			cm.gainMeso(-ticketPrices[siteChoice])
			cm.gainItem(items[siteChoice], 1)//ticket
			cm.sendOk(NPCVerbalResponses[siteChoice] + " has some precious items available but with so many traps all over the place most come back out early. Please be safe.")
		}
		else
			cm.sendOk("Are you lacking mesos? Check and see if you have an empty slot on your etc. inventory or not.")
		cm.dispose()
	}
}