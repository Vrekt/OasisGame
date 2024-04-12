package me.vrekt.oasis;

import gdx.lunar.protocol.GdxProtocol;
import gdx.lunar.server.game.GameServer;

/**
 * The main oasis game server
 */
public final class CrimsonGameServer extends GameServer {
    public CrimsonGameServer(GdxProtocol protocol) {
        super(protocol, "1.0");
    }
}
