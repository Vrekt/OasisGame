package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.obj.S2CDestroyWorldObjectResponse;

/**
 * Client destroys a world object.
 */
public final class C2SDestroyWorldObject extends GamePacket {

    public static final int PACKET_ID = 2222_32;
    private int objectId;

    public C2SDestroyWorldObject(ByteBuf buffer) {
        super(buffer);
    }

    public C2SDestroyWorldObject(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public int response() {
        return S2CDestroyWorldObjectResponse.PACKET_ID;
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
