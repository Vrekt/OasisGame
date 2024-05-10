package me.vrekt.oasis.network.player;

import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.protocol.packet.server.S2CPacketJoinWorld;
import gdx.lunar.protocol.packet.server.S2CPacketPing;
import io.netty.channel.Channel;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.network.*;

/**
 * Represents our local players connection
 */
public final class PlayerConnection extends PlayerConnectionHandler {

    private final OasisGame game;
    private final OasisPlayer player;

    public PlayerConnection(Channel channel, GdxProtocol protocol, OasisGame game, OasisPlayer player) {
        super(channel, protocol);
        this.game = game;
        this.player = player;
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
            sendImmediately(new C2SEquipItem(player.getEntityId(), item.getKey()));
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

    @Override
    public void handleJoinWorld(S2CPacketJoinWorld packet) {
        GameLogging.info(this, "Attempting to join world %s, our entity ID is %d", packet.getWorldName(), packet.getEntityId());
        player.setEntityId(packet.getEntityId());

        game.loadIntoNetworkWorld(packet.getWorldName());
    }

    @Override
    public void handlePing(S2CPacketPing packet) {
        player.setServerPingTime((System.currentTimeMillis() - packet.getClientTime()));
    }

    private void handleSpawnEntity(ServerPacketSpawnEntity packet) {
        switch (packet.getType()) {
            case INVALID:
                break;
            case TUTORIAL_COMBAT_DUMMY:
                final EntityNPCType type = EntityNPCType.typeOfServer(packet.getType());
                if (type != EntityNPCType.INVALID) {
                    player.getGameWorldIn().spawnEntityTypeAt(type, packet.getPosition());
                }
                break;
        }
    }

    private void handlePlayerEquippedItem(ServerPacketPlayerEquippedItem packet) {
        if (player.getGameWorldIn().hasPlayer(packet.getEntityId())) {
            player.getGameWorldIn()
                    .getPlayer(packet.getEntityId())
                    .setEquippingItem(packet.getItemId());
        } else {
            GameLogging.warn(this, "No player by ID (equip)" + packet.getEntityId());
        }
    }

    private void handleSwingItem(ServerPacketPlayerSwingItem packet) {
        if (player.getGameWorldIn().hasPlayer(packet.getEntityId())) {
            player.getGameWorldIn()
                    .getPlayer(packet.getEntityId())
                    .setSwingingItem(packet.getItemId());
        } else {
            GameLogging.warn(this, "No player by ID (swing)" + packet.getEntityId());
        }
    }

}
