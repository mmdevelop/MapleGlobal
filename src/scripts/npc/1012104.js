/* 
** NPC Name: Brittany the Assistant
** Location: Henesys
** Purpose: Hair Stylist (EXP)
** Made by: wackyracer / Joren McGrew & Diamondo25 / ????? ??????
** Fully GMS-like speech
*/

var status;
var mType;
var currentStyles;

function start()
{
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection)
{
	var z = cm.getPlayer().getHair() % 10;
	var mStyles = new Array((30030 + z), (30020 + z), (30000 + z), (30060 + z), (30150 + z), (30210 + z), (30140 + z), (30120 + z), (30200 + z), (30170 + z)); // apparently hairstyles with IDs 30300 and above do not exist in this old version
	var fStyles = new Array((31050 + z), (31040 + z), (31000 + z), (31150 + z), (31160 + z), (31100 + z), (31030 + z), (31080 + z), (31030 + z), (31070 + z));
	var x = Math.floor(Math.random() * (mStyles.length - 0) + 0);
	
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
		cm.sendSimple("I'm Brittany the assistant. If you have #b#t4050000##k or #b#t4051000##k by any chance, then how about letting me change your hairdo? Please choose the one you want.#b\r\n#L0##i4050000##t4050000##l\r\n\r\n#L1##i4051000##t4051000##l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0 && cm.haveItem(4050000, 1, false, true))
		{
			mType = 1;
			currentStyles = cm.getChar().getGender() == 0 ? mStyles : fStyles;
			cm.sendYesNo("If you use the regular coupon your hairstyle will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t4050000##k and really change your hairstyle?");
		}
		else if (selection == 1 && cm.haveItem(4051000, 1, false, true))
		{
			mType = 2;
			currentStyles = new Array();
			var currentHairType = cm.getPlayer().getHair() - (z); // removes last digit from hair
			for (var i = 0; i < 8; i++)
			{
				currentStyles.push(currentHairType + i);
			}
			cm.sendYesNo("If you use the regular coupon your haircolor will change RANDOMLY. Do you still want to use #b#t4051000##k and change your haircolor?");
		}
		else if (!cm.haveItem(4050000) || !cm.haveItem(4051000, 1, false, true))
		{
			cm.sendOk("Hmmm...it looks like you don't have our designated coupon..I'm afraid I can't style your hair without it. I'm sorry...");
			cm.dispose();
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (status == 2)
	{
		if (x < 0 || x > currentStyles.length)
		{
			cm.dispose();
		}
		else if (mode == 1 && mType == 1 && cm.haveItem(4050000, 1, false, true))
		{
			cm.gainItem(4050000, -1);
			cm.setHair(currentStyles[x]);
			cm.sendOk("Enjoy your new and improved hairstyle!");
			cm.dispose();
		}
		else if (mode == 1 && mType == 2 && cm.haveItem(4051000, 1, false, true))
		{
			cm.gainItem(4051000, -1);
			cm.setHair(currentStyles[x]);
			cm.sendOk("Enjoy your new and improved haircolor!");
			cm.dispose();
		}
		else
		{
			cm.sendOk("Hmmm...it looks like you don't have our designated coupon..I'm afraid I can't style your hair without it. I'm sorry...");
			cm.dispose();
		}
	}
}