package me.vrekt.oasis.network.server.world;

import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.server.concurrency.EntityStateCache;
import me.vrekt.oasis.network.server.concurrency.GameStateCache;
import me.vrekt.oasis.network.server.entity.ServerEntity;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.utility.NetworkValidation;
import me.vrekt.oasis.utility.logging.ServerLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.S2CStartGame;
import me.vrekt.shared.packet.server.player.*;

/**
 * Represents a basic world within a server.
 */
public final class ServerWorld {

    private final IntegratedGameServer gameServer;

    private final IntMap<ServerEntity> entities = new IntMap<>();
    private final IntMap<ServerPlayer> players = new IntMap<>();

    private final GameWorld derived;
    private final int worldId;

    private long mspt;

    public ServerWorld(GameWorld from, IntegratedGameServer server) {
        this.gameServer = server;
        this.derived = from;
        this.worldId = derived.worldId();
    }

    /**
     * @return the world this world was derived from.
     */
    public GameWorld derived() {
        return derived;
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
    public void removePlayerInWorld(ServerPlayer player) {
        if (!hasPlayer(player.entityId())) return;
        players.remove(player.entityId());

        gameServer.handler().handlePlayerDisconnected(player.entityId());
        broadcastImmediatelyExcluded(player.entityId(), new S2CPacketRemovePlayer(player.entityId(), player.name()));
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayerInWorld(ServerPlayer player) {
        player.setWorldIn(this);

        ServerLogging.info(this, "Spawning a new player: %s", player.name());

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
            broadcastImmediatelyExcluded(player.entityId(), new S2CPacketCreatePlayer(player.name(), player.entityId(), player.getPosition().x, player.getPosition().y));
            // now we can send the player that joined all other players
            // add the host to this list
            networkPlayers[0] = hostPlayer;

            int index = 1;
            for (ServerPlayer other : players.values()) {
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

    /**
     * Update this world from local game world state
     * For now only entity data is updated, players handle themselves.
     */
    public void updateFromCapture(GameStateCache capture, PlayerSP hostPlayer) {
        NetworkValidation.ensureOnThread(IntegratedGameServer.threadId);

        for (IntMap.Entry<EntityStateCache> entry : capture.entities()) {
            final ServerEntity local = this.entities.get(entry.key);
            if (local != null) {
                local.updateFromCapture(entry.value);
            }
        }

        // notify other players of the host position
        broadcastImmediately(new S2CPacketPlayerPosition(hostPlayer.entityId(), hostPlayer.rotation().ordinal(), hostPlayer.getX(), hostPlayer.getY()));
        broadcastImmediately(new S2CPacketPlayerVelocity(hostPlayer.entityId(), hostPlayer.rotation().ordinal(), hostPlayer.getVelocity().x, hostPlayer.getVelocity().y));
    }

    /**
     * Tick this world.
     */
    public void tick() {
        NetworkValidation.ensureOnThread(IntegratedGameServer.threadId);

        final long now = System.currentTimeMillis();
        for (ServerPlayer player : players.values()) {
            player.getConnection().flush();
            player.getConnection().updateHandlingQueue();

            // notify everybody of their position.
            if (!isPlayerTimedOut(player, now)) {
                queuePlayerPosition(player);
                queuePlayerVelocity(player);

                // host player send network update
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

    /**
     * Queue a player position update
     *
     * @param player the player
     */
    private void queuePlayerPosition(ServerPlayer player) {
        broadcastImmediatelyExcluded(player.entityId(), new S2CPacketPlayerPosition(player.entityId(), player.getRotation(), player.getPosition()));
    }

    /**
     * Queue a player velocity update
     *
     * @param player the player
     */
    private void queuePlayerVelocity(ServerPlayer player) {
        broadcastImmediatelyExcluded(player.entityId(), new S2CPacketPlayerVelocity(player.entityId(), player.getRotation(), player.getVelocity()));
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
