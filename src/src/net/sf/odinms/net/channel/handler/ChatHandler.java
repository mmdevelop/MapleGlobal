package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.PublicChatHandler;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * MapleGlobal.org - 2016
 * @Author - Ginseng
 */
public class ChatHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        String message = slea.readMapleAsciiString();
        MapleCharacter player = c.getPlayer();

        if (!player.isGM() && message.length() > 90) {
            AutobanManager.getInstance().autoban(player.getClient(), ", " + player.getName() + " Sent a message with length: " + message.length() + ". ");
            return;
        }
        /**
         * 0 = White
         * 1 = Orange (Buddy)
         * 2 = Pink (Party)
         * 3 = Purple (Guild)
         * 4 = White
         * 5 = Light Pink (Special)
         * 6 = Blue (Server Notice)
         */
        if (!CommandProcessor.getInstance().processCommand(c, message) && player.getCanTalk() && !PublicChatHandler.doChat(c, message)) {
            if (player.getGMText() == 0) {
                player.getMap().broadcastMessage(CField.UserPool.Chat.sendMessage(player.getId(), message));
            } else if (player.getGMText() == 7) {
                player.getMap().broadcastMessage(CField.UserPool.Chat.sendAdminMessage(player.getId(), message));
            } else {
                switch (player.getGMText()) {
                    case 1: //Buddy (Orange)
                    case 2: //Party (Pink)
                    case 3: //Guild (Purple)
                    case 4: //Regular (White)
                        player.getMap().broadcastMessage(CField.GroupMessage.multiChat(player.getName(), message, player.getGMText() - 1));
                        break;
                    case 5: //Special (Light Pink)
                    case 6: //Blue (Server Notice)
                        player.getMap().broadcastMessage(MaplePacketCreator.serverNotice(player.getGMText(), player.getName() + " : " + message));
                        break;
                    case 8: //Whisper
                        player.getMap().broadcastMessage(MaplePacketCreator.getWhisper(player.getName(), c.getChannel(), message));
                        break;
                }
                player.getMap().broadcastMessage(CField.UserPool.Chat.sendMessage(player.getId(), message));
            }
        }
        
        // c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.startKiteEffect(player, 2130002, message));

    }
}
