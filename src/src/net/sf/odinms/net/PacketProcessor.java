package net.sf.odinms.net;

import net.sf.odinms.net.handler.KeepAliveHandler;
import net.sf.odinms.net.channel.handler.*;
import net.sf.odinms.net.login.handler.*;

public final class PacketProcessor {

    public enum Mode {

        LOGINSERVER,
        CHANNELSERVER
    };

    private static PacketProcessor instance;
    private MaplePacketHandler[] handlers;

    private PacketProcessor() {
        int maxRecvOp = 0;
        for (RecvPacketOpcode op : RecvPacketOpcode.values()) {
            if (op.getValue() > maxRecvOp) {
                maxRecvOp = op.getValue();
            }
        }
        handlers = new MaplePacketHandler[maxRecvOp + 1];
    }

    public MaplePacketHandler getHandler(short packetId) {
        if (packetId > handlers.length) {
            return null;
        }
        
        MaplePacketHandler handler = handlers[packetId];
        if (handler != null) {
            return handler;
        }
        return null;
    }

    public void registerHandler(RecvPacketOpcode code, MaplePacketHandler handler) {
        try {
            handlers[code.getValue()] = handler;
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            ;
        }
    }

    public synchronized static PacketProcessor getProcessor(Mode mode) {
        if (instance == null) {
            instance = new PacketProcessor();
            instance.reset(mode);
        }
        return instance;
    }

