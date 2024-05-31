package me.vrekt.oasis;

import gdx.lunar.server.game.GameServer;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * The main oasis game server
 */
public final class CrimsonGameServer extends GameServer {
    public CrimsonGameServer(GameProtocol protocol) {
        super(protocol, "1.0");
    }
}
