package me.vrekt.shared.packet.server.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Server -> Client enter interior request
 */
public final class S2CEnterInteriorWorld extends GamePacket {

    private Interior interior;
    private boolean isEnterable;

    public S2CEnterInteriorWorld(ByteBuf buffer) {
        super(buffer);
    }

    public S2CEnterInteriorWorld(Interior type, boolean isEnterable) {
        this.interior = type;
        this.isEnterable = isEnterable;
    }

    /**
     * @return the interior of the original request
     */
    public Interior interior() {
        return interior;
    }

    /**
     * @return {@code true} if the player is allowed to enter
     */
    public boolean isEnterable() {
        return isEnterable;
    }

    @Override
    public int getId() {
        return Packets.S2C_TRY_ENTER_INTERIOR;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(interior.ordinal());
        buffer.writeBoolean(isEnterable);
    }

    @Override
    public void decode() {
        interior = Interior.values()[buffer.readInt()];
        isEnterable = buffer.readBoolean();
    }

}
