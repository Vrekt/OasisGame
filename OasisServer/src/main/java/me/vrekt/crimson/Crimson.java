package me.vrekt.crimson;

import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.netty.NettyServer;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * Initializes the server
 */
public final class Crimson {

    public static String CRIMSON_VERSION = "2024-2";

    public static final String TAG = "IntegratedServer";
    private GameProtocol protocol;
    private NettyServer server;
    private CrimsonGameServer gameServer;

    Crimson(String[] arguments) {
       /* log("Starting Crimson version: " + CRIMSON_VERSION);

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

        protocol = new GameProtocol(ProtocolDefaults.PROTOCOL_VERSION, ProtocolDefaults.PROTOCOL_NAME);
        gameServer = new CrimsonGameServer(protocol);

        server = new NettyServer(ip, port, protocol, gameServer);
        server.bind();

        log("Netty server successfully started!");
        gameServer.start();

        final String localTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MMdd-HHmm"));
        log("Server started successfully at " + localTime + ", version: " + CRIMSON_VERSION);*/
    }

    public static void log(String information, Object... arguments) {
        GameLogging.info(TAG, information, arguments);
    }

    public static void warning(String information, Object... arguments) {
        GameLogging.warn(TAG, information, arguments);
    }

    public static void error(String information, Object... arguments) {
        GameLogging.error(TAG, information, arguments);
    }

    public static void exception(String information, Throwable ex, Object... arguments) {
        GameLogging.exceptionThrown(TAG, information, ex, arguments);
    }

}
