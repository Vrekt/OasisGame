package me.vrekt.shared.packet.server.obj;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Start animating an object.
 */
public final class S2CAnimateObject extends GamePacket {

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
        return Packets.S2C_ANIMATE_OBJECT;
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
