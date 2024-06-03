package me.vrekt.shared.packet.server.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.packet.GamePacket;

/**
 * Server -> Client enter interior request
 */
public final class S2CEnterInteriorWorld extends GamePacket {

    public static final int ID = 3000_3;

    private InteriorWorldType interiorWorldType;
    private boolean isEnterable;

    public S2CEnterInteriorWorld(ByteBuf buffer) {
        super(buffer);
    }

    public S2CEnterInteriorWorld(InteriorWorldType type, boolean isEnterable) {
        this.interiorWorldType = type;
        this.isEnterable = isEnterable;
    }

    public InteriorWorldType interior() {
        return interiorWorldType;
    }

    public boolean isEnterable() {
        return isEnterable;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(interiorWorldType.ordinal());
        buffer.writeBoolean(isEnterable);
    }

    @Override
    public void decode() {
        interiorWorldType = InteriorWorldType.values()[buffer.readInt()];
        isEnterable = buffer.readBoolean();
    }

    @Override
    public int getId() {
        return ID;
    }
}
