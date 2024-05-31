package me.vrekt.oasis.network;

import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerPlayerConnection;
import gdx.lunar.server.world.World;
import io.netty.channel.Channel;
import me.vrekt.oasis.entity.CrimsonPlayer;
import me.vrekt.oasis.logging.Logging;
import me.vrekt.oasis.world.CrimsonWorld;
import me.vrekt.shared.packet.client.C2SPacketAuthenticate;
import me.vrekt.shared.packet.client.C2SPacketJoinWorld;
import me.vrekt.shared.packet.server.S2CPacketWorldInvalid;

/**
 * Handles the players connection
 */
public final class CrimsonPlayerConnection extends ServerPlayerConnection {

    private CrimsonWorld worldIn;
    private CrimsonPlayer localPlayer;

    public CrimsonPlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);
    }

    @Override
    public void handleAuthentication(C2SPacketAuthenticate packet) {
        Logging.info(this, "Attempting to authenticate a new player from [" + channel.localAddress() + "]");
        super.handleAuthentication(packet);
    }

    @Override
    public void handleJoinWorld(C2SPacketJoinWorld packet) {
        Logging.info(this, "New player requesting to join world: " + packet.worldName() + " with username " + packet.username());

        if (packet.username() == null || packet.username().isEmpty()) {
            this.sendImmediately(new S2CPacketWorldInvalid(packet.worldName(), "Invalid username."));
            return;
        } else if ((packet.worldName() == null || packet.worldName().isEmpty()) || !server.getWorldManager().worldExists(packet.worldName())) {
            this.sendImmediately(new S2CPacketWorldInvalid(packet.worldName(), "World does not exist."));
            return;
        }

        final World world = server.getWorldManager().getWorld(packet.worldName());
        if (world.isFull()) {
            this.sendImmediately(new S2CPacketWorldInvalid(packet.worldName(), "World is full."));
            return;
        }

        this.worldIn = (CrimsonWorld) world;
        this.localPlayer = new CrimsonPlayer(server, this);
        this.localPlayer.setName(packet.username());
        this.localPlayer.setWorldIn(world);
        this.localPlayer.setEntityId(world.assignEntityIdFor(true));
    }

}
