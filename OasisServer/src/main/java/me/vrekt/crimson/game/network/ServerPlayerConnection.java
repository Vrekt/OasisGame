package me.vrekt.crimson.game.network;

import io.netty.channel.Channel;
import me.vrekt.crimson.Crimson;
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.entity.ServerEntityPlayer;
import me.vrekt.crimson.game.world.World;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.client.*;
import me.vrekt.shared.packet.client.player.C2SChatMessage;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.packet.server.player.*;
import me.vrekt.shared.protocol.ProtocolDefaults;

/**
 * Represents the default player connection handler.
 */
public final class ServerPlayerConnection extends ServerAbstractConnection {

    // timed out after 5 seconds
    private static final float TIMEOUT_SECONDS = 5.0f;

    private ServerEntityPlayer player;
    private boolean disconnected, hasJoined;

    public ServerPlayerConnection(Channel channel, CrimsonGameServer server) {
        super(channel, server);
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

    /**
     * Ensure the player is in a world and has joined.
     *
     * @return {@code true} if so
     */
    private boolean isValid() {
        return hasJoined && player.isInWorld();
    }

    @Override
    public void handle(GamePacket packet) {
        switch (packet.getId()) {
            case C2SPacketAuthenticate.PACKET_ID -> handleAuthentication((C2SPacketAuthenticate) packet);
            case C2SPacketPing.PACKET_ID -> handlePing((C2SPacketPing) packet);
            case C2SPacketJoinWorld.PACKET_ID -> handleJoinWorld((C2SPacketJoinWorld) packet);
            case C2SPacketClientLoaded.PACKET_ID -> handlePlayerClientLoaded((C2SPacketClientLoaded) packet);
            case C2SPacketDisconnected.PACKET_ID -> handleDisconnected((C2SPacketDisconnected) packet);
            case C2SPacketPlayerPosition.PACKET_ID -> handlePlayerPosition((C2SPacketPlayerPosition) packet);
            case C2SPacketPlayerVelocity.PACKET_ID -> handlePlayerVelocity((C2SPacketPlayerVelocity) packet);
            case C2SKeepAlive.PACKET_ID -> handleKeepAlive((C2SKeepAlive) packet);
            case C2SChatMessage.PACKET_ID -> handleChatMessage((C2SChatMessage) packet);
            default -> Crimson.warning("Unhandled packet ID! %d", packet.getId());
        }
    }

    /**
     * Handle authentication to the server
     *
     * @param packet the packet
     */
    private void handleAuthentication(C2SPacketAuthenticate packet) {
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
    private void handleDisconnected(C2SPacketDisconnected packet) {
        server.playerDisconnected(player, packet.reason());
    }

    /**
     * Handle ping time
     *
     * @param packet packet
     */
    private void handlePing(C2SPacketPing packet) {
        sendImmediately(new S2CPacketPing(packet.tick()));
    }

    /**
     * Handle keep alive
     *
     * @param packet packet
     */
    private void handleKeepAlive(C2SKeepAlive packet) {
        lastKeepAlive = player.world().getTick();
    }

    /**
     * Handling joining a world.
     *
     * @param packet the packet
     */
    private void handleJoinWorld(C2SPacketJoinWorld packet) {
        // prevent loaded worlds from being joined or if the player is already joining one.
        if (!server.isWorldLoaded(packet.worldId()) || (player != null && player.loading())) {
            sendImmediately(new S2CPacketWorldInvalid(packet.worldId(), "World not found."));
            return;
        }

        final int worldId = packet.worldId();
        final World world = server.getWorld(worldId);
        player = new ServerEntityPlayer(server, this);
        player.setName(packet.username());
        player.setWorldIn(world);
        player.setEntityId(server.acquireEntityId());
        player.setLoading(true);
        player.setPosition(world.worldOrigin());
        sendImmediately(new S2CPacketJoinWorld(worldId, player.entityId(), world.getTime()));

        server.handlePlayerJoinServer(player);
        hasJoined = true;
    }

    /**
     * Handle when the player has loaded their world
     * TODO: Ensure correct world ID.
     *
     * @param packet packet
     */
    private void handlePlayerClientLoaded(C2SPacketClientLoaded packet) {
        if (hasJoined) {
            player.setLoaded(true);
            player.setLoading(false);
            player.world().spawnPlayerInWorld(player);
        }
    }

    /**
     * Update player position in the server
     *
     * @param packet packet
     */
    public void handlePlayerPosition(C2SPacketPlayerPosition packet) {
        if (isValid()) {
            player.world().handlePlayerPosition(player, packet.x(), packet.y(), packet.rotation());
        }
    }

    /**
     * Update player velocity in the server
     *
     * @param packet packet
     */
    public void handlePlayerVelocity(C2SPacketPlayerVelocity packet) {
        if (isValid()) {
            player.world().handlePlayerVelocity(player, packet.x(), packet.y(), packet.rotation());
        }
    }

    /**
     * Handle chat message
     *
     * @param packet packet
     */
    public void handleChatMessage(C2SChatMessage packet) {
        if (packet.message() == null || packet.message().length() > 150 || !isValid()) {
            Crimson.log("Invalid chat message. msg=%s", packet.message());
            return;
        }

        player.world().broadcastNowWithExclusion(packet.from(), new S2CChatMessage(packet.from(), packet.message()));
    }

    @Override
    public void disconnect() {
        if (disconnected) return;
        this.disconnected = true;

        channel.pipeline().remove(this);
        if (channel.isOpen()) channel.close();
    }

    @Override
    public void connectionClosed(Throwable exception) {
        if (exception != null) Crimson.exception("Connection closed with exception!", exception);
        if (isValid()) {
            if (!disconnected) server.playerDisconnected(player, "Error");
        } else {
            if (!disconnected) disconnect();
        }
    }

}
