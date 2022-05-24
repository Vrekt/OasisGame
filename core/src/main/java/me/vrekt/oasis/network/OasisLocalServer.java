package me.vrekt.oasis.network;

import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.Lunar;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.GameServer;
import gdx.lunar.server.NettyServer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.world.server.WorldServer;

import java.util.concurrent.CompletableFuture;

/**
 * A local single player game server.
 */
public final class OasisLocalServer implements Disposable {

    private final OasisGame game;

    private final LunarProtocol protocol;
    private final NettyServer server;
    private final GameServer gameServer;

    public OasisLocalServer(OasisGame game, LunarProtocol protocol) {
        this.game = game;
        this.protocol = protocol;
        this.gameServer = new GameServer(protocol, Lunar.gameVersion);
        this.server = new NettyServer("localhost", 6969, protocol, gameServer);
    }

    /**
     * Start local game server async
     */
    public void startAsync() {
        CompletableFuture.runAsync(() -> {
            server.bind();

            gameServer.getWorldManager().addWorld("TutorialWorld",
                    new WorldServer(game.getWorldManager().getWorld("TutorialWorld"), "TutorialWorld"));
            gameServer.start();

            Logging.info(this, "Local Server Started.");

        });
    }

    @Override
    public void dispose() {
        protocol.dispose();
        server.shutdown();
        gameServer.stop();
        gameServer.dispose();
    }
}
