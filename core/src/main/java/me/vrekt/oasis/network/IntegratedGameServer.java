package me.vrekt.oasis.network;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.network.connection.server.PlayerServerConnection;
import me.vrekt.oasis.network.game.world.HostNetworkHandler;
import me.vrekt.oasis.network.netty.IntegratedNettyServer;
import me.vrekt.oasis.network.server.cache.GameStateCache;
import me.vrekt.oasis.network.server.entity.ServerEntity;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.network.server.world.obj.ServerBreakableWorldObject;
import me.vrekt.oasis.network.server.world.obj.ServerWorldObject;
import me.vrekt.oasis.network.utility.NetworkValidation;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.logging.ServerLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.protocol.GameProtocol;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    public static long threadId;

    private final Array<PlayerServerConnection> connections = new Array<>();

    // the current capture to use when ticking the server worlds.
    private final AtomicReference<GameStateCache> capture = new AtomicReference<>();
    private final IntMap<ServerWorld> loadedWorlds = new IntMap<>();
    private ServerWorld activeWorld;

    private boolean started;

    public IntegratedGameServer(OasisGame game, GameProtocol protocol, PlayerSP player, HostNetworkHandler handler) {
        this.game = game;
        this.protocol = protocol;
        this.player = player;
        this.handler = handler;

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
     * Start the integrated server
     * Will tick the server every 25ms
     */
    public void start() {
        netty.bindSync();

        activeWorld = new ServerWorld(player.getWorldState(), this);
        loadedWorlds.put(activeWorld.worldId(), activeWorld);

        capture.set(new GameStateCache());
        populate(player.getWorldState());

        service.scheduleAtFixedRate(this::tick, 1L, 25, TimeUnit.MILLISECONDS);
        started = true;
    }

    /**
     * @return {@code true} if this server is started.
     */
    public boolean started() {
        return started;
    }

    /**
     * Populate the server with all the objects and entities already loaded.
     */
    private void populate(GameWorld world) {
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

        GameLogging.info(this, "Loaded %d entities and %d world objects", entityCount, objectCount);
    }

    /**
     * Stop the integrated server
     */
    public void stop() {
        service.shutdown();
        netty.shutdown();
    }

    /**
     * Tick the server
     */
    private void tick() {
        if (threadId == 0) {
            threadId = Thread.currentThread().threadId();
            Thread.currentThread().setName("ServerTickThread");
        }

        final GameStateCache captureOf = capture.get();
        activeWorld.updateFromCapture(captureOf, player);

        final NetworkState state = handler.latestState();
        activeWorld.broadcastNetworkState(state);

        // update active world
        activeWorld.tick();
    }

    /**
     * Capture the current world state to be broadcast on the main server thread.
     *
     * @param world active world.
     */
    public void captureLocalStateSync(GameWorld world) {
        if (!NetworkValidation.ensureMainThread())
            throw new UnsupportedOperationException("Main server thread please.");

        capture.updateAndGet(c -> c.capture(world));
    }

    /**
     * A player connected
     *
     * @param connection connection
     * @return the players new entity ID
     */
    public int playerConnected(PlayerServerConnection connection) {
        this.connections.add(connection);
        return connections.size + loadedWorlds.size + 1;
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
        ServerLogging.info(this, "Player %s was disconnected because %s", player.name(), reason);

        if (player.isInWorld()) player.world().removePlayerInWorld(player);
        connections.removeValue(player.getConnection(), true);
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
