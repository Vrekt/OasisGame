package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Notify clients to remove a multiplayer player from their game instance
 */
public final class S2CNetworkRemovePlayer extends GamePacket {

    private int entityId;
    private String username;

    public S2CNetworkRemovePlayer(int entityId, String username) {
        this.entityId = entityId;
        this.username = (username == null ? StringUtil.EMPTY_STRING : username);
    }

    public S2CNetworkRemovePlayer(ByteBuf buffer) {
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
        return Packets.S2C_REMOVE_PLAYER;
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
