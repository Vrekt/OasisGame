package me.vrekt.crimson.game.world.interior;

import me.vrekt.crimson.game.entity.ServerEntityPlayer;
import me.vrekt.crimson.game.world.World;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;

public abstract class InteriorWorld extends World {

    private final InteriorWorldType type;

    public InteriorWorld(String worldName) {
        super(1213, null);

        this.type = InteriorWorldType.WRYNN_HOUSE;
    }

    /**
     * @return the type
     */
    public InteriorWorldType type() {
        return type;
    }

    @Override
    public void spawnPlayerInWorld(ServerEntityPlayer player) {
        player.setWorldIn(this);

        if (players.isEmpty()) {
            //     player.getConnection().sendImmediately(new S2CPacketPlayers(worldName, true));
        } else {
            //   final S2CNetworkPlayer[] serverPlayers = new S2CNetworkPlayer[players.size()];

            int index = 0;
            for (ServerEntityPlayer other : players.values()) {
                //       serverPlayers[index] = new S2CNetworkPlayer(other.entityId(), other.name(), other.getPosition());
                index++;

                other.getConnection().sendImmediately(new S2CPlayerEnteredInterior(type, player.entityId()));
            }
//     player.getConnection().sendImmediately(new S2CPacketPlayers(type.name(), true, serverPlayers));
        }

        players.put(player.entityId(), player);
    }
}
