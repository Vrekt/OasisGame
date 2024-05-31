package me.vrekt.oasis.network.player;

import io.netty.channel.Channel;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.client.C2SKeepAlive;
import me.vrekt.shared.packet.client.C2SPacketJoinWorld;
import me.vrekt.shared.packet.client.abilities.C2SArtifactActivated;
import me.vrekt.shared.packet.client.item.C2SEquipItem;
import me.vrekt.shared.packet.client.item.C2SResetEquippedItem;
import me.vrekt.shared.packet.server.*;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * Represents our local players connection
 */
public final class PlayerConnection extends AbstractConnection {

    private static final C2SKeepAlive KEEP_ALIVE = new C2SKeepAlive();

    private final OasisGame game;
    private final PlayerSP player;

    // technically not MS
    private float pingMs;

    public PlayerConnection(Channel channel, GameProtocol protocol, OasisGame game, PlayerSP player) {
        super(channel, protocol, player);
        this.game = game;
        this.player = player;

        attach(S2CKeepAlive.PACKET_ID, packet -> sendImmediately(KEEP_ALIVE));
        attach(S2CPacketPing.PACKET_ID, packet -> updatePingMs((S2CPacketPing) packet));
        attach(S2CPacketAuthenticate.PACKET_ID, packet -> handleAuthenticationResult((S2CPacketAuthenticate) packet));
        attach(S2CPacketJoinWorld.PACKET_ID, packet -> handleJoinWorld((S2CPacketJoinWorld) packet));
        attach(S2CPacketDisconnected.PACKET_ID, packet -> handleServerDisconnect((S2CPacketDisconnected) packet));
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
    private void updatePingMs(S2CPacketPing packet) {
        pingMs = (GameManager.getTick() - packet.tick()) / 20;
    }

    /**
     * Handle an authentication result
     * Does nothing for now
     *
     * @param packet packet
     */
    private void handleAuthenticationResult(S2CPacketAuthenticate packet) {
        GameLogging.info(this, "Authentication result is %s", packet.isAuthenticationSuccessful());
    }

    /**
     * Handle loading into the game world
     *
     * @param packet packet
     */
    private void handleJoinWorld(S2CPacketJoinWorld packet) {
        GameLogging.info(this, "Loading into network world " + packet.getWorldName());

        player.setEntityId(packet.getEntityId());
        game.loadIntoNetworkWorld(packet.getWorldName());
    }

    /**
     * Handle disconnecting from the server
     *
     * @param packet packet
     */
    private void handleServerDisconnect(S2CPacketDisconnected packet) {
        GameLogging.warn(this, "Disconnected from remote server for %s", packet.getDisconnectReason());
        game.exitNetworkWorld(packet.getDisconnectReason());
    }

    /**
     * Attempt to join a world.
     * unlike {@code joinWorld(world, username, time)} this method substitutes time for {@code System.currentTimeMillis}
     *
     * @param world    the world
     * @param username the username of this player
     */
    public void joinWorld(String world, String username) {
        final C2SPacketJoinWorld packet = new C2SPacketJoinWorld(world, username, 0L);
        sendImmediatelyWithCallback(packet, 5000, true, this::joinWorldTimedOut, callback -> {
            GameManager.game().loadIntoNetworkWorld(world);
        });
    }

    /**
     * Invoked if the join world timed out.
     */
    private void joinWorldTimedOut() {
        GameLogging.error(this, "Join world timed out!");
    }

    /**
     * Update item equip status
     *
     * @param item the item or {@code null} if no item is currently equipped anymore.
     */
    public void updateItemEquipped(Item item) {
        if (item == null) {
            sendImmediately(new C2SResetEquippedItem());
        } else {
            sendImmediately(new C2SEquipItem(player.entityId(), item.key()));
        }
    }

    /**
     * Update artifact activation
     *
     * @param artifact the artifact
     */
    public void updateArtifactActivated(Artifact artifact) {
        sendImmediately(new C2SArtifactActivated(artifact));
    }

}
