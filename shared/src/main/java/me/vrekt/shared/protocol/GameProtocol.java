package me.vrekt.shared.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.vrekt.shared.packet.client.*;
import me.vrekt.shared.packet.client.interior.C2SEnterInteriorWorld;
import me.vrekt.shared.packet.client.player.C2SChatMessage;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.packet.server.*;
import me.vrekt.shared.codec.C2SPacketHandler;
import me.vrekt.shared.codec.S2CPacketHandler;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Game protocol
 * Adapter from LunarGdx
 */
public final class GameProtocol {

    private final int protocolVersion;
    private final String protocolName;
    // max frame length allowed of a packet
    // Depending on how complex/and or large packets can get
    private final int maxPacketFrameLength = 65536;

    private final Map<Integer, BiConsumer<ByteBuf, S2CPacketHandler>> server = new HashMap<>();
    private final Map<Integer, BiConsumer<ByteBuf, C2SPacketHandler>> client = new HashMap<>();

    /**
     * Initialize a new protocol.
     *
     * @param protocolVersion the current protocol version
     * @param protocolName    the protocol name
     */
    public GameProtocol(int protocolVersion, String protocolName) {
        this.protocolVersion = protocolVersion;
        this.protocolName = protocolName;
        initializeDefaults();
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public int getMaxPacketFrameLength() {
        return maxPacketFrameLength;
    }

    /**
     * @return a map of all server packet handlers
     */
    public Map<Integer, BiConsumer<ByteBuf, S2CPacketHandler>> getServerHandlers() {
        return server;
    }

    /**
     * @return a map of all client packet handlers
     */
    public Map<Integer, BiConsumer<ByteBuf, C2SPacketHandler>> getClientHandlers() {
        return client;
    }

    /**
     * Initialize default packet handlers
     */
    private void initializeDefaults() {
        initializeClientHandlers();
        initializeServerHandlers();
    }

    private void initializeServerHandlers() {
        server.put(S2CPacketAuthenticate.PACKET_ID, (buf, handler) -> S2CPacketAuthenticate.handle(handler, buf));
        server.put(S2CPacketCreatePlayer.PACKET_ID, (buf, handler) -> S2CPacketCreatePlayer.handle(handler, buf));
        server.put(S2CPacketDisconnected.PACKET_ID, (buf, handler) -> S2CPacketDisconnected.handle(handler, buf));
        server.put(S2CPacketJoinWorld.PACKET_ID, (buf, handler) -> S2CPacketJoinWorld.handle(handler, buf));
        server.put(S2CPacketPing.PACKET_ID, (buf, handler) -> S2CPacketPing.handle(handler, buf));
        server.put(S2CPacketPlayerPosition.PACKET_ID, (buf, handler) -> S2CPacketPlayerPosition.handle(handler, buf));
        server.put(S2CPacketPlayerVelocity.PACKET_ID, (buf, handler) -> S2CPacketPlayerVelocity.handle(handler, buf));
        server.put(S2CPacketRemovePlayer.PACKET_ID, (buf, handler) -> S2CPacketRemovePlayer.handle(handler, buf));
        server.put(S2CStartGame.PACKET_ID, (buf, handler) -> S2CStartGame.handle(handler, buf));
        server.put(S2CPacketWorldInvalid.PACKET_ID, (buf, handler) -> S2CPacketWorldInvalid.handle(handler, buf));
        server.put(S2CKeepAlive.PACKET_ID, (buf, handler) -> S2CKeepAlive.handle(handler, buf));
        server.put(S2CPlayerEnteredInterior.ID, (buf, handler) -> S2CPlayerEnteredInterior.handle(handler, buf));
        server.put(S2CChatMessage.PACKET_ID, (buf, handler) -> handler.handle(new S2CChatMessage(buf)));
        server.put(S2CNetworkFrame.ID, (buf, handler) -> handler.handle(new S2CNetworkFrame(buf)));
    }

    private void initializeClientHandlers() {
        client.put(C2SPacketAuthenticate.PACKET_ID, (buf, handler) -> C2SPacketAuthenticate.handle(handler, buf));
        client.put(C2SPacketDisconnected.PACKET_ID, (buf, handler) -> C2SPacketDisconnected.handle(handler, buf));
        client.put(C2SPacketJoinWorld.PACKET_ID, (buf, handler) -> C2SPacketJoinWorld.handle(handler, buf));
        client.put(C2SPacketPing.PACKET_ID, (buf, handler) -> C2SPacketPing.handle(handler, buf));
        client.put(C2SPacketPlayerPosition.PACKET_ID, (buf, handler) -> C2SPacketPlayerPosition.handle(handler, buf));
        client.put(C2SPacketPlayerVelocity.PACKET_ID, (buf, handler) -> C2SPacketPlayerVelocity.handle(handler, buf));
        client.put(C2SPacketClientLoaded.PACKET_ID, (buf, handler) -> C2SPacketClientLoaded.handle(handler, buf));
        client.put(C2SKeepAlive.PACKET_ID, (buf, handler) -> C2SKeepAlive.handle(handler, buf));
        client.put(C2SEnterInteriorWorld.ID, (buf, handler) -> C2SEnterInteriorWorld.handle(handler, buf));
        client.put(C2SChatMessage.PACKET_ID, (buf, handler) -> handler.handle(new C2SChatMessage(buf)));
    }

    /**
     * Check if the packet exists
     *
     * @param pid the packet ID
     * @return {@code true} if so
     */
    public boolean isClientPacket(int pid) {
        return client.containsKey(pid);
    }

    /**
     * Check if the provided {@code pid} is server.
     *
     * @param pid the pid
     * @return {@code true} if so
     */
    public boolean isServerPacket(int pid) {
        return server.containsKey(pid);
    }

    /**
     * Change a server packet handler
     * This must be invoked after the protocol is initialized (only IF, {@code initializeDefaults} in the constructor is {@code true})
     *
     * @param pid     the pid
     * @param handler the new handler
     */
    public void changeServerPacketHandler(int pid, BiConsumer<ByteBuf, S2CPacketHandler> handler) {
        server.put(pid, handler);
    }

    /**
     * Change a client packet handler
     * This must be invoked after the protocol is initialized (only IF, {@code initializeDefaults} in the constructor is {@code true})
     *
     * @param pid     the pid
     * @param handler the new handler
     */
    public void changeClientPacketHandler(int pid, BiConsumer<ByteBuf, C2SPacketHandler> handler) {
        client.put(pid, handler);
    }

    /**
     * Register a new client packet.
     *
     * @param pid     the id
     * @param handler the handler
     */
    public void registerClientPacket(int pid, BiConsumer<ByteBuf, C2SPacketHandler> handler) {
        client.put(pid, handler);
    }

    /**
     * Register a new server packet.
     *
     * @param pid     the id
     * @param handler the handler
     */
    public void registerServerPacket(int pid, BiConsumer<ByteBuf, S2CPacketHandler> handler) {
        server.put(pid, handler);
    }

    /**
     * Handle a server packet
     * The provided {@code in} buffer should be released by decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context the context (allowed to be null)
     */
    public void handleServerPacket(int pid, ByteBuf in, S2CPacketHandler handler, ChannelHandlerContext context) {
        try {
            server.get(pid).accept(in, handler);
        } catch (Exception exception) {
            if (context != null) context.fireExceptionCaught(exception);
        }
    }

    /**
     * Handle a client packet
     * The provided {@code in} buffer should be released by the decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context the context (allowed to be null)
     */
    public void handleClientPacket(int pid, ByteBuf in, C2SPacketHandler handler, ChannelHandlerContext context) {
        try {
            client.get(pid).accept(in, handler);
        } catch (Exception exception) {
            if (context != null) context.fireExceptionCaught(exception);
        }
    }

    public void dispose() {
        client.clear();
        server.clear();
    }

}
