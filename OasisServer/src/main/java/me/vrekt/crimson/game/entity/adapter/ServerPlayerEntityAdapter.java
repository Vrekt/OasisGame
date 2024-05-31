package me.vrekt.crimson.game.entity.adapter;

import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.entity.ServerPlayerEntity;
import me.vrekt.crimson.game.network.ServerPlayerConnection;

/**
 * Default adapter
 */
public final class ServerPlayerEntityAdapter extends ServerPlayerEntity {

    public ServerPlayerEntityAdapter(CrimsonGameServer server, ServerPlayerConnection connection) {
        super(server, connection);
    }
}
