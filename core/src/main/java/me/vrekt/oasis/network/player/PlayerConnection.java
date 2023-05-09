package me.vrekt.oasis.network.player;

import gdx.lunar.network.types.PlayerConnectionHandler;
import gdx.lunar.protocol.LunarProtocol;
import io.netty.channel.Channel;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.shared.network.ServerPlayerEquippedItem;
import me.vrekt.shared.network.ServerPlayerSwungItem;
import me.vrekt.shared.network.ServerSpawnEntity;

public class PlayerConnection extends PlayerConnectionHandler {

    private final OasisPlayerSP player;

    public PlayerConnection(Channel channel, LunarProtocol protocol, OasisPlayerSP player) {
        super(channel, protocol);
        this.player = player;

        registerPacket(ServerSpawnEntity.ID, ServerSpawnEntity::new, this::handleSpawnEntity);
        registerPacket(ServerPlayerEquippedItem.ID, ServerPlayerEquippedItem::new, this::handlePlayerEquippedItem);
        registerPacket(ServerPlayerSwungItem.ID, ServerPlayerSwungItem::new, this::handleSwingItem);
    }

    private void handleSpawnEntity(ServerSpawnEntity packet) {
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

    private void handlePlayerEquippedItem(ServerPlayerEquippedItem packet) {
        if (player.getGameWorldIn().hasNetworkPlayer(packet.getEntityId())) {
            player.getGameWorldIn()
                    .getNetworkPlayer(packet.getEntityId())
                    .setEquippingItem(packet.getItemId());
        } else {
            Logging.warn(this, "No player by ID (equip)" + packet.getEntityId());
        }
    }

    private void handleSwingItem(ServerPlayerSwungItem packet) {
        if (player.getGameWorldIn().hasNetworkPlayer(packet.getEntityId())) {
            player.getGameWorldIn()
                    .getNetworkPlayer(packet.getEntityId())
                    .setSwingingItem(packet.getItemId());
        } else {
            Logging.warn(this, "No player by ID (swing)" + packet.getEntityId());
        }
    }

}
