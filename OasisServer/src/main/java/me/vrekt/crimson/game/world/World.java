package me.vrekt.crimson.game.world;

import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.crimson.Crimson;
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.entity.AbstractServerEntity;
import me.vrekt.crimson.game.entity.ServerEntityPlayer;
import me.vrekt.crimson.game.entity.adapter.ServerEntity;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.S2CStartGame;
import me.vrekt.shared.packet.server.player.*;

/**
 * A world.
 */
public class World implements Disposable {
    protected final CrimsonGameServer gameServer;

    protected IntMap<ServerEntityPlayer> players = new IntMap<>();
    protected IntMap<ServerEntity> entities = new IntMap<>();

    protected final int worldId;

    protected long currentTime;
    protected float currentTick;

    public World(int worldId, CrimsonGameServer gameServer) {
        this.worldId = worldId;
        this.gameServer = gameServer;

        // TODO: Fix this, not ideal but nested() errors are thrown with concurrency
        Collections.allocateIterators = true;
    }

    /**
     * @return the ID of this world
     */
    public int worldId() {
        return worldId;
    }

    /**
     * Check if a player is timed out based on the world configuration
     *
     * @param player the player
     * @return {@code true} if the player is timed out
     */
    public boolean isTimedOut(ServerEntityPlayer player) {
        return !player.getConnection().isAlive();
    }

    /**
     * Timeout the player
     *
     * @param player the player
     */
    public void timeoutPlayer(ServerEntityPlayer player) {
        player.kick("Timed out.");
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
     * @param entityId player ID
     * @return {@code true} if this world has the player
     */
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
    public void handlePlayerPosition(ServerEntityPlayer player, float x, float y, int rotation) {
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
    public void handlePlayerVelocity(ServerEntityPlayer player, float x, float y, int rotation) {
        player.setVelocity(x, y);
        player.setRotation(rotation);
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayerInWorld(ServerEntityPlayer player) {
        player.setWorldIn(this);

        Crimson.log("Spawning a new player %s", player.name());

        final S2CNetworkPlayer hostPlayer = new S2CNetworkPlayer(
                gameServer.hostPlayer().entityId(),
                gameServer.hostPlayer().name(),
                gameServer.hostPlayer().getX(),
                gameServer.hostPlayer().getY());

        // ideally, this will set the right origin point of the player
        gameServer.handler().handlePlayerConnected(player);

        if (players.isEmpty()) {
            // no players besides the host.
            player.getConnection().sendImmediately(new S2CStartGame(worldId, hostPlayer));
        } else {
            final S2CNetworkPlayer[] networkPlayers = new S2CNetworkPlayer[players.size + 1];
            // tell host we have a player
            // notify other players that a new player has joined
            broadcastNowWithExclusion(player.entityId(), new S2CPacketCreatePlayer(player.name(), player.entityId(), player.getPosition().x, player.getPosition().y));
            // now we can send the player that joined all other players
            // add the host to this list
            networkPlayers[0] = hostPlayer;

            int index = 1;
            for (ServerEntityPlayer other : players.values()) {
                networkPlayers[index] = new S2CNetworkPlayer(other.entityId(), other.name(), other.getPosition().x, other.getPosition().y);
                index++;
            }
            player.getConnection().sendImmediately(new S2CStartGame(worldId, networkPlayers));
        }

        // add this new player to the list
        players.put(player.entityId(), player);
    }

    /**
     * Spawn an entity in this world
     *
     * @param entity entity
     */
    public void spawnEntityInWorld(ServerEntity entity) {
        this.entities.put(entity.entityId(), entity);
    }

    public void removePlayerTemporarily(ServerEntityPlayer player) {
        if (!hasPlayer(player.entityId())) return;
        players.remove(player.entityId());
    }

    /**
     * Remove a player in this world
     *
     * @param player the player
     */
    public void removePlayerInWorld(ServerEntityPlayer player) {
        if (!hasPlayer(player.entityId())) return;
        players.remove(player.entityId());

        gameServer.handler().handlePlayerDisconnected(player.entityId());
        broadcastNowWithExclusion(player.entityId(), new S2CPacketRemovePlayer(player.entityId(), player.name()));
    }

    /**
     * Get a player from their entity ID
     *
     * @param entityId the entity ID
     * @return the player or {@code  null} if none exists
     */
    public ServerEntityPlayer getPlayer(int entityId) {
        return players.get(entityId);
    }

    /**
     * Update this world from local game world state
     * For now only entity data is updated, players handle themselves.
     *
     * @param world the world
     */
    public void localUpdate(GameWorld world, PlayerSP hostPlayer) {
        for (GameEntity entity : world.entities().values()) {
            final AbstractServerEntity local = this.entities.get(entity.entityId());
            local.updateLocal(entity);
        }

        // notify other players of the host position
        broadcast(new S2CPacketPlayerPosition(hostPlayer.entityId(), hostPlayer.rotation().ordinal(), hostPlayer.getX(), hostPlayer.getY()));
        broadcast(new S2CPacketPlayerVelocity(hostPlayer.entityId(), hostPlayer.rotation().ordinal(), hostPlayer.getVelocity().x, hostPlayer.getVelocity().y));
    }

    /**
     * Broadcast the active state
     *
     * @param state the state
     */
    public void broadcastState(NetworkState state) {
        final S2CNetworkFrame frame = new S2CNetworkFrame(state);
        broadcast(frame);
    }

    /**
     * Broadcast a packet.
     * This is queued and not sent instantly.
     *
     * @param packet the packet
     */
    public void broadcast(GamePacket packet) {
        for (ServerEntityPlayer player : players.values()) {
            player.getConnection().sendImmediately(packet);
        }
    }

    /**
     * Broadcast a packet (now) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    public void broadcastNowWithExclusion(int exclusion, GamePacket packet) {
        for (ServerEntityPlayer player : players.values())
            if (player.entityId() != exclusion)
                player.getConnection().sendImmediately(packet);
    }

    /**
     * Broadcast a packet (queued) but exclude an entity ID from that broadcast.
     *
     * @param exclusion the entity ID to exclude
     * @param packet    the packet
     */
    public void broadcastWithExclusion(int exclusion, GamePacket packet) {
        for (ServerEntityPlayer value : players.values()) {
            if (value.entityId() != exclusion) {
                value.getConnection().queue(packet);
            }
        }
    }

    /**
     * Tick this world
     */
    public void tick() {
        for (ServerEntityPlayer player : players.values()) {
            player.getConnection().flush();
            currentTime = System.currentTimeMillis();

            // notify everybody of their position.
            if (!isTimedOut(player)) {
                queuePlayerPosition(player);
                queuePlayerVelocity(player);

                // host player send network update
                gameServer.handler().queuePlayerNetworkUpdate(
                        player.entityId(),
                        player.getPosition(),
                        player.getVelocity(),
                        player.getRotation());
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
    protected void queuePlayerPosition(ServerEntityPlayer player) {
        broadcastWithExclusion(player.entityId(), new S2CPacketPlayerPosition(player.entityId(), player.getRotation(), player.getPosition()));
    }

    /**
     * Queue a player velocity update
     *
     * @param player the player
     */
    private void queuePlayerVelocity(ServerEntityPlayer player) {
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
        players.values().forEach(ServerEntityPlayer::dispose);
        players.clear();
        entities.values().forEach(AbstractServerEntity::dispose);
        entities.clear();
    }

}
