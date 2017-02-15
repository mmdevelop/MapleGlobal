package net.sf.odinms.net;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public enum SendPacketOpcode implements WritableIntValueHolder {

// GENERAL
    PING, // 16

    //LOGIN
    CHECK_PASSWORD_RESULT, // 1
    GUEST_ID_LOGIN_RESULT, // 2
    CHECK_USER_LIMIT_RESULT, // 3
    SET_ACCOUNT_RESULT, // 4
    CONFIRM_EULA_RESULT, // 5
    PIN_OPERATION, // 7
    PIN_ASSIGNED, // 8

    //SERVERLIST
    WORLD_INFORMATION, // 9
    SELECT_WORLD_RESULT, // 10
    SELECT_CHARACTER_RESULT, // 11
    CHECK_DUPLICATED_ID_RESULT, // 12
    CREATE_NEW_CHARACTER_RESULT, // 13
    DELETE_CHARACTER_RESULT, // 14

    //CHANNEL
    CHANGE_CHANNEL, // 14
    RELOG_RESPONSE, // ?? not in v12

    INVENTORY_OPERATION, // 20
    INVENTORY_GROW, // 21
    STAT_CHANGED, // 22
    FORCED_STAT_SET, // 23
    FORCED_STAT_RESET, // 24
    CHANGE_SKILL_RECORD_RESULT, // 25
    SKILL_USE_RESULT, // 26
    GIVE_POPULARITY_RESULT, // 27
    SHOW_STATUS_INFO, // 28
    MEMO_RESULT, // 29
    MAP_TRANSFER_RESULT, // 30
    SUE_CHARACTER_RESULT, // 31
    //Not in v12, but in v30
    ANTI_MACRO_RESULT, // 35
    CLAIM_RESULT, // 36
    SET_CLAIM_SVR_AVAILABLE_TIME, // 37
    CLAIM_SVR_STATUS_CHANGED, // 38
    QUEST_CLEAR, // 39
    ENTRUSTED_SHOP_CHECK_RESULT, // 40
    //v12
    CHARACTER_INFO, // 34
    PARTY_RESULT, // 35
    FRIEND_RESULT, // 36
    
    GUILD_RESULT, // 48 (v30, not in v12)
    
    TOWN_PORTAL, // 37
    BROADCAST_MSG, // 38
    BBS_OPERATION, // 53 (v30, not in v12)

    SET_FIELD, // 41
    SET_CASH_SHOP, // 42

    TRANSFER_FIELD_REQ_IGNORED, // 45
    TRANSFER_CHANNEL_REQ_IGNORED, // 46
    FIELD_SPECIFIC_DATA, // 47
    GROUP_MESSAGE, // 48
    WHISPER, // 49
    SUMMON_ITEM_INAVAILABLE, // 50
    FIELD_EFFECT, // 51
    BLOW_WEATHER, // 52
    PLAY_JUKE_BOX, // 53
    ADMIN_RESULT, // 54
    QUIZ, // 55
    DESC, // 56
    CLOCK, // 57
    BOAT_EFFECT, // 59
    WARN_MESSAGE, // 61
    
    //These are in v30, not in v12, maybe there's only one in v12
    SET_QUEST_CLEAR, // 76
    SET_QUEST_TIME, // 77

    USER_ENTER_FIELD, // 63
    USER_LEAVE_FIELD, // 64

    CHAT, // 66
    MINI_ROOM_BALLOON, // 67

    SPAWN_PET, // 70
    PET_MOVE, // 71
    PET_ACTION, // 72
    PET_NAME_CHANGED, // 73
    PET_COMMAND, // 74

    SPAWN_SPECIAL_MAPOBJECT, // 95
    REMOVE_SPECIAL_MAPOBJECT, // 96

    MOVE_SUMMON, // 97
    SUMMON_ATTACK, // 98
    DAMAGE_SUMMON, // 99

    //CUserPool::OnUserRemotePacket
    MOVE_PLAYER, // 85
    CLOSE_RANGE_ATTACK, // 86
    RANGED_ATTACK, // 87
    MAGIC_ATTACK, // 88
    SKILL_PREPARE, // 89
    SKILL_CANCEL, // 90

    DAMAGE_PLAYER, // 91
    FACIAL_EXPRESSION, // 92
    SHOW_ITEM_EFFECT, // 99
    
    UPDATE_CHAR_LOOK, // 113
    SHOW_FOREIGN_EFFECT, // 114
    GIVE_FOREIGN_BUFF, // 115
    CANCEL_FOREIGN_BUFF, // 116
    UPDATE_PARTYMEMBER_HP, // 117

    GUILD_NAME_CHANGED, // 118
    GUILD_MARK_CHANGED, // 119

    SHOW_CHAIR, // 100
    SHOW_ITEM_GAIN_INCHAT, // 101
    MESOBAG_FAILED, // 105

    MOB_ENTER_FIELD, // 136
    MOB_LEAVE_FIELD, // 137
    MOB_CHANGE_CONTROLLER, // 138
    MOB_MOVE, // 140
    MOB_MOVE_RESPONSE, // 141
    MOB_STAT_SET, // 143
    MOB_STAT_RESET, // 144
    MOB_SUSPEND_RESET, // 145
    MOB_AFFECTED, // 146
    MOB_DAMAGED, // 147
    MOB_SPECIAL_EFFECT_BY_SKILL, // 148

    NPC_ENTER_FIELD, // 153
    NPC_LEAVE_FIELD, // 154
    NPC_CHANGE_CONTROLLER, // 155
    NPC_SET_SPECIAL_ACTION, // 129

    EMPLOYEE_ENTER_FIELD, // 161
    EMPLOYEE_LEAVE_FIELD, // 162
    EMPLOYEE_MINI_ROOM_BALLOON, // 163

    DROP_ENTER_FIELD, // 166
    DROP_LEAVE_FIELD, // 167

    MESSAGE_BOX_CREATE_FAILED, // 170
    MESSAGE_BOX_ENTER_FIELD, // 171
    MESSAGE_BOX_LEAVE_FIELD, // 172

    AFFECTED_AREA_CREATED, // 175
    AFFECTED_AREA_REMOVED, // 176

    TOWN_PORTAL_CREATED, // 179
    TOWN_PORTAL_REMOVED, // 180

    REACTOR_CHANGE_STATE, // 183
    REACTOR_ENTER_FIELD, // 185
    REACTOR_LEAVE_FIELD, // 186

    NPC_TALK, // 199
    SHOP, // 202
    SHOP_TRANSACTION, // 203
    STORAGE, // 207
    STORAGE_RESULT, // 208

    STORE_BANK, // 211

    MESSENGER, // 174
    MINI_ROOM_BASE, // 177

    SPAWN_DOOR, // 0xFF
    REMOVE_DOOR, // 0xFF
    AVATAR_MEGA, // 0xFF
    SHOW_MONSTER_HP, // 0xFF
    CS_OPERATION, // 0xFF
    CS_CASH, //NX cash
    CHALKBOARD; // 0xFF
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
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("net.sf.odinms.sendops"));
        props.load(fileInputStream);
        fileInputStream.close();
        return props;
    }

    static {
        try {
            ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sendops", e);
        }
    }
}
