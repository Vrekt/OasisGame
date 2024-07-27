package me.vrekt.shared.packet.server.obj;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.client.C2SInteractWithObject;

/**
 * A response for {@link C2SInteractWithObject}
 */
public final class S2CInteractWithObjectResponse extends GamePacket {

    public static final int PACKET_ID = 3000_15;

    private int objectId;
    private boolean valid;

    public S2CInteractWithObjectResponse(ByteBuf buffer) {
        super(buffer);
    }

    public S2CInteractWithObjectResponse(int objectId, boolean valid) {
        this.objectId = objectId;
        this.valid = valid;
    }

    public int objectId() {
        return objectId;
    }

    public boolean valid() {
        return valid;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();

        buffer.writeInt(objectId);
        buffer.writeBoolean(valid);
    }

    @Override
    public void decode() {
        objectId = buffer.readInt();
        valid = buffer.readBoolean();
    }

}
