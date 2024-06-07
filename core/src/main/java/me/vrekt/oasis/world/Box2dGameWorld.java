package me.vrekt.oasis.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.utility.logging.GameLogging;

import java.util.Optional;

/**
 * Box2d base world
 */
public abstract class Box2dGameWorld {

    private static final float MAX_FRAME_TIME = 0.25f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 4;
    private static final float STEP_TIME = 1 / 144.0f;

    protected IntMap<NetworkPlayer> players = new IntMap<>();
    protected IntMap<GameEntity> entities = new IntMap<>();

    protected World world;
    protected Engine engine;

    protected final Vector2 worldOrigin = new Vector2();

    protected float accumulator;

    public Box2dGameWorld(World world, Engine engine) {
        this.world = world;
        this.engine = engine;
    }

    public Engine engine() {
        return engine;
    }

    public World boxWorld() {
        return world;
    }

    /**
     * @return world origin (spawn)
     */
    public Vector2 origin() {
        return worldOrigin;
    }

    /**
     * Check if this world has the player
     *
     * @param entityId player id
     * @return {@code true} if so
     */
    public boolean hasPlayer(int entityId) {
        return players.containsKey(entityId);
    }

    /**
     * Check if this world has the entity
     *
     * @param entityId entity id
     * @return {@code true} if so
     */
    public boolean hasEntity(int entityId) {
        return entities.containsKey(entityId);
    }

    /**
     * Add a network player
     *
     * @param player player
     */
    public void addPlayer(NetworkPlayer player) {
        players.put(player.entityId(), player);
    }

    /**
     * Add a network entity
     *
     * @param entity entity
     */
    public void addEntity(GameEntity entity) {
        entities.put(entity.entityId(), entity);
    }

    /**
     * Get a player by ID
     *
     * @param id their ID
     * @return the player
     */
    public NetworkPlayer getPlayer(int id) {
        return players.get(id);
    }

    public Optional<NetworkPlayer> player(int id) {
        return Optional.ofNullable(getPlayer(id));
    }

    /**
     * Get an entity by ID
     *
     * @param id their ID
     * @return the entity
     */
    public GameEntity getEntity(int id) {
        return entities.get(id);
    }

    /**
     * Spawn a player in this world at the position
     *
     * @param player   player
     * @param position position
     */
    public void spawnPlayerInWorld(NetworkPlayer player, Vector2 position) {
        addPlayer(player);
        player.setPosition(position, true);
    }

    /**
     * Spawn a player in this world at the world origin
     *
     * @param player player
     */
    public void spawnPlayerInWorld(NetworkPlayer player) {
        spawnPlayerInWorld(player, worldOrigin);
    }

    /**
     * Remove a player in this world
     *
     * @param entityId ID
     * @param destroy  if the player should be disposed of
     */
    public void removePlayerInWorld(int entityId, boolean destroy) {
        if (hasPlayer(entityId)) {
            final NetworkPlayer player = players().get(entityId);
            players.remove(entityId);
            player.removeFromWorld();

            if (destroy) player.dispose();
        }
    }

    public IntMap<NetworkPlayer> players() {
        return players;
    }

    public IntMap<GameEntity> entities() {
        return entities;
    }

    /**
     * Update the physics and entity engines
     *
     * @param delta delta
     * @return the capped frame time delta
     */
    public float update(float delta) {
        final float capped = Math.min(delta, MAX_FRAME_TIME);

        stepPhysicsSimulation(capped);
        engine.update(capped);

        for (NetworkPlayer player : players().values()) {
            player.interpolatePosition();
            player.update(delta);
        }

        return capped;
    }

    /**
     * Step box2d world
     *
     * @param delta delta
     */
    public void stepPhysicsSimulation(float delta) {
        accumulator += delta;

        while (accumulator >= STEP_TIME) {
            // update the previous position of these players
            // for interpolation later, if enabled.
            for (NetworkPlayer player : players().values()) {
                player.getPreviousPosition().set(player.getPosition());
            }

            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= STEP_TIME;
        }
    }

    /**
     * Update player position
     *
     * @param entityId id
     * @param x        x
     * @param y        y
     * @param angle    angle/rotation
     */
    public void updatePlayerPositionInWorld(int entityId, float x, float y, float angle) {
        player(entityId).ifPresentOrElse(p -> p.updatePositionFromNetwork(x, y, angle), () -> GameLogging.warn(this, "No player (pos)! %d", entityId));
    }

    /**
     * Update player velocity
     *
     * @param entityId id
     * @param x        x
     * @param y        y
     * @param angle    angle/rotation
     */
    public void updatePlayerVelocityInWorld(int entityId, float x, float y, float angle) {
        player(entityId).ifPresentOrElse(p -> p.updateVelocityFromNetwork(x, y, angle), () -> GameLogging.warn(this, "No player (vel)! %d", entityId));
    }

}
