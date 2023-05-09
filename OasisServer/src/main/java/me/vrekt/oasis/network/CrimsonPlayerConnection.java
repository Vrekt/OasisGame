package me.vrekt.oasis.network;

import gdx.lunar.protocol.packet.client.CPacketAuthentication;
import gdx.lunar.protocol.packet.client.CPacketJoinWorld;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerPlayerConnection;
import io.netty.channel.Channel;
import me.vrekt.oasis.logging.Logging;
import me.vrekt.oasis.world.CrimsonWorld;
import me.vrekt.shared.network.*;

/**
 * Handles the players connection
 */
public final class CrimsonPlayerConnection extends ServerPlayerConnection {

    private CrimsonWorld worldIn;

    public CrimsonPlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);

        registerPacket(ClientSpawnEntity.ID, ClientSpawnEntity::new, this::handleSpawnEntity);
        registerPacket(ClientEquipItem.ID, ClientEquipItem::new, this::handleEquipItem);
        registerPacket(ClientSwingItem.ID, ClientSwingItem::new, this::handleSwingItem);
    }

    @Override
    public void handleAuthentication(CPacketAuthentication packet) {
        Logging.info(this, "Attempting to authenticate a new player from [" + channel.localAddress() + "]");
        super.handleAuthentication(packet);
    }

    @Override
    public void handleJoinWorld(CPacketJoinWorld packet) {
        Logging.info(this, "New player requesting to join world: " + packet.getWorldName() + " with username " + packet.getUsername());

        worldIn = (CrimsonWorld) server.getWorldManager().getWorld(packet.getWorldName());
        super.handleJoinWorld(packet);
    }

    private void handleSpawnEntity(ClientSpawnEntity packet) {
        Logging.info(this, "Spawning a new entity by request from player, type=" + packet.getType() + ", pos=" + packet.getPosition());

        if (worldIn != null) {
            worldIn.spawnEntityInWorld(packet.getType(), packet.getPosition());

            final ServerSpawnEntity entity = new ServerSpawnEntity(worldIn.assignEntityId(), packet.getType(), packet.getPosition());
            sendImmediately(entity);
        }
    }

    private void handleEquipItem(ClientEquipItem packet) {
        worldIn.broadcastPacketImmediately(packet.getEntityId(), new ServerPlayerEquippedItem(packet.getEntityId(), packet.getItemId()));
    }

    private void handleSwingItem(ClientSwingItem packet) {
        worldIn.broadcastPacketImmediately(packet.getEntityId(), new ServerPlayerSwungItem(packet.getEntityId(), packet.getItemId()));
    }

}
