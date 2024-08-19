package me.vrekt.oasis.network.connection.client;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import io.netty.channel.Channel;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.network.connection.NetworkConnection;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.client.C2SPacketDisconnected;
import me.vrekt.shared.packet.client.C2SPacketPing;
import me.vrekt.shared.packet.client.C2SWorldLoaded;
import me.vrekt.shared.packet.client.interior.C2SInteriorLoaded;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.protocol.GameProtocol;

import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base connection handler for the game client.
 */
public abstract class AbstractPlayerConnection extends NetworkConnection {

    protected PlayerSP player;
    protected GameProtocol protocol;

    protected float lastUpdate;
    protected float lastPacketReceived;
    protected float lastPingTime;

    // TODO: Perhaps in the future concurrent set + have a specific packet
    // TODO: That is a network callback with a special ID
    // TODO: So we could just lookup instead of looping whole table
    // TODO: Currently, it is not worth it since executing only takes a few milliseconds
    // TODO: plus, the map is never big
    protected final IntMap<NetworkCallback> callbacks = new IntMap<>();
    private IntMap.Entries<NetworkCallback> entries;

    protected final AtomicBoolean entriesValid = new AtomicBoolean(false);

    public AbstractPlayerConnection(Channel channel, GameProtocol protocol, PlayerSP player) {
        super(channel, true);
        this.protocol = protocol;
        this.player = player;
    }

    /**
     * Indicates this connection is still alive
     */
    public void alive() {
        lastPacketReceived = GameManager.tick();
    }

    /**
     * Handle a packet
     *
     * @param packet packet
     */
    @Override
    public void handle(GamePacket packet) {
        final int packetId = packet.getId();

        loadCallbackEntries();
        boolean wasHandled = false;

        final long now = System.currentTimeMillis();
        for (Iterator<IntMap.Entry<NetworkCallback>> it = entries.iterator(); it.hasNext(); ) {
            final IntMap.Entry<NetworkCallback> entry = it.next();
            if (entry.value.responseId == packetId) {
                entry.value.run(packet);
                entry.value.free();
                it.remove();

                wasHandled = true;
                break;
            } else if (entry.value.isTimedOut(now) && !entry.value.handled) {
                entry.value.timeout();
                entry.value.free();
                it.remove();

                wasHandled = true;
            } else if (entry.value.handled) {
                entry.value.free();
                it.remove();

                wasHandled = true;

            }
        }

        if (wasHandled) return;

        // post this packet to the handling queue.
        handlers.get(packetId, notFound).handle(packet);
    }

    /**
     * Reload entries if more was added.
     */
    private void loadCallbackEntries() {
        if (entries != null) entries.reset();

        if (!entriesValid.get()) {
            entries = new IntMap.Entries<>(callbacks);
            entriesValid.set(true);
        }
    }


    /**
     * Send a packet immediately and wait for a response
     *
     * @param callback the callback handler
     */
    public void sendImmediatelyWithCallback(NetworkCallback callback) {
        final int callbackId = ThreadLocalRandom.current().nextInt(1024, 9999) + callbacks.size + 1;
        sendImmediately(callback.of);

        callback.timeCreated = System.currentTimeMillis();
        callbacks.put(callbackId, callback);

        entriesValid.set(false);
    }

    /**
     * Update the server on this players' position
     *
     * @param position position
     * @param rotation rotation
     */
    public void updatePosition(Vector2 position, int rotation) {
        sendImmediately(new C2SPacketPlayerPosition(position.x, position.y, rotation));
    }

    /**
     * Update the server on this players' velocity
     *
     * @param velocity velocity
     * @param rotation rotation
     */
    public void updateVelocity(Vector2 velocity, int rotation) {
        sendImmediately(new C2SPacketPlayerVelocity(velocity.x, velocity.y, rotation));
    }

    /**
     * Notify the server of this disconnect
     *
     * @param reason the reason or {@code null}
     */
    public void disconnect(String reason) {
        sendImmediately(new C2SPacketDisconnected(reason));
    }

    /**
     * Notify the server this clients world has loaded
     *
     * @param worldId the world ID loaded.
     */
    public void updateWorldHasLoaded(int worldId) {
        sendImmediately(new C2SWorldLoaded(worldId));
    }

    /**
     * Notify the server interior as loaded
     *
     * @param interior the interior loaded.
     */
    public void updateInteriorHasLoaded(Interior interior) {
        sendImmediately(new C2SInteriorLoaded(interior));
    }

    /**
     * Update this connection sync.
     * Finds all packets to handle and executes them on current thread.
     */
    @Override
    public void updateHandlingQueue() {
        super.updateHandlingQueue();

        // flush any queued items.
        if (GameManager.hasTimeElapsed(lastUpdate, 0.55f)) {
            virtual().execute(this::flush);
            lastUpdate = GameManager.tick();
        }

        // update network ping
        updatePing();
    }

    /**
     * Update ping time
     */
    private void updatePing() {
        if (GameManager.hasTimeElapsed(lastPingTime, 2.0f)) {
            sendToQueue(new C2SPacketPing(GameManager.tick()));
            lastPingTime = GameManager.tick();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        handlers.clear();
        callbacks.clear();
        entries = null;
    }

}
