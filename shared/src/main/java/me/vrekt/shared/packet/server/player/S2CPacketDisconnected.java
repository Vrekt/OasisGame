package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.S2CPacketHandler;

/**
 * Sent from the server to indicate a disconnect for whatever reason.
 */
public final class S2CPacketDisconnected extends GamePacket {

    public static final int PACKET_ID = 1112;

    // the reason the server disconnected the client
    private String disconnectReason;

    public static void handle(S2CPacketHandler handler, ByteBuf buffer) {
        handler.handle(new S2CPacketDisconnected(buffer));
    }

    public S2CPacketDisconnected(String disconnectReason) {
        this.disconnectReason = disconnectReason;
    }

    public S2CPacketDisconnected(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the disconnect reason as a string
     */
    public String getDisconnectReason() {
        return disconnectReason;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(disconnectReason);
    }

    @Override
    public void decode() {
        disconnectReason = readString();
    }
}
