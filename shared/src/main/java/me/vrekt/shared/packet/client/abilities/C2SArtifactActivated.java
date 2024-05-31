package me.vrekt.shared.packet.client.abilities;

import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.shared.packet.GamePacket;

/**
 * The player activated an artifact
 */
public final class C2SArtifactActivated extends GamePacket {

    public static final int ID = 2001_6;

    private String key;
    private int level;
    private float duration;

    public C2SArtifactActivated(Artifact artifact) {
        this.key = artifact.getKey();
        this.level = artifact.getArtifactLevel();
        this.duration = artifact.getArtifactDuration();
    }

    public C2SArtifactActivated(ByteBuf buffer) {
        super(buffer);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(key);
        buffer.writeInt(level);
        buffer.writeFloat(duration);
    }

    @Override
    public void decode() {
        key = readString();
        level = buffer.readInt();
        duration = buffer.readFloat();
    }
}
