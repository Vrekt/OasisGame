package me.vrekt.oasis.network.connection.client;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.google.common.util.concurrent.Runnables;
import io.netty.channel.Channel;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.network.connection.cb.NetworkCallback;
import me.vrekt.oasis.network.connection.cb.NetworkResponseCallback;
import me.vrekt.oasis.network.connection.NetworkConnection;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.client.C2SPacketClientLoaded;
import me.vrekt.shared.packet.client.C2SPacketDisconnected;
import me.vrekt.shared.packet.client.C2SPacketPing;
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

    protected final IntMap<NetworkResponseCallback<?>> callbacks = new IntMap<>();

    private IntMap.Entries<NetworkResponseCallback<?>> entries;
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
        lastPacketReceived = GameManager.getTick();
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
        for (Iterator<IntMap.Entry<NetworkResponseCallback<?>>> it = entries.iterator(); it.hasNext(); ) {
            final IntMap.Entry<NetworkResponseCallback<?>> entry = it.next();

            if (entry.value.waitResponsePacketId() == packetId) {
                entry.value.run(packet);
                it.remove();

                wasHandled = true;
                break;
            } else if (entry.value.isTimedOut()) {
                entry.value.timeOut();
                it.remove();
            }

        }

        if (wasHandled) return;

        if (handlers.containsKey(packetId)) {
            handlers.get(packetId, notFound).handle(packet);
        } else {
            GameLogging.warn(this, "No registered handler for %d", packetId);
        }
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
     * Send a packet immediately but wait for a response
     *
     * @param packet         packet
     * @param callback       callback
     * @param timeoutHandler timeout handler
     * @param timeout        timeout duration
     * @param <T>            type
     */
    public <T extends GamePacket> void sendImmediatelyWithCallback(GamePacket packet,
                                                                   long timeout,
                                                                   boolean synchronize,
                                                                   Runnable timeoutHandler,
                                                                   NetworkCallback<T> callback) {
        final int callbackId = ThreadLocalRandom.current().nextInt(1024, 9999) + callbacks.size + 1;
        sendImmediately(packet);

        callbacks.put(callbackId,
                new NetworkResponseCallback<>(callback,
                        timeoutHandler,
                        packet.response(),
                        timeout,
                        synchronize,
                        System.currentTimeMillis()));

        entriesValid.set(false);
    }

    /**
     * Send a packet immediately but wait for a response
     *
     * @param packet   packet
     * @param callback callback
     * @param timeout  timeout duration
     * @param <T>      type
     */
    public <T extends GamePacket> void sendImmediatelyWithCallback(GamePacket packet,
                                                                   long timeout,
                                                                   boolean synchronize,
                                                                   NetworkCallback<T> callback) {
        final int callbackId = ThreadLocalRandom.current().nextInt(1024, 9999) + callbacks.size + 1;
        sendImmediately(packet);

        callbacks.put(callbackId,
                new NetworkResponseCallback<>(callback,
                        Runnables.doNothing(),
                        packet.response(),
                        timeout,
                        synchronize,
                        System.currentTimeMillis()));


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
     */
    public void updateWorldHasLoaded() {
        sendImmediately(new C2SPacketClientLoaded(C2SPacketClientLoaded.ClientLoadedType.WORLD));
    }

    /**
     * Notify the server interior as loaded
     */
    public void updateInteriorHasLoaded() {
        sendImmediately(new C2SPacketClientLoaded(C2SPacketClientLoaded.ClientLoadedType.INTERIOR));
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
            lastUpdate = GameManager.getTick();
        }

        // update network ping
        updatePing();
    }

    /**
     * Update ping time
     */
    private void updatePing() {
        if (GameManager.hasTimeElapsed(lastPingTime, 2.0f)) {
            sendToQueue(new C2SPacketPing(GameManager.getTick()));
            lastPingTime = GameManager.getTick();
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
