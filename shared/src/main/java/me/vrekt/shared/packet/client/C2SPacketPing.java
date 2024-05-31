package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.C2SPacketHandler;

/**
 * Ping request from the client to -> server
 * Adapted from LunarGdx
 */
public final class C2SPacketPing extends GamePacket {

    public static final int PACKET_ID = 2223;
    private long currentTime;

    public static void handle(C2SPacketHandler handler, ByteBuf buffer) {
        handler.handle(new C2SPacketPing(buffer));
    }

    public C2SPacketPing(ByteBuf buffer) {
        super(buffer);
    }

    public C2SPacketPing(long time) {
        this.currentTime = time;
    }

    public long time() {
        return currentTime;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeLong(currentTime);
    }

    @Override
    public void decode() {
        currentTime = buffer.readLong();
    }
}
