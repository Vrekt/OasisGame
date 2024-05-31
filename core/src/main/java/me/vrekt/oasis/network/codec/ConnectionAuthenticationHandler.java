package me.vrekt.oasis.network.codec;

import com.badlogic.gdx.Gdx;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.vrekt.oasis.network.player.PlayerConnection;
import me.vrekt.shared.protocol.ProtocolDefaults;
import me.vrekt.shared.packet.client.C2SPacketAuthenticate;

/**
 * Handles authentication when connection is first established
 * Adapter from LunarGdx
 */
public final class ConnectionAuthenticationHandler extends ChannelInboundHandlerAdapter {

    private final PlayerConnection connection;

    public ConnectionAuthenticationHandler(PlayerConnection connection) {
        this.connection = connection;
    }

    /**
     * Invoked when the channel is first open, IE: we connected to the server.
     *
     * @param context context.
     */
    @Override
    public void channelActive(ChannelHandlerContext context) {
        connection.sendImmediately(new C2SPacketAuthenticate(ProtocolDefaults.PROTOCOL_NAME, ProtocolDefaults.PROTOCOL_VERSION));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Gdx.app.log("InboundNetworkHandler", "Exception caught", cause);
        connection.close();
    }
}
