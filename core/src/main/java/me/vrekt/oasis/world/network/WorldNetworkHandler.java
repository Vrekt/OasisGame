package me.vrekt.oasis.world.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.obj.TiledWorldObjectProperties;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.S2CStartGame;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.obj.S2CNetworkAddWorldObject;
import me.vrekt.shared.packet.server.obj.S2CNetworkPopulateContainer;
import me.vrekt.shared.packet.server.obj.S2CNetworkSpawnWorldDrop;
import me.vrekt.shared.packet.server.obj.WorldNetworkObject;
import me.vrekt.shared.packet.server.player.*;

/**
 * Handles all world/player related networking as a player connected to a remote server
 */
public final class WorldNetworkHandler {

    private final OasisGame game;
    private final PlayerSP player;
    private boolean receivedBefore;

    public WorldNetworkHandler(OasisGame game) {
        this.game = game;
        this.player = game.getPlayer();
    }

    /**
     * Attach all relevant packets to their handlers
     */
    public void attach() {
        player.getConnection().attach(S2CStartGame.PACKET_ID, packet -> handleStartGame((S2CStartGame) packet));
        player.getConnection().attach(S2CPacketCreatePlayer.PACKET_ID, packet -> handleNetworkCreatePlayer((S2CPacketCreatePlayer) packet));
        player.getConnection().attach(S2CPacketPlayerPosition.PACKET_ID, packet -> handlePlayerPosition((S2CPacketPlayerPosition) packet));
        player.getConnection().attach(S2CPacketPlayerVelocity.PACKET_ID, packet -> handlePlayerVelocity((S2CPacketPlayerVelocity) packet));
        player.getConnection().attach(S2CTeleport.PACKET_ID, packet -> handleSelfTeleport((S2CTeleport) packet));
        player.getConnection().attach(S2CTeleportPlayer.PACKET_ID, packet -> handlePlayerTeleport((S2CTeleportPlayer) packet));
        player.getConnection().attach(S2CPacketRemovePlayer.PACKET_ID, packet -> handleRemovePlayer((S2CPacketRemovePlayer) packet));
        player.getConnection().attach(S2CPlayerEnteredInterior.ID, packet -> handlePlayerEnteredInterior((S2CPlayerEnteredInterior) packet));
        player.getConnection().attach(S2CNetworkFrame.ID, packet -> handleNetworkFrame((S2CNetworkFrame) packet));
        player.getConnection().attach(S2CNetworkAddWorldObject.PACKET_ID, packet -> createNetworkWorldObject((S2CNetworkAddWorldObject) packet));
        player.getConnection().attach(S2CNetworkSpawnWorldDrop.PACKET_ID, packet -> networkSpawnDroppedItem((S2CNetworkSpawnWorldDrop) packet));
        player.getConnection().attach(S2CNetworkPopulateContainer.PACKET_ID, packet -> networkPopulateContainer((S2CNetworkPopulateContainer) packet));
    }

    /**
     * Create a world object
     *
     * @param packet packet
     */
    private void createNetworkWorldObject(S2CNetworkAddWorldObject packet) {
        final WorldNetworkObject object = packet.object();
        if (object.mapObject() == null) {
            // debugging purposes.
            GameLogging.info(this, "Cannot register a network object %s with no map properties.", object.type());
        } else {
            final TiledWorldObjectProperties properties = new TiledWorldObjectProperties(object.mapObject());
            // position is already offset from the server.
            properties.offsetX = false;
            properties.offsetY = false;

            final Rectangle size = new Rectangle(object.position().x, object.position().y, object.size().x, object.size().y);
            final AbstractInteractableWorldObject worldObject = this.player.getWorldState().createInteractableObject(
                    properties,
                    object.mapObject(),
                    size,
                    OasisGameSettings.SCALE,
                    game.getAsset()
            );
            worldObject.setObjectId(object.objectId());
            GameLogging.info(this, "Created network object type %s @ %f,%f", object.type(), object.position().x, object.position().y);
        }
    }

    /**
     * Spawn a dropped item in the world
     *
     * @param packet packet
     */
    private void networkSpawnDroppedItem(S2CNetworkSpawnWorldDrop packet) {
        this.player.getWorldState().spawnWorldDrop(packet.item(), packet.position());
    }

    /**
     * Spawn and populate a container
     *
     * @param packet packet
     */
    private void networkPopulateContainer(S2CNetworkPopulateContainer packet) {
        final ContainerInventory inventory = new ContainerInventory(packet.size());
        final OpenableContainerInteraction interaction = new OpenableContainerInteraction(inventory);
        for (int i = 0; i < packet.contents().length; i++) {
            if (packet.contents()[i] != null) inventory.add(packet.contents()[i], i);
        }
        this.player.getWorldState().spawnWorldObject(interaction, packet.containerType(), packet.position());
    }

    /**
     * Handle a network frame incoming
     * Update all entities and world state.
     *
     * @param frame frame
     */
    private void handleNetworkFrame(S2CNetworkFrame frame) {
        final NetworkState state = frame.state();
        final float now = Gdx.graphics.getDeltaTime();

        updateNetworkEntityState(state, now);
        receivedBefore = true;
    }

