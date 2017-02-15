/* 
** NPC Name: Denma the Owner
** Location: Henesys
** Purpose: Plastic Surgeon (VIP)
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
		cm.sendSimple("Well, hello! Welcome to the Henesys Plastic Surgery! Would you like to transform your face into something new? With a #b#t4052001##k, you can let us take care of the rest and have the face you've always wanted~!#b\r\n#L0##i4052001##t4052001##l#k");
	}
	
	else if (status == 1)
	{
		if (selection == 0)
		{
			mType = 1;
			currentStyles = cm.getChar().getGender() == 0 ? mStyles : fStyles;
			cm.sendStyle("Let's see... I can totally transform your face into something new. Don't you want to try it? For #b#t4052001##k, you can get the face of your liking. Take your time in choosing the face of your preference.", currentStyles);
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
		else if (mType == 1 && cm.haveItem(4052001, 1, false, true))
		{
			cm.gainItem(4052001, -1);
			cm.setHair(currentStyles[selection]);
			cm.sendOk("Enjoy your new and improved face!");
			cm.dispose();
		}
		else
		{
			cm.sendOk("Hmmm...it looks like you don't have our designated coupon..I'm afraid I can't style your hair without it. I'm sorry...");
			cm.dispose();
		}
	}
}