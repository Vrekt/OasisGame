package me.vrekt.shared.packet.server.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Notify a player entered an interior
 * Does not spawn a player, only notifies the players in the current world, so they can remove them.
 */
public final class S2CPlayerEnteredInterior extends GamePacket {

    private Interior type;
    private int entityId;

    public S2CPlayerEnteredInterior(ByteBuf buffer) {
        super(buffer);
    }

    public S2CPlayerEnteredInterior(Interior type, int entityId) {
        this.type = type;
        this.entityId = entityId;
    }

    /**
     * @return the ID of the player
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return the type entered into
     */
    public Interior type() {
        return type;
    }

    @Override
    public int getId() {
        return Packets.S2C_PLAYER_ENTERED_INTERIOR;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
        final int of = buffer.readInt();
        type = Interior.of(of);
    }
}
