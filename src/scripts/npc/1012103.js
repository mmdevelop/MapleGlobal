/* 
** NPC Name: Natalie the Owner
** Location: Henesys
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
	var mStyles = new Array((30030 + z), (30020 + z), (30000 + z), (30060 + z), (30150 + z), (30210 + z), (30140 + z), (30120 + z), (30200 + z), (30170 + z)); // apparently hairstyles with IDs 30300 and above do not exist in this old version
	var fStyles = new Array((31050 + z), (31040 + z), (31000 + z), (31150 + z), (31160 + z), (31100 + z), (31030 + z), (31080 + z), (31030 + z), (31070 + z));
	
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
		cm.sendSimple("I'm the head of this hair salon. If you have a #b#t4050001##k or a #b#t4051001##k allow me to take care of your hairdo. Please choose the one you want.#b\r\n#L0##i4050001##t4050001##l\r\n\r\n#L1##i4051001##t4051001##l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0)
		{
			mType = 1;
			currentStyles = cm.getChar().getGender() == 0 ? mStyles : fStyles;
			cm.sendStyle("I can totally change up your hairstyle and make it look so good. Why don't you change it up a bit? If you have #b#t4050001##k I'll change it for you. Choose the one to your liking.", currentStyles);
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
			cm.sendStyle("I can totally change your haircolor and make it look so good. Why don't you change it up a bit? With #b#t4051001##k I'll change it for you. Choose the one to your liking.", currentStyles);
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
		else if (mType == 1 && cm.haveItem(4050001, 1, false, true))
		{
			cm.gainItem(4050001, -1);
			cm.setHair(currentStyles[selection]);
			cm.sendOk("Enjoy your new and improved hairstyle!");
			cm.dispose();
		}
		else if (mType == 2 && cm.haveItem(4051001, 1, false, true))
		{
			cm.gainItem(4051001, -1);
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