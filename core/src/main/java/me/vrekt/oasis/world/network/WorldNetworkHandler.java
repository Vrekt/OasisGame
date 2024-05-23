package me.vrekt.oasis.world.network;

import gdx.lunar.protocol.packet.server.S2CPacketCreatePlayer;
import gdx.lunar.protocol.packet.server.S2CPacketStartGame;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;

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
        player.getConnection().registerHandlerSync(S2CPacketStartGame.PACKET_ID, packet -> handleNetworkStartGame((S2CPacketStartGame) packet));
    }

    /**
     * The world will handle creating players
     */
    public void registerCreatePlayerHandler() {
        player.getConnection().registerHandlerSync(S2CPacketCreatePlayer.PACKET_ID, packet -> handleNetworkCreatePlayer((S2CPacketCreatePlayer) packet));
    }

    /**
     * Handle the {@link S2CPacketStartGame} packet
     *
     * @param packet the packet
     */
    private void handleNetworkStartGame(S2CPacketStartGame packet) {
        GameLogging.info(this, "Start game packet received, creating " + packet.getPlayers().length + " players.");
        if (packet.hasPlayers()) {
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
        if (packet.getEntityId() == player.getEntityId()) {
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
        if (!player.isInWorld()) return;

        GameLogging.info(this, "Spawning new network player with ID %d and username %s", entityId, username);
        final NetworkPlayer networkPlayer = new NetworkPlayer(true);
        networkPlayer.load(game.getAsset());

        networkPlayer.setProperties(username, entityId);
        networkPlayer.setSize(15, 25, OasisGameSettings.SCALE);
        networkPlayer.spawnPlayerAndSetWorldState(world);
        networkPlayer.setBodyPosition(world.getWorldOrigin(), 1.0f, true);
    }

}
