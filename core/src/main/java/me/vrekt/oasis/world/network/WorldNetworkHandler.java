package me.vrekt.oasis.world.network;

import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.player.*;

/**
 * Handles the barebones of world networking
 */
public final class WorldNetworkHandler {

    private final OasisGame game;
    private final PlayerSP player;

    public WorldNetworkHandler(OasisGame game) {
        this.game = game;
        this.player = game.getPlayer();
    }

    public void attach() {
        player.getConnection().attach(S2CPacketPlayers.PACKET_ID, packet -> handleBatchedPlayers((S2CPacketPlayers) packet));
        player.getConnection().attach(S2CPacketCreatePlayer.PACKET_ID, packet -> handleNetworkCreatePlayer((S2CPacketCreatePlayer) packet));
        player.getConnection().attach(S2CPacketPlayerPosition.PACKET_ID, packet -> handlePlayerPosition((S2CPacketPlayerPosition) packet));
        player.getConnection().attach(S2CPacketPlayerVelocity.PACKET_ID, packet -> handlePlayerVelocity((S2CPacketPlayerVelocity) packet));
        player.getConnection().attach(S2CPacketRemovePlayer.PACKET_ID, packet -> handleRemovePlayer((S2CPacketRemovePlayer) packet));
        player.getConnection().attach(S2CPlayerEnteredInterior.ID, packet -> handlePlayerEnteredInterior((S2CPlayerEnteredInterior) packet));
    }

    /**
     * Handle batched player packet
     *
     * @param packet packet
     */
    private void handleBatchedPlayers(S2CPacketPlayers packet) {
        if (!NetworkValidation.ensureWorldContext(player, packet)) return;

        if (packet.hasPlayers()) {
            for (S2CNetworkPlayer networkPlayer : packet.getPlayers()) {
                if (!NetworkValidation.ensureValidEntityId(player, networkPlayer.entityId)) continue;
                createPlayerInActiveWorld(networkPlayer.username, networkPlayer.entityId, networkPlayer.position.x, networkPlayer.position.y);
            }
        }
    }

    /**
     * Handle the {@link S2CPacketCreatePlayer} packet
     * TODO: WorldState information in packet
     *
     * @param packet the packet
     */
    private void handleNetworkCreatePlayer(S2CPacketCreatePlayer packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;
        if (!NetworkValidation.ensureValidEntityId(player, packet.getEntityId())) return;

        createPlayerInActiveWorld(packet.getUsername(), packet.getEntityId(), packet.getX(), packet.getY());
    }

    /**
     * Create a player from the server network
     *
     * @param username their username
     * @param entityId their ID
     * @param x        x
     * @param y        y
     */
    private void createPlayerInActiveWorld(String username, int entityId, float x, float y) {
        if (player.getWorldState().hasPlayer(entityId)) return;

        GameLogging.info(this, "Spawning new network player with ID %d and username %s", entityId, username);

        final NetworkPlayer networkPlayer = new NetworkPlayer(player.getWorldState());
        networkPlayer.load(game.getAsset());

        // Fixes EM-99
        if (x == 0.0f && y == 0.0f) {
            networkPlayer.setPosition(player.getWorldState().worldOrigin().x, player.getWorldState().worldOrigin().y, false);
        } else {
            networkPlayer.setPosition(x, y, false);
        }

        networkPlayer.setProperties(username, entityId);
        networkPlayer.createBoxBody(player.getWorldState().boxWorld());

        player.getConnection().addPlayer(entityId, networkPlayer);
        player.getWorldState().spawnPlayerInWorld(networkPlayer);
    }

    /**
     * Remove a player from the world
     * TODO: WorldState information in packet
     *
     * @param packet packet
     */
    private void handleRemovePlayer(S2CPacketRemovePlayer packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;

        player.getConnection().removePlayer(packet.getEntityId());
        player.getWorldState().removePlayerInWorld(packet.getEntityId(), true);

        GameLogging.info(this, "Player (%d) (%s) left.", packet.getEntityId(), packet.getUsername());
    }

    /**
     * Updates the network players position
     *
     * @param packet packet
     */
    private void handlePlayerPosition(S2CPacketPlayerPosition packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;

        player.getWorldState().updatePlayerPositionInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    /**
     * Updates the network players velocity
     *
     * @param packet packet
     */
    private void handlePlayerVelocity(S2CPacketPlayerVelocity packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;

        player.getWorldState().updatePlayerVelocityInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    /**
     * Handle when a player enters an interior
     *
     * @param packet packet
     */
    private void handlePlayerEnteredInterior(S2CPlayerEnteredInterior packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;
        if (!NetworkValidation.ensureValidEntityId(player, packet.entityId())) return;
        if (packet.type() == InteriorWorldType.NONE) return;

        final NetworkPlayer mp = player.getConnection().getPlayer(packet.entityId());
        if (mp != null) {
            if (player.getWorldState().isPlayerVisible(mp)) {
                mp.transferPlayerToWorldVisible(packet.type());
            } else {
                mp.transferImmediately(packet.type());
            }
        } else {
            GameLogging.warn(this, "Failed to find a player %d", packet.entityId());
        }

        GameLogging.info(this, "Player %d entered interior: %s", packet.entityId(), packet.type());
    }

}
