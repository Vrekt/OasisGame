package me.vrekt.oasis.network.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import io.netty.util.internal.PlatformDependent;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.network.server.world.obj.ServerWorldObject;
import me.vrekt.oasis.network.utility.GameValidation;
import me.vrekt.oasis.save.world.mp.NetworkPlayerSave;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.items.BreakableObjectInteraction;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.network.state.NetworkWorldState;

import java.util.Queue;

/**
 * The host network handler, handles incoming player updates to this host client
 */
public final class HostNetworkHandler {

    private final Queue<PlayerNetworkUpdate> playerNetworkUpdates = PlatformDependent.newMpscQueue();
    private final Queue<Runnable> networkUpdates = PlatformDependent.newMpscQueue();

    private final PlayerSP player;
    private final OasisGame game;

    public HostNetworkHandler(PlayerSP player, OasisGame game) {
        this.player = player;
        this.game = game;
    }

    /**
     * Build a network state from the provided world.
     * Will capture the most current entity state and other world properties.
     *
     * @param world the world to capture
     */
    public NetworkState captureNetworkState(GameWorld world) {
        GameValidation.ensureMainThreadOrThrow();

        final NetworkEntityState[] entities = new NetworkEntityState[world.entities().size];
        int e = 0;
        for (GameEntity entity : world.entities().values()) {
            entities[e] = new NetworkEntityState(entity);
            e++;
        }

        final NetworkWorldState ws = new NetworkWorldState(world);
        return new NetworkState(ws, entities, TimeUtils.nanoTime());
    }

    /**
     * Update and process all queue entries
     */
    public void update() {
        if (!GameValidation.ensureInWorld(this.player)) return;
        if (!GameValidation.ensureMainThread()) return;

        PlayerNetworkUpdate pn;
        while ((pn = playerNetworkUpdates.poll()) != null) {
            final NetworkPlayer player = this.player.getWorldState().getPlayer(pn.entityId());
            if (player != null) {
                player.updateNetworkPosition(pn.x(), pn.y(), pn.rotation());
                player.updateNetworkVelocity(pn.vx(), pn.vy(), pn.rotation());
            }
        }

        Runnable hn;
        // execute all runnable tasks
        while ((hn = networkUpdates.poll()) != null) {
            hn.run();
        }

    }

    /**
     * Handle a player network update
     * The host will process the entries and update players in their world accordingly.
     *
     * @param entityId entity ID
     * @param position position
     * @param velocity velocity
     */
    public void queueHostPlayerNetworkUpdate(int entityId, Vector2 position, Vector2 velocity, int rotation) {
        playerNetworkUpdates.add(new PlayerNetworkUpdate(entityId, position.x, position.y, velocity.x, velocity.y, rotation));
    }

    /**
     * Post a network update
     * Will typically call a function within this class
     *
     * @param toRun action
     */
    public void postNetworkUpdate(Runnable toRun) {
        networkUpdates.add(toRun);
    }

    /**
     * Find the origin point for the player
     * Ideally, move this to server later.
     *
     * @param player player
     * @return the origin
     */
    public Vector2 findOriginForPlayer(ServerPlayer player) {
        final Vector2 origin = new Vector2();
        final NetworkPlayerSave save = this.player.getWorldState().playerStorage().get(player.name());
        if (save != null) {
            // player has pre-saved position
            origin.set(save.position());
            GameLogging.info(this, "Loaded player %s from network storage at %s", player.name(), save.position());
        } else {
            origin.set(this.player.getPosition()).add(1, 1);
        }
        return origin;
    }

    /**
     * Create a new connected player
     *
     * @param player player
     */
    public void createConnectedPlayer(ServerPlayer player) {
        if (this.player.getWorldState().hasPlayer(player.entityId())) return;
        GameLogging.info(this, "Creating local player %s", player.name());

        final NetworkPlayer networkPlayer = new NetworkPlayer(this.player.getWorldState());
        networkPlayer.load(game.asset());

        networkPlayer.setProperties(player.name(), player.entityId());
        networkPlayer.createCircleBody(this.player.getWorldState().boxWorld(), false);
        networkPlayer.setPosition(player.getPosition());
        player.setLocal(networkPlayer);

        this.player.getWorldState().spawnPlayerInWorld(networkPlayer);
    }

    /**
     * Handle player disconnected
     *
     * @param entityId entity ID of the player
     */
    public void handlePlayerDisconnected(int entityId) {
        if (!GameValidation.ensureInWorld(this.player)) return;

        this.player.getWorldState().removePlayerInWorld(entityId, true);
        GameLogging.info(this, "Player (%d) left.", entityId);
    }

    /**
     * Handle object animation from another player
     *
     * @param from   from
     * @param object object
     */
    public void handleObjectAnimation(ServerPlayer from, ServerWorldObject object) {
        final AbstractInteractableWorldObject wo = this.player.getWorldState().getWorldObjectById(object.objectId());
        if (wo instanceof BreakableObjectInteraction interaction) {
            interaction.animate(true);
        }
    }

    /**
     * Handle object destroyed
     *
     * @param from   from
     * @param object object
     */
    public void handleObjectDestroyed(ServerPlayer from, ServerWorldObject object) {
        this.player.getWorldState().removeObjectById(object.objectId());
    }

    /**
     * Will automatically create a map item and spawn it in the world.
     *
     * @param item     item
     * @param position position
     */
    public void createDroppedItemAndBroadcast(Item item, Vector2 position) {
        player.getWorldState().spawnWorldDrop(item, position);
    }

    /**
     * Validate the interior is valid and load it if required.
     *
     * @param who  the player
     * @param type the type
     */
    public void validateAndLoadPlayerEnteredInterior(ServerPlayer who, Interior type) {
        // TODO: Will not work if in different worlds
        final GameWorld in = who.isInWorld() ? game.worldManager().getWorld(who.world().worldId()) : null;
        if (in == null || in.isInterior()) {
            who.getConnection().handleTryInteriorRequestResult(type, null, false);
        } else {
            final GameWorldInterior interior = player.getWorldState().findInteriorByType(type);
            if (interior == null) {
                who.getConnection().handleTryInteriorRequestResult(type, null, false);
            } else {
                final ServerWorld world = loadNetworkedInterior(interior);
                who.getConnection().handleTryInteriorRequestResult(type, world, true);

                final NetworkPlayer local = who.local();
                if (player.getWorldState().isPlayerVisible(local)) {
                    local.transferPlayerToWorldVisible(type);
                } else {
                    local.transferImmediately(type);
                }
            }
        }
    }

    /**
     * Load networked interior if not already loaded.
     *
     * @param interior the interior
     */
    private ServerWorld loadNetworkedInterior(GameWorldInterior interior) {
        if (!interior.isWorldLoaded()) {
            interior.loadWorldTiledMap(false);
            return game.integratedServer().addAndPrePopulateWorld(interior);
        } else {
            return game.integratedServer().getWorld(interior.worldId());
        }
    }

    /**
     * Handle when a player enters an interior
     * Will transfer them and then also start ticking that interior.
     *
     * @param who     who
     * @param entered interior entered
     */
    public void handlePlayerInteriorLoaded(ServerPlayer who, Interior entered) {
        final GameWorldInterior interior = player.getWorldState().findInteriorByType(entered);
        if (interior != null) {
            if (!interior.doTicking()) {
                interior.enableTicking();
                game.integratedServer().getWorld(interior.worldId()).setDoTicking(true);
            }
        } else {
            // not helpful :)
            who.kick("Internal network interior error");
        }
    }

}
