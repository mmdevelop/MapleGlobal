/* 
** NPC Name: Dr. Feeble
** Location: Henesys
** Purpose: Plastic Surgeon (EXP)
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
	var currentColor = (cm.getPlayer().getFace() % 1000) - (cm.getPlayer().getFace() % 100);
	var mStyles = new Array((20000 + currentColor), (20001 + currentColor), (20002 + currentColor), (20003 + currentColor), (20004 + currentColor), (20005 + currentColor), (20006 + currentColor), (20007 + currentColor), (20008 + currentColor));
	var fStyles = new Array((21000 + currentColor), (21001 + currentColor), (21002 + currentColor), (21003 + currentColor), (21004 + currentColor), (21005 + currentColor), (21006 + currentColor), (21007 + currentColor), (21008 + currentColor));
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
		cm.sendSimple("Hi, I pretty much shouldn't be doing this, but with a #b#t4052000##k, I will do it anyways for you. But don't forget, it will be random!#b\r\n#L0##i4052000##t4052000##l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0 && cm.haveItem(4052000, 1, false, true))
		{
			mType = 1;
			currentStyles = cm.getChar().getGender() == 0 ? mStyles : fStyles;
			cm.sendYesNo("If you use the regular coupon your face will change RANDOMLY with a chance to obtain a new experimental look that I came up with. Are you going to use #b#t4050000##k and really change your face?");
		}
		else if (!cm.haveItem(4052000, 1, false, true))
		{
			cm.sendOk("Hmmm...it looks like you don't have our designated coupon..I'm afraid I can't change your face without it. I'm sorry...");
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
		else if (mode == 1 && mType == 1 && cm.haveItem(4052000, 1, false, true))
		{
			cm.gainItem(4052000, -1);
			cm.setHair(currentStyles[x]);
			cm.sendOk("Enjoy your new and improved face!");
			cm.dispose();
		}
		else
		{
			cm.sendOk("Hmmm...it looks like you don't have our designated coupon..I'm afraid I can't change your face without it. I'm sorry...");
			cm.dispose();
		}
	}
}