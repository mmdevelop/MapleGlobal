/**
	*Npc: Mr. Kim (Lith Harbor)
	*ID: 1002005
	*Description: Lith Harbor Storage
	*Author: Straight Edgy
**/

function start()
{
	cm.getPlayer().getStorage().sendStorage(cm.getC(), 1002005);
}

function action()
{
	cm.dispose();
}