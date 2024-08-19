package me.vrekt.oasis.network.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.vrekt.oasis.network.connection.NetworkConnection;
import me.vrekt.oasis.utility.logging.ServerLogging;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * Handles decoding packets sent from clients
 */
public final class ClientProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The local session packet handler
     */
    private final NetworkConnection handler;
    private final GameProtocol protocol;

    public ClientProtocolPacketDecoder(NetworkConnection handler, GameProtocol protocol) {
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
            ServerLogging.info(this, "Failed to decode packet from client!");
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
        return null;
    }
}
