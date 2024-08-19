package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;
import org.apache.commons.lang3.StringUtils;

/**
 * A request to join a world yielded a world that was not found, or invalid.
 */
public final class S2CWorldInvalid extends GamePacket {

    private int worldId;
    private String reason;

    public S2CWorldInvalid(int worldId, String reason) {
        this.worldId = worldId;
        this.reason = reason;
    }

    public S2CWorldInvalid(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the world ID the player requested to join.
     */
    public int worldId() {
        return worldId;
    }

    /**
     * @return the reason the world was invalid.
     */
    public String reason() {
        return reason;
    }

    @Override
    public int getId() {
        return Packets.S2C_WORLD_INVALID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(worldId);
        writeString(reason == null ? StringUtils.EMPTY : reason);
    }

    @Override
    public void decode() {
        worldId = buffer.readInt();
        reason = readString();
    }


}
