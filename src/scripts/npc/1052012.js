/* 
** NPC Name: Mong from Kong
** Location: Kerning City
** Purpose: Internet Cafe Warper
** Made by: Rod Jalali / Editor417312 / etc & wackyracer / Joren McGrew
** Not GMS-like speech
*/

Packages.net.sf.odinms.server
Packages.net.sf.odinms.client
Packages.net.sf.odinms.scripting
Packages.java.lang

var status

function start()
{
	status = -1
	action(1, 0, 0)
}

function action(mode, type, selection)//TODO
{
	cm.sendNext("Hey, hey ... I don't think you're logging on from the internet cafe. You can't enter this place if you are logging on from home ...")
	cm.dispose()
}


//		if (State == 0) {
//			mHost.SendNext("Hey, hey ... I don't think you're logging on from the internet cafe. You can't enter this place if you are logging on from home ..."); //mHost.AskYesNo("Aren't you connected through the Internet Cafe? If so, then go in here ... you'll probably head to a familiar place. What do you think? Do you want to go in?");
//			mHost.Stop(); // Not letting anyone in the Internet Cafe just yet... Just.... Not... Yet... :P - wackyracer
//		}
//		else if (State == 1) {
//			if (Answer == 1) {
//				character.ChangeMap(193000000);
//				mHost.Stop();
//			}
//			else if (Answer == 0) {
//				mHost.SendNext("You must be busy, huh? But if you're loggin on from the internet cafe, then you should try going in. You may end up in a strange place once inside.");
//				mHost.Stop();
//			}
//			/*else { // this isn't needed for now because there isn't a check for anything before entering this map, unlike GMS which checks if you're really logged into a Wizet-sponsored Internet Cafe that permits you access into the internet cafe... i think...
//				mHost.sendNext("Hey, hey ... I don't think you're logging on from the internet cafe. You can't enter this place if you are logging on from home ...");
//				mHost.Stop();
//			}*/
//		}
//		else {
//			mHost.Stop();
//		}