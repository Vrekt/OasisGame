package me.vrekt.oasis.network;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.network.connection.server.PlayerServerConnection;
import me.vrekt.oasis.network.game.HostNetworkHandler;
import me.vrekt.oasis.network.netty.IntegratedNettyServer;
import me.vrekt.oasis.network.server.cache.GameStateSnapshot;
import me.vrekt.oasis.network.server.entity.ServerEntity;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.network.server.world.obj.ServerBreakableWorldObject;
import me.vrekt.oasis.network.server.world.obj.ServerWorldObject;
import me.vrekt.oasis.network.utility.GameValidation;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.logging.ServerLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.protocol.GameProtocol;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Integrated co-op multiplayer server
 */
public final class IntegratedGameServer implements Disposable {

    private final OasisGame game;
    private final GameProtocol protocol;
    private final PlayerSP player;
    private final HostNetworkHandler handler;

    private final IntegratedNettyServer netty;

    // for updating all worlds
    private final ScheduledExecutorService service;
    public static long threadId;

    private final Array<ServerPlayer> allPlayers = new Array<>();

    private final Map<Integer, ServerWorld> loadedWorlds = new ConcurrentHashMap<>();
    private final Map<Integer, GameStateSnapshot> snapshots = new ConcurrentHashMap<>();
    private final Map<Integer, NetworkState> states = new ConcurrentHashMap<>();

    private ServerWorld activeWorld;

    private boolean started;

    public IntegratedGameServer(OasisGame game, GameProtocol protocol, PlayerSP player, HostNetworkHandler handler) {
        this.game = game;
        this.protocol = protocol;
        this.player = player;
        this.handler = handler;

        this.service = Executors.newSingleThreadScheduledExecutor();
        this.netty = new IntegratedNettyServer("localhost", 6969, protocol, this);
    }

    /**
     * @return host handler
     */
    public HostNetworkHandler handler() {
        return handler;
    }

    /**
     * @return the hosting player.
     */
    public PlayerSP hostPlayer() {
        return player;
    }

    /**
     * @return {@code true} if this server is started.
     */
    public boolean started() {
        return started;
    }

    /**
     * Stop the integrated server
     */
    public void stop() {
        service.shutdown();
        netty.shutdown();
    }

    /**
     * Start the integrated server
     * Will tick the server every 25ms
     */
    public void start(GameWorld in) {
        netty.bindSync();

        activeWorld = new ServerWorld(in, this);
        loadedWorlds.put(activeWorld.worldId(), activeWorld);

        populateServerWorld(in);

        service.scheduleAtFixedRate(this::tick, 0L, 25, TimeUnit.MILLISECONDS);
        started = true;
    }

    /**
     * Populate the corresponding {@link ServerWorld} with all the objects and entities of the provided {@link GameWorld}
     *
     * @param world the game world
     */
    private void populateServerWorld(GameWorld world) {
        int entityCount = 0;
        for (GameEntity ge : world.entities().values()) {
            final ServerEntity entity = new ServerEntity(this);
            entity.setName(ge.name());
            entity.setEntityId(ge.entityId());
            entity.setPosition(ge.getPosition().x, ge.getPosition().y);
            entity.setWorldIn(activeWorld);

            activeWorld.spawnEntityInWorld(entity);
            entityCount++;
        }

        int objectCount = 0;
        for (AbstractInteractableWorldObject object : world.interactableWorldObjects().values()) {
            ServerWorldObject swo;
            if (object.getType() == WorldInteractionType.BREAKABLE_OBJECT) {
                swo = new ServerBreakableWorldObject(activeWorld, object);
            } else {
                swo = new ServerWorldObject(activeWorld, object);
            }

            activeWorld.addWorldObject(swo);
            objectCount++;
        }

        ServerLogging.info(this, "Loaded %d entities and %d world objects", entityCount, objectCount);
    }

    /**
     * Add a world to the loaded worlds list
     *
     * @param ticking the world
     */
    public void addLoadedWorld(GameWorld ticking) {
        if (!loadedWorlds.containsKey(ticking.worldId())) {
            loadedWorlds.put(ticking.worldId(), new ServerWorld(ticking, this));
        } else {
            GameLogging.warn(this, "Failed to add a ticking world: %s", ticking.getWorldName());
        }
    }

    /**
     * @param from from
     * @return the corresponding server world
     */
    public ServerWorld getLoadedWorld(GameWorld from) {
        return loadedWorlds.get(from.worldId());
    }

    /**
     * Tick the server
     */
    private void tick() {
        assignThread();

        // capture state of all active loaded worlds.
        for (ServerWorld world : loadedWorlds.values()) {
            final GameStateSnapshot snapshot = snapshots.get(world.worldId());
            if (snapshot != null) {
                world.updateFromCapture(snapshot, player);
                snapshot.free();
            }

            final NetworkState state = states.get(world.worldId());
            if (state != null && !state.wasSent()) {
                world.broadcastNetworkState(state);
                state.setWasSent(true);
            }

            world.tick();
        }
    }

    /**
     * Assign name and get ID for the server thread.
     */
    private void assignThread() {
        if (threadId == 0) {
            threadId = Thread.currentThread().threadId();
            Thread.currentThread().setName("ServerTickThread");
        }
    }

    /**
     * Capture a snapshot of the provided world.
     * Will be broadcast at the next interval.
     *
     * @param world the world to capture
     */
    public void captureSnapshotSync(GameWorld world) {
        GameValidation.ensureMainThreadOrThrow();

        final GameStateSnapshot snapshot = GameStateSnapshot.of(world);
        snapshots.put(world.worldId(), snapshot);
    }

    /**
     * Store a network state for a world.
     *
     * @param world the world
     * @param state the state
     */
    public void storeNetworkState(GameWorld world, NetworkState state) {
        final NetworkState old = states.put(world.worldId(), state);

        // ensure an old value was not skipped
        if (old != null && !old.wasSent()) {
            ServerLogging.warn(this, "A network state was skipped.");
        }
    }

    /**
     * A player connected
     *
     * @param player the player
     * @return the players new entity ID
     */
    public int playerConnected(ServerPlayer player) {
        this.allPlayers.add(player);
        return allPlayers.size + loadedWorlds.size() + 1;
    }

    /**
     * Notify the server a player disconnected, either by itself or due to an error.
     * Should only be notified if the player is active in a world.
     * Otherwise, disconnect() in {@link PlayerServerConnection} should be called itself.
     *
     * @param player player
     * @param reason reason
     */
    public void disconnectPlayer(ServerPlayer player, String reason) {
        ServerLogging.info(this, "Player %s disconnected. %s",
                player.name(),
                reason == null ? StringUtils.EMPTY : reason);

        if (player.isInWorld()) player.world().removePlayer(player);
        allPlayers.removeValue(player, true);
        player.getConnection().disconnect();
        player.dispose();
    }

    /**
     * @return mspt of the active world.
     */
    public long mspt() {
        return activeWorld.mspt();
    }

    /**
     * Get a world
     *
     * @param id id
     * @return the world {@code null} if not found
     */
    public ServerWorld getWorld(int id) {
        return loadedWorlds.get(id);
    }

    /**
     * @return the active world.
     */
    public ServerWorld activeWorld() {
        return activeWorld;
    }

    /**
     * Check if the active world is ready.
     *
     * @return {@code true} if so
     */
    public boolean isWorldReady() {
        return activeWorld != null;
    }

    @Override
    public void dispose() {
        stop();
        loadedWorlds.clear();
        activeWorld = null;
    }
}
