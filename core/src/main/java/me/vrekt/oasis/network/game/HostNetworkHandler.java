package me.vrekt.oasis.network.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;
import me.vrekt.oasis.world.obj.interaction.impl.items.BreakableObjectInteraction;
import me.vrekt.oasis.world.obj.interaction.impl.items.MapItemInteraction;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.network.state.NetworkWorldState;
import me.vrekt.shared.packet.server.interior.S2CEnterInteriorWorld;
import me.vrekt.shared.packet.server.obj.S2CNetworkAddWorldObject;
import me.vrekt.shared.packet.server.obj.S2CNetworkPopulateContainer;
import me.vrekt.shared.packet.server.obj.S2CNetworkSpawnWorldDrop;
import me.vrekt.shared.packet.server.obj.WorldNetworkObject;

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
     * Handle a player connected
     *
     * @param player player
     */
    public void handlePlayerConnected(ServerPlayer player) {
        if (this.player.getWorldState().hasPlayer(player.entityId())) return;
        GameLogging.info(this, "Creating local player %s", player.name());

        final Vector2 origin = new Vector2();
        final NetworkPlayerSave save = this.player.getWorldState().playerStorage().get(player.name());
        if (save != null) {
            // player has pre-saved position
            origin.set(save.position());
            GameLogging.info(this, "Loaded player %s from network storage at %s", player.name(), save.position());
        } else {
            origin.set(this.player.getPosition()).add(1, 1);
        }

        player.teleportSilent(origin);

        final NetworkPlayer networkPlayer = new NetworkPlayer(this.player.getWorldState());
        networkPlayer.load(game.asset());

        networkPlayer.setProperties(player.name(), player.entityId());
        networkPlayer.createCircleBody(this.player.getWorldState().boxWorld(), false);
        networkPlayer.setPosition(origin);
        player.setLocal(networkPlayer);

        this.player.getWorldState().spawnPlayerInWorld(networkPlayer);

        // sync world objects to the connecting player
        syncNetworkWorldObjects(player);
    }

    /**
     * Sync active world objects
     *
     * @param player player
     */
    private void syncNetworkWorldObjects(ServerPlayer player) {
        // not all populated entries will be valid, since some objects have their own packet.
        // TODO: In the future, maybe just keep this list around and not have to rebuild it for every player.
        final Array<WorldNetworkObject> objects = new Array<>();

        for (AbstractInteractableWorldObject worldObject : this.player.getWorldState().interactableWorldObjects().values()) {
            if (worldObject.getType() == WorldInteractionType.MAP_ITEM) {
                // spawn world drops right away.
                final MapItemInteraction interaction = (MapItemInteraction) worldObject;
                player.getConnection().sendImmediately(new S2CNetworkSpawnWorldDrop(interaction.item(), interaction.getPosition(), interaction.objectId()));
            } else if (worldObject.getType() == WorldInteractionType.CONTAINER) {
                // spawn containers right away.
                final OpenableContainerInteraction interaction = (OpenableContainerInteraction) worldObject;
                player.getConnection().sendImmediately(new S2CNetworkPopulateContainer(interaction.inventory(), interaction.textureAsset(), interaction.getPosition()));
            } else {
                // otherwise, create and add to the list.
                objects.add(new WorldNetworkObject(worldObject.getType(), worldObject.getKey(), worldObject.getPosition(), worldObject.getSize(), worldObject.objectId(), worldObject.object()));
            }
        }

        for (WorldNetworkObject object : objects)
            player.getConnection().sendImmediately(new S2CNetworkAddWorldObject(object));

        GameLogging.info(this, "Synced %d total world objects to player %s", objects.size, player.name());
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
     * Handle when a player wants to enter an interior
     * Ideally, don't kick the player for bad behaviour, ignore it instead.
     *
     * @param who     who wants to enter
     * @param request the interior to enter
     */
    public void handlePlayerTryEnterInterior(ServerPlayer who, InteriorWorldType request) {
        final GameWorld in = who.isInWorld() ? who.world().derived() : null;
        if (in != null) {
            if (in.isInterior()) {
                who.kick("Interiors cannot be within interiors");
            } else {
                final GameWorldInterior interior = player.getWorldState().findInteriorByType(request);
                if (interior != null) {
                    // notify the server of a new loaded world and start ticking
                    // TODO: Will possibly not work with saving.
                    if (!interior.isWorldLoaded()) {
                        game.runOnMainThread(() -> {
                            interior.loadNetworkWorld();
                            game.integratedServer().addLoadedWorld(interior);
                            interior.enableTicking();
                        });
                    }
                    who.setAllowedToEnter(request);
                    who.getConnection().sendImmediately(new S2CEnterInteriorWorld(request, true));
                } else {
                    who.getConnection().sendImmediately(new S2CEnterInteriorWorld(request, false));
                }
            }
        } else {
            who.kick("No active world.");
        }
    }

    /**
     * Handle when a player enters an interior
     * Will transfer them and then also start ticking that interior.
     *
     * @param who     who
     * @param entered interior entered
     */
    public void handlePlayerEnteredInterior(ServerPlayer who, InteriorWorldType entered) {
        final GameWorldInterior interior = player.getWorldState().findInteriorByType(entered);
        if (interior != null) {
            final NetworkPlayer local = who.local();
            if (local != null) {
                if (player.getWorldState().isPlayerVisible(local)) {
                    local.transferPlayerToWorldVisible(entered);
                } else {
                    local.transfer(interior);
                }

                final ServerWorld world = game.integratedServer().getLoadedWorld(interior);
                who.transfer(world);
            } else {
                who.kick("Failed to find corresponding network player");
            }
        } else {
            // not helpful :)
            who.kick("Internal network interior error");
        }
    }

}
