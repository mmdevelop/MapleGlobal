/* 
** NPC Name: Ms. Tan
** Location: Henesys
** Purpose: Skin Color Changer
** Made by: wackyracer / Joren McGrew & Diamondo25 / ????? ??????
** Fully GMS-like speech
*/

var status;

function start()
{
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection)
{
	var skinColors = new Array(0, 1, 2, 3, 4);
	
	if (mode == 1)
	{
		status++;
	}
	else if (mode == -1)
	{
		status = -1;
		cm.dispose();
		return;
    }
	else
	{
		status--;
	}
	
	if (status == 0)
	{
		cm.sendSimple("Well, hello! Welcome to the Henesys Skin-Care! Would you like to have a firm, tight, healthy looking skin like mine? With a #b#t4053000##k, you can let us take care of the rest and have the kind of skin you've always wanted!#b\r\n#L0##i4053000##t4053000##l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0)
		{
			cm.sendStyle("With our specialized machine, you can see yourself after the treatment in advance. What kind of skin-treatment would you like to do? Choose the style of your liking.", skinColors);
		}
		else {
			cm.dispose();
		}
	}
	
	else if (status == 2)
	{
		if (selection < 0 || selection > skinColors.length - 1)
		{
			cm.dispose();
		}
		else if (cm.haveItem(4053000, 1, false, true))
		{
			cm.gainItem(4053000, -1);
			cm.setSkin(skinColors[selection]);
			cm.sendOk("Enjoy your new and improved skin color!");
			cm.dispose();
		}
		else
		{
			cm.sendOk("Um... you don't have the skin-care coupon you need to receive the treatment. Sorry, but I am afraid we can't do it for you...");
			cm.dispose();
		}
	}
}