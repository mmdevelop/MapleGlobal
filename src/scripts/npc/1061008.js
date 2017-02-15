/**
	*Npc: Mr. Oh (Sleepywood)
	*ID: 1061008
	*Description: Sleepywood Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getC(), 1061008);
}

function action()
{
	cm.dispose();
}