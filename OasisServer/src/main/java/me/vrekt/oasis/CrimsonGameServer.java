package me.vrekt.oasis;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.game.GameServer;

/**
 * The main oasis game server
 */
public final class CrimsonGameServer extends GameServer {
    public CrimsonGameServer(LunarProtocol protocol) {
        super(protocol, "1.0");
    }
}
