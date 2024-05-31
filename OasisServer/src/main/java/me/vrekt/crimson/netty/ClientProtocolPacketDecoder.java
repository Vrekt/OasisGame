package me.vrekt.crimson.netty;

import me.vrekt.crimson.game.network.ServerAbstractConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * Handles decoding packets sent from clients
 */
public final class ClientProtocolPacketDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * The local session packet handler
     */
    private final ServerAbstractConnection handler;
    private final GameProtocol protocol;

    public ClientProtocolPacketDecoder(ServerAbstractConnection handler, GameProtocol protocol) {
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
                if (protocol.isClientPacket(pid)) {
                    protocol.handleClientPacket(pid, buf, handler, ctx);
                }
            }
        } catch (Exception any) {
            ctx.fireExceptionCaught(any);
        } finally {
            if (buf != null) {
                buf.release();
            }
        }
        return null;
    }
}
