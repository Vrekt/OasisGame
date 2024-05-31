package me.vrekt.oasis.network.netty;

import com.badlogic.gdx.utils.Disposable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.network.codec.ConnectionAuthenticationHandler;
import me.vrekt.oasis.network.codec.ServerProtocolPacketDecoder;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.codec.ClientChannels;
import me.vrekt.shared.codec.ProtocolPacketEncoder;
import me.vrekt.shared.protocol.GameProtocol;

import javax.net.ssl.SSLException;

/**
 * Local game client server
 * Adapter from netty
 */
public final class GameClientServer implements Disposable {

    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    private final String ip;
    private final int port;

    private final SslContext ssl;
    private final GameProtocol protocol;

    private PlayerConnection connection;

    /**
     * Initialize the bootstrap
     *
     * @param protocol the protocol to use
     * @param ip       the server IP address
     * @param port     the server port
     */
    public GameClientServer(GameProtocol protocol, String ip, int port) {
        this.protocol = protocol;
        this.ip = ip;
        this.port = port;

        final ClientChannels channelConfig = Epoll.isAvailable()
                ? ClientChannels.EPOLL
                : KQueue.isAvailable()
                ? ClientChannels.KQUEUE
                : ClientChannels.NIO;

        group = channelConfig.get();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(channelConfig.channel())
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        handleSocketConnection(channel);
                    }
                });

        try {
            ssl = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Handle a new socket connection
     *
     * @param channel channel
     */
    private void handleSocketConnection(SocketChannel channel) {
        GameLogging.info(this, "Socket connection establish to remote server.");

        connection = new PlayerConnection(channel, protocol, GameManager.game(), GameManager.getPlayer());
        channel.pipeline().addLast(ssl.newHandler(channel.alloc(), ip, port));
        channel.pipeline().addLast(new ServerProtocolPacketDecoder(connection, protocol));
        channel.pipeline().addLast(new ProtocolPacketEncoder());
        channel.pipeline().addLast(new ConnectionAuthenticationHandler(connection));
    }

    public PlayerConnection getConnection() {
        return connection;
    }

    /**
     * Connect.
     *
     * @throws Exception any
     */
    public void connect() throws Exception {
        bootstrap.connect(ip, port).sync();
    }

    @Override
    public void dispose() {
        group.shutdownGracefully();
        connection.dispose();
        protocol.dispose();
    }

}
