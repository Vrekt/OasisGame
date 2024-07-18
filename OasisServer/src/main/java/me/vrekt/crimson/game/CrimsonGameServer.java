package me.vrekt.crimson.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.crimson.Crimson;
import me.vrekt.crimson.game.entity.ServerEntityPlayer;
import me.vrekt.crimson.game.entity.adapter.ServerEntity;
import me.vrekt.crimson.game.network.ServerAbstractConnection;
import me.vrekt.crimson.game.world.World;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.network.WorldNetworkHandler;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.packet.server.S2CPacketDisconnected;
import me.vrekt.shared.protocol.GameProtocol;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The main game server
 */
public final class CrimsonGameServer implements Disposable {

    private static final int TICKS_PER_SECOND = 20;

    private final WorldNetworkHandler handler;
    private PlayerSP hostPlayer;

    private final List<ServerEntityPlayer> allPlayers = new CopyOnWriteArrayList<>();
    // set of connections that are connected, but not in a world.
    private final List<ServerAbstractConnection> connections = new CopyOnWriteArrayList<>();
    // the last time it took to tick all worlds.
    private long worldTickTime;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ScheduledExecutorService service;

    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    // all loaded worlds in this server
    // TODO: Interior worlds
    private final IntMap<World> loadedWorlds = new IntMap<>();
    private final GameProtocol protocol;

    public CrimsonGameServer(GameProtocol protocol, WorldNetworkHandler handler) {
        this.service = Executors.newScheduledThreadPool(0, Thread.ofVirtual().factory());
        this.protocol = protocol;
        this.handler = handler;
    }

    public GameProtocol getProtocol() {
        return protocol;
    }

    /**
     * Attempt to authenticate a new player into the server
     *
     * @param version         the players game version
     * @param protocolVersion the player protocol version
     * @return {@code true} if the player is allowed to join, other-wise connection will be closed.
     */
    public boolean authenticatePlayer(String version, int protocolVersion) {
        return true;
    }

    /**
     * Check if a username is valid.
     *
     * @param username the username
     * @param world    the world
     * @return {@code true} if so
     */
    public boolean isUsernameValidInWorld(String world, String username) {
        return true;
    }

    /**
     * Handle when a player joins the server
     *
     * @param player the player
     */
    public void handlePlayerJoinServer(ServerEntityPlayer player) {
        allPlayers.add(player);
        connections.add(player.getConnection());
    }

    /**
     * Disconnect a player
     */
    public void disconnectPlayer(ServerEntityPlayer player, String reason) {
        player.getConnection().sendImmediately(new S2CPacketDisconnected(reason));
        if (player.isInWorld()) player.world().removePlayerInWorld(player);
        player.getConnection().disconnect();
        player.dispose();
    }

    /**
     * Player was disconnected
     *
     * @param player the player
     */
    public void playerDisconnected(ServerEntityPlayer player, String reason) {
        if (player.isInWorld()) player.world().removePlayerInWorld(player);
        player.getConnection().disconnect();

        Crimson.log("Player %s was disconnected because {%s}", player.name(), reason);
    }

    /**
     * Check if a world is loaded.
     *
     * @param worldId the ID
     * @return {@code true} if so
     */
    public boolean isWorldLoaded(int worldId) {
        return loadedWorlds.containsKey(worldId);
    }

    /**
     * Get a loaded world
     *
     * @param worldId the ID
     * @return the world
     */
    public World getWorld(int worldId) {
        return loadedWorlds.get(worldId);
    }

    /**
     * Assign a random entity ID
     *
     * @return the new ID
     */
    public int acquireEntityId() {
        return allPlayers.size() + MathUtils.random(1, 99);
    }

    /**
     * Start this server.
     */
    public void start() {
        worldTickTime = 0;

        // 20 world ticks per second
        service.scheduleAtFixedRate(this::tick, 0L, (1000 / TICKS_PER_SECOND), TimeUnit.MILLISECONDS);
        // keep alive players every 1s
        service.scheduleAtFixedRate(this::ensurePlayerConnections, 1L, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Register a loaded world
     *
     * @param world the world
     */
    public void registerLoadedWorld(GameWorld world) {
        loadedWorlds.put(world.worldId(), new World(world.worldId(), this));
    }

    /**
     * Set the initial network state of the server
     *
     * @param state state
     */
    public void setInitialNetworkState(NetworkState state, GameWorld loadedWorld) {
        final World world = loadedWorlds.get(loadedWorld.worldId());

        for (int i = 0; i < state.entities().length; i++) {
            final NetworkEntityState es = state.entities()[i];
            final ServerEntity entity = new ServerEntity(this);
            entity.setName(es.name());
            entity.setEntityId(es.entityId());
            entity.setPosition(es.x(), es.y());
            entity.setWorldIn(world);

            world.spawnEntityInWorld(entity);
        }

        Crimson.log("Loaded a total of %d local entities", state.entities().length);
    }

    /**
     * Add the local host player to this server
     * Should only be invoked once.
     *
     * @param player player
     */
    public void setLocalHostPlayer(PlayerSP player) {
        this.hostPlayer = player;
    }

    /**
     * @return the host player
     */
    public PlayerSP hostPlayer() {
        return hostPlayer;
    }

    /**
     * @return world network handler
     */
    public WorldNetworkHandler handler() {
        return handler;
    }

    /**
     * Tick this server.
     */
    public void tick() {
        if (!running.get()) {
            return;
        }

        try {
            tickAllWorlds();
            runAllTasks();

            // cap max ticks to skip to 50.
            final long time = worldTickTime / 50;
            // amount of ticks to skip if falling behind.
            long ticksToSkip = time >= 50 ? 50 : time;

            if (ticksToSkip > 1) {
                Crimson.warning("Running %d ms behind! Skipping %d ticks", worldTickTime, ticksToSkip);
            }

            if (ticksToSkip != 0) {
                while (ticksToSkip > 0) {
                    ticksToSkip--;
                    tickAllWorlds();
                }
                worldTickTime = System.currentTimeMillis();
            }

        } catch (Exception any) {
            Crimson.exception("Exception caught during tick phase", any);
            running.compareAndSet(true, false);
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {
        running.compareAndSet(true, false);

        service.shutdownNow();
        this.dispose();
    }

    /**
     * Update all worlds within the server.
     */
    private void tickAllWorlds() {
        final long now = System.currentTimeMillis();

        for (World world : loadedWorlds.values()) {
            world.tick();
        }

        worldTickTime = System.currentTimeMillis() - now;
    }

    /**
     * Run all tasks
     */
    private void runAllTasks() {
        while (tasks.peek() != null) {
            tasks.remove().run();
        }
    }

    /**
     * Send keep alive and check timed out status.
     */
    private void ensurePlayerConnections() {
        if (!running.get()) return;

        for (ServerAbstractConnection connection : connections) {
            if (connection.isAlive()) connection.keepAlive();
        }
    }

    @Override
    public void dispose() {
        running.set(false);
        allPlayers.clear();
        connections.clear();
        tasks.clear();
        service.shutdown();
    }

}
