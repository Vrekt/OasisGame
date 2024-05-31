package me.vrekt.oasis.world.network;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.shared.packet.server.S2CPacketStartGame;
import me.vrekt.shared.packet.server.player.S2CPacketCreatePlayer;
import me.vrekt.shared.packet.server.player.S2CPacketPlayerPosition;
import me.vrekt.shared.packet.server.player.S2CPacketPlayerVelocity;
import me.vrekt.shared.packet.server.player.S2CPacketRemovePlayer;

/**
 * Handles world networking.
 */
public final class WorldNetworkHandler {

    private final OasisGame game;
    private final GameWorld world;
    private final PlayerSP player;

    public WorldNetworkHandler(OasisGame game, GameWorld world) {
        this.game = game;
        this.world = world;
        this.player = world.getLocalPlayer();
    }

    /**
     * The world allows for a start game packet to be handled.
     */
    public void registerStartGameHandler() {
        player.getConnection().attach(S2CPacketStartGame.PACKET_ID, packet -> handleNetworkStartGame((S2CPacketStartGame) packet));
    }

    /**
     * The world will handle creating players
     */
    public void registerPlayerHandlers() {
        player.getConnection().attach(S2CPacketCreatePlayer.PACKET_ID, packet -> handleNetworkCreatePlayer((S2CPacketCreatePlayer) packet));
        player.getConnection().attach(S2CPacketPlayerPosition.PACKET_ID, packet -> handlePlayerPosition((S2CPacketPlayerPosition) packet));
        player.getConnection().attach(S2CPacketPlayerVelocity.PACKET_ID, packet -> handlePlayerVelocity((S2CPacketPlayerVelocity) packet));
        player.getConnection().attach(S2CPacketRemovePlayer.PACKET_ID, packet -> handleRemovePlayer((S2CPacketRemovePlayer) packet));
    }

    /**
     * Register interior related network packets
     */
    public void registerInteriorHandlers() {

    }

    /**
     * Handle the {@link S2CPacketStartGame} packet
     *
     * @param packet the packet
     */
    private void handleNetworkStartGame(S2CPacketStartGame packet) {
        if (packet.hasPlayers()) {
            GameLogging.info(this, "Start game packet received, creating " + packet.getPlayers().length + " players.");

            // TODO: Position
            for (S2CPacketStartGame.BasicServerPlayer serverPlayer : packet.getPlayers()) {
                createPlayer(serverPlayer.username, serverPlayer.entityId);
            }
        }
    }

    /**
     * Handle the {@link S2CPacketCreatePlayer} packet
     *
     * @param packet the packet
     */
    private void handleNetworkCreatePlayer(S2CPacketCreatePlayer packet) {
        if (packet.getEntityId() == player.entityId()) {
            GameLogging.warn(this, "Server sent the same entity ID as us!");
            return;
        }

        // TODO: Position
        createPlayer(packet.getUsername(), packet.getEntityId());
    }

    /**
     * Create a player from the server network
     *
     * @param username their username
     * @param entityId their ID
     */
    private void createPlayer(String username, int entityId) {
        GameLogging.info(this, "Spawning new network player with ID %d and username %s", entityId, username);
        final NetworkPlayer networkPlayer = new NetworkPlayer();
        networkPlayer.load(game.getAsset());

        networkPlayer.setProperties(username, entityId);
        networkPlayer.createBoxBody(player.getWorldState().boxWorld());

        player.getWorldState().spawnPlayerInWorld(networkPlayer);
    }

    /**
     * Remove a player from the world
     *
     * @param packet packet
     */
    private void handleRemovePlayer(S2CPacketRemovePlayer packet) {
        GameLogging.info(this, "Removing player %d %s", packet.getEntityId(), packet.getUsername());
        world.removePlayerInWorld(packet.getEntityId(), true);
    }

    /**
     * Updates the network players position
     *
     * @param packet packet
     */
    private void handlePlayerPosition(S2CPacketPlayerPosition packet) {
        player.getWorldState().updatePlayerPositionInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    /**
     * Updates the network players velocity
     *
     * @param packet packet
     */
    private void handlePlayerVelocity(S2CPacketPlayerVelocity packet) {
        player.getWorldState().updatePlayerVelocityInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

}
