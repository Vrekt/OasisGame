package me.vrekt.oasis.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.vrekt.oasis.network.connection.client.PlayerConnection;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * Handles decoding local server packets
 * Adapter from LunarGdx
 */
public class ServerProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    private final PlayerConnection handler;
    private final GameProtocol protocol;

    /**
     * Initialize this local decoder
     *
     * @param handler the handler
     */
    public ServerProtocolPacketDecoder(PlayerConnection handler, GameProtocol protocol) {
        super(protocol.getMaxPacketFrameLength(), 0, 4);
        this.handler = handler;
        this.protocol = protocol;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        ByteBuf buf = null;

        try {
            buf = (ByteBuf) super.decode(ctx, in);
            if (buf != null) {
                // ignore the length of the packet.
                buf.readInt();
                // retrieve packet from PID
                final int pid = buf.readInt();
                protocol.handle(pid, buf, handler, ctx);
            }
        } catch (Exception any) {
            GameLogging.exceptionThrown(this, "Failed to decode a packet", any);
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
        return null;
    }
}

