package me.vrekt.oasis.network;

import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.Lunar;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.game.GameServer;
import gdx.lunar.server.netty.NettyServer;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.server.world.impl.WorldAdapter;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.utility.logging.Logging;

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
    public void start() {
        server.bind();

        gameServer.getWorldManager().addWorld("TutorialWorld", new WorldAdapter(new ServerWorldConfiguration(), "TutorialWorld"));
        gameServer.start();

        Logging.info(this, "Local Server Started.");
    }

    @Override
    public void dispose() {
        protocol.dispose();
        server.shutdown();
        gameServer.stop();
        gameServer.dispose();
    }
}
