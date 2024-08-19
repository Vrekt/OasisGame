package me.vrekt.shared.protocol;

import com.badlogic.gdx.utils.Disposable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import me.vrekt.oasis.network.PacketHandler;
import me.vrekt.shared.packet.client.*;
import me.vrekt.shared.packet.client.interior.C2SInteriorLoaded;
import me.vrekt.shared.packet.client.interior.C2STryEnterInteriorWorld;
import me.vrekt.shared.packet.client.player.C2SChatMessage;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.entity.S2CNetworkCreateEntity;
import me.vrekt.shared.packet.server.entity.S2CNetworkEntitySync;
import me.vrekt.shared.packet.server.entity.S2CNetworkRemoveEntity;
import me.vrekt.shared.packet.server.interior.S2CEnterInteriorWorld;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.obj.*;
import me.vrekt.shared.packet.server.player.*;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Game protocol
 * Adapter from LunarGdx
 */
public final class GameProtocol implements Disposable {

    // max frame length allowed of a packet
    // Depending on how complex/and or large packets can get
    public static final int MAX_FRAME_LENGTH = 65536;

    private final BiConsumer<ByteBuf, PacketHandler> def = (buf, packetHandler) -> {
        System.err.println("Unhandled packet.");
    };

    private final int protocolVersion;
    private final String protocolName;

