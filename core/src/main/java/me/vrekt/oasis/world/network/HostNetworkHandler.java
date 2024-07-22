package me.vrekt.oasis.world.network;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.vrekt.crimson.game.entity.ServerEntityPlayer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.world.mp.NetworkPlayerSave;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.network.state.NetworkWorldState;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The host network handler, handles incoming player updates to this host client
 */
public final class HostNetworkHandler {

    private final ConcurrentLinkedQueue<NetworkUpdate> networkUpdateQueue = new ConcurrentLinkedQueue<>();
    private final PlayerSP player;
    private final OasisGame game;

    public HostNetworkHandler(PlayerSP player, OasisGame game) {
        this.player = player;
        this.game = game;
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
     * Update and process all queue entries
     */
    public void update() {
        if (!NetworkValidation.ensureInWorld(this.player)) return;
        if (!NetworkValidation.ensureMainThread()) return;

        for (Iterator<NetworkUpdate> it = networkUpdateQueue.iterator(); it.hasNext(); ) {
            final NetworkUpdate update = it.next();
            if (update != null) {
                final NetworkPlayer player = this.player.getWorldState().getPlayer(update.entityId());
                if (player != null) {
                    player.updateNetworkPosition(update.x(), update.y(), update.rotation());
                    player.updateNetworkVelocity(update.vx(), update.vy(), update.rotation());
                }
                it.remove();
            }
        }
    }

    /**
     * Handle a player network update
     *
     * @param entityId entity ID
     * @param position position
     * @param velocity velocity
     */
    public void queuePlayerNetworkUpdate(int entityId, Vector2 position, Vector2 velocity, int rotation) {
        networkUpdateQueue.add(new NetworkUpdate(entityId, position.x, position.y, velocity.x, velocity.y, rotation));
    }

    /**
     * Handle a player connected
     *
     * @param player player
     */
    public void handlePlayerConnected(ServerEntityPlayer player) {
        if (this.player.getWorldState().hasPlayer(player.entityId())) return;
        GameLogging.info(this, "Creating local player %s", player.name());

        final Vector2 origin = new Vector2();
        final NetworkPlayerSave save = this.player.getWorldState().playerStorage().get(player.name());
        if (save != null) {
            // player has pre-saved position
            origin.set(save.position());
            GameLogging.info(this, "Loaded player %s from network storage at %s", player.name(), save.position());
        } else {
            origin.set(this.player.getPosition().cpy().add(1, 1));
        }

        player.teleport(origin);

        final NetworkPlayer networkPlayer = new NetworkPlayer(this.player.getWorldState());
        networkPlayer.load(game.getAsset());

        networkPlayer.setProperties(player.name(), player.entityId());
        networkPlayer.createCircleBody(this.player.getWorldState().boxWorld(), false);
        networkPlayer.setPosition(origin);

        this.player.getWorldState().spawnPlayerInWorld(networkPlayer);
    }

    /**
     * Handle player disconnected
     *
     * @param entityId entity ID of the player
     */
    public void handlePlayerDisconnected(int entityId) {
        if (!NetworkValidation.ensureInWorld(this.player)) return;

        this.player.getWorldState().removePlayerInWorld(entityId, true);
        GameLogging.info(this, "Player (%d) left.", entityId);
    }

}
