package me.vrekt.shared.packet.client;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Request to join a world.
 */
public final class C2SPacketJoinWorld extends GamePacket {

    private AtomicInteger worldId;
    private String username;
    private long clientTime;

    public C2SPacketJoinWorld(int worldId, String username, long clientTime) {
        this.worldId = new AtomicInteger(worldId);
        this.username = username == null ? StringUtil.EMPTY_STRING : username;
        this.clientTime = clientTime;
    }

    public C2SPacketJoinWorld(ByteBuf buffer) {
        super(buffer);
    }

    public int worldId() {
        return worldId.get();
    }

    public String username() {
        return username;
    }

    public long time() {
        return clientTime;
    }

    @Override
    public int getId() {
        return Packets.C2S_JOIN_WORLD;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(worldId.get());
        writeString(username);
        buffer.writeLong(clientTime);
    }

    @Override
    public void decode() {
        if (buffer.isReadable(4)) worldId = new AtomicInteger(buffer.readInt());
        username = readString();
        if (buffer.isReadable(8)) clientTime = buffer.readLong();
    }
}
