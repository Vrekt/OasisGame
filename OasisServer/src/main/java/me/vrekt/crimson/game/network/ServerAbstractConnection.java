package me.vrekt.crimson.game.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.shared.codec.C2SPacketHandler;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.player.S2CKeepAlive;

/**
 * Represents a basic connection.
 */
public abstract class ServerAbstractConnection extends ChannelInboundHandlerAdapter implements C2SPacketHandler {

    protected final Channel channel;
    protected boolean isConnected;
    protected CrimsonGameServer server;
    protected float lastKeepAlive;

    public ServerAbstractConnection(Channel channel, CrimsonGameServer server) {
        this.channel = channel;
        this.server = server;
    }

    public ByteBufAllocator alloc() {
        return channel.alloc();
    }

    /**
     * Send client keep alive
     */
    public void keepAlive() {
        sendImmediately(new S2CKeepAlive());
    }

    public abstract boolean isAlive();

    /**
     * Update this connection.
     */
    public void update() {

    }

    /**
     * Disconnect
     */
    public abstract void disconnect();

    /**
     * Connection closed error
     *
     * @param exception the possible exception or {@code  null} if none
     */
    public abstract void connectionClosed(Throwable exception);

    /**
     * @return if this connection is connected in any way.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Send a packet
     *
     * @param packet the packet
     */
    public void sendImmediately(GamePacket packet) {
        packet.alloc(alloc());
        channel.writeAndFlush(packet);
    }

    /**
     * Queue a packet
     *
     * @param packet the packet
     */
    public void queue(GamePacket packet) {
        packet.alloc(alloc());
        channel.write(packet);
    }

    /**
     * Queue a direct buffer
     *
     * @param direct buffer
     */
    public void queue(ByteBuf direct) {
        channel.write(direct);
    }

    /**
     * Flush
     */
    public void flush() {
        channel.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        connectionClosed(null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connectionClosed(cause);
    }
}
