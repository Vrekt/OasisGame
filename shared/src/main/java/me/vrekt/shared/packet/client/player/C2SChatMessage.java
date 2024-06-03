package me.vrekt.shared.packet.client.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Chat message, should be validated
 */
public final class C2SChatMessage extends GamePacket {

    public static final int PACKET_ID = 2001_10;

    private int from;
    private String message;

    public C2SChatMessage(ByteBuf buffer) {
        super(buffer);
    }

    public C2SChatMessage(int from, String message) {
        if (message.length() > 150) {
            // differs from the text length in the GUI, because I don't exactly trust it.
            throw new UnsupportedOperationException("Message is too long!");
        }

        this.from = from;
        this.message = message;
    }

    public int from() {
        return from;
    }

    public String message() {
        return message;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(from);
        writeString(message);
    }

    @Override
    public void decode() {
        from = buffer.readInt();
        message = readString();
    }
}
