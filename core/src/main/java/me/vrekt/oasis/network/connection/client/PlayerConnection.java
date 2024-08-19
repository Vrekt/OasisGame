package me.vrekt.oasis.network.connection.client;

import com.badlogic.gdx.utils.IntMap;
import io.netty.channel.Channel;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.client.C2SKeepAlive;
import me.vrekt.shared.packet.client.C2SPacketJoinWorld;
import me.vrekt.shared.packet.client.player.C2SChatMessage;
import me.vrekt.shared.packet.server.player.S2CAuthenticate;
import me.vrekt.shared.packet.server.player.S2CDisconnected;
import me.vrekt.shared.packet.server.player.S2CJoinWorld;
import me.vrekt.shared.packet.server.player.S2CPing;
import me.vrekt.shared.protocol.GameProtocol;
import me.vrekt.shared.protocol.Packets;

/**
 * Represents our local players connection
 * <p>
 * Will handle basic tasks.
 */
public final class PlayerConnection extends AbstractPlayerConnection {

    private static final C2SKeepAlive KEEP_ALIVE = new C2SKeepAlive();

    private final OasisGame game;
    private final PlayerSP player;

    // technically not MS
    private float pingMs;

    // map of all global players
    private final IntMap<NetworkPlayer> allPlayers = new IntMap<>();

    public PlayerConnection(Channel channel, GameProtocol protocol, OasisGame game, PlayerSP player) {
        super(channel, protocol, player);
        this.game = game;
        this.player = player;

        attach(Packets.S2C_KEEP_ALIVE, packet -> sendImmediately(KEEP_ALIVE));
        attach(Packets.S2C_PING, packet -> updatePingMs((S2CPing) packet));
        attach(Packets.S2C_AUTHENTICATE, packet -> handleAuthenticationResult((S2CAuthenticate) packet));
        attach(Packets.S2C_DISCONNECTED, packet -> handleServerDisconnect((S2CDisconnected) packet));
    }

    /**
     * Add a network player to the global list.
     *
     * @param player player
     */
    public void registerGlobalNetworkPlayer(NetworkPlayer player) {
        allPlayers.put(player.entityId(), player);
    }

    /**
     * Remove a network player from the global list
     *
     * @param entityId entity ID
     */
    public void removeGlobalNetworkPlayer(int entityId) {
        allPlayers.remove(entityId);
    }

    /**
     * Get a network player.
     *
     * @param entityId ID
     * @return the player or {@code null} if not found.
     */
    public NetworkPlayer getGlobalPlayer(int entityId) {
        return allPlayers.get(entityId);
    }

    /**
     * Send a chat message.
     *
     * @param text text
     */
    public void sendChatMessage(String text) {
        if (text == null || text.isEmpty() || text.length() >= 150) return;

        sendImmediately(new C2SChatMessage(text));
    }

    /**
     * @return ping time
     */
    public float getPingMs() {
        return pingMs;
    }

    /**
     * Update ping ms
     *
     * @param packet packet
     */
    private void updatePingMs(S2CPing packet) {
        pingMs = (GameManager.tick() - packet.tick()) / 20;
    }

    /**
     * Handle an authentication result
     * Does nothing for now
     *
     * @param packet packet
     */
    private void handleAuthenticationResult(S2CAuthenticate packet) {
        GameLogging.info(this, "Authentication result is %s", packet.isAuthenticationSuccessful());
    }

    /**
     * Handle disconnecting from the server
     *
     * @param packet packet
     */
    private void handleServerDisconnect(S2CDisconnected packet) {
        GameLogging.warn(this, "Disconnected from remote server for %s", packet.getDisconnectReason());
        game.exitNetworkWorld(packet.getDisconnectReason());
    }

    /**
     * Send a join world request to the server and wait for a response
     * This function will time out after 5 seconds if no response has been received.
     *
     * @param worldId  world ID
     * @param username the username of this player
     */
    public void joinWorld(int worldId, String username) {
        final C2SPacketJoinWorld packet = new C2SPacketJoinWorld(worldId, username, 0L);
        NetworkCallback.immediate(packet)
                .waitFor(Packets.S2C_JOIN_WORLD)
                .timeoutAfter(5000)
                .ifTimedOut(this::joinWorldTimedOut)
                .sync()
                .accept(callback -> handleJoinWorld((S2CJoinWorld) callback))
                .send();
    }

    /**
     * Handle loading into the game world
     *
     * @param packet packet
     */
    private void handleJoinWorld(S2CJoinWorld packet) {
        GameLogging.info(this, "Loading into network world %s with ID %d", packet.worldId(), packet.entityId());

        player.setEntityId(packet.entityId());
        game.loadIntoNetworkWorld(packet.worldId());
    }

    /**
     * Invoked if the join world timed out.
     */
    private void joinWorldTimedOut() {
        GameLogging.error(this, "Join world timed out!");
    }

}
