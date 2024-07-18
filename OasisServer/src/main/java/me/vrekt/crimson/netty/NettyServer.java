package me.vrekt.crimson.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
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
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.network.ServerAbstractConnection;
import me.vrekt.crimson.game.network.ServerPlayerConnection;
import me.vrekt.shared.codec.ProtocolPacketEncoder;
import me.vrekt.shared.codec.ServerChannels;
import me.vrekt.shared.protocol.GameProtocol;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.concurrent.CompletableFuture;

/**
 * The netty server.
 */
public final class NettyServer {

    private final ServerBootstrap bootstrap;
    private final EventLoopGroup parent, child;
    private final ProtocolPacketEncoder encoder;

    private final String ip;
    private final int port;

    private final SslContext sslContext;

    private final GameProtocol protocol;
    private final CrimsonGameServer server;

    /**
     * Initialize the bootstrap and server.
     *
     * @param ip   the server IP address
     * @param port the server port
     */
    public NettyServer(String ip, int port, GameProtocol protocol, CrimsonGameServer server) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
        this.server = server;

        // java.security.NoSuchProviderException: no such provider: BC
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            System.out.println("JVM Installing BouncyCastle Security Providers to the Runtime");
            Security.addProvider(new BouncyCastleProvider());
        } else {
            System.out.println("JVM Installed with BouncyCastle Security Providers");
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
                    protected void initChannel(SocketChannel channel) {
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
        final ServerAbstractConnection connection = new ServerPlayerConnection(channel, server);
        final LengthFieldBasedFrameDecoder decoder = new ClientProtocolPacketDecoder(connection, protocol);

        channel.pipeline().addLast(sslContext.newHandler(channel.alloc()));
        channel.pipeline().addLast(decoder);
        channel.pipeline().addLast(encoder);
        channel.pipeline().addLast(connection);
    }

    /**
     * Bind
     *
     * @return the result.
     */
    public CompletableFuture<ChannelFuture> bind() {
        final CompletableFuture<ChannelFuture> result = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                final ChannelFuture future = bootstrap.bind(ip, port).sync();
                result.complete(future);
            } catch (Exception any) {
                result.completeExceptionally(any);
            }
        });

        return result;
    }

    /**
     * Shutdown the server
     */
    public void shutdown() {
        child.shutdownGracefully();
        parent.shutdownGracefully();
    }

}
