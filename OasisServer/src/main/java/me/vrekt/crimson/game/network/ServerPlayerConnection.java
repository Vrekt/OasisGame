package me.vrekt.crimson.game.network;

import com.badlogic.gdx.Gdx;
import me.vrekt.crimson.Crimson;
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.entity.ServerPlayerEntity;
import io.netty.channel.Channel;
import me.vrekt.crimson.game.entity.adapter.ServerPlayerEntityAdapter;
import me.vrekt.crimson.game.world.World;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.client.*;
import me.vrekt.shared.packet.client.interior.C2SEnterInteriorWorld;
import me.vrekt.shared.packet.client.player.C2SChatMessage;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.packet.server.*;
import me.vrekt.shared.packet.server.player.S2CChatMessage;
import me.vrekt.shared.protocol.ProtocolDefaults;

/**
 * Represents the default player connection handler.
 */
public final class ServerPlayerConnection extends ServerAbstractConnection {

    // timed out after 5 seconds
    private static final float TIMEOUT_SECONDS = 5.0f;

    private ServerPlayerEntity player;
    private boolean disconnected, hasJoined;

    public ServerPlayerConnection(Channel channel, CrimsonGameServer server) {
        super(channel, server);
    }

    /**
     * @return {@code  true} if this {connection} is disconnected
     */
    public boolean isDisconnected() {
        return disconnected;
    }

    /**
     * @return {@code true} if this connection is alive
     */
    public boolean isAlive() {
        if (player.isInWorld()) {
            return !player.world().hasTimeElapsed(lastKeepAlive, TIMEOUT_SECONDS);
        }
        return true;
    }

    @Override
    public void handle(GamePacket packet) {
        switch (packet.getId()) {
            case C2SPacketAuthenticate.PACKET_ID -> handleAuthentication((C2SPacketAuthenticate) packet);
            case C2SPacketPing.PACKET_ID -> handlePing((C2SPacketPing) packet);
            case C2SPacketJoinWorld.PACKET_ID -> handleJoinWorld((C2SPacketJoinWorld) packet);
            case C2SPacketClientLoaded.PACKET_ID -> handleClientLoaded((C2SPacketClientLoaded) packet);
            case C2SPacketDisconnected.PACKET_ID -> handleDisconnected((C2SPacketDisconnected) packet);
            case C2SPacketPlayerPosition.PACKET_ID -> handlePlayerPosition((C2SPacketPlayerPosition) packet);
            case C2SPacketPlayerVelocity.PACKET_ID -> handlePlayerVelocity((C2SPacketPlayerVelocity) packet);
            case C2SKeepAlive.PACKET_ID -> handleKeepAlive((C2SKeepAlive) packet);
            case C2SEnterInteriorWorld.ID -> handleEnterInterior((C2SEnterInteriorWorld) packet);
            case C2SChatMessage.PACKET_ID -> handleChatMessage((C2SChatMessage) packet);
            default -> Crimson.warning("Unhandled packet ID! %d", packet.getId());
        }
    }

    /**
     * Handle authentication to the server
     *
     * @param packet the packet
     */
    public void handleAuthentication(C2SPacketAuthenticate packet) {
        if (server.authenticatePlayer(packet.getGameVersion(), packet.getProtocolVersion())) {
            sendImmediately(new S2CPacketAuthenticate(true, ProtocolDefaults.PROTOCOL_NAME, ProtocolDefaults.PROTOCOL_VERSION));
        } else {
            sendImmediately(new S2CPacketAuthenticate(false, ProtocolDefaults.PROTOCOL_NAME, ProtocolDefaults.PROTOCOL_VERSION));
            channel.close();
        }
    }

    /**
     * Handle a player disconnect
     *
     * @param packet the packet
     */
    public void handleDisconnected(C2SPacketDisconnected packet) {
        Crimson.log("Player %d disconnected because %s", player.entityId(), packet.reason());
        disconnect();
    }

