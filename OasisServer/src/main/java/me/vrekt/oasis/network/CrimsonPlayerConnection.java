package me.vrekt.oasis.network;

import gdx.lunar.protocol.packet.client.CPacketAuthentication;
import gdx.lunar.protocol.packet.client.CPacketJoinWorld;
import gdx.lunar.protocol.packet.client.CPacketWorldLoaded;
import gdx.lunar.protocol.packet.server.SPacketJoinWorld;
import gdx.lunar.protocol.packet.server.SPacketWorldInvalid;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerPlayerConnection;
import gdx.lunar.server.world.World;
import io.netty.channel.Channel;
import me.vrekt.oasis.entity.CrimsonPlayer;
import me.vrekt.oasis.logging.Logging;
import me.vrekt.oasis.world.CrimsonWorld;
import me.vrekt.shared.network.*;

/**
 * Handles the players connection
 */
public final class CrimsonPlayerConnection extends ServerPlayerConnection {

    private CrimsonWorld worldIn;
    private CrimsonPlayer localPlayer;

    public CrimsonPlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);

        registerPacket(ClientPacketSpawnEntity.ID, ClientPacketSpawnEntity::new, this::handleSpawnEntity);
        registerPacket(ClientPacketEquipItem.ID, ClientPacketEquipItem::new, this::handleEquipItem);
        registerPacket(ClientPacketSwingItem.ID, ClientPacketSwingItem::new, this::handleSwingItem);
    }

    @Override
    public void handleAuthentication(CPacketAuthentication packet) {
        Logging.info(this, "Attempting to authenticate a new player from [" + channel.localAddress() + "]");
        super.handleAuthentication(packet);
    }

    @Override
    public void handleJoinWorld(CPacketJoinWorld packet) {
        Logging.info(this, "New player requesting to join world: " + packet.getWorldName() + " with username " + packet.getUsername());

        if (packet.getUsername() == null || packet.getUsername().isEmpty()) {
            this.sendImmediately(new SPacketWorldInvalid(packet.getWorldName(), "Invalid username."));
            return;
        } else if ((packet.getWorldName() == null || packet.getWorldName().isEmpty()) || !server.getWorldManager().worldExists(packet.getWorldName())) {
            this.sendImmediately(new SPacketWorldInvalid(packet.getWorldName(), "World does not exist."));
            return;
        }

        final World world = server.getWorldManager().getWorld(packet.getWorldName());
        if (world.isFull()) {
            this.sendImmediately(new SPacketWorldInvalid(packet.getWorldName(), "World is full."));
            return;
        }

        this.worldIn = (CrimsonWorld) world;
        this.localPlayer = new CrimsonPlayer(true, server, this);
        this.localPlayer.setName(packet.getUsername());
        this.localPlayer.setServerWorldIn(world);
        this.localPlayer.setEntityId(world.assignEntityIdFor(true));
        this.player = localPlayer;
        sendImmediately(new SPacketJoinWorld(packet.getWorldName(), localPlayer.getEntityId()));
    }

    @Override
    public void handleWorldLoaded(CPacketWorldLoaded packet) {
        super.handleWorldLoaded(packet);
        worldIn.handlePlayerLoaded(localPlayer);
    }

    private void handleSpawnEntity(ClientPacketSpawnEntity packet) {
        Logging.info(this, "Spawning a new entity by request from player, type=" + packet.getType() + ", pos=" + packet.getPosition());

        if (worldIn != null) {
            worldIn.spawnEntityInWorld(packet.getType(), packet.getPosition());

            final ServerPacketSpawnEntity entity = new ServerPacketSpawnEntity(worldIn.assignEntityIdFor(false), packet.getType(), packet.getPosition());
            sendImmediately(entity);
        }
    }

    private void handleEquipItem(ClientPacketEquipItem packet) {
        worldIn.broadcastNowWithExclusion(packet.getEntityId(), new ServerPacketPlayerEquippedItem(packet.getEntityId(), packet.getItemId()));
    }

    private void handleSwingItem(ClientPacketSwingItem packet) {
        worldIn.broadcastNowWithExclusion(packet.getEntityId(), new ServerPacketPlayerSwingItem(packet.getEntityId(), packet.getItemId()));
    }

}
