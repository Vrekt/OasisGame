package me.vrekt.shared.packet.server.obj;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * A response to {@link me.vrekt.shared.packet.client.C2SDestroyWorldObject}
 */
public final class S2CDestroyWorldObjectResponse extends GamePacket {

    public static final int PACKET_ID = 3000_14;

    private int objectId;
    private boolean valid;

    public S2CDestroyWorldObjectResponse(ByteBuf buffer) {
        super(buffer);
    }

    public S2CDestroyWorldObjectResponse(int objectId, boolean valid) {
        this.objectId = objectId;
        this.valid = valid;
    }

    public int objectId() {
        return objectId;
    }

    /**
     * @return if the action was allowed.
     */
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