    /**
     * Handle ping time
     *
     * @param packet packet
     */
    public void handlePing(C2SPacketPing packet) {
        sendImmediately(new S2CPacketPing(packet.tick()));
    }

    /**
     * Handle keep alive
     *
     * @param packet packet
     */
    public void handleKeepAlive(C2SKeepAlive packet) {
        lastKeepAlive = player.world().getTick();
    }

    /**
     * Handle a join world request
     *
     * @param packet the packet
     */
    public void handleJoinWorld(C2SPacketJoinWorld packet) {
        if (!server.isUsernameValidInWorld(packet.worldName(), packet.username())) {
            sendImmediately(new S2CPacketWorldInvalid(packet.worldName(), "Invalid username or world."));
            return;
        }

        final World world = server.getWorldManager().getWorld(packet.worldName());
        if (world.isFull()) {
            sendImmediately(new S2CPacketWorldInvalid(packet.worldName(), "World is full."));
            return;
        }

        player = new ServerPlayerEntityAdapter(server, this);
        player.setName(packet.username());
        player.setWorldIn(world);
        player.setEntityId(world.assignId());
        sendImmediately(new S2CPacketJoinWorld(world.getName(), player.entityId(), world.getTime()));

        server.registerGlobalPlayer(player);
        server.handleGlobalConnection(this);

        hasJoined = true;
    }

    /**
     * Handle when the player has loaded their world
     *
     * @param packet packet
     */
    public void handleClientLoaded(C2SPacketClientLoaded packet) {
        if (hasJoined) {
            if(packet.loadedType() == C2SPacketClientLoaded.ClientLoadedType.WORLD) {
                player.setLoaded(true);
                player.world().spawnPlayerInWorld(player);
            } else {
                player.finalizeTransfer();
            }
        }
    }

    /**
     * Update player position in the server
     *
     * @param packet packet
     */
    public void handlePlayerPosition(C2SPacketPlayerPosition packet) {
        if (hasJoined && player.isInWorld()) {
            player.world().handlePlayerPosition(player, packet.getX(), packet.getY(), packet.getRotation());
        }
    }

    /**
     * Update player velocity in the server
     *
     * @param packet packet
     */
    public void handlePlayerVelocity(C2SPacketPlayerVelocity packet) {
        if (hasJoined && player.isInWorld()) {
            player.world().handlePlayerVelocity(player, packet.x(), packet.y(), packet.rotation());
        }
    }

    public void handleEnterInterior(C2SEnterInteriorWorld packet) {
        Crimson.log("Player %d entered interior %s", packet.entityId(), packet.type());

        // TODO: WRONG ENTITY ID FIX
        if (hasJoined && player.isInWorld()) {
            player.prepareTransfer(server.getWorldManager().getInteriorWorld(packet.type()));
        }
    }

    /**
     * Handle chat message
     *
     * @param packet packet
     */
    public void handleChatMessage(C2SChatMessage packet) {
        if (packet.message() == null) {
            Crimson.log("Chat message was null? from=%d", packet.from());
            return;
        }

        if (packet.message().length() > 150) {
            Crimson.log("Chat message packet is too long! l=%d", packet.message().length());
            return;
        }

        if (hasJoined && player.isInWorld()) {
            player.world().broadcastNowWithExclusion(packet.from(), new S2CChatMessage(packet.from(), packet.message()));
        }
    }

    @Override
    public void disconnect() {
        if (disconnected) return;
        this.disconnected = true;

        if (server != null) server.removeGlobalConnection(this);
        if (player != null) {
            if (player.isInWorld()) player.world().removePlayerInWorld(player);
            player.dispose();
        }

        channel.pipeline().remove(this);
        if (channel.isOpen()) channel.close();
    }

    @Override
    public void connectionClosed(Throwable exception) {
        if (exception != null) Gdx.app.log("ServerPlayerConnection", "Connection closed with exception!", exception);
        if (!disconnected) disconnect();
    }

}
