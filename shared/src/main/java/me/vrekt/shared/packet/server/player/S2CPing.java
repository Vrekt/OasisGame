package me.vrekt.shared.packet.server.player;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Ping packet sent from the server to the client in response to a ping
 */
public final class S2CPing extends GamePacket {

    private float gameTick;

    public S2CPing(float gameTick) {
        this.gameTick = gameTick;
    }

    public S2CPing(ByteBuf buffer) {
        super(buffer);
    }

    public float tick() {
        return gameTick;
    }

    @Override
    public int getId() {
        return Packets.S2C_PING;
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
