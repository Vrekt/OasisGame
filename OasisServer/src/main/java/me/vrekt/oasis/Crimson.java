package me.vrekt.oasis;

import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.netty.NettyServer;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import me.vrekt.oasis.logging.Logging;
import me.vrekt.oasis.network.CrimsonPlayerConnection;
import me.vrekt.oasis.world.CrimsonWorld;
import me.vrekt.oasis.world.CrimsonWorldManager;

/**
 * Crimson server for Oasis.
 */
public final class Crimson {

    public static String GAME_VERSION = "0.1-32023a";
    public static String CRIMSON_VERSION = "0.1a";

    private final LunarProtocol protocol;
    private final CrimsonGameServer gameServer;
    private final NettyServer server;

    private final CrimsonWorldManager worldManager;

    Crimson() {
        protocol = new LunarProtocol(true);
        gameServer = new CrimsonGameServer(protocol);
        worldManager = new CrimsonWorldManager();

        server = new NettyServer("localhost", 6969, protocol, gameServer);
        server.setConnectionProvider(socketChannel -> new CrimsonPlayerConnection(socketChannel, gameServer));
        server.bind();

        gameServer.setWorldManager(worldManager);
        gameServer.getWorldManager().addWorld("TutorialWorld", new CrimsonWorld(new ServerWorldConfiguration(), "TutorialWorld"));
        gameServer.start();

        Logging.info(this, "Server started successfully, version: " + CRIMSON_VERSION + ", game version: " + GAME_VERSION);
    }
}

