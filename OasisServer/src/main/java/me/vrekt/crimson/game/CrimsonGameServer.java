package me.vrekt.crimson.game;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.crimson.game.entity.ServerPlayerEntity;
import me.vrekt.crimson.game.network.ServerAbstractConnection;
import me.vrekt.crimson.Crimson;
import me.vrekt.crimson.game.world.WorldManager;
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

    private static CrimsonGameServer instance;

    private final List<ServerPlayerEntity> allPlayers = new CopyOnWriteArrayList<>();
    // set of connections that are connected, but not in a world.
    private final List<ServerAbstractConnection> connections = new CopyOnWriteArrayList<>();
    // the last time it took to tick all worlds.
    private long worldTickTime;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ScheduledExecutorService service;

    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    private final GameProtocol protocol;
    private final WorldManager worldManager;
    private long lastKeepAlive;

    public CrimsonGameServer(GameProtocol protocol) {
        instance = this;
        this.service = Executors.newScheduledThreadPool(0, Thread.ofVirtual().factory());
        this.worldManager = new WorldManager();
        this.protocol = protocol;
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
     * @return the world manager
     */
    public WorldManager getWorldManager() {
        return worldManager;
    }

    /**
     * Register player joined
     *
     * @param player the player
     */
    public void registerGlobalPlayer(ServerPlayerEntity player) {
        this.allPlayers.add(player);
    }

    /**
     * Remove the global connection
     *
     * @param connection the connection
     */
    public void removeGlobalConnection(ServerAbstractConnection connection) {
        this.connections.remove(connection);
    }

    /**
     * Register global connection
     *
     * @param connection connection
     */
    public void handleGlobalConnection(ServerAbstractConnection connection) {
        this.connections.add(connection);
    }

    /**
     * Handle a player disconnection.
     *
     * @param player the player
     */
    public void removeGlobalPlayer(ServerPlayerEntity player) {
        this.allPlayers.remove(player);
        this.connections.remove(player.getConnection());
    }

    /**
     * Test if server is at capacity.
     *
     * @return {@code true} if the player can join.
     */
    public boolean isFull() {
        return allPlayers.size() <= 100;
    }

    /**
     * Execute an async task to be run the next server tick
     *
     * @param task the task
     */
    public void executeAsyncTaskOnNextTick(Runnable task) {
        tasks.add(task);
    }

    /**
     * Execute an async task and execute it now.
     *
     * @param task the task
     */
    public void executeAsyncTaskNow(Runnable task) {
        service.schedule(task, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Execute an async task after the provided delay has elapsed.
     *
     * @param task  the task
     * @param delay the delay
     */
    public void executeAsyncTaskLater(Runnable task, long delay) {
        service.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Start this server.
     */
    public void start() {
        worldTickTime = 0;

        service.scheduleAtFixedRate(this::tick, 0L, (1000 / TICKS_PER_SECOND), TimeUnit.MILLISECONDS);
        service.scheduleAtFixedRate(this::ensurePlayerConnections, 1L, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Suspend (pause) this server
     * TODO: ExecutorService thread is still running, perhaps in the future stop that too.
     */
    public void suspend() {
        running.set(false);
    }

    /**
     * Resume this server
     */
    public void resume() {
        running.set(true);
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
                Crimson.warning("WARNING: Running %d ms behind! Skipping %d ticks", worldTickTime, ticksToSkip);
            }

            if (ticksToSkip != 0) {
                while (ticksToSkip > 0) {
                    ticksToSkip--;
                    tickAllWorlds();
                }
                worldTickTime = System.currentTimeMillis();
            }

        } catch (Exception exception) {
            Crimson.error("Exception caught during tick phase");
            exception.printStackTrace();

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
        worldManager.update();
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

    public static CrimsonGameServer getServer() {
        return instance;
    }

    @Override
    public void dispose() {
        running.set(false);
        allPlayers.clear();
        connections.clear();
        tasks.clear();
        worldManager.dispose();
        service.shutdown();
    }

}
