package me.vrekt.oasis.network.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.Entities;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.inventory.container.ContainerInventory;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.network.utility.GameValidation;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.obj.TiledWorldObjectProperties;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.container.OpenableContainerInteraction;
import me.vrekt.oasis.world.obj.interaction.impl.items.BreakableObjectInteraction;
import me.vrekt.shared.network.state.NetworkEntityState;
import me.vrekt.shared.network.state.NetworkState;
import me.vrekt.shared.packet.server.S2CNetworkFrame;
import me.vrekt.shared.packet.server.entity.S2CNetworkCreateEntity;
import me.vrekt.shared.packet.server.interior.S2CPlayerEnteredInterior;
import me.vrekt.shared.packet.server.obj.*;
import me.vrekt.shared.packet.server.player.*;
import me.vrekt.shared.protocol.Packets;

/**
 * Handles all world/player related networking as a player connected to a remote server
 */
public final class WorldNetworkHandler {

    private final OasisGame game;
    private final PlayerSP player;
    private boolean receivedBefore;

    public WorldNetworkHandler(OasisGame game) {
        this.game = game;
        this.player = game.player();
    }

    /**
     * Attach all relevant packets to their handlers
     */
    public void attach() {
        player.getConnection().attach(Packets.S2C_CREATE_PLAYER, packet -> handleNetworkCreatePlayer((S2CNetworkCreatePlayer) packet));
        player.getConnection().attach(Packets.S2C_PLAYER_POSITION, packet -> handlePlayerPosition((S2CNetworkPlayerPosition) packet));
        player.getConnection().attach(Packets.S2C_PLAYER_VELOCITY, packet -> handlePlayerVelocity((S2CNetworkPlayerVelocity) packet));
        player.getConnection().attach(Packets.S2C_TELEPORT, packet -> handleSelfTeleport((S2CTeleport) packet));
        player.getConnection().attach(Packets.S2C_PLAYER_TELEPORTED, packet -> handlePlayerTeleport((S2CTeleportPlayer) packet));
        player.getConnection().attach(Packets.S2C_REMOVE_PLAYER, packet -> handleRemovePlayer((S2CNetworkRemovePlayer) packet));
        player.getConnection().attach(Packets.S2C_PLAYER_ENTERED_INTERIOR, packet -> handlePlayerEnteredInterior((S2CPlayerEnteredInterior) packet));
        player.getConnection().attach(Packets.S2C_NETWORK_FRAME, packet -> handleNetworkFrame((S2CNetworkFrame) packet));
        player.getConnection().attach(Packets.S2C_CREATE_OBJECT, packet -> createNetworkWorldObject((S2CNetworkAddWorldObject) packet));
        player.getConnection().attach(Packets.S2C_CREATE_WORLD_DROP, packet -> networkSpawnDroppedItem((S2CNetworkSpawnWorldDrop) packet));
        player.getConnection().attach(Packets.S2C_CREATE_CONTAINER, packet -> networkPopulateContainer((S2CNetworkCreateContainer) packet));
        player.getConnection().attach(Packets.S2C_REMOVE_OBJECT, packet -> networkDestroyWorldObject((S2CNetworkRemoveWorldObject) packet));
        player.getConnection().attach(Packets.S2C_ANIMATE_OBJECT, packet -> networkAnimateObject((S2CAnimateObject) packet));
        player.getConnection().attach(Packets.S2C_CREATE_ENTITY, packet -> createNetworkEntity((S2CNetworkCreateEntity) packet));
    }

    /**
     * Create a world object
     *
     * @param packet packet
     */
    private void createNetworkWorldObject(S2CNetworkAddWorldObject packet) {
        final WorldNetworkObject object = packet.object();
        if (object.mapObject() == null) {
            GameLogging.info(this, "Cannot register a network object %s with no map properties.", object.type());
        } else {
            final TiledWorldObjectProperties properties = new TiledWorldObjectProperties(object.mapObject());
            // position is already offset from the server.
            properties.offsetX = false;
            properties.offsetY = false;

            final Rectangle size = new Rectangle(object.position().x, object.position().y, object.size().x, object.size().y);
            this.player.getWorldState().createInteractableObject(
                    properties,
                    object.mapObject(),
                    size,
                    OasisGameSettings.SCALE,
                    game.asset(),
                    object.objectId()
            );
            GameLogging.info(this, "Created network object type (%d) %s @ %f,%f", object.objectId(), object.type(), object.position().x, object.position().y);
        }
    }

    /**
     * Spawn a dropped item in the world
     *
     * @param packet packet
     */
    private void networkSpawnDroppedItem(S2CNetworkSpawnWorldDrop packet) {
        final Item item = ItemRegistry.createItem(packet.item(), packet.amount());
        this.player.getWorldState().localSpawnWorldDrop(item, packet.position(), packet.objectId());
    }

    /**
     * Spawn and populate a container
     *
     * @param packet packet
     */
    private void networkPopulateContainer(S2CNetworkCreateContainer packet) {
        final ContainerInventory inventory = new ContainerInventory(packet.size());
        final OpenableContainerInteraction interaction = new OpenableContainerInteraction(inventory);
        for (int i = 0; i < packet.contents().length; i++) {
            if (packet.contents()[i] != null) inventory.add(packet.contents()[i], i);
        }
        this.player.getWorldState().spawnWorldObject(interaction, packet.containerType(), packet.position());
    }

