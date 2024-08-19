package me.vrekt.oasis.network.server.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.game.HostNetworkHandler;
import me.vrekt.oasis.network.server.cache.EntityStateCache;
import me.vrekt.oasis.network.server.cache.GameStateSnapshot;
import me.vrekt.oasis.network.server.entity.ServerEntity;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.server.world.obj.ServerContainerWorldObject;
import me.vrekt.oasis.network.server.world.obj.ServerMapItemWorldObject;
import me.vrekt.oasis.network.server.world.obj.ServerWorldObject;
import me.vrekt.oasis.network.utility.GameValidation;
import me.vrekt.oasis.utility.logging.ServerLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.entity.S2CNetworkCreateEntity;
import me.vrekt.shared.packet.server.obj.S2CNetworkAddWorldObject;
import me.vrekt.shared.packet.server.obj.S2CNetworkCreateContainer;
import me.vrekt.shared.packet.server.obj.S2CNetworkSpawnWorldDrop;
import me.vrekt.shared.packet.server.obj.WorldNetworkObject;
import me.vrekt.shared.packet.server.player.*;

/**
 * Represents a basic world within a server.
 */
public final class ServerWorld {

    private final IntegratedGameServer gameServer;

    private final IntMap<ServerEntity> entities = new IntMap<>();
    private final IntMap<ServerPlayer> players = new IntMap<>();

    private final IntMap<ServerWorldObject> objects = new IntMap<>();

    private final int worldId;

    private long mspt;
    private boolean doTicking = true;

    public ServerWorld(GameWorld from, IntegratedGameServer server) {
        this.gameServer = server;
        this.worldId = from.worldId();
    }

    /**
     * Set if this world should be ticked
     *
     * @param doTicking state
     */
    public void setDoTicking(boolean doTicking) {
        this.doTicking = doTicking;
    }

    /**
     * @return {@code true} if this world should be ticked.
     */
    public boolean doTicking() {
        return doTicking;
    }

    private HostNetworkHandler host() {
        return gameServer.handler();
    }

    /**
     * Notify the host of an action to run
     * Will run on the main game thread instead of network thread.
     *
     * @param action the action
     */
    private void notifyHost(Runnable action) {
        host().postNetworkUpdate(action);
    }

    /**
     * @return the ID of this world matching the game client
     */
    public int worldId() {
        return worldId;
    }

    /**
     * @return ms per tick
     */
    public long mspt() {
        return mspt;
    }

    /**
     * Check if the player has timed out, which is defined as 5 seconds of no activity.
     *
     * @param player the player
     * @return {@code true} if this player has timed out.
     */
    public boolean isPlayerTimedOut(ServerPlayer player, long now) {
        return (now - player.getConnection().lastActive()) >= 5000;
    }

    /**
     * Get a player
     *
     * @param entityId ID
     * @return the player or {@code null} if not found.
     */
    public ServerPlayer getPlayer(int entityId) {
        return players.get(entityId);
    }

    /**
     * @param entityId player ID
     * @return {@code true} if this world has the player
     */
    public boolean hasPlayer(int entityId) {
        return players.containsKey(entityId);
    }

    /**
     * Remove a player in this world
     *
     * @param player the player
     */
    public void removePlayer(ServerPlayer player) {
        if (!hasPlayer(player.entityId())) return;
        players.remove(player.entityId());

        broadcastImmediatelyExcluded(player.entityId(), new S2CNetworkRemovePlayer(player.entityId(), player.name()));
    }

    /**
     * Remove a player from this world
     *
     * @param player player
     */
    public void removePlayerFromWorld(ServerPlayer player) {
        if (!hasPlayer(player.entityId())) return;
        players.remove(player.entityId());
    }