    /**
     * Update all entity states from the network
     *
     * @param state state
     */
    private void updateNetworkEntityState(NetworkState state, float delta) {
        for (int i = 0; i < state.entities().length; i++) {
            final NetworkEntityState entityState = state.entities()[i];
            final GameEntity entity = player.getWorldState().findEntityById(entityState.entityId());
            if (entity != null) {
                if (!receivedBefore) {
                    // teleport instead since this is the first frame.
                    entity.teleport(entityState.x(), entityState.y());
                } else {
                    // otherwise interpolate
                    entity.networkInterpolate(entityState, delta, TimeUtils.nanosToMillis(state.timeToArrive()));
                }
            } else {
                GameLogging.warn(this, "Failed to find network entity state for %d", entityState.entityId());
            }
        }
    }

    /**
     * Create a network player in the active world
     *
     * @param name     name
     * @param entityId ID
     * @param origin   origin
     */
    private void createLocalNetworkPlayer(String name, int entityId, Vector2 origin) {
        final NetworkPlayer networkPlayer = new NetworkPlayer(this.player.getWorldState());
        networkPlayer.load(game.getAsset());

        networkPlayer.setProperties(name, entityId);
        networkPlayer.createCircleBody(this.player.getWorldState().boxWorld(), false);
        networkPlayer.setPosition(origin);

        this.player.getWorldState().spawnPlayerInWorld(networkPlayer);
    }

    /**
     * Create a player from the server network
     *
     * @param username their username
     * @param entityId their ID
     * @param x        x
     * @param y        y
     */
    private void createPlayerInActiveWorld(String username, int entityId, float x, float y) {
        if (player.getWorldState().hasPlayer(entityId)) return;

        GameLogging.info(this, "Spawning new network player with ID %d and username %s at {%f,%f}", entityId, username, x, y);
        createLocalNetworkPlayer(username, entityId, new Vector2(x, y));
    }

    /**
     * Handle batched player packet
     *
     * @param packet packet
     */
    private void handleStartGame(S2CStartGame packet) {
        //TODO: if (!NetworkValidation.ensureWorldContext(player, packet)) return;

        if (packet.hasPlayers()) {
            GameLogging.info(this, "Starting network game with %d players to create", packet.players().length);

            for (S2CNetworkPlayer networkPlayer : packet.players()) {
                if (!NetworkValidation.ensureValidEntityId(player, networkPlayer.entityId)) continue;
                createPlayerInActiveWorld(networkPlayer.username, networkPlayer.entityId, networkPlayer.x, networkPlayer.y);
            }
        }
    }

    /**
     * Handle the {@link S2CPacketCreatePlayer} packet
     * TODO: WorldState information in packet
     *
     * @param packet the packet
     */
    private void handleNetworkCreatePlayer(S2CPacketCreatePlayer packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;
        if (!NetworkValidation.ensureValidEntityId(player, packet.getEntityId())) return;

        createPlayerInActiveWorld(packet.getUsername(), packet.getEntityId(), packet.getX(), packet.getY());
    }

    /**
     * Remove a player from the world
     * TODO: WorldState information in packet
     *
     * @param packet packet
     */
    private void handleRemovePlayer(S2CPacketRemovePlayer packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;

        player.getConnection().removePlayer(packet.entityId());
        player.getWorldState().removePlayerInWorld(packet.entityId(), true);

        GameLogging.info(this, "Player (%d) (%s) left.", packet.entityId(), packet.username());
    }

    /**
     * Updates the network players position
     *
     * @param packet packet
     */
    private void handlePlayerPosition(S2CPacketPlayerPosition packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;

        player.getWorldState().updatePlayerPositionInWorld(packet.getEntityId(), packet.getX(), packet.getY(), packet.getRotation());
    }

    /**
     * Updates the network players velocity
     *
     * @param packet packet
     */
    private void handlePlayerVelocity(S2CPacketPlayerVelocity packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;

        player.getWorldState().updatePlayerVelocityInWorld(packet.entityId(), packet.x(), packet.y(), packet.rotation());
    }

    /**
     * Teleport the client player
     *
     * @param packet packet
     */
    private void handleSelfTeleport(S2CTeleport packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;
        player.setPosition(packet.x(), packet.y());
    }

    /**
     * Handle player teleported
     *
     * @param packet packet
     */
    private void handlePlayerTeleport(S2CTeleportPlayer packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;
        if (!NetworkValidation.ensureValidEntityId(player, packet.entityId())) return;
        this.player.getWorldState().player(packet.entityId()).ifPresent(player -> player.teleport(packet.x(), packet.y()));
    }

    /**
     * Handle when a player enters an interior
     *
     * @param packet packet
     */
    private void handlePlayerEnteredInterior(S2CPlayerEnteredInterior packet) {
        if (!NetworkValidation.ensureInWorld(player, packet)) return;
        if (!NetworkValidation.ensureValidEntityId(player, packet.entityId())) return;
        if (packet.type() == InteriorWorldType.NONE) return;

        final NetworkPlayer mp = player.getConnection().getPlayer(packet.entityId());
        if (mp != null) {
            if (player.getWorldState().isPlayerVisible(mp)) {
                mp.transferPlayerToWorldVisible(packet.type());
            } else {
                mp.transferImmediately(packet.type());
            }
        } else {
            GameLogging.warn(this, "Failed to find a player %d", packet.entityId());
        }

        GameLogging.info(this, "Player %d entered interior: %s", packet.entityId(), packet.type());
    }

}
