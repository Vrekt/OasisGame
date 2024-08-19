package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Sent from the server to authenticate a new client
 */
public final class S2CAuthenticate extends GamePacket {

    // if the authentication was successful
    private boolean authenticationSuccessful;
    private String gameVersion;
    private int protocolVersion;

    public S2CAuthenticate(boolean authenticationSuccessful, String gameVersion, int protocolVersion) {
        this.authenticationSuccessful = authenticationSuccessful;
        this.gameVersion = gameVersion;
        this.protocolVersion = protocolVersion;
    }

    public S2CAuthenticate(ByteBuf buffer) {
        super(buffer);
    }

    /**
     * @return {@code true} if the authentication attempt was successful.
     */
    public boolean isAuthenticationSuccessful() {
        return authenticationSuccessful;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public int getId() {
        return Packets.S2C_AUTHENTICATE;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeBoolean(authenticationSuccessful);
        writeString(gameVersion);
        buffer.writeInt(protocolVersion);
    }

    @Override
    public void decode() {
        authenticationSuccessful = buffer.readBoolean();
        gameVersion = readString();
        protocolVersion = buffer.readInt();
    }
}
