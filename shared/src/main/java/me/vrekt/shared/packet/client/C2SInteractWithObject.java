package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.obj.S2CInteractWithObjectResponse;

/**
 * Client interacts with an object.
 */
public final class C2SInteractWithObject extends GamePacket {

    public static final int PACKET_ID = 2222_33;

    private int objectId;
    private InteractionType type;

    public C2SInteractWithObject(ByteBuf buffer) {
        super(buffer);
    }

    public C2SInteractWithObject(int objectId, InteractionType type) {
        this.objectId = objectId;
        this.type = type;
    }

    public int objectId() {
        return objectId;
    }

    public InteractionType type() {
        return type;
    }

    @Override
    public int response() {
        return S2CInteractWithObjectResponse.PACKET_ID;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();

        buffer.writeInt(objectId);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void decode() {
        objectId = buffer.readInt();
        int ord = buffer.readInt();
        if (ord >= InteractionType.values().length || ord <= 0) {
            ord = 0;
        }
        type = InteractionType.values()[ord];
    }


    /**
     * Various interaction types
     */
    public enum InteractionType {
         PICK_UP
    }

}
