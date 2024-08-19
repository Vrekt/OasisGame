package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Server -> client chat message
 */
public final class S2CChatMessage extends GamePacket {

    private int from;
    private String message;

    public S2CChatMessage(ByteBuf buffer) {
        super(buffer);
    }

    public S2CChatMessage(int from, String message) {
        this.from = from;
        this.message = message;
    }

    /**
     * @return player ID from
     */
    public int from() {
        return from;
    }

    /**
     * @return msg contents
     */
    public String message() {
        return message;
    }

    @Override
    public int getId() {
        return Packets.S2C_CHAT_MESSAGE;
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