    /**
     * Destroy/remove and object
     *
     * @param object the object
     */
    private void networkDestroyWorldObject(S2CNetworkRemoveWorldObject object) {
        this.player.getWorldState().removeObjectById(object.objectId());
    }

    /**
     * Animate an object.
     *
     * @param packet packets
     */
    private void networkAnimateObject(S2CAnimateObject packet) {
        final AbstractInteractableWorldObject object = this.player.getWorldState().getWorldObjectById(packet.objectId());
        if (object instanceof BreakableObjectInteraction interaction) {
            interaction.animate(true);
        }
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
     * Create a network entity
     *
     * @param packet the packet
     */
    private void createNetworkEntity(S2CNetworkCreateEntity packet) {
        final NetworkEntityState state = packet.state();

        GameEntity entity = null;
        if (state.type().generic()) {
            entity = Entities.generic(state.key(), player.getWorldState(), new Vector2(state.x(), state.y()), game);
        } else if (state.type().interactable()) {
            entity = Entities.interactable(state.key(), player.getWorldState(), new Vector2(state.x(), state.y()), game);
        } else if (state.type().enemy()) {
            entity = Entities.enemy(state.key(), player.getWorldState(), new Vector2(state.x(), state.y()), game);
        } else {
            GameLogging.info(this, "Failed to create an entity %s", state.type());
        }

        if (entity != null) {
            entity.setNetworked(true);
            entity.setEntityId(state.entityId());
            entity.setVelocity(state.vx(), state.vy());
            entity.load(GameManager.asset());

            player.getWorldState().addNetworkedEntity(entity);
            GameLogging.info(this, "Created networked interactable entity: %d,%s", entity.entityId(), entity.name());
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
        networkPlayer.load(game.asset());

        networkPlayer.setProperties(name, entityId);
        networkPlayer.createCircleBody(this.player.getWorldState().boxWorld(), false);
        networkPlayer.setPosition(origin);

        this.player.getConnection().registerGlobalNetworkPlayer(networkPlayer);

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
     * Handle the {@link S2CNetworkCreatePlayer} packet
     * TODO: WorldState information in packet
     *
     * @param packet the packet
     */
    private <T extends S2CNetworkCreatePlayer> void handleNetworkCreatePlayer(T packet) {
        if (!GameValidation.ensureInWorld(player, packet)) return;
        if (!GameValidation.ensureValidEntityId(player, packet.getEntityId())) return;

        createPlayerInActiveWorld(packet.getUsername(), packet.getEntityId(), packet.getX(), packet.getY());
    }

    /**
     * Remove a player from the world
     * TODO: WorldState information in packet
     *
     * @param packet packet
     */
    private void handleRemovePlayer(S2CNetworkRemovePlayer packet) {
        if (!GameValidation.ensureInWorld(player, packet)) return;

        player.getConnection().removeGlobalNetworkPlayer(packet.entityId());
        player.getWorldState().removePlayerInWorld(packet.entityId(), true);

        GameLogging.info(this, "Player (%d) (%s) left.", packet.entityId(), packet.username());
    }

    /**
     * Updates the network players position
     *
     * @param packet packet
     */
    private void handlePlayerPosition(S2CNetworkPlayerPosition packet) {
        if (!GameValidation.ensureInWorld(player, packet)) return;

        player.getWorldState().updatePlayerPositionInWorld(packet.entityId(), packet.x(), packet.y(), packet.rotation());
    }

    /**
     * Updates the network players velocity
     *
     * @param packet packet
     */
    private void handlePlayerVelocity(S2CNetworkPlayerVelocity packet) {
        if (!GameValidation.ensureInWorld(player, packet)) return;

        player.getWorldState().updatePlayerVelocityInWorld(packet.entityId(), packet.x(), packet.y(), packet.rotation());
    }

    /**
     * Teleport the client player
     *
     * @param packet packet
     */
    private void handleSelfTeleport(S2CTeleport packet) {
        if (!GameValidation.ensureInWorld(player, packet)) return;
        player.setPosition(packet.x(), packet.y());
    }

    /**
     * Handle player teleported
     *
     * @param packet packet
     */
    private void handlePlayerTeleport(S2CTeleportPlayer packet) {
        if (!GameValidation.ensureInWorld(player, packet)) return;
        if (!GameValidation.ensureValidEntityId(player, packet.entityId())) return;
        this.player.getWorldState().player(packet.entityId()).ifPresent(player -> player.teleport(packet.x(), packet.y()));
    }

    /**
     * Handle when a player enters an interior
     *
     * @param packet packet
     */
    private void handlePlayerEnteredInterior(S2CPlayerEnteredInterior packet) {
        if (!GameValidation.ensureInWorld(player, packet)) return;
        if (!GameValidation.ensureValidEntityId(player, packet.entityId())) return;
        if (packet.type() == Interior.NONE) return;

        final NetworkPlayer mp = player.getConnection().getGlobalPlayer(packet.entityId());
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
