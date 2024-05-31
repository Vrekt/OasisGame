package me.vrekt.oasis.network.player;

import io.netty.channel.Channel;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.client.abilities.C2SArtifactActivated;
import me.vrekt.shared.packet.client.item.C2SEquipItem;
import me.vrekt.shared.packet.client.item.C2SResetEquippedItem;
import me.vrekt.shared.packet.server.S2CPacketAuthenticate;
import me.vrekt.shared.packet.server.S2CPacketDisconnected;
import me.vrekt.shared.packet.server.S2CPacketJoinWorld;
import me.vrekt.shared.packet.server.S2CPacketPing;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * Represents our local players connection
 */
public final class PlayerConnection extends AbstractConnection {

    private final OasisGame game;
    private final PlayerSP player;

    private float lastPingSent, pingMs;

    public PlayerConnection(Channel channel, GameProtocol protocol, OasisGame game, PlayerSP player) {
        super(channel, protocol, player);
        this.game = game;
        this.player = player;

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
        pingMs = System.currentTimeMillis() - packet.getClientTime();
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
