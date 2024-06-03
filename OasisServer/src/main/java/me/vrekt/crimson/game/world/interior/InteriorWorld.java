package me.vrekt.crimson.game.world.interior;

import me.vrekt.crimson.game.entity.ServerPlayerEntity;
import me.vrekt.crimson.game.world.World;
import me.vrekt.shared.packet.server.player.S2CNetworkPlayer;
import me.vrekt.shared.packet.server.player.S2CPacketPlayersInWorld;

public abstract class InteriorWorld extends World {

    public InteriorWorld(String worldName) {
        super(worldName);
    }

    @Override
    public void spawnPlayerInWorld(ServerPlayerEntity player) {
        player.setWorldIn(this);

        if (players.isEmpty()) {
            player.getConnection().sendImmediately(new S2CPacketPlayersInWorld());
        } else {
            final S2CNetworkPlayer[] serverPlayers = new S2CNetworkPlayer[players.size()];

            int index = 0;
            for (ServerPlayerEntity other : players.values()) {
                serverPlayers[index] = new S2CNetworkPlayer(other.entityId(), other.name(), other.getPosition());
                index++;
            }

            player.getConnection().sendImmediately(new S2CPacketPlayersInWorld(serverPlayers));
        }

        players.put(player.entityId(), player);
    }
}
