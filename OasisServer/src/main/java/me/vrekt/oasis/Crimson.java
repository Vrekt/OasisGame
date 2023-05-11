package me.vrekt.oasis;

import com.google.common.flogger.FluentLogger;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.netty.NettyServer;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.network.CrimsonPlayerConnection;
import me.vrekt.oasis.world.CrimsonWorldManager;
import me.vrekt.oasis.world.tutorial.ServerGameTutorialWorld;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

/**
 * Crimson server for Oasis.
 */
public final class Crimson {

    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    public static String GAME_VERSION = "0.1-32023a";
    public static String CRIMSON_VERSION = "0.1a";

    private final LunarProtocol protocol;
    private final CrimsonGameServer gameServer;
    private final NettyServer server;

    private final CrimsonWorldManager worldManager;

    Crimson(String[] arguments) {
        log("Starting Crimson version: " + GAME_VERSION);

        String ip = arguments[0];
        int port;
        if (ip == null) {
            ip = "localhost";
            port = 6969;
        } else {
            try {
                port = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException exception) {
                warning("No valid host port! Set port to default: 6969");
                port = 6969;
            }
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });

        protocol = new LunarProtocol(true);
        gameServer = new CrimsonGameServer(protocol);
        worldManager = new CrimsonWorldManager();

        server = new NettyServer(ip, port, protocol, gameServer);
        server.setConnectionProvider(socketChannel -> new CrimsonPlayerConnection(socketChannel, gameServer));
        server.bind();

        log("Netty server successfully started!");

        gameServer.setWorldManager(worldManager);
        gameServer.getWorldManager().addWorld("TutorialWorld", new ServerGameTutorialWorld(new ServerWorldConfiguration(), "TutorialWorld"));
        gameServer.start();

        final String localTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMdd-HHmm"));

        log("Server started successfully at " + localTime + ", version: " + CRIMSON_VERSION + ", game version: " + GAME_VERSION);
    }

    private void handlePacket(int id, ByteBuf in) {

    }

    public static void log(String information) {
        logger.at(Level.INFO).log(information);
    }

    public static void error(String information) {
        logger.at(Level.SEVERE).log(information);
    }

    public static void warning(String information) {
        logger.at(Level.WARNING).log(information);
    }

}

