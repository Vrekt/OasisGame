package me.vrekt.shared.packet.client.animation;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Notify the server of a player animation
 */
public final class C2SPlayerAnimation extends GamePacket {

    public static final int ID = 2001_9;

    private NetworkAnimation animation;
    private float animationTime;

    public C2SPlayerAnimation(ByteBuf buffer) {
        super(buffer);
    }

    public C2SPlayerAnimation(NetworkAnimation animation, float animationTime) {
        this.animation = animation;
        this.animationTime = animationTime;
    }

    public NetworkAnimation animation() {
        return animation;
    }

    public float animationTime() {
        return animationTime;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(animation.ordinal());
        buffer.writeFloat(animationTime);
    }
}
