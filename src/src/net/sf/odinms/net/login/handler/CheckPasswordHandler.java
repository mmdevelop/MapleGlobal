/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.odinms.net.login.handler;

import java.util.Calendar;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacketHandler;
import net.sf.odinms.net.login.LoginWorker;
import net.sf.odinms.tools.BCrypt;
import net.sf.odinms.tools.CLogin;
import net.sf.odinms.tools.KoreanDateUtil;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CheckPasswordHandler implements MaplePacketHandler {

    @Override
    public boolean validateState(MapleClient c) {
        return !c.isLoggedIn();
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String username = slea.readAsciiString(slea.readShort());
        String password = slea.readAsciiString(slea.readShort());
        
        c.setAccountName(username);

        boolean ipBan = c.hasBannedIP();
        boolean macBan = c.hasBannedMac();
        int loginState = c.login(username, password, ipBan || macBan);

        Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginState == 0 && (ipBan || macBan)) {
            loginState = 3;
            if (macBan) {
                String[] ipSplit = c.getSession().getRemoteAddress().toString().split(":");
                MapleCharacter.ban(ipSplit[0], "Enforcing account ban on account: " + username, false);
            }
        }

        if (loginState != 0) {
            c.getSession().write(CLogin.CheckPasswordResult.getAuthFailed(loginState));
            return;
        } else if (tempbannedTill.getTimeInMillis() != 0) {
            long tempban = KoreanDateUtil.getTempBanTimestamp(tempbannedTill.getTimeInMillis());
            byte reason = c.getBanReason();
            c.getSession().write(MaplePacketCreator.getTempBan(tempban, reason)); // TODO fix this packet
            return;
        }

        if (c.isGm()) {
            LoginWorker.getInstance().registerGMClient(c);
        } else {
            LoginWorker.getInstance().registerClient(c);
        }
    }
}
