package me.vrekt.oasis.world.network;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.mp.AbstractNetworkPlayer;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.player.*;

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
        this.player = world.player();
    }

    /**
     * The world allows for a start game packet to be handled.
     */
    public void registerStartGameHandler() {
        player.getConnection().attach(S2CPacketPlayersInWorld.PACKET_ID, packet -> handleNetworkStartGame((S2CPacketPlayersInWorld) packet));
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
        player.getConnection().attach(S2CPlayerEnteredInterior.ID, packet -> handlePlayerEnteredInterior((S2CPlayerEnteredInterior) packet));
    }

    /**
     * Handle the {@link S2CPacketPlayersInWorld} packet
     *
     * @param packet the packet
     */
    private void handleNetworkStartGame(S2CPacketPlayersInWorld packet) {
        if (!NetworkUtility.ensureWorldState(player, packet)) return;

        if (packet.hasPlayers()) {
            GameLogging.info(this, "Start game packet received, creating " + packet.getPlayers().length + " players.");

            for (S2CNetworkPlayer serverPlayer : packet.getPlayers()) {
                createPlayer(serverPlayer.username, serverPlayer.entityId, serverPlayer.position.x, serverPlayer.position.y);
            }
        }
    }

    /**
     * Handle the {@link S2CPacketCreatePlayer} packet
     *
     * @param packet the packet
     */
    private void handleNetworkCreatePlayer(S2CPacketCreatePlayer packet) {
        if (!NetworkUtility.ensureValidEntityId(player, packet.getEntityId())) return;

        createPlayer(packet.getUsername(), packet.getEntityId(), packet.getX(), packet.getY());
    }

    /**
     * Create a player from the server network
     *
     * @param username their username
     * @param entityId their ID
     */
    private void createPlayer(String username, int entityId, float x, float y) {
        GameLogging.info(this, "Spawning new network player with ID %d and username %s", entityId, username);

        final NetworkPlayer networkPlayer = new NetworkPlayer(player.getWorldState());
        networkPlayer.load(game.getAsset());

        networkPlayer.setProperties(username, entityId);
        networkPlayer.createBoxBody(player.getWorldState().boxWorld());
        networkPlayer.setPosition(x, y, true);

        player.getWorldState().spawnPlayerInWorld(networkPlayer);
    }

    /**
     * Remove a player from the world
     *
     * @param packet packet
     */
    private void handleRemovePlayer(S2CPacketRemovePlayer packet) {
        if (!NetworkUtility.ensureWorldState(player, packet)) return;

        GameLogging.info(this, "Removing player %d %s", packet.getEntityId(), packet.getUsername());
        world.removePlayerInWorld(packet.getEntityId(), true);
    }

    /**
     * Updates the network players position
     *
     * @param packet packet
     */
    private void handlePlayerPosition(S2CPacketPlayerPosition packet) {
        if (!NetworkUtility.ensureWorldState(player, packet)) return;

        player.getWorldState().updatePlayerPositionInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    /**
     * Updates the network players velocity
     *
     * @param packet packet
     */
    private void handlePlayerVelocity(S2CPacketPlayerVelocity packet) {
        if (!NetworkUtility.ensureWorldState(player, packet)) return;

        player.getWorldState().updatePlayerVelocityInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    /**
     * Handle when a player enters an interior
     *
     * @param packet packet
     */
    private void handlePlayerEnteredInterior(S2CPlayerEnteredInterior packet) {
        if (!NetworkUtility.ensureWorldState(player, packet)) return;
        GameLogging.info(this, "Player %d entered an interior.", packet.entityId());

        world.player(packet.entityId()).ifPresentOrElse(
                AbstractNetworkPlayer::transferIntoInterior,
                () -> GameLogging.info(this, "No player with id=%d in world!", packet.entityId())
        );
    }

}