    public void reset(Mode mode) {
        handlers = new MaplePacketHandler[handlers.length];
        registerHandler(RecvPacketOpcode.PONG, new KeepAliveHandler());
        registerHandler(RecvPacketOpcode.CLIENT_HASH, new ClientHashHandler());
        registerHandler(RecvPacketOpcode.CLIENT_CRASH_REPORT, new ClientCrashHandler());
        if (mode == Mode.LOGINSERVER) {
            //Login
            registerHandler(RecvPacketOpcode.CHECK_PASSWORD, new CheckPasswordHandler());
            registerHandler(RecvPacketOpcode.CONFIRM_EULA, new ConfirmEULAHandler());
            registerHandler(RecvPacketOpcode.SET_GENDER, new SetGenderHandler());
            registerHandler(RecvPacketOpcode.CHECK_PIN_CODE, new CheckPinCodeHandler());
            registerHandler(RecvPacketOpcode.UPDATE_PIN_CODE, new UpdatePinCodeHandler());
            //World Select
            registerHandler(RecvPacketOpcode.WORLD_INFO_REQUEST, new WorldInfoRequestHandler());
            registerHandler(RecvPacketOpcode.WORLD_INFO_REREQUEST, new WorldInfoRequestHandler());
            registerHandler(RecvPacketOpcode.WORLD_STATUS_REQUEST, new WorldStatusRequestHandler());
            registerHandler(RecvPacketOpcode.SELECT_WORLD, new SelectWorldHandler());
            //Character Selection
            registerHandler(RecvPacketOpcode.SELECT_CHARACTER, new SelectCharacterHandler());
            registerHandler(RecvPacketOpcode.CREATE_CHARACTER, new CreateCharacterHandler());
            registerHandler(RecvPacketOpcode.CHECK_CHARACTER_NAME, new CheckCharacterNameHandler());
            registerHandler(RecvPacketOpcode.DELETE_CHARACTER, new DeleteCharacterHandler());

            registerHandler(RecvPacketOpcode.RELOG_REQUEST, new RelogRequestHandler());
        } else if (mode == Mode.CHANNELSERVER) {
            registerHandler(RecvPacketOpcode.PLAYER_LOGGEDIN, new PlayerLoggedInHandler());
            
            registerHandler(RecvPacketOpcode.ENTER_PORTAL, new EnterPortalHandler());
            registerHandler(RecvPacketOpcode.CHANGE_CHANNEL, new ChangeChannelHandler());
            registerHandler(RecvPacketOpcode.ENTER_CASH_SHOP, new EnterCashShopHandler());
            
            registerHandler(RecvPacketOpcode.MOVE_PLAYER, new MovePlayerHandler());
            registerHandler(RecvPacketOpcode.SIT_REQUEST, new SitRequestHandler());
            registerHandler(RecvPacketOpcode.CLOSE_RANGE_ATTACK, new CloseRangeAttackHandler());
            registerHandler(RecvPacketOpcode.RANGED_ATTACK, new RangedAttackHandler());
            
            registerHandler(RecvPacketOpcode.MAGIC_ATTACK, new MagicAttackHandler());
            
            registerHandler(RecvPacketOpcode.TAKE_DAMAGE, new TakeDamageHandler());
            registerHandler(RecvPacketOpcode.CHAT, new ChatHandler());
            registerHandler(RecvPacketOpcode.EMOTE, new EmoteHandler());
            
            registerHandler(RecvPacketOpcode.NPC_TALK, new NpcTalkHandler());
            registerHandler(RecvPacketOpcode.NPC_TALK_MORE, new NpcMoreTalkHandler());
            registerHandler(RecvPacketOpcode.NPC_CONTROL_SPECIAL, new NpcControlHandler());
            registerHandler(RecvPacketOpcode.SHOP_ACTION, new ShopActionHandler());
            registerHandler(RecvPacketOpcode.STORAGE_ACTION, new StorageActionHandler());
            registerHandler(RecvPacketOpcode.ITEM_MOVE, new ItemMoveHandler());
            registerHandler(RecvPacketOpcode.ITEM_USE, new ItemUseHandler());
            registerHandler(RecvPacketOpcode.SUMMON_BAG_USE, new SummonBagHandler());
            registerHandler(RecvPacketOpcode.CASH_ITEM_USE, new CashItemUseHandler());
            registerHandler(RecvPacketOpcode.RETURN_SCROLL_USE, new ReturnScrollUseHandler());
            registerHandler(RecvPacketOpcode.USE_SCROLL, new ScrollUseHandler());
            registerHandler(RecvPacketOpcode.DISTRIBUTE_AP, new DistributeAPHandler());
            registerHandler(RecvPacketOpcode.HEAL_OVER_TIME, new HealOverTimeHandler());
            registerHandler(RecvPacketOpcode.DISTRIBUTE_SP, new DistributeSPHandler());
            
            registerHandler(RecvPacketOpcode.GIVE_BUFF, new GiveBuffHandler());
            registerHandler(RecvPacketOpcode.DROP_MESOS, new DropMesosHandler());
            registerHandler(RecvPacketOpcode.GIVE_FAME, new GiveFameHandler());
            registerHandler(RecvPacketOpcode.CHAR_INFO_REQUEST, new CharInfoRequestHandler());
            registerHandler(RecvPacketOpcode.GROUP_MESSAGE, new GroupMessageHandler());
            registerHandler(RecvPacketOpcode.WHISPER, new WhisperHandler());
            registerHandler(RecvPacketOpcode.MESSENGER, new MessengerHandler());
            registerHandler(RecvPacketOpcode.MINI_ROOM_OPERATION, new MiniRoomHandler());
            registerHandler(RecvPacketOpcode.PARTY_OPERATION, new PartyOperationHandler());
            registerHandler(RecvPacketOpcode.FRIEND_OPERATION, new FriendOperationHandler());
            
            registerHandler(RecvPacketOpcode.PORTAL_SCRIPT, new PortalScriptHandler());
            registerHandler(RecvPacketOpcode.MOVE_LIFE, new MoveLifeHandler());
            registerHandler(RecvPacketOpcode.DROP_PICK_UP, new DropPickUpHandler());
            registerHandler(RecvPacketOpcode.TOUCH_CS, new CheckPlayerCashHandler());
            registerHandler(RecvPacketOpcode.BUY_CS_ITEM, new BuyCSItemHandler());
            //registerHandler(RecvPacketOpcode.CS_COUPON, new RedeemCSCoupon());//TODO
        } else {
            throw new RuntimeException("Unknown packet processor mode");
        }
    }
}
