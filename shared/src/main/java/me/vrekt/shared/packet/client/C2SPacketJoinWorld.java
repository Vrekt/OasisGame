package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.codec.C2SPacketHandler;
import me.vrekt.shared.packet.server.S2CPacketJoinWorld;

/**
 * Request to join a world.
 */
public final class C2SPacketJoinWorld extends GamePacket {

    public static final int PACKET_ID = 2224;

    private String worldName;
    private String username;
    private long clientTime;
    // indicates to send all current players within separate packets
    // instead of one large chunk.
    // TODO
    private boolean batchPlayers;

    public static void handle(C2SPacketHandler handler, ByteBuf buffer) {
        handler.handle(new C2SPacketJoinWorld(buffer));
    }

    public C2SPacketJoinWorld(String worldName, String username, long clientTime) {
        this.worldName = worldName;
        this.username = username == null ? StringUtil.EMPTY_STRING : username;
        this.clientTime = clientTime;
    }

    public C2SPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    public String worldName() {
        return worldName;
    }

    public String username() {
        return username;
    }

    public long time() {
        return clientTime;
    }

    @Override
    public int response() {
        return S2CPacketJoinWorld.PACKET_ID;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();
        writeString(worldName);
        writeString(username);
        buffer.writeLong(clientTime);
    }

    @Override
    public void decode() {
        worldName = readString();
        username = readString();
        clientTime = buffer.readLong();
    }
}
