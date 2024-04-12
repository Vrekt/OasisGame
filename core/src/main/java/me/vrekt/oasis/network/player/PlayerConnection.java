package me.vrekt.oasis.network.player;

import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.GdxProtocol;
import io.netty.channel.Channel;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.network.ServerPacketPlayerEquippedItem;
import me.vrekt.shared.network.ServerPacketPlayerSwingItem;
import me.vrekt.shared.network.ServerPacketSpawnEntity;

public class PlayerConnection extends PlayerConnectionHandler {

    private final OasisPlayer player;

    public PlayerConnection(Channel channel, GdxProtocol protocol, OasisPlayer player) {
        super(channel, protocol);
        this.player = player;

      //  registerPacket(ServerPacketSpawnEntity.ID, ServerPacketSpawnEntity::new, this::handleSpawnEntity);
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
