/**
	*Npc: Mr. Park (Ellinia)
	*ID: 1032006
	*Description: Ellinia Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getC(), 1032006);
}

function action()
{
	cm.dispose();
}