package me.vrekt.shared.packet.client;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Client interacts with an object.
 */
public final class C2SInteractWithObject extends GamePacket {

    private int objectId = -1;
    private InteractionType type;

    public C2SInteractWithObject(ByteBuf buffer) {
        super(buffer);
    }

    public C2SInteractWithObject(int objectId, InteractionType type) {
        Preconditions.checkNotNull(type);
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
    public int getId() {
        return Packets.C2S_INTERACT_WITH_OBJECT;
    }

    @Override
    public void encode() {
        writeId();

        buffer.writeInt(objectId);
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void decode() {
        if (buffer.isReadable(4)) objectId = buffer.readInt();
        if (buffer.isReadable(4)) {
            int ord = buffer.readInt();
            if (ord >= InteractionType.values().length || ord <= 0) {
                ord = 0;
            }
            type = InteractionType.values()[ord];
        }
    }


    /**
     * Various interaction types
     */
    public enum InteractionType {
        PICK_UP
    }

}
