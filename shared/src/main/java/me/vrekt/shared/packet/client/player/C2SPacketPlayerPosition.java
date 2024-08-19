package me.vrekt.shared.packet.client.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * A players position
 */
public final class C2SPacketPlayerPosition extends GamePacket {

    private int rotation;
    private float x, y;

    public C2SPacketPlayerPosition(float x, float y, int rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public C2SPacketPlayerPosition(ByteBuf buffer) {
        super(buffer);
    }


    /**
     * @return x position
     */
    public float x() {
        return x;
    }

    /**
     * @return y position
     */
    public float y() {
        return y;
    }

    /**
     * @return {@link me.vrekt.oasis.entity.component.facing.EntityRotation} ordinal.
     */
    public int rotation() {
        return rotation;
    }

    @Override
    public int getId() {
        return Packets.C2S_POSITION;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeInt(rotation);
    }

    @Override
    public void decode() {
        x = buffer.readFloat();
        y = buffer.readFloat();
        rotation = buffer.readInt();
    }

}
