package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Client destroys a world object.
 */
public final class C2SDestroyWorldObject extends GamePacket {

    private int objectId = -1;

    public C2SDestroyWorldObject(ByteBuf buffer) {
        super(buffer);
    }

    public C2SDestroyWorldObject(int objectId) {
        this.objectId = objectId;
    }

    public int objectId() {
        return objectId;
    }

    @Override
    public int getId() {
        return Packets.C2S_DESTROY_OBJECT;
    }

    @Override
    public void encode() {
        writeId();

        buffer.writeInt(objectId);
    }

    @Override
    public void decode() {
        if (buffer.isReadable(4)) objectId = buffer.readInt();
    }
}
