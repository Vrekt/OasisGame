package me.vrekt.oasis.network;

import gdx.lunar.protocol.packet.client.CPacketAuthentication;
import gdx.lunar.protocol.packet.client.CPacketJoinWorld;
import gdx.lunar.server.game.LunarServer;
import gdx.lunar.server.network.connection.ServerPlayerConnection;
import io.netty.channel.Channel;
import me.vrekt.oasis.logging.Logging;

/**
 * Handles the players connection
 */
public final class CrimsonPlayerConnection extends ServerPlayerConnection {

    public CrimsonPlayerConnection(Channel channel, LunarServer server) {
        super(channel, server);
    }

    @Override
    public void handleAuthentication(CPacketAuthentication packet) {
        Logging.info(this, "Attempting to authenticate a new player from [" + channel.localAddress() + "]");
        super.handleAuthentication(packet);
    }

    @Override
    public void handleJoinWorld(CPacketJoinWorld packet) {
        Logging.info(this, "New player requesting to join world: " + packet.getWorldName() + " with username " + packet.getUsername());
        super.handleJoinWorld(packet);
    }


}
