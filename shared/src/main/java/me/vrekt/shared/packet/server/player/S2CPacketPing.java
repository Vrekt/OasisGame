package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;

/**
 * Ping packet sent from the server to the client in response to a ping
 */
public final class S2CPacketPing extends GamePacket {

    public static final int PACKET_ID = 1113;

    // current client time in ms, current server time in ms.
    private long clientTime, serverTime;
    private float gameTick;

    public S2CPacketPing(float gameTick) {
        this.gameTick = gameTick;
    }

    public S2CPacketPing(ByteBuf buffer) {
        super(buffer);
    }

    public float tick() {
        return gameTick;
    }

    public long getClientTime() {
        return clientTime;
    }

    public long getServerTime() {
        return serverTime;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeFloat(gameTick);
    }

    @Override
    public void decode() {
        gameTick = buffer.readFloat();
    }
}
