/**
	*Npc: Mr. Hong (Kerning City)
	*ID: 1052017
	*Description: Kerning City Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getC(), 1052017);
}

function action()
{
	cm.dispose();
}