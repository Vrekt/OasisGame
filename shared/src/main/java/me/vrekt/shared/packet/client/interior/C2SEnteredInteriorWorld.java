package me.vrekt.shared.packet.client.interior;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.interior.S2CEnterInteriorWorld;

/**
 * Enter an interior
 */
public class C2SEnteredInteriorWorld extends GamePacket {

    public static final int ID = 2001_11;

    private InteriorWorldType interiorWorldType;

    public C2SEnteredInteriorWorld(InteriorWorldType type) {
        Preconditions.checkNotNull(type);
        this.interiorWorldType = type;
    }

    public C2SEnteredInteriorWorld(ByteBuf buffer) {
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
