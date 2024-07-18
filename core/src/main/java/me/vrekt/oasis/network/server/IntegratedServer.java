package me.vrekt.oasis.network.server;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.crimson.game.CrimsonGameServer;
import me.vrekt.crimson.game.world.World;
import me.vrekt.crimson.netty.NettyServer;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.network.WorldNetworkHandler;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.protocol.GameProtocol;

/**
 * Integrated single player server (that can be multiplayer enabled)
 */
public final class IntegratedServer implements Disposable {

    private final OasisGame game;
    private final GameProtocol protocol;
    private final PlayerSP player;

    private NettyServer networkServer;
    private CrimsonGameServer gameServer;

    private boolean started;

    private World activeWorld;

    public IntegratedServer(OasisGame game, GameProtocol protocol, PlayerSP player) {
        this.game = game;
        this.protocol = protocol;
        this.player = player;
    }

    public void start() {
        init();

        networkServer.bind();

        loadWorlds();
        buildAndLoadLocalState();
        gameServer.start();

        this.started = true;

        GameLogging.info(this, "Game server ready");
    }

    /**
     * Update network state to all players
     */
    public void update() {
        final GameWorld world = player.getWorldState();
        activeWorld.localUpdate(world, player);

        final NetworkState state = game.networkHandler().build();
        activeWorld.broadcastState(state);

        // update world origin to around the player
        activeWorld.setWorldOrigin(player.getBody().getPosition().cpy().sub(1, 1));
    }

    private void init() {
        if (gameServer == null) {
            gameServer = new CrimsonGameServer(protocol, game.networkHandler());
        }

        if (networkServer == null) {
            networkServer = new NettyServer("localhost", 6969, protocol, gameServer);
        }
    }

    /**
     * Load local worlds into the server
     */
    private void loadWorlds() {
        for (GameWorld world : game.getWorldManager().worlds().values()) {
            gameServer.registerLoadedWorld(world);
        }

        // TODO:
        GameLogging.info(this, "Loaded %d worlds into the game server", 1);
    }

    /**
     * Build local network state and load it.
     */
    private void buildAndLoadLocalState() {
        final WorldNetworkHandler handler = game.networkHandler();
        final NetworkState state = handler.build();

        gameServer.setInitialNetworkState(state, player.getWorldState());
        gameServer.setLocalHostPlayer(player);

        activeWorld = gameServer.getWorld(player.getWorldState().worldId());
        activeWorld.setWorldOrigin(player.getWorldState().worldOrigin());
    }

    public boolean started() {
        return started;
    }


    @Override
    public void dispose() {
        gameServer.dispose();
        networkServer.shutdown();
    }
}
