/**
	*Npc: Mr. Wang (Perion)
	*ID: 1022005
	*Description: Perion Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getC(), 1022005);
}

function action()
{
	cm.dispose();
}