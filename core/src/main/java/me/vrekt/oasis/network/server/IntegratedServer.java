package me.vrekt.oasis.network.server;

import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.server.game.GameServer;
import gdx.lunar.server.netty.NettyServer;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.server.world.impl.WorldAdapter;
import gdx.lunar.v2.GdxProtocol;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.utility.logging.GameLogging;

/**
 * Integrated single player server
 */
public final class IntegratedServer implements Disposable {

    private final OasisGame game;
    private final GdxProtocol protocol;
    private final NettyServer server;
    private final GameServer gameServer;

    public IntegratedServer(OasisGame game, GdxProtocol protocol) {
        this.game = game;
        this.protocol = protocol;
        this.gameServer = new GameServer(protocol, "1.0");
        this.server = new NettyServer("localhost", 6969, protocol, gameServer);
    }

    /**
     * Start local game server async
     */
    public void start() {
        server.bind();

        gameServer.getWorldManager().addWorld("TutorialWorld", new WorldAdapter(new ServerWorldConfiguration(), "TutorialWorld"));
        gameServer.start();

        GameLogging.info(this, "Integrated server started.");
    }

    public GameServer getGameServer() {
        return gameServer;
    }

    @Override
    public void dispose() {
        server.shutdown();
        gameServer.stop();
        gameServer.dispose();
    }
}
