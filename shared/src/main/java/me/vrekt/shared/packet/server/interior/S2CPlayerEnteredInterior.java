package me.vrekt.shared.packet.server.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.packet.GamePacket;

public final class S2CPlayerEnteredInterior extends GamePacket {

    public static final int ID = 3000_4;

    private InteriorWorldType type;
    private int entityId;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPlayerEnteredInterior(buffer));
    }

    public S2CPlayerEnteredInterior(ByteBuf buffer) {
        super(buffer);
    }

    public S2CPlayerEnteredInterior(InteriorWorldType type, int entityId) {
        this.type = type;
        this.entityId = entityId;
    }

    public int entityId() {
        return entityId;
    }

    public InteriorWorldType type() {
        return type;
    }

    @Override
    public int getId() {
        return ID;
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
        try {
            type = InteriorWorldType.values()[of];
        } catch (IndexOutOfBoundsException exception) {
            type = InteriorWorldType.NONE;
            GameLogging.exceptionThrown(this, "Failed to decode packet, type was %d", exception, of);
        }
    }
}
