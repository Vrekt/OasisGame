package me.vrekt.shared.packet.server.obj;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Tells the client to remove a world object
 */
public final class S2CNetworkRemoveWorldObject extends GamePacket {

    private int objectId;

    public S2CNetworkRemoveWorldObject(ByteBuf buffer) {
        super(buffer);
    }

    public S2CNetworkRemoveWorldObject(int objectId) {
        this.objectId = objectId;
    }

    /**
     * @return the ID of the object to remove
     */
    public int objectId() {
        return objectId;
    }

    @Override
    public int getId() {
        return Packets.S2C_REMOVE_OBJECT;
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
