package me.vrekt.crimson.game.world;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.crimson.game.entity.ServerEntity;
import me.vrekt.crimson.game.entity.ServerPlayerEntity;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.player.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A world.
 */
public abstract class World implements Disposable {

    private static final int MAX_CAPACITY = 10;

    // network players and entities
    protected Map<Integer, ServerPlayerEntity> players = new ConcurrentHashMap<>();
    protected Map<Integer, ServerEntity> entities = new ConcurrentHashMap<>();

    // starting/spawn point of this world.
    protected final Vector2 worldOrigin = new Vector2();
    protected final String worldName;

    protected long currentTime;
    protected float currentTick;

    protected long lastKeepAlive;

    public World(String worldName) {
        this.worldName = worldName;
    }

    public String getName() {
        return worldName;
    }

    /**
     * @return {@code true} if this world is full.
     */
    public boolean isFull() {
        return players.size() >= MAX_CAPACITY;
    }

    /**
     * Check if a player is timed out based on the world configuration
     *
     * @param player the player
     * @return {@code true} if the player is timed out
     */
    public boolean isTimedOut(ServerPlayerEntity player) {
        return !player.getConnection().isAlive();
    }

    /**
     * Timeout the player
     *
     * @param player the player
     */
    public void timeoutPlayer(ServerPlayerEntity player) {
        player.kick("Timed out.");

        removePlayerInWorld(player);
        player.server().removeGlobalPlayer(player);
    }

    /**
     * Check if time  has elapsed
     *
     * @param last    last updated
     * @param seconds seconds
     * @return {@code true} if so
     */
    public boolean hasTimeElapsed(float last, float seconds) {
        if (last == 0) return false;
        return currentTick - last >= secondsToTicks(seconds);
    }

    public static float secondsToTicks(float seconds) {
        return seconds * 20; // 20 ticks in a second
    }

    /**
     * Assign an entity ID
     *
     * @return the new entity ID
     */
    public int assignId() {
        return players.size() + 1 + entities.size() + 1 + ThreadLocalRandom.current().nextInt(1, 99);
    }

    public boolean hasPlayer(int entityId) {
        return players.containsKey(entityId);
    }

    public boolean hasEntity(int entityId) {
        return entities.containsKey(entityId);
    }

    /**
     * Handle a player position update
     *
     * @param player   the player
     * @param x        their X
     * @param y        their Y
     * @param rotation their rotation
     */
    public void handlePlayerPosition(ServerPlayerEntity player, float x, float y, float rotation) {
        player.setPosition(x, y);
        player.setRotation(rotation);
    }

    /**
     * Handle a player velocity update
     *
     * @param player   the player
     * @param x        their vel X
     * @param y        their vel Y
     * @param rotation their rotation
     */
    public void handlePlayerVelocity(ServerPlayerEntity player, float x, float y, float rotation) {
        player.setVelocity(x, y);
        player.setRotation(rotation);
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayerInWorld(ServerPlayerEntity player) {
        player.setWorldIn(this);
        if (players.isEmpty()) {
            // no players, send empty start game
            player.getConnection().sendImmediately(new S2CPacketPlayersInWorld());
        } else {
            final S2CNetworkPlayer[] serverPlayers = new S2CNetworkPlayer[players.size()];
            // first, notify other players a new player as joined
            broadcastNowWithExclusion(player.entityId(), new S2CPacketCreatePlayer(player.name(), player.entityId(), 0.0f, 0.0f));

            // next, construct start game packet
            int index = 0;
            for (ServerPlayerEntity other : players.values()) {
                serverPlayers[index] = new S2CNetworkPlayer(other.entityId(), other.name(), other.getPosition());
                index++;
            }

            // send!
            player.getConnection().sendImmediately(new S2CPacketPlayersInWorld(serverPlayers));
        }

        // add this new player to the list
        players.put(player.entityId(), player);
    }

    public void spawnEntityInWorld(ServerEntity entity) {
        this.entities.put(entity.entityId(), entity);
    }

    public void removePlayerTemporarily(ServerPlayerEntity player) {
        if (!hasPlayer(player.entityId())) return;
        players.remove(player.entityId());
    }

    /**
     * Remove a player in this world
     *
     * @param player the player
     */
    public void removePlayerInWorld(ServerPlayerEntity player) {
        if (!hasPlayer(player.entityId())) return;
        players.remove(player.entityId());

        broadcastNowWithExclusion(player.entityId(), new S2CPacketRemovePlayer(player.entityId()));
    }

    /**
     * Get a player from their entity ID
     *
     * @param entityId the entity ID
     * @return the player or {@code  null} if none exists
     */
    public ServerPlayerEntity getPlayer(int entityId) {
        return players.get(entityId);
    }

    /**
     * Broadcast a packet.
     * This is queued and not sent instantly.
     *
     * @param packet the packet
     */
    public void broadcast(GamePacket packet) {
        for (ServerPlayerEntity player : players.values()) player.getConnection().queue(packet);
    }

    /**
     * Broadcast a packet (now) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    public void broadcastNowWithExclusion(int exclusion, GamePacket packet) {
        for (ServerPlayerEntity player : players.values())
            if (player.entityId() != exclusion) player.getConnection().sendImmediately(packet);
    }

    /**
     * Broadcast a packet (queued) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    public void broadcastWithExclusion(int exclusion, GamePacket packet) {
        for (ServerPlayerEntity value : players.values()) {
            if (value.entityId() != exclusion) {
                value.getConnection().queue(packet);
            }
        }
    }

    /**
     * Tick this world
     */
    public void tick() {
        for (ServerPlayerEntity player : players.values()) {

            player.getConnection().flush();
            currentTime = System.currentTimeMillis();

            if (!isTimedOut(player)) {
                queuePlayerPosition(player);
                queuePlayerVelocity(player);
            } else {
                timeoutPlayer(player);
                player.dispose();
            }
        }

        currentTick++;
    }

    /**
     * Queue a player position update
     *
     * @param player the player
     */
    protected void queuePlayerPosition(ServerPlayerEntity player) {
        broadcastWithExclusion(player.entityId(), new S2CPacketPlayerPosition(player.entityId(), player.getRotation(), player.getPosition()));
    }

    /**
     * Queue a player velocity update
     *
     * @param player the player
     */
    private void queuePlayerVelocity(ServerPlayerEntity player) {
        broadcastWithExclusion(player.entityId(), new S2CPacketPlayerVelocity(player.entityId(), player.getRotation(), player.getVelocity()));
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public float getTick() {
        return currentTick;
    }

    @Override
    public void dispose() {
        players.values().forEach(ServerPlayerEntity::dispose);
        players.clear();
        entities.values().forEach(ServerEntity::dispose);
        entities.clear();
    }

}
