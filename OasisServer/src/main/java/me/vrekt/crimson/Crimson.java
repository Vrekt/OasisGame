package me.vrekt.crimson;

import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.world.WorldAdapter;
import me.vrekt.crimson.netty.NettyServer;
import me.vrekt.shared.protocol.GameProtocol;
import me.vrekt.shared.protocol.ProtocolDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Initializes the server
 */
public final class Crimson {

    public static String CRIMSON_VERSION = "2024-2";
    private final GameProtocol protocol;

    private final NettyServer server;
    private final CrimsonGameServer gameServer;

    Crimson(String[] arguments) {
        log("Starting Crimson version: " + CRIMSON_VERSION);

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

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> e.printStackTrace());

        protocol = new GameProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME);
        gameServer = new CrimsonGameServer(protocol);

        server = new NettyServer(ip, port, protocol, gameServer);
        server.bind();

        log("Netty server successfully started!");

        gameServer.getWorldManager().addWorld("TutorialWorld", new WorldAdapter("TutorialWorld"));
        gameServer.start();

        final String localTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMdd-HHmm"));
        log("Server started successfully at " + localTime + ", version: " + CRIMSON_VERSION);

        // keep alive
        // TODO: Fix this shit
        while (true) {}
    }

    public static void log(String information, Object... arguments) {
        System.err.printf("INFO: " + (information) + "%n", arguments);
    }

    public static void error(String information, Object... arguments) {
        System.err.printf("ERROR: " + (information) + "%n", arguments);
    }

    public static void warning(String information, Object... arguments) {
        System.err.printf("WARNING: " + (information) + "%n", arguments);
    }

}
