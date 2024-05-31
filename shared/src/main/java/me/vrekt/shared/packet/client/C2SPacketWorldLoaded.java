package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.C2SPacketHandler;

/**
 * Notify the server this client has loaded the world.
 */
public final class C2SPacketWorldLoaded extends GamePacket {

    public static final int PACKET_ID = 2225;

    public static void handle(C2SPacketHandler handler, ByteBuf buffer) {
        handler.handle(new C2SPacketWorldLoaded());
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
    }
}
