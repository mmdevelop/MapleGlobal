/* 
** NPC Name: Andre
** Location: Kerning City
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
	var mStyles = new Array((30000 + z), (30020 + z), (30030 + z), (30040 + z), (30050 + z), (30110 + z), (30130 + z), (30160 + z), (30180 + z), (30190 + z)); // apparently hairstyles with IDs 30300 and above do not exist in this old version
	var fStyles = new Array((31000 + z), (31010 + z), (31020 + z), (31040 + z), (31050 + z), (31060 + z), (31090 + z), (31120 + z), (31130 + z), (31140 + z));
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
		cm.sendSimple("I'm Brittany the assistant. If you have #b#t4050002##k or #b#t4051002##k by any chance, then how about letting me change your hairdo? Please choose the one you want.#b\r\n#L0##i4050002##t4050002##l\r\n\r\n#L1##i4051002##t4051002##l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0 && cm.haveItem(4050002, 1, false, true))
		{
			mType = 1;
			currentStyles = cm.getChar().getGender() == 0 ? mStyles : fStyles;
			cm.sendYesNo("If you use the regular coupon your hairstyle will change RANDOMLY with a chance to obtain a new experimental style that I came up with. Are you going to use #b#t4050002##k and really change your hairstyle?");
		}
		else if (selection == 1 && cm.haveItem(4051002, 1, false, true))
		{
			mType = 2;
			currentStyles = new Array();
			var currentHairType = cm.getPlayer().getHair() - (z); // removes last digit from hair
			for (var i = 0; i < 8; i++)
			{
				currentStyles.push(currentHairType + i);
			}
			cm.sendYesNo("If you use the regular coupon your haircolor will change RANDOMLY. Do you still want to use #b#t4051002##k and change your haircolor?");
		}
		else if (!cm.haveItem(4050002) || !cm.haveItem(4051002, 1, false, true))
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
		else if (mode == 1 && mType == 1 && cm.haveItem(4050002, 1, false, true))
		{
			cm.gainItem(4050002, -1);
			cm.setHair(currentStyles[x]);
			cm.sendOk("Enjoy your new and improved hairstyle!");
			cm.dispose();
		}
		else if (mode == 1 && mType == 2 && cm.haveItem(4051002, 1, false, true))
		{
			cm.gainItem(4051002, -1);
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