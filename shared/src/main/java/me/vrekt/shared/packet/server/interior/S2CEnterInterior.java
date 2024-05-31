package me.vrekt.shared.packet.server.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Server -> Client enter interior request
 */
public final class S2CEnterInterior extends GamePacket {

    public static final int ID = 3000_3;

    private String interiorName;
    private boolean isEnterable;

    public S2CEnterInterior(ByteBuf buffer) {
        super(buffer);
    }

    public S2CEnterInterior(String interiorName, boolean isEnterable) {
        this.interiorName = interiorName;
        this.isEnterable = isEnterable;
    }

    public String interiorName() {
        return interiorName;
    }

    public boolean isEnterable() {
        return isEnterable;
    }

    @Override
    public void encode() {
        writeId();
        writeString(interiorName);
        buffer.writeBoolean(isEnterable);
    }

    @Override
    public void decode() {
        interiorName = readString();
        isEnterable = buffer.readBoolean();
    }

    @Override
    public int getId() {
        return ID;
    }
}
