package me.vrekt.shared.packet.client.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Client is requesting to enter an interior
 */
public final class C2SEnterInterior extends GamePacket {

    public static final int ID = 2001_7;

    private String interiorName;

    public C2SEnterInterior(String interiorName) {
        this.interiorName = interiorName;
    }

    public C2SEnterInterior(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void encode() {
        writeId();
        writeString(interiorName);
    }

    @Override
    public void decode() {
        this.interiorName = readString();
    }

    @Override
    public int getId() {
        return ID;
    }
}
