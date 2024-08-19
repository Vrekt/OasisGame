package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Ping request from the client to -> server
 * Adapted from LunarGdx
 */
public final class C2SPacketPing extends GamePacket {
    private float gameTick;

    public C2SPacketPing(ByteBuf buffer) {
        super(buffer);
    }

    public C2SPacketPing(float gameTick) {
        this.gameTick = gameTick;
    }

    public float tick() {
        return gameTick;
    }

    @Override
    public int getId() {
        return Packets.C2S_PING;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeFloat(gameTick);
    }

    @Override
    public void decode() {
        if (buffer.isReadable(4)) gameTick = buffer.readFloat();
    }
}
