package me.vrekt.shared.packet.server.interior;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.packet.GamePacket;

public final class S2CPlayerEnteredInterior extends GamePacket {

    public static final int ID = 3000_4;

    private int entityId;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPlayerEnteredInterior(buffer));
    }

    public S2CPlayerEnteredInterior(ByteBuf buffer) {
        super(buffer);
    }

    public S2CPlayerEnteredInterior(int entityId) {
        this.entityId = entityId;
    }

    public int entityId() {
        return entityId;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(entityId);
    }

    @Override
    public void decode() {
        entityId = buffer.readInt();
    }
}
