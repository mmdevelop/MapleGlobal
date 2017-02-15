/**
	*Npc: Trina (Orbis)
	*ID: 2010006
	*Description: Orbis Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getC(), 2010006);
}

function action()
{
	cm.dispose();
}