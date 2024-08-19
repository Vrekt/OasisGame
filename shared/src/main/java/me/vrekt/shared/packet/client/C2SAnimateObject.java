package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Client animation interacts with an object.
 * If the object is not valid, only the client sending will see the interaction
 * Drops by breaking pots/barrels are decided by host/server.
 * <p>
 * Should NOT be used for general interactions like items, etc
 */
public final class C2SAnimateObject extends GamePacket {

    private int objectId = -1;

    public C2SAnimateObject(ByteBuf buffer) {
        super(buffer);
    }

    public C2SAnimateObject(int objectId) {
        this.objectId = objectId;
    }

    public int objectId() {
        return objectId;
    }

    @Override
    public int getId() {
        return Packets.C2S_ANIMATE_OBJECT;
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
