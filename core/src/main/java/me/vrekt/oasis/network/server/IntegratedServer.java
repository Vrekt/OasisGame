package me.vrekt.oasis.network.server;

/**
 * Integrated single player server (that can be multiplayer enabled)
 */
public final class IntegratedServer {

    /*private final OasisGame game;
    private final GameProtocol protocol;
    private final NettyServer server;
    private final GameServer gameServer;
    private boolean isStarted;

    public IntegratedServer(OasisGame game, GdxProtocol protocol) {
        this.game = game;
        this.protocol = protocol;
        this.gameServer = new GameServer(protocol, "1.0");
        this.server = new NettyServer("localhost", 6969, protocol, gameServer);
    }

    *//**
     * Start local game server async
     *//*
    public void start() {
        server.bind();

        gameServer.getWorldManager().addWorld("TutorialWorld", new WorldAdapter(new ServerWorldConfiguration(), "TutorialWorld"));
        gameServer.start();

        GameLogging.info(this, "Integrated server started.");
        isStarted = true;
    }

    public void suspend() {
        server.disableIncomingConnections();
        gameServer.suspend();
    }

    public void resume() {
        server.enableIncomingConnections();
        gameServer.resume();
    }

    public boolean isStarted() {
        return isStarted;
    }

    public GameServer getGameServer() {
        return gameServer;
    }

    public NettyServer getNettyServer() {
        return server;
    }

    @Override
    public void dispose() {
        server.shutdown();
        gameServer.stop();
        gameServer.dispose();
    }*/
}
