package net.sf.odinms.net.login.handler;

import java.util.Calendar;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.CLogin;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class DeleteCharacterHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String date = Integer.toString(slea.readInt());
       
        try {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(4, 6));
            int day = Integer.parseInt(date.substring(6, 8));
            int cid = slea.readInt();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month - 1, day);
            boolean correctDate = c.checkBirthDate(cal);
            byte mode = 18; // 
            if (correctDate) {
                mode = 0;
                if (!c.deleteCharacter(cid)) {
                    mode = 22;
                }
            }
            c.getSession().write(CLogin.DeleteCharacterResult.deleteCharacter(cid, mode));
        } catch (NumberFormatException e) { //Packet Editting

        }

    }
}
