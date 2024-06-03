package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Server -> client chat message
 */
public final class S2CChatMessage extends GamePacket {

    public static final int PACKET_ID = 3000_6;

    private int from;
    private String message;

    public S2CChatMessage(ByteBuf buffer) {
        super(buffer);
    }

    public S2CChatMessage(int from, String message) {
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
