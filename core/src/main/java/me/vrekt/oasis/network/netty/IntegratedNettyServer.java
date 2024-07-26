package me.vrekt.oasis.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.connection.DefaultChannelHandler;
import me.vrekt.oasis.network.connection.server.PlayerServerConnection;
import me.vrekt.oasis.network.netty.codec.ClientProtocolPacketDecoder;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.codec.ProtocolPacketEncoder;
import me.vrekt.shared.codec.ServerChannels;
import me.vrekt.shared.protocol.GameProtocol;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.security.Security;

/**
 * The netty server.
 */
public final class IntegratedNettyServer {

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup parent, child;
    private final ProtocolPacketEncoder encoder;

    private final String ip;
    private final int port;

    private final SslContext sslContext;

    private final GameProtocol protocol;
    private final IntegratedGameServer server;

    /**
     * Initialize the bootstrap and server.
     *
     * @param ip   the server IP address
     * @param port the server port
     */
    public IntegratedNettyServer(String ip, int port, GameProtocol protocol, IntegratedGameServer server) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.server = server;

        // java.security.NoSuchProviderException: no such provider: BC
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            GameLogging.info(this, "Installing BouncyCastle Security Providers");
            Security.addProvider(new BouncyCastleProvider());
        }

        try {
            final SelfSignedCertificate ssc = new SelfSignedCertificate();
            final SslProvider provider = SslProvider.JDK;
            this.sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .sslProvider(provider)
                    .build();
        } catch (Exception any) {
            throw new RuntimeException(any);
        }

        final ServerChannels channelConfig = Epoll.isAvailable()
                ? ServerChannels.EPOLL
                : KQueue.isAvailable()
                ? ServerChannels.KQUEUE
                : ServerChannels.NIO;

        bootstrap = new ServerBootstrap();

        parent = channelConfig.get();
        child = channelConfig.newGroup();

        bootstrap.group(parent, child)
                .channel(channelConfig.channel())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(@NonNull SocketChannel channel) {
                        handleSocketConnection(channel);
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        encoder = new ProtocolPacketEncoder();
    }

    /**
     * Handle a new socket channel
     *
     * @param channel the channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        final PlayerServerConnection connection = new PlayerServerConnection(channel, server);

        final LengthFieldBasedFrameDecoder decoder = new ClientProtocolPacketDecoder(connection, protocol);
        final DefaultChannelHandler channelHandler = new DefaultChannelHandler(connection);

        channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
        channel.pipeline().addLast(decoder);
        channel.pipeline().addLast(encoder);
        channel.pipeline().addLast(channelHandler);
    }

    /**
     * Bind sync.
     */
    public boolean bindSync() {
        try {
            bootstrap.bind(ip, port).sync();
            return true;
        } catch (InterruptedException exception) {
            GameLogging.exceptionThrown(this, "Failed to bind server!", exception);
        }
        return false;
    }

    /**
     * Shutdown the server
     */
    public void shutdown() {
        child.shutdownGracefully();
        parent.shutdownGracefully();
    }

}
