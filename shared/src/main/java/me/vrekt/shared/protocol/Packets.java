package me.vrekt.shared.protocol;

/**
 * All packet IDs
 */
public final class Packets {

    // client
    public static final int C2S_AUTHENTICATE = 1000_1;
    public static final int C2S_JOIN_WORLD = 1000_2;
    public static final int C2S_WORLD_LOADED = 1000_3;
    public static final int C2S_INTERIOR_LOADED = 1000_4;
    public static final int C2S_TRY_ENTER_INTERIOR = 1000_5;
    public static final int C2S_KEEP_ALIVE = 1000_6;
    public static final int C2S_PING = 1000_7;
    public static final int C2S_DISCONNECTED = 1000_8;
    public static final int C2S_CHAT = 1000_9;
    public static final int C2S_POSITION = 1000_10;
    public static final int C2S_VELOCITY = 1000_11;
    public static final int C2S_ANIMATE_OBJECT = 1000_12;
    public static final int C2S_DESTROY_OBJECT = 1000_13;
    public static final int C2S_INTERACT_WITH_OBJECT = 1000_14;

    // server
    public static final int S2C_AUTHENTICATE = 2000_1;
    public static final int S2C_JOIN_WORLD = 2000_2;
    public static final int S2C_WORLD_INVALID = 2000_3;
    public static final int S2C_TRY_ENTER_INTERIOR = 2000_4;
    public static final int S2C_PLAYER_ENTERED_INTERIOR = 2000_5;
    public static final int S2C_PLAYER_SYNC = 2000_6;
    public static final int S2C_ENTITY_SYNC = 2000_7;
    public static final int S2C_OBJECT_SYNC = 2000_8;
    public static final int S2C_CREATE_PLAYER = 2000_9;
    public static final int S2C_CREATE_ENTITY = 2000_10;
    public static final int S2C_CREATE_OBJECT = 2000_11;
    public static final int S2C_REMOVE_PLAYER = 2000_12;
    public static final int S2C_REMOVE_ENTITY = 2000_13;
    public static final int S2C_REMOVE_OBJECT = 2000_14;
    public static final int S2C_CREATE_CONTAINER = 2000_15;
    public static final int S2C_CREATE_WORLD_DROP = 2000_16;
    public static final int S2C_DESTROY_OBJECT_RESPONSE = 2000_17;
    public static final int S2C_INTERACT_OBJECT_RESPONSE = 2000_18;
    public static final int S2C_ANIMATE_OBJECT = 2000_19;
    public static final int S2C_KEEP_ALIVE = 2000_20;
    public static final int S2C_PLAYER_POSITION = 2000_21;
    public static final int S2C_PLAYER_VELOCITY = 2000_22;
    public static final int S2C_DISCONNECTED = 2000_23;
    public static final int S2C_CHAT_MESSAGE = 2000_24;
    public static final int S2C_TELEPORT = 2000_25;
    public static final int S2C_PLAYER_TELEPORTED = 2000_26;
    public static final int S2C_NETWORK_FRAME = 2000_27;
    public static final int S2C_PING = 2000_28;

}
