package me.vrekt.oasis.server;

import gdx.lunar.Lunar;
import gdx.lunar.LunarClientServer;
import gdx.lunar.protocol.LunarProtocol;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.utilities.Disposable;

public final class LocalOasisServer implements Disposable {

    private LunarClientServer server;
    private LunarServer gameServer;

    public LocalOasisServer() {
        server = new LunarClientServer(new Lunar(), new LunarProtocol(true), "localhost", 6969);
        server.connect().join();
    }

    public LunarClientServer getServer() {
        return server;
    }

    @Override
    public void dispose() {

    }
}
