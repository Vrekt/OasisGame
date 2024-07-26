package me.vrekt.oasis.network.game.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.network.game.NetworkUpdate;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.utility.NetworkValidation;
import me.vrekt.oasis.save.world.mp.NetworkPlayerSave;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;
import me.vrekt.oasis.world.obj.interaction.impl.items.MapItemInteraction;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.network.state.NetworkWorldState;
import me.vrekt.shared.packet.server.obj.S2CNetworkAddWorldObject;
import me.vrekt.shared.packet.server.obj.S2CNetworkPopulateContainer;
import me.vrekt.shared.packet.server.obj.S2CNetworkSpawnWorldDrop;
import me.vrekt.shared.packet.server.obj.WorldNetworkObject;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The host network handler, handles incoming player updates to this host client
 */
public final class HostNetworkHandler {

    private final ConcurrentLinkedQueue<NetworkUpdate> networkUpdateQueue = new ConcurrentLinkedQueue<>();
    private final PlayerSP player;
    private final OasisGame game;

    private final AtomicReference<NetworkState> reference = new AtomicReference<>();

    public HostNetworkHandler(PlayerSP player, OasisGame game) {
        this.player = player;
        this.game = game;
    }

    /**
     * @return latest network state
     */
    public NetworkState latestState() {
        return reference.get();
    }

    /**
     * Build a network state from the active world
     */
    public void build() {
        if (!NetworkValidation.ensureMainThread()) return;

        final GameWorld world = player.getWorldState();

        final NetworkEntityState[] entities = new NetworkEntityState[world.entities().size];
        int e = 0;
        for (GameEntity entity : world.entities().values()) {
            entities[e] = new NetworkEntityState(entity);
            e++;
        }

        final NetworkWorldState ws = new NetworkWorldState(world);
        final NetworkState state = new NetworkState(ws, entities, TimeUtils.nanoTime());
        reference.set(state);
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
     * The host will process the entries and update players in their world accordingly.
     *
     * @param entityId entity ID
     * @param position position
     * @param velocity velocity
     */
    public void queueHostPlayerNetworkUpdate(int entityId, Vector2 position, Vector2 velocity, int rotation) {
        networkUpdateQueue.add(new NetworkUpdate(entityId, position.x, position.y, velocity.x, velocity.y, rotation));
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
            origin.set(this.player.getPosition().cpy().add(1, 1));
        }

        player.teleport(origin);

        final NetworkPlayer networkPlayer = new NetworkPlayer(this.player.getWorldState());
        networkPlayer.load(game.getAsset());

        networkPlayer.setProperties(player.name(), player.entityId());
        networkPlayer.createCircleBody(this.player.getWorldState().boxWorld(), false);
        networkPlayer.setPosition(origin);

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
        final WorldNetworkObject[] objects = new WorldNetworkObject[this.player.getWorldState().interactableWorldObjects().size];
        int o = 0;

        for (AbstractInteractableWorldObject worldObject : this.player.getWorldState().interactableWorldObjects().values()) {
            if (worldObject.getType() == WorldInteractionType.MAP_ITEM) {
                // spawn world drops right away.
                final MapItemInteraction interaction = (MapItemInteraction) worldObject;
                player.getConnection().sendImmediately(new S2CNetworkSpawnWorldDrop(interaction.item(), interaction.getPosition()));
            } else if (worldObject.getType() == WorldInteractionType.CONTAINER) {
                // spawn containers right away.
                final OpenableContainerInteraction interaction = (OpenableContainerInteraction) worldObject;
                player.getConnection().sendImmediately(new S2CNetworkPopulateContainer(interaction.inventory(), interaction.textureAsset(), interaction.getPosition()));
            } else {
                // otherwise, create and add to the list.
                objects[o] = new WorldNetworkObject(worldObject.getType(), worldObject.getKey(), worldObject.getPosition(), worldObject.getSize(), worldObject.objectId(), worldObject.object());
                o++;
            }
        }

        for (WorldNetworkObject object : objects) {
            // some objects will be null.
            if (object != null) {
                player.getConnection().sendImmediately(new S2CNetworkAddWorldObject(object));
            }
        }

        GameLogging.info(this, "Synced %d total world objects to player %s", objects.length, player.name());
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