    private final HashMap<Integer, BiConsumer<ByteBuf, PacketHandler>> register = new HashMap<>();

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
        register.put(Packets.S2C_AUTHENTICATE, (buf, handler) -> handler.handle(new S2CAuthenticate(buf)));
        register.put(Packets.S2C_JOIN_WORLD, (buf, handler) -> handler.handle(new S2CJoinWorld(buf)));
        register.put(Packets.S2C_WORLD_INVALID, (buf, handler) -> handler.handle(new S2CWorldInvalid(buf)));
        register.put(Packets.S2C_TRY_ENTER_INTERIOR, (buf, handler) -> handler.handle(new S2CEnterInteriorWorld(buf)));
        register.put(Packets.S2C_PLAYER_ENTERED_INTERIOR, (buf, handler) -> handler.handle(new S2CPlayerEnteredInterior(buf)));
        register.put(Packets.S2C_PLAYER_SYNC, (buf, handler) -> handler.handle(new S2CNetworkPlayerSync(buf)));
        register.put(Packets.S2C_ENTITY_SYNC, (buf, handler) -> handler.handle(new S2CNetworkEntitySync(buf)));
        register.put(Packets.S2C_OBJECT_SYNC, (buf, handler) -> handler.handle(new S2CNetworkWorldObjectSync(buf)));
        register.put(Packets.S2C_CREATE_PLAYER, (buf, handler) -> handler.handle(new S2CNetworkCreatePlayer(buf)));
        register.put(Packets.S2C_CREATE_ENTITY, (buf, handler) -> handler.handle(new S2CNetworkCreateEntity(buf)));
        register.put(Packets.S2C_CREATE_OBJECT, (buf, handler) -> handler.handle(new S2CNetworkAddWorldObject(buf)));
        register.put(Packets.S2C_REMOVE_PLAYER, (buf, handler) -> handler.handle(new S2CNetworkRemovePlayer(buf)));
        register.put(Packets.S2C_REMOVE_ENTITY, (buf, handler) -> handler.handle(new S2CNetworkRemoveEntity(buf)));
        register.put(Packets.S2C_REMOVE_OBJECT, (buf, handler) -> handler.handle(new S2CNetworkRemoveWorldObject(buf)));
        register.put(Packets.S2C_CREATE_CONTAINER, (buf, handler) -> handler.handle(new S2CNetworkCreateContainer(buf)));
        register.put(Packets.S2C_CREATE_WORLD_DROP, (buf, handler) -> handler.handle(new S2CNetworkSpawnWorldDrop(buf)));
        register.put(Packets.S2C_DESTROY_OBJECT_RESPONSE, (buf, handler) -> handler.handle(new S2CDestroyWorldObjectResponse(buf)));
        register.put(Packets.S2C_INTERACT_OBJECT_RESPONSE, (buf, handler) -> handler.handle(new S2CInteractWithObjectResponse(buf)));
        register.put(Packets.S2C_ANIMATE_OBJECT, (buf, handler) -> handler.handle(new S2CAnimateObject(buf)));
        register.put(Packets.S2C_KEEP_ALIVE, (buf, handler) -> handler.handle(new S2CKeepAlive(buf)));
        register.put(Packets.S2C_PLAYER_POSITION, (buf, handler) -> handler.handle(new S2CNetworkPlayerPosition(buf)));
        register.put(Packets.S2C_PLAYER_VELOCITY, (buf, handler) -> handler.handle(new S2CNetworkPlayerVelocity(buf)));
        register.put(Packets.S2C_DISCONNECTED, (buf, handler) -> handler.handle(new S2CDisconnected(buf)));
        register.put(Packets.S2C_CHAT_MESSAGE, (buf, handler) -> handler.handle(new S2CChatMessage(buf)));
        register.put(Packets.S2C_TELEPORT, (buf, handler) -> handler.handle(new S2CTeleport(buf)));
        register.put(Packets.S2C_PLAYER_TELEPORTED, (buf, handler) -> handler.handle(new S2CTeleportPlayer(buf)));
        register.put(Packets.S2C_NETWORK_FRAME, (buf, handler) -> handler.handle(new S2CNetworkFrame(buf)));
        register.put(Packets.S2C_PING, (buf, handler) -> handler.handle(new S2CPing(buf)));
    }

    private void initializeClientHandlers() {
        register.put(Packets.C2S_AUTHENTICATE, (buf, handler) -> handler.handle(new C2SPacketAuthenticate(buf)));
        register.put(Packets.C2S_JOIN_WORLD, (buf, handler) -> handler.handle(new C2SPacketJoinWorld(buf)));
        register.put(Packets.C2S_WORLD_LOADED, (buf, handler) -> handler.handle(new C2SWorldLoaded(buf)));
        register.put(Packets.C2S_INTERIOR_LOADED, (buf, handler) -> handler.handle(new C2SInteriorLoaded(buf)));
        register.put(Packets.C2S_TRY_ENTER_INTERIOR, (buf, handler) -> handler.handle(new C2STryEnterInteriorWorld(buf)));
        register.put(Packets.C2S_KEEP_ALIVE, (buf, handler) -> handler.handle(new C2SKeepAlive(buf)));
        register.put(Packets.C2S_PING, (buf, handler) -> handler.handle(new C2SPacketPing(buf)));
        register.put(Packets.C2S_DISCONNECTED, (buf, handler) -> handler.handle(new C2SPacketDisconnected(buf)));
        register.put(Packets.C2S_CHAT, (buf, handler) -> handler.handle(new C2SChatMessage(buf)));
        register.put(Packets.C2S_POSITION, (buf, handler) -> handler.handle(new C2SPacketPlayerPosition(buf)));
        register.put(Packets.C2S_VELOCITY, (buf, handler) -> handler.handle(new C2SPacketPlayerVelocity(buf)));
        register.put(Packets.C2S_ANIMATE_OBJECT, (buf, handler) -> handler.handle(new C2SAnimateObject(buf)));
        register.put(Packets.C2S_DESTROY_OBJECT, (buf, handler) -> handler.handle(new C2SDestroyWorldObject(buf)));
        register.put(Packets.C2S_INTERACT_WITH_OBJECT, (buf, handler) -> handler.handle(new C2SInteractWithObject(buf)));
    }

    /**
     * Handle a packet.
     *
     * @param pid     the ID
     * @param in      the byte contents
     * @param handler the handler
     * @param context the channel context
     */
    public void handle(int pid, ByteBuf in, PacketHandler handler, ChannelHandlerContext context) {
        try {
            register.get(pid).accept(in, handler);
        } catch (Exception any) {
            context.fireExceptionCaught(any);
        }
    }

    @Override
    public void dispose() {
        register.clear();
    }

}
