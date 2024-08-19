package me.vrekt.shared.packet.client;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Attempt to authenticate with a server
 */
public final class C2SPacketAuthenticate extends GamePacket {

    private String gameVersion;
    private int protocolVersion;

    public C2SPacketAuthenticate(String gameVersion, int protocolVersion) {
        Preconditions.checkNotNull(gameVersion);
        this.gameVersion = gameVersion;
        this.protocolVersion = protocolVersion;
    }

    public C2SPacketAuthenticate(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return the client version
     */
    public String getGameVersion() {
        return gameVersion;
    }

    /**
     * @return the protocol version
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public int getId() {
        return Packets.C2S_AUTHENTICATE;
    }

    @Override
    public void encode() {
        writeId();
        writeString(gameVersion);
        buffer.writeInt(protocolVersion);
    }

    @Override
    public void decode() {
        gameVersion = readString();
        if (buffer.isReadable(4)) protocolVersion = buffer.readInt();
    }
}
