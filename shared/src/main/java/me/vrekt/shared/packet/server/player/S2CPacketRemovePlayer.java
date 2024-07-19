package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import me.vrekt.shared.packet.GamePacket;

/**
 * Notify clients to remove a multiplayer player from their game instance
 */
public final class S2CPacketRemovePlayer extends GamePacket {

    public static final int PACKET_ID = 1121;

    private int entityId;
    private String username;


    public S2CPacketRemovePlayer(int entityId, String username) {
        this.entityId = entityId;
        this.username = (username == null ? StringUtil.EMPTY_STRING : username);
    }

    public S2CPacketRemovePlayer(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the ID
     */
    public int entityId() {
        return entityId;
    }

    public String username() {
        return username;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        writeString(username);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        username = readString();
    }

}
