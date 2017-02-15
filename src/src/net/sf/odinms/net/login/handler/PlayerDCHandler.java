package net.sf.odinms.net.login.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class PlayerDCHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        /* Connection con = DatabaseConnection.getConnection();
        try {
        PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin WHERE name = ?");
        ps.setString(1, c.getAccountName());
        ps.executeUpdate();
        ps.close();
        con.close();
        //Even in commented code I will not let any connections leak - Exile
        } catch (SQLException se) {
        }
        }*/
    }
}