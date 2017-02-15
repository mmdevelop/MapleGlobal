/**
	*Npc: Seppy (Ludibrium)
	*ID: 2041008
	*Description: Ludibrium Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getC(), 2041008);
}

function action()
{
	cm.dispose();
}