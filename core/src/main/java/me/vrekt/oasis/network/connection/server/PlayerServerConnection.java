package me.vrekt.oasis.network.connection.server;

import com.badlogic.gdx.math.MathUtils;
import io.netty.channel.Channel;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.network.IntegratedGameServer;
import me.vrekt.oasis.network.connection.NetworkConnection;
import me.vrekt.oasis.network.server.entity.player.ServerPlayer;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.network.server.world.obj.ServerBreakableWorldObject;
import me.vrekt.oasis.network.server.world.obj.ServerWorldObject;
import me.vrekt.oasis.utility.logging.ServerLogging;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.shared.packet.client.*;
import me.vrekt.shared.packet.client.player.C2SChatMessage;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.packet.server.obj.S2CDestroyWorldObjectResponse;
import me.vrekt.shared.packet.server.player.*;
import me.vrekt.shared.protocol.ProtocolDefaults;

/**
 * Connection for a server player
 */
public final class PlayerServerConnection extends NetworkConnection {

    private final IntegratedGameServer server;

    private boolean disconnected;
    private ServerPlayer player;

    private boolean joiningWorld;

    public PlayerServerConnection(Channel channel, IntegratedGameServer server) {
        super(channel, false);
        this.server = server;
        this.isConnected = true;

        attachAll();

        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
    }

    /**
     * Attach all relevant packet handlers
     */
    private void attachAll() {
        attach(C2SPacketAuthenticate.PACKET_ID, packet -> handleAuthentication((C2SPacketAuthenticate) packet));
        attach(C2SPacketPing.PACKET_ID, packet -> handlePing((C2SPacketPing) packet));
        attach(C2SPacketJoinWorld.PACKET_ID, packet -> handleJoinWorld((C2SPacketJoinWorld) packet));
        attach(C2SPacketClientLoaded.PACKET_ID, packet -> handlePlayerClientLoaded((C2SPacketClientLoaded) packet));
        attach(C2SPacketDisconnected.PACKET_ID, packet -> handleDisconnected((C2SPacketDisconnected) packet));
        attach(C2SPacketPlayerPosition.PACKET_ID, packet -> handlePlayerPosition((C2SPacketPlayerPosition) packet));
        attach(C2SPacketPlayerVelocity.PACKET_ID, packet -> handlePlayerVelocity((C2SPacketPlayerVelocity) packet));
        attach(C2SKeepAlive.PACKET_ID, packet -> handleKeepAlive((C2SKeepAlive) packet));
        attach(C2SChatMessage.PACKET_ID, packet -> handleChatMessage((C2SChatMessage) packet));
        attach(C2SPacketAuthenticate.PACKET_ID, packet -> handleAuthentication((C2SPacketAuthenticate) packet));
        attach(C2SPacketPing.PACKET_ID, packet -> handlePing((C2SPacketPing) packet));
        attach(C2SInteractWithObject.PACKET_ID, packet -> handleInteractWithObject((C2SInteractWithObject) packet));
        attach(C2SDestroyWorldObject.PACKET_ID, packet -> handleDestroyWorldObject((C2SDestroyWorldObject) packet));
    }

    /**
     * @return {@code true} if the player is in a world.
     */
    private boolean isValid() {
        return player != null && player.isInWorld();
    }

    /**
     * Handle authentication to the server
     * TODO: Server authentication
     *
     * @param packet the packet
     */
    private void handleAuthentication(C2SPacketAuthenticate packet) {
        sendImmediately(new S2CPacketAuthenticate(true, ProtocolDefaults.PROTOCOL_NAME, ProtocolDefaults.PROTOCOL_VERSION));
    }

    /**
     * Handle a player disconnect
     *
     * @param packet the packet
     */
    private void handleDisconnected(C2SPacketDisconnected packet) {
        server.disconnectPlayer(player, packet.reason());
    }

    /**
     * Handle ping time
     *
     * @param packet packet
     */
    private void handlePing(C2SPacketPing packet) {
        sendImmediately(new S2CPacketPing(packet.tick()));
    }

    /**
     * Handle keep alive
     *
     * @param packet packet
     */
    private void handleKeepAlive(C2SKeepAlive packet) {
        // TODO:
    }

    /**
     * Handling joining a world.
     *
     * @param packet the packet
     */
    private void handleJoinWorld(C2SPacketJoinWorld packet) {
        // prevent loaded worlds from being joined or if the player is already joining one.
        if (!server.isWorldReady() || (player != null && joiningWorld)) {
            sendImmediately(new S2CPacketWorldInvalid(packet.worldId(), "World not found."));
            return;
        }

        final int entityId = server.playerConnected(this);

        final int worldId = packet.worldId();
        final ServerWorld world = server.getWorld(worldId);
        player = new ServerPlayer(this, server);
        player.setName(packet.username());
        player.setWorldIn(world);
        player.setEntityId(entityId);
        sendImmediately(new S2CPacketJoinWorld(worldId, player.entityId(), world.mspt()));

        joiningWorld = true;
        ServerLogging.info(this, "Player: %s connected", player.name());
    }

