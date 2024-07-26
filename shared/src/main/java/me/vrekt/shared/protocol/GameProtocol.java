package me.vrekt.shared.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.vrekt.oasis.network.PacketHandler;
import me.vrekt.shared.packet.client.*;
import me.vrekt.shared.packet.client.interior.C2SEnterInteriorWorld;
import me.vrekt.shared.packet.client.player.C2SChatMessage;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.S2CStartGame;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.obj.*;
import me.vrekt.shared.packet.server.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Game protocol
 * Adapter from LunarGdx
 */
public final class GameProtocol {

    // max frame length allowed of a packet
    // Depending on how complex/and or large packets can get
    public static final int MAX_FRAME_LENGTH = 65536;

    private final int protocolVersion;
    private final String protocolName;

    private final Map<Integer, BiConsumer<ByteBuf, PacketHandler>> server = new HashMap<>();
    private final Map<Integer, BiConsumer<ByteBuf, PacketHandler>> client = new HashMap<>();

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

    public int getMaxPacketFrameLength() {
        return MAX_FRAME_LENGTH;
    }

    /**
     * Initialize default packet handlers
     */
    private void initializeDefaults() {
        initializeClientHandlers();
        initializeServerHandlers();
    }

    private void initializeServerHandlers() {
        server.put(S2CPacketAuthenticate.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketAuthenticate(buf)));
        server.put(S2CPacketCreatePlayer.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketCreatePlayer(buf)));
        server.put(S2CPacketDisconnected.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketDisconnected(buf)));
        server.put(S2CPacketJoinWorld.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketJoinWorld(buf)));
        server.put(S2CPacketPing.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketPing(buf)));
        server.put(S2CPacketPlayerPosition.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketPlayerPosition(buf)));
        server.put(S2CPacketPlayerVelocity.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketPlayerVelocity(buf)));
        server.put(S2CPacketRemovePlayer.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketRemovePlayer(buf)));
        server.put(S2CStartGame.PACKET_ID, (buf, handler) -> handler.handle(new S2CStartGame(buf)));
        server.put(S2CPacketWorldInvalid.PACKET_ID, (buf, handler) -> handler.handle(new S2CPacketWorldInvalid(buf)));
        server.put(S2CKeepAlive.PACKET_ID, (buf, handler) -> handler.handle(new S2CKeepAlive(buf)));
        server.put(S2CPlayerEnteredInterior.ID, (buf, handler) -> handler.handle(new S2CPlayerEnteredInterior(buf)));
        server.put(S2CChatMessage.PACKET_ID, (buf, handler) -> handler.handle(new S2CChatMessage(buf)));
        server.put(S2CNetworkFrame.ID, (buf, handler) -> handler.handle(new S2CNetworkFrame(buf)));
        server.put(S2CTeleport.PACKET_ID, (buf, handler) -> handler.handle(new S2CTeleport(buf)));
        server.put(S2CNetworkAddWorldObject.PACKET_ID, (buf, handler) -> handler.handle(new S2CNetworkAddWorldObject(buf)));
        server.put(S2CNetworkSpawnWorldDrop.PACKET_ID, (buf, handler) -> handler.handle(new S2CNetworkSpawnWorldDrop(buf)));
        server.put(S2CNetworkPopulateContainer.PACKET_ID, (buf, handler) -> handler.handle(new S2CNetworkPopulateContainer(buf)));
        server.put(S2CNetworkRemoveWorldObject.PACKET_ID, (buf, handler) -> handler.handle(new S2CNetworkRemoveWorldObject(buf)));
        server.put(S2CAnimateObject.PACKET_ID, (buf, handler) -> handler.handle(new S2CAnimateObject(buf)));
        server.put(S2CDestroyWorldObjectResponse.PACKET_ID, (buf, handler) -> handler.handle(new S2CDestroyWorldObjectResponse(buf)));
    }

    private void initializeClientHandlers() {
        client.put(C2SPacketAuthenticate.PACKET_ID, (buf, handler) -> handler.handle(new C2SPacketAuthenticate(buf)));
        client.put(C2SPacketDisconnected.PACKET_ID, (buf, handler) -> handler.handle(new C2SPacketDisconnected(buf)));
        client.put(C2SPacketJoinWorld.PACKET_ID, (buf, handler) -> handler.handle(new C2SPacketJoinWorld(buf)));
        client.put(C2SPacketPing.PACKET_ID, (buf, handler) -> handler.handle(new C2SPacketPing(buf)));
        client.put(C2SPacketPlayerPosition.PACKET_ID, (buf, handler) -> handler.handle(new C2SPacketPlayerPosition(buf)));
        client.put(C2SPacketPlayerVelocity.PACKET_ID, (buf, handler) -> handler.handle(new C2SPacketPlayerVelocity(buf)));
        client.put(C2SPacketClientLoaded.PACKET_ID, (buf, handler) -> handler.handle(new C2SPacketClientLoaded(buf)));
        client.put(C2SKeepAlive.PACKET_ID, (buf, handler) -> handler.handle(new C2SKeepAlive(buf)));
        client.put(C2SEnterInteriorWorld.ID, (buf, handler) -> handler.handle(new C2SEnterInteriorWorld(buf)));
        client.put(C2SChatMessage.PACKET_ID, (buf, handler) -> handler.handle(new C2SChatMessage(buf)));
        client.put(C2SInteractWithObject.PACKET_ID, (buf, handler) -> handler.handle(new C2SInteractWithObject(buf)));
        client.put(C2SDestroyWorldObject.PACKET_ID, (buf, handler) -> handler.handle(new C2SDestroyWorldObject(buf)));
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
     * Handle a server packet
     * The provided {@code in} buffer should be released by decoder.
     *
     * @param pid     the packet ID
     * @param in      the ByteBuf in
     * @param handler the handler
     * @param context the context (allowed to be null)
     */
    public void handleServerPacket(int pid, ByteBuf in, PacketHandler handler, ChannelHandlerContext context) {
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
    public void handleClientPacket(int pid, ByteBuf in, PacketHandler handler, ChannelHandlerContext context) {
        try {
            client.get(pid).accept(in, handler);
        } catch (Exception exception) {
            exception.printStackTrace();
            if (context != null) context.fireExceptionCaught(exception);
        }
    }

    public void dispose() {
        client.clear();
        server.clear();
    }

}
