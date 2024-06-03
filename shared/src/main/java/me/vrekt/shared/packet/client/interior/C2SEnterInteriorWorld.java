package me.vrekt.shared.packet.client.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.codec.C2SPacketHandler;
import me.vrekt.shared.packet.GamePacket;

/**
 * Client is requesting to enter an interior
 */
public final class C2SEnterInteriorWorld extends GamePacket {

    public static final int ID = 2001_7;

    private InteriorWorldType interiorWorldType;
    private int entityId;

    public static void handle(C2SPacketHandler handler, ByteBuf in) {
        handler.handle(new C2SEnterInteriorWorld(in));
    }

    public C2SEnterInteriorWorld(int entityId, InteriorWorldType type) {
        this.entityId = entityId;
        this.interiorWorldType = type;
    }

    public C2SEnterInteriorWorld(ByteBuf buffer) {
        super(buffer);
    }

    public InteriorWorldType type() {
        return interiorWorldType;
    }

    public int entityId() {
        return entityId;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
        buffer.writeInt(interiorWorldType.ordinal());
    }

    @Override
    public void decode() {
        this.entityId = buffer.readInt();
        this.interiorWorldType = InteriorWorldType.values()[buffer.readInt()];
    }

    @Override
    public int getId() {
        return ID;
    }
}
