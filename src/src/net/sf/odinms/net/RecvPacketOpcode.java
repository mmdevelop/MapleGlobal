package net.sf.odinms.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public enum RecvPacketOpcode implements WritableIntValueHolder {

// GENERAL
    PONG, // 14
    CLIENT_CRASH_REPORT, // ??
    CLIENT_HASH, // 16

    //LOGIN_PASSWORD
    CHECK_PASSWORD, // 1
    SELECT_WORLD, // 2
    
    WORLD_STATUS_REQUEST, // 3
    CONFIRM_EULA, // 4
    SET_GENDER, // 5
    
    CHECK_PIN_CODE, // 6
    UPDATE_PIN_CODE, // 7
    WORLD_INFO_REQUEST, // 8
    WORLD_INFO_REREQUEST, // 11 (in v30, not in v12?)
    SELECT_CHARACTER, // 9

    PLAYER_LOGGEDIN, // 10

    CHECK_CHARACTER_NAME, // 11
    CREATE_CHARACTER, // 12
    DELETE_CHARACTER, // 13

    //Neither in v12?
    LOGIN_SERVER_STATUS, // 19
    RELOG_REQUEST, // 21

    //From in-game
    ENTER_PORTAL, // 19
    CHANGE_CHANNEL, // 20
    ENTER_CASH_SHOP, // 21
    MOVE_PLAYER, // 22
    SIT_REQUEST, // 23

    CLOSE_RANGE_ATTACK, // 24
    RANGED_ATTACK, //25
    MAGIC_ATTACK, //26
    
    TAKE_DAMAGE, // 28
    CHAT, // 29
    EMOTE, // 30

    NPC_TALK, // 33
    NPC_TALK_MORE, // 34
    SHOP_ACTION, // 35
    STORAGE_ACTION, // 36

    ITEM_MOVE, // 37
    ITEM_USE, // 38
    SUMMON_BAG_USE, // 39
    CASH_ITEM_USE, // 41
    RETURN_SCROLL_USE, //42
    USE_SCROLL, //43
    DISTRIBUTE_AP, // 44
    HEAL_OVER_TIME, // 45
    DISTRIBUTE_SP, // 46
    GIVE_BUFF, // 47 (60 in v30)
    DROP_MESOS, // 50 (63 in v30)
    GIVE_FAME, // 51
    CHAR_INFO_REQUEST, // 53
    GROUP_MESSAGE, // 61
    WHISPER, // 62
    MESSENGER, // 63
    MINI_ROOM_OPERATION, // 64
    PARTY_OPERATION, // 65
    FRIEND_OPERATION, // 69

    PORTAL_SCRIPT, // 69
    
    MOVE_LIFE, // 88

    NPC_CONTROL_SPECIAL,
    DROP_PICK_UP, // 118
    TOUCH_CS,
    BUY_CS_ITEM,
    CS_COUPON;

    private int code = -2;

    public void setValue(int code) {
        this.code = code;
    }

    @Override
    public int getValue() {
        return code;
    }

    public static Properties getDefaultProperties() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("net.sf.odinms.recvops"));
        props.load(fis);
        fis.close();
        return props;
    }

    static {
        try {
            ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load recvops", e);
        }
    }
}
