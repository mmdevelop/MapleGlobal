/* 
** NPC Name: Don Giovanni
** Location: Kerning City
** Purpose: Hair Stylist (VIP)
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
	var mStyles = new Array((30030 + z), (30020 + z), (30000 + z), (30130 + z), (30190 + z), (30110 + z), (30180 + z), (30050 + z), (30040 + z), (30160 + z)); // apparently hairstyles with IDs 30300 and above do not exist in this old version
	var fStyles = new Array((31050 + z), (31040 + z), (31000 + z), (31060 + z), (31090 + z), (31020 + z), (31130 + z), (31120 + z), (31140 + z), (31010 + z));
	
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
		cm.sendSimple("I'm the head of this hair salon. If you have a #b#t4050003##k or a #b#t4051003##k allow me to take care of your hairdo. Please choose the one you want.#b\r\n#L0##i4050003##t4050003##l\r\n\r\n#L1##i4051003##t4051003##l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0)
		{
			mType = 1;
			currentStyles = cm.getChar().getGender() == 0 ? mStyles : fStyles;
			cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? If you have #b#t4050003##k I'll change it for you. Choose the one to your liking.", currentStyles);
		}
		else if (selection == 1)
		{
			mType = 2;
			currentStyles = new Array();
			var currentHairType = cm.getPlayer().getHair() - (z); // removes last digit from hair
			for (var i = 0; i < 8; i++)
			{
				currentStyles.push(currentHairType + i);
			}
			cm.sendStyle("I can totally change your haircolor and make it look so good. Why don't you change it up a bit? With #b#t4051003##k I'll change it for you. Choose the one to your liking.", currentStyles);
		}
		else
		{
			cm.dispose();
		}
	}
	
	else if (status == 2)
	{
		if (selection < 0 || selection > currentStyles.length)
		{
			cm.dispose();
		}
		else if (mType == 1 && cm.haveItem(4050003, 1, false, true))
		{
			cm.gainItem(4050003, -1);
			cm.setHair(currentStyles[selection]);
			cm.sendOk("Enjoy your new and improved hairstyle!");
			cm.dispose();
		}
		else if (mType == 2 && cm.haveItem(4051003, 1, false, true))
		{
			cm.gainItem(4051003, -1);
			cm.setHair(currentStyles[selection]);
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