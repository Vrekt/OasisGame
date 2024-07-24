package me.vrekt.shared.packet.server.obj;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Tells the client to remove a world object
 */
public final class S2CNetworkRemoveWorldObject extends GamePacket {

    public static final int PACKET_ID = 3000_9;
    private int objectId;

    public S2CNetworkRemoveWorldObject(ByteBuf buffer) {
        super(buffer);
    }

    public S2CNetworkRemoveWorldObject(int objectId) {
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
