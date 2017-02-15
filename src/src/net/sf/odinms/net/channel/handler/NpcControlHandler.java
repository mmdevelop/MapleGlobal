/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.SendPacketOpcode;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Max
 */
public class NpcControlHandler extends AbstractMaplePacketHandler
{

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) 
    {
        int length = (int) slea.available();
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (length == 6)
        {
           // ;
            mplew.write(130);
            mplew.writeInt(slea.readInt());
            mplew.writeShort(slea.readShort());
            c.announce(mplew.getPacket());                      
        } 
        else if (length > 6)
        {
//            ;
//            byte[] bytes = slea.read(length - 9);
//            mplew.write(130);
//            mplew.write(bytes);
//            c.announce(mplew.getPacket());            
        }
          
    }
    
}
