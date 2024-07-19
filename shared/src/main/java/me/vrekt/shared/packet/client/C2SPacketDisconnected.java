package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import me.vrekt.shared.packet.GamePacket;

/**
 * Inform the server the client has disconnected.
 */
public final class C2SPacketDisconnected extends GamePacket {

    public static final int PACKET_ID = 2222;

    // given reason, if any.
    private String givenReason;

    /**
     * @param givenReason the reason or {@code null} if not specified.
     */
    public C2SPacketDisconnected(String givenReason) {
        this.givenReason = givenReason == null ? StringUtil.EMPTY_STRING : givenReason;
    }

    public C2SPacketDisconnected(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the given reason, or {@code ""} if none.
     */
    public String reason() {
        return givenReason;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(givenReason);
    }

    @Override
    public void decode() {
        givenReason = readString();
    }
}
