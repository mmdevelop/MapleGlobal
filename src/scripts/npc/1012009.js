/**
	*Npc: Mr. Lee (Henesys)
	*ID: 1012009
	*Description: Henesys Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getClient(), 1012009);
}

function action()
{
	cm.dispose();
}