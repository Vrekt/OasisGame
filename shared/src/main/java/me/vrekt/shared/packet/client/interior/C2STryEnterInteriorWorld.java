package me.vrekt.shared.packet.client.interior;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.interior.S2CEnterInteriorWorld;

/**
 * Client is requesting to enter an interior
 */
public final class C2STryEnterInteriorWorld extends GamePacket {

    public static final int ID = 2001_7;

    private InteriorWorldType interiorWorldType;

    public C2STryEnterInteriorWorld(InteriorWorldType type) {
        Preconditions.checkNotNull(type);
        this.interiorWorldType = type;
    }

    public C2STryEnterInteriorWorld(ByteBuf buffer) {
        super(buffer);
    }

    public InteriorWorldType type() {
        return interiorWorldType;
    }

    @Override
    public int response() {
        return S2CEnterInteriorWorld.ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(interiorWorldType.ordinal());
    }

    @Override
    public void decode() {
        this.interiorWorldType = InteriorWorldType.values()[buffer.readInt()];
    }

    @Override
    public int getId() {
        return ID;
    }
}
