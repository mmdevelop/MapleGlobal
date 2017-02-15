package net.sf.odinms.net;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.login.LoginWorker;
import net.sf.odinms.tools.CField;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.MapleAESOFB;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapleServerHandler extends IoHandlerAdapter {

    private final static Logger log = LoggerFactory.getLogger(MapleServerHandler.class);
    private final static short MAPLE_VERSION = 12;
    private PacketProcessor processor;
    private int channel = -1;

    public MapleServerHandler(PacketProcessor processor) {
        this.processor = processor;
    }

    public MapleServerHandler(PacketProcessor processor, int channel) {
        this.processor = processor;
        this.channel = channel;
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        //(message)).getBytes()[0] + " to " + ((MapleClient)(session.getAttribute(MapleClient.CLIENT_KEY))).getPlayer().getName());
        Runnable r = ((MaplePacket) message).getOnSend();
        if (r != null) {
            r.run();
        }
        super.messageSent(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (!(cause instanceof ClassCastException))
        {
            cause.printStackTrace();            
        }
//        MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
//        log.error(MapleClient.getLogMessage(client, cause.getMessage()), cause);
//        ChannelServer.getInstance(1).broadcastPacket(
//         CField.UserPool.Chat.sendMessage(30000, "Exception: " + cause.getClass().getName() + ": " +
//         cause.getMessage()));
//         for (int i = 0; i < cause.getStackTrace().length; i++) {
//         StackTraceElement ste = cause.getStackTrace()[i];
//         ChannelServer.getInstance(1).broadcastPacket(CField.UserPool.Chat.sendMessage(30000, ste.toString()));
//         if (i > 2) {
//         break;
//         }
//         }
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {

        if (channel > -1) {
            if (ChannelServer.getInstance(channel).isShutdown()) {
                session.close();
                return;
            }
        }

        byte ivRecv[] = {70, 114, 122, 82};
        byte ivSend[] = {82, 48, 120, 115};

        ivRecv[3] = (byte) (Math.random() * 255);
        ivSend[3] = (byte) (Math.random() * 255);
        MapleAESOFB sendCypher = new MapleAESOFB(ivSend, (short) (0xFFFF - MAPLE_VERSION));
        MapleAESOFB recvCypher = new MapleAESOFB(ivRecv, MAPLE_VERSION);

        MapleClient client = new MapleClient(sendCypher, recvCypher, session);
        client.setChannel(channel);

        session.write(MaplePacketCreator.getHello(MAPLE_VERSION, ivSend, ivRecv, false));
        session.setAttribute(MapleClient.CLIENT_KEY, client);
        session.setIdleTime(IdleStatus.READER_IDLE, 30);
        session.setIdleTime(IdleStatus.WRITER_IDLE, 30);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        synchronized (session) {
            MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
            if (client != null) {
                client.disconnect();
                LoginWorker.getInstance().deregisterClient(client);
                session.removeAttribute(MapleClient.CLIENT_KEY);
            }
        }
        super.sessionClosed(session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        byte[] content = (byte[]) message;
        SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(content));
        short packetId = slea.readByte();
        MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        MaplePacketHandler packetHandler = processor.getHandler(packetId);
        // HexTool#toSting on large buffers is rather expensive - so only do it when we really need to
        
        //log.info("Received Message Recv({}) Message: {}\n{}", new Object[]{packetId, HexTool.toString(content), HexTool.toStringFromAscii(content)});
        if (log.isTraceEnabled() || log.isInfoEnabled()) {
            
            String from = "";
            if (client.getPlayer() != null) {
                from = "from " + client.getPlayer().getName() + " ";
            }
            //log.trace("Got Message {}handled by {} ({}) {}\n{}", new Object[]{from, content.length, HexTool.toString(content)});
            if (packetHandler == null && packetId != 0xE) {
                if (client.getPlayer() != null) {
                    client.getSession().write(MaplePacketCreator.serverNotice(2, "Unhandled packet [" + packetId + "] : " + HexTool.toString(content)));
                }
                //log.info("Got unhandeled Message {} ({}) {}\n{}", new Object[]{from, packetId, HexTool.toString(content), HexTool.toStringFromAscii(content)});
            } 
            else if (log.isTraceEnabled()) {
                //log.trace("Got Message {}handled by {} ({}) {}\n{}", new Object[]{from, content.length, HexTool.toString(content)});
            }
        }
        
        if (packetHandler != null && packetHandler.validateState(client)) {
            try {
                packetHandler.handlePacket(slea, client);
            } catch (Throwable t) {
                //log.error(MapleClient.getLogMessage(client, "Exception during processing packet: " + packetHandler.getClass().getName() + ": " + t.getMessage()), t);
            }
        }
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) throws Exception {
        MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        if (client != null && client.getPlayer() != null) {
        }
        if (client != null) {
            client.sendPing();
        }
        super.sessionIdle(session, status);
    }
}
