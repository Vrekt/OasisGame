package me.vrekt.oasis.world.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import me.vrekt.crimson.game.entity.ServerEntityPlayer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.network.state.NetworkWorldState;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.S2CStartGame;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.player.*;

/**
 * Handles the barebones of world networking
 */
public final class WorldNetworkHandler {

    private final OasisGame game;
    private final PlayerSP player;

    private boolean receivedBefore;

    public WorldNetworkHandler(OasisGame game) {
        this.game = game;
        this.player = game.getPlayer();
    }

    public void attach() {
        player.getConnection().attach(S2CStartGame.PACKET_ID, packet -> handleStartGame((S2CStartGame) packet));
        player.getConnection().attach(S2CPacketCreatePlayer.PACKET_ID, packet -> handleNetworkCreatePlayer((S2CPacketCreatePlayer) packet));
        player.getConnection().attach(S2CPacketPlayerPosition.PACKET_ID, packet -> handlePlayerPosition((S2CPacketPlayerPosition) packet));
        player.getConnection().attach(S2CPacketPlayerVelocity.PACKET_ID, packet -> handlePlayerVelocity((S2CPacketPlayerVelocity) packet));
        player.getConnection().attach(S2CPacketRemovePlayer.PACKET_ID, packet -> handleRemovePlayer((S2CPacketRemovePlayer) packet));
        player.getConnection().attach(S2CPlayerEnteredInterior.ID, packet -> handlePlayerEnteredInterior((S2CPlayerEnteredInterior) packet));
        player.getConnection().attach(S2CNetworkFrame.ID, packet -> handleNetworkFrame((S2CNetworkFrame) packet));
    }

    /**
     * Build a network state from the active world
     *
     * @return the new state
     */
    public NetworkState build() {
        final GameWorld world = player.getWorldState();

        final NetworkEntityState[] entities = new NetworkEntityState[world.entities().size];
        int e = 0;
        for (GameEntity entity : world.entities().values()) {
            entities[e] = new NetworkEntityState(entity);
            e++;
        }

        final NetworkWorldState state = new NetworkWorldState(world);
        return new NetworkState(state, entities, TimeUtils.nanoTime());
    }

    /**
     * Handle a network frame incoming
     * Update all entities and world state.
     *
     * @param frame frame
     */
    private void handleNetworkFrame(S2CNetworkFrame frame) {
        final NetworkState state = frame.state();
        final float now = Gdx.graphics.getDeltaTime();

        updateNetworkEntityState(state, now);
        receivedBefore = true;
    }

    /**
     * Update all entity states from the network
     *
     * @param state state
     */
    private void updateNetworkEntityState(NetworkState state, float delta) {
        for (int i = 0; i < state.entities().length; i++) {
            final NetworkEntityState entityState = state.entities()[i];
            final GameEntity entity = player.getWorldState().findEntityById(entityState.entityId());
            if (entity != null) {
                if (!receivedBefore) {
                    // teleport instead since this is the first frame.
                    entity.teleport(entityState.x(), entityState.y());
                } else {
                    // otherwise interpolate
                    entity.networkInterpolate(entityState, delta, TimeUtils.nanosToMillis(state.timeToArrive()));
                }
            } else {
                GameLogging.warn(this, "Failed to find network entity state for %d", entityState.entityId());
            }
        }
    }

    /**
     * TODO: Handle all relevant packets we may want to watch
     *
     * @param packet packet
     */
    public void handleRelevantPacket(GamePacket packet) {

    }

    /**
     * Host: handle a players position
     *
     * @param player player
     */
    public void handlePlayerPosition(ServerEntityPlayer player) {
        this.player.getWorldState().getPlayer(player.entityId()).updateNetworkPosition(player.getPosition().x, player.getPosition().y, 1.0f);
    }

    /**
     * Host: handle a players velocity
     *
     * @param player player
     */
    public void handlePlayerVelocity(ServerEntityPlayer player) {
        this.player.getWorldState().getPlayer(player.entityId()).updateNetworkVelocity(player.getVelocity().x, player.getVelocity().y, 1.0f);
    }

    /**
     * Host: handle a player joined this host server
     *
     * @param player the player
     */
    public void handlePlayerJoined(ServerEntityPlayer player) {
        createPlayerInActiveWorld(player.name(), player.entityId(), player.getPosition().x, player.getPosition().y);
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

        GameLogging.info(this, "Spawning new network player with ID %d and username %s at {%f,%f}", entityId, username, x, y);

        final NetworkPlayer networkPlayer = new NetworkPlayer(player.getWorldState());
        networkPlayer.load(game.getAsset());

        networkPlayer.setProperties(username, entityId);
        networkPlayer.createCircleBody(player.getWorldState().boxWorld(), false);
        networkPlayer.setPosition(x, y);

        player.getConnection().addPlayer(entityId, networkPlayer);
        player.getWorldState().spawnPlayerInWorld(networkPlayer);
    }


    /**
     * Handle a player disconnected from this host server
     *
     * @param player player
     */
    public void handlePlayerDisconnected(ServerEntityPlayer player) {
        if (!NetworkValidation.ensureInWorld(this.player)) return;

        this.player.getConnection().removePlayer(player.entityId());
        this.player.getWorldState().removePlayerInWorld(player.entityId(), true);

        GameLogging.info(this, "Player (%d) (%s) left.", player.entityId(), player.name());
    }

    /**
     * Handle batched player packet
     *
     * @param packet packet
     */
    private void handleStartGame(S2CStartGame packet) {
        //TODO: if (!NetworkValidation.ensureWorldContext(player, packet)) return;

        if (packet.hasPlayers()) {
            GameLogging.info(this, "Starting network game with %d players to create", packet.players().length);

            for (S2CNetworkPlayer networkPlayer : packet.players()) {
                if (!NetworkValidation.ensureValidEntityId(player, networkPlayer.entityId)) continue;
                createPlayerInActiveWorld(networkPlayer.username, networkPlayer.entityId, networkPlayer.x, networkPlayer.y);
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
     * Remove a player from the world
     * TODO: WorldState information in packet
     *
     * @param packet packet
     */
    private void handleRemovePlayer(S2CPacketRemovePlayer packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;

        player.getConnection().removePlayer(packet.entityId());
        player.getWorldState().removePlayerInWorld(packet.entityId(), true);

        GameLogging.info(this, "Player (%d) (%s) left.", packet.entityId(), packet.username());
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

        player.getWorldState().updatePlayerVelocityInWorld(packet.entityId(), packet.x(), packet.y(), packet.rotation());
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
