package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class ReturnScrollUseHandler extends AbstractMaplePacketHandler
{
    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) 
    {   
        System.out.println("return to map handler invoked.");
        short slotid = slea.readShort();
        int itemid = slea.readInt();
        
        MapleCharacter player = c.getPlayer();
        
        //Client side already checks for correct location usage (vicky scrolls cant be used in ossyria and vice versa). TODO server side check just in case (packet editing)
        //In v12, return scrolls aren't allowed in orbis tower
        if (MapleItemInformationProvider.getInstance().isTownScroll(itemid) && c.getPlayer().haveItem(itemid, 1, false, true) && !checkOrbisTower(player))
        {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte)slotid, (short)1, false, true);
            
            switch (itemid)
            {
                case 2030000: //Nearest Town.  
                    int destination = player.getMap().getReturnMapId();
                    ;
                    player.changeMap(destination);
                    break;
                case 2030001: //Lith Harbor
                    player.changeMap(104000000);
                    break;
                case 2030002: //Ellinia
                    player.changeMap(101000000);
                    break;
                case 2030003: //Perion
                    player.changeMap(102000000);
                    break;
                case 2030004: //Henesys
                    player.changeMap(100000000);
                    break;
                case 2030005: //Kerning
                    player.changeMap(103000000);
                    break;
                case 2030006: //Sleepywood
                    player.changeMap(105040300);
                    break;
                case 2030007: //Dead Mine - oddly enough, there are no scrolls for el nath or orbis in v12, yet there is one for Dead Mines
                    player.changeMap(211041500);
                    break;                    
            }
        }
    }
    
    private boolean checkOrbisTower(MapleCharacter player)
    {
        switch (player.getMapId())
        {
            case 200080200:    //Orbis: Orbis Tower <20th Floor>
            case 200080300:    //Orbis: Orbis Tower <19th Floor>
            case 200080400:    //Orbis: Orbis Tower <18th Floor>
            case 200080500:    //Orbis: Orbis Tower <17th Floor>
            case 200080600:    //Orbis: Orbis Tower <16th Floor>
            case 200080700:    //Orbis: Orbis Tower <15th Floor>
            case 200080800:    //Orbis: Orbis Tower <14th Floor>
            case 200080900:    //Orbis: Orbis Tower <13th Floor>
            case 200081000:    //Orbis: Orbis Tower <12th Floor>
            case 200081100:    //Orbis: Orbis Tower <11th Floor>
            case 200081200:    //Orbis: Orbis Tower <10th Floor>
            case 200081300:    //Orbis: Orbis Tower <9th Floor>
            case 200081400:    //Orbis: Orbis Tower <8th Floor>
            case 200081500:    //Orbis: Orbis Tower <7th Floor>
            case 200081600:    //Orbis: Orbis Tower <6th Floor>
            case 200081700:    //Orbis: Orbis Tower <5th Floor>
            case 200081800:    //Orbis: Orbis Tower <4th Floor>
            case 200081900:    //Orbis: Orbis Tower <3rd Floor>
            case 200082000:    //Orbis: Orbis Tower <2nd Floor>
            case 200082100:    //Orbis: Orbis Tower <1st Floor>        
                return true;
            default:
                return false;            
        }
    }
    
}
