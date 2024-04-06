package me.vrekt.oasis;

import gdx.lunar.server.game.GameServer;
import gdx.lunar.v2.GdxProtocol;

/**
 * The main oasis game server
 */
public final class CrimsonGameServer extends GameServer {
    public CrimsonGameServer(GdxProtocol protocol) {
        super(protocol, "1.0");
    }
}
