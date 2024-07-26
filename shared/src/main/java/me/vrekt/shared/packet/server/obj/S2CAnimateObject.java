package me.vrekt.shared.packet.server.obj;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Start animating an object.
 */
public final class S2CAnimateObject extends GamePacket {

    public static final int PACKET_ID = 3000_13;
    private int objectId;

    public S2CAnimateObject(ByteBuf buffer) {
        super(buffer);
    }

    public S2CAnimateObject(int objectId) {
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