    /**
     * Transfer a player into this world.
     *
     * @param player the player
     */
    public void transferPlayerInto(ServerPlayer player) {
        players.put(player.entityId(), player);
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayerInWorld(ServerPlayer player) {
        player.setWorldIn(this);

        ServerLogging.info(this, "Spawning a new player: %s", player.name());

        final Vector2 origin = host().findOriginForPlayer(player);
        player.setPosition(origin);

        notifyHost(() -> host().createConnectedPlayer(player));

        beginNetworkPlayerSync(player);
        this.players.put(player.entityId(), player);
    }

    /**
     * Start syncing entities, players and objects.
     *
     * @param player the player
     */
    public void beginNetworkPlayerSync(ServerPlayer player) {
        // sync server entities
        for (ServerEntity serverEntity : entities.values()) {
            player.getConnection().sendImmediately(new S2CNetworkCreateEntity(serverEntity));
        }

        // sync server objects
        for (ServerWorldObject object : objects.values()) {
            if (object.type() == WorldInteractionType.MAP_ITEM) {
                final ServerMapItemWorldObject obj = (ServerMapItemWorldObject) object;
                player.getConnection().sendImmediately(new S2CNetworkSpawnWorldDrop(obj.item(), obj.amount(), obj.position(), obj.objectId()));
            } else if (object.type() == WorldInteractionType.CONTAINER) {
                final ServerContainerWorldObject obj = (ServerContainerWorldObject) object;
                player.getConnection().sendImmediately(new S2CNetworkCreateContainer(obj.inventory(), obj.textureAsset(), obj.position()));
            } else {
                player.getConnection().sendImmediately(new S2CNetworkAddWorldObject(new WorldNetworkObject(object)));
            }
        }

        // sync players
        final S2CNetworkPlayer hostPlayer = new S2CNetworkPlayer(
                gameServer.hostPlayer().entityId(),
                gameServer.hostPlayer().name(),
                gameServer.hostPlayer().getX(),
                gameServer.hostPlayer().getY());

        player.getConnection().sendImmediately(new S2CNetworkCreatePlayer(hostPlayer.username, hostPlayer.entityId, hostPlayer.x, hostPlayer.y));

        if (!players.isEmpty()) {
            // notify other players this one joined
            broadcastImmediatelyExcluded(player.entityId(), new S2CNetworkCreatePlayer(player.name(), player.entityId(), player.getPosition().x, player.getPosition().y));
            // sync players to the joining player
            for (ServerPlayer serverPlayer : players.values()) {
                player.getConnection().sendImmediately(new S2CNetworkCreatePlayer(
                        serverPlayer.name(),
                        serverPlayer.entityId(),
                        serverPlayer.getPosition().x,
                        serverPlayer.getPosition().y));
            }
        }

    }

    /**
     * Spawn an entity in this world
     *
     * @param entity entity
     */
    public void spawnEntityInWorld(ServerEntity entity) {
        this.entities.put(entity.entityId(), entity);
    }

    /**
     * Add a world object
     *
     * @param worldObject wo
     */
    public void addWorldObject(ServerWorldObject worldObject) {
        this.objects.put(worldObject.objectId(), worldObject);
    }

    /**
     * Get a world object
     *
     * @param objectId ID
     * @return the object or {@code null} if not found.
     */
    public ServerWorldObject getWorldObject(int objectId) {
        return this.objects.get(objectId);
    }

    /**
     * Destroy/remove a world object
     *
     * @param object object
     */
    public void removeWorldObject(ServerWorldObject object) {
        this.objects.remove(object.objectId());
    }

    /**
     * Update this world from local game world state
     * For now only entity data is updated, players handle themselves.
     */
    public void updateFromCapture(GameStateSnapshot capture, PlayerSP hostPlayer) {
        GameValidation.ensureOnThread(IntegratedGameServer.threadId);

        for (IntMap.Entry<EntityStateCache> entry : capture.entities()) {
            final ServerEntity local = this.entities.get(entry.key);
            if (local != null) {
                local.updateFromCapture(entry.value);
            }
        }
        // notify other players of the host position
        broadcastImmediately(new S2CNetworkPlayerPosition(hostPlayer.entityId(), hostPlayer.rotation().ordinal(), hostPlayer.getX(), hostPlayer.getY()));
        broadcastImmediately(new S2CNetworkPlayerVelocity(hostPlayer.entityId(), hostPlayer.rotation().ordinal(), hostPlayer.getVelocity().x, hostPlayer.getVelocity().y));
    }

    /**
     * Tick this world.
     */
    public void tick() {
        GameValidation.ensureOnThread(IntegratedGameServer.threadId);

        // player connections should still be handled
        if (!doTicking) {
            for (ServerPlayer player : players.values()) {
                player.getConnection().flush();
                player.getConnection().updateHandlingQueue();
            }
        } else {
            final long now = System.currentTimeMillis();
            for (ServerPlayer player : players.values()) {
                player.getConnection().flush();
                player.getConnection().updateHandlingQueue();

                // notify everybody of their position.
                if (!isPlayerTimedOut(player, now)) {
                    queuePlayerPosition(player);
                    queuePlayerVelocity(player);

                    // host player send network update
                    // will post to a queue for sync handling on main game thread
                    gameServer.handler().queueHostPlayerNetworkUpdate(
                            player.entityId(),
                            player.getPosition(),
                            player.getVelocity(),
                            player.getRotation());
                } else {
                    player.kick("Timed out");
                }
            }

            mspt = System.currentTimeMillis() - now;
        }
    }

    /**
     * Queue a player position update
     *
     * @param player the player
     */
    private void queuePlayerPosition(ServerPlayer player) {
        broadcastImmediatelyExcluded(player.entityId(), new S2CNetworkPlayerPosition(player.entityId(), player.getRotation(), player.getPosition()));
    }

    /**
     * Queue a player velocity update
     *
     * @param player the player
     */
    private void queuePlayerVelocity(ServerPlayer player) {
        broadcastImmediatelyExcluded(player.entityId(), new S2CNetworkPlayerVelocity(player.entityId(), player.getRotation(), player.getVelocity()));
    }

    /**
     * Broadcast the provided network frame.
     *
     * @param state state
     */
    public void broadcastNetworkState(NetworkState state) {
        final S2CNetworkFrame frame = new S2CNetworkFrame(state);
        broadcastImmediately(frame);
    }

    /**
     * Broadcast a packet.
     * This is queued and not sent instantly.
     *
     * @param packet the packet
     */
    public void broadcastImmediately(GamePacket packet) {
        for (ServerPlayer player : players.values())
            player.getConnection().sendImmediately(packet);
    }

    /**
     * Broadcast a packet (now) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    public void broadcastImmediatelyExcluded(int exclusion, GamePacket packet) {
        for (ServerPlayer player : players.values())
            if (player.entityId() != exclusion)
                player.getConnection().sendImmediately(packet);
    }

    /**
     * Broadcast a packet (queued) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    public void broadcastQueuedExcluded(int exclusion, GamePacket packet) {
        for (ServerPlayer player : players.values())
            if (player.entityId() != exclusion)
                player.getConnection().sendToQueue(packet);
    }

}
