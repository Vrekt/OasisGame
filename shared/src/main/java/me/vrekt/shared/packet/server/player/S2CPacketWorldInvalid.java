package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.packet.GamePacket;
import org.apache.commons.lang3.StringUtils;

/**
 * A request to join a world yielded a world that was not found, or invalid.
 */
public final class S2CPacketWorldInvalid extends GamePacket {

    public static final int PACKET_ID = 1115;

    private int worldId;
    private String reason;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPacketWorldInvalid(buffer));
    }

    public S2CPacketWorldInvalid(int worldId, String reason) {
        this.worldId = worldId;
        this.reason = reason;
    }

    public S2CPacketWorldInvalid(ByteBuf buffer) {
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
    public String getReason() {
        return reason;
    }

    @Override
    public int getId() {
        return PACKET_ID;
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
