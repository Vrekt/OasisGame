package me.vrekt.oasis.network.connection;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Default channel handler for exceptions and disconnects.
 */
public final class DefaultChannelHandler extends ChannelInboundHandlerAdapter {

    private final NetworkConnection connection;

    public DefaultChannelHandler(NetworkConnection connection) {
        this.connection = connection;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        connection.channelClosed(null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connection.channelClosed(cause);
    }
}
