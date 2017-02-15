/* 
** NPC Name: Computer
** Location: Kerning City Internet Cafe
** Purpose: Internet Cafe Premium Road Warper
** Made by: Rod Jalali / Editor417213 / etc & wackyracer / Joren McGrew
** Not GMS-like speech, due to the fact that no one can ever get into the Internet Cafe in GMS, even if they attempted to hack their way in. Near impossible to find the proper dialog.
*/

var status

var trainingAreas = [190000000, 191000000, 192000000, 195000000]

function start()
{
	status = -1
	action(1, 0, 0)
}

function action(mode, type, selection)
{
	if(mode == 0 || mode == -1)
	{
		cm.dispose()
		return
	}
	
	//if (Server.CharacterDatabase.isDonator == true) { // not sure if this is the right way to check for donator//TODO
	if(status == -1)
	{
		cm.sendNext("Bzzzt~Beep~Boop!! Welcome.. to the Internet Cafe!!.. I.. can warp.. you.. to different.. training areas.. within.. the special.. Internet Cafe.. Premium Road.. areas.... Experience points.. from.. monsters.. are doubled.. as well as.. drops.. and.. mesos!!..")
		
		++status
	}
	else if(status == 0)
	{
		cm.sendSimple("Please.. choose.. a.. destination....\r\n" +
		"#b#L0##m" + "190000000" +//selection = 0
		"##l\r\n#L1##m" + "191000000" +//selection = 1
		"##l\r\n#L2##m" + "192000000" +//selection = 2
		"##l\r\n#L3##m" + "195000000")//selection = 3
		
		++status
	}
	else//if(status == 1)
	{
		cm.getPlayer().changeMap(trainingAreas[selection])//cm.warp(trainingAreas[selection]) does the same (i.e. it doesn't warp all players)
		cm.dispose()
	}
}