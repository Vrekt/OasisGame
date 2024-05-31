package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.C2SPacketHandler;

/**
 * Attempt to authenticate with a server
 */
public final class C2SPacketAuthenticate extends GamePacket {

    public static final int PACKET_ID = 2221;

    private String gameVersion;
    private int protocolVersion;

    public static void handle(C2SPacketHandler handler, ByteBuf buffer) {
        handler.handle(new C2SPacketAuthenticate(buffer));
    }

    public C2SPacketAuthenticate(String gameVersion, int protocolVersion) {
        this.gameVersion = gameVersion;
        this.protocolVersion = protocolVersion;
    }

    private C2SPacketAuthenticate(ByteBuf buffer) {
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
        return PACKET_ID;
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
        protocolVersion = buffer.readInt();
    }
}
