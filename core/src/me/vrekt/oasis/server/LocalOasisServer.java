package me.vrekt.oasis.server;

import gdx.lunar.server.LunarGameServer;
import gdx.lunar.server.LunarNettyServer;
import gdx.lunar.server.LunarServer;
import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.world.impl.LunarWorldAdapter;

import java.util.concurrent.CompletableFuture;

public final class LocalOasisServer implements Disposable {

    private LunarNettyServer server;
    private LunarServer gameServer;

    public LocalOasisServer() {
        CompletableFuture.runAsync(() -> {
            server = new LunarNettyServer("localhost", 6969);
            server.bind();

        }).whenComplete((v, e) -> {
            gameServer = new LunarGameServer();
            gameServer.getWorldManager().addWorld("Athena", new LunarWorldAdapter());
            gameServer.start();
        });
    }

    @Override
    public void dispose() {
        gameServer.stop();
    }
}
