package me.vrekt.shared.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.vrekt.shared.packet.GamePacket;

/**
 * Encodes incoming packets then appends the length + packet.
 * Adapted from LunarGdx
 */
@ChannelHandler.Sharable
public class ProtocolPacketEncoder extends MessageToByteEncoder<GamePacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, GamePacket packet, ByteBuf out) {
        try {
            packet.encode();

            final int length = packet.getBuffer().readableBytes();
            out.writeInt(length);
            out.writeBytes(packet.getBuffer());
        } catch (Exception any) {
            ctx.fireExceptionCaught(any);
        } finally {
            packet.release();
        }
    }

}
