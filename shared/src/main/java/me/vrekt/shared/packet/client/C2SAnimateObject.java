package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Client animation interacts with an object.
 * If the object is not valid, only the client sending will see the interaction
 * Drops by breaking pots/barrels are decided by host/server.
 * <p>
 * Should NOT be used for general interactions like items, etc
 */
public final class C2SAnimateObject extends GamePacket {

    public static final int PACKET_ID = 2222_31;

    private int objectId;

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
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();

        buffer.writeInt(objectId);
    }

    @Override
    public void decode() {
        objectId = buffer.readInt();
    }


}