    /**
     * Handle when the player has loaded their world
     * TODO: Ensure correct world ID.
     *
     * @param packet packet
     */
    private void handlePlayerClientLoaded(C2SPacketClientLoaded packet) {
        if (joiningWorld) {
            joiningWorld = false;

            player.world().spawnPlayerInWorld(player);
            isServerPlayerReady = true;
        }
    }

    /**
     * Update player position in the server
     *
     * @param packet packet
     */
    public void handlePlayerPosition(C2SPacketPlayerPosition packet) {
        if (isValid()) player.updatePosition(packet.x(), packet.y(), packet.rotation());
    }

    /**
     * Update player velocity in the server
     *
     * @param packet packet
     */
    public void handlePlayerVelocity(C2SPacketPlayerVelocity packet) {
        if (isValid()) player.updateVelocity(packet.x(), packet.y(), packet.rotation());
    }

    /**
     * Handle chat message
     *
     * @param packet packet
     */
    public void handleChatMessage(C2SChatMessage packet) {
        if (packet.message() == null || packet.message().length() > 150 || !isValid()) {
            ServerLogging.warn(this, "Invalid chat message was sent");
            return;
        }

        player.world().broadcastImmediatelyExcluded(packet.from(), new S2CChatMessage(packet.from(), packet.message()));
    }

    /**
     * Handle player interact with an object, ensure its valid.
     *
     * @param packet object
     */
    private void handleInteractWithObject(C2SInteractWithObject packet) {
        if (isValid()) {
            final ServerWorldObject worldObject = player.world().getWorldObject(packet.objectId());
            if (worldObject != null) {

                // broadcasts the relevant packets, also notify host.
                if (worldObject.interact(player)) server.handler().handleObjectInteraction(player, worldObject);
            }
        }
    }

    /**
     * Handle destroying an object
     * This is not decided by the client, must be validated here
     *
     * @param packet packet
     */
    private void handleDestroyWorldObject(C2SDestroyWorldObject packet) {
        if (isValid()) {
            final ServerWorldObject worldObject = player.world().getWorldObject(packet.objectId());

            // ensure object was touched and we are the same player.
            if (worldObject != null
                    && worldObject.wasInteracted()
                    && worldObject.interactedId() == player.entityId()
                    && worldObject.hasInteractionTimeElapsed()) {

                // player can break this object.
                player.getConnection().sendImmediately(new S2CDestroyWorldObjectResponse(worldObject.objectId(), true));
                // notify others too.
                server.handler().handleObjectDestroyed(player, worldObject);
                worldObject.destroy(player);

                // this object will generate a random item to drop
                if (worldObject.type() == WorldInteractionType.BREAKABLE_OBJECT) {
                    generateRandomItem(worldObject.asBreakable());
                }

            } else {
                player.getConnection().sendImmediately(new S2CDestroyWorldObjectResponse(packet.objectId(), false));
            }
        }
    }

    /**
     * Generate a random item and spawn it in
     * Host will see first, a bit unfair but hey lets all be nice!
     *
     * @param object object
     */
    private void generateRandomItem(ServerBreakableWorldObject object) {
        final float unlucky = 0.1f;
        final float multipleItemsChance = 0.2f;

        final boolean isUnlucky = MathUtils.randomBoolean(unlucky);
        if (isUnlucky) return;

        final int amount = MathUtils.randomBoolean(multipleItemsChance) ? MathUtils.random(1, 3) : 1;
        final Item item = ItemRegistry.createRandomItemWithRarity(object.assignedRarity(), amount);

        server.handler().createDroppedItemAndBroadcast(item, object.position().add(0.25f, 0.25f));
    }

    @Override
    public void channelClosed(Throwable ifAny) {
        if (ifAny != null) ServerLogging.exceptionThrown(this, "Connection closed with exception", ifAny);
        if (isValid()) {
            if (!disconnected)
                server.disconnectPlayer(player, ifAny == null ? "Channel unregistered" : ifAny.getLocalizedMessage());
        } else {
            if (!disconnected) disconnect();
        }
    }

    @Override
    public void disconnect() {
        if (disconnected) return;
        this.disconnected = true;

        if (channel.isOpen()) channel.close();
    }

}
