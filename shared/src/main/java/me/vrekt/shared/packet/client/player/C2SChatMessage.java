package me.vrekt.shared.packet.client.player;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Chat message, should be validated
 */
public final class C2SChatMessage extends GamePacket {

    private String message;

    public C2SChatMessage(ByteBuf buffer) {
        super(buffer);
    }

    public C2SChatMessage(String message) {
        Preconditions.checkNotNull(message);
        Preconditions.checkArgument(message.length() < 150);

        this.message = message;
    }

    public String message() {
        return message;
    }

    @Override
    public int getId() {
        return Packets.C2S_CHAT;
    }

    @Override
    public void encode() {
        writeId();
        writeString(message);
    }

    @Override
    public void decode() {
        message = readString();
    }
}
