package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;
import org.apache.commons.lang3.StringUtils;

/**
 * Sent from the server to indicate a disconnect for whatever reason.
 */
public final class S2CDisconnected extends GamePacket {

    // the reason the server disconnected the client
    private String disconnectReason;

    public S2CDisconnected(String disconnectReason) {
        this.disconnectReason = disconnectReason == null ? StringUtils.EMPTY : disconnectReason;
    }

    public S2CDisconnected(ByteBuf buffer) {
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
        return Packets.S2C_DISCONNECTED;
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
