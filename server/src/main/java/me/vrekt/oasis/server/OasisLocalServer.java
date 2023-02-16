package me.vrekt.oasis.server;

import gdx.lunar.Lunar;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.game.GameServer;
import gdx.lunar.server.netty.NettyServer;
import gdx.lunar.server.world.impl.WorldAdapter;

/**
 * A local single player game server.
 */
public final class OasisLocalServer {

    private final LunarProtocol protocol;
    private final NettyServer server;
    private final GameServer gameServer;

    public OasisLocalServer(LunarProtocol protocol) {
        this.protocol = protocol;
        this.gameServer = new GameServer(protocol, Lunar.gameVersion);
        this.server = new NettyServer("localhost", 6969, protocol, gameServer);
    }

    /**
     * Start local game server async
     */
    public void startAsync() {

        server.bind();

        gameServer.getWorldManager().addWorld("TutorialWorld",
                new WorldAdapter());
        gameServer.start();

    }
}
