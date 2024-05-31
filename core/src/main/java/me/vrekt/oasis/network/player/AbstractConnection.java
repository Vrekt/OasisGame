package me.vrekt.oasis.network.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Queue;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.client.*;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerPosition;
import me.vrekt.shared.packet.client.player.C2SPacketPlayerVelocity;
import me.vrekt.shared.protocol.GameProtocol;
import me.vrekt.shared.codec.S2CPacketHandler;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Base connection handler
 */
public abstract class AbstractConnection implements S2CPacketHandler, Disposable {

    protected PlayerSP player;

    protected final Channel channel;
    protected GameProtocol protocol;
    protected boolean isConnected;

    // 50 ms hopefully
    protected float updateInterval = 0.1f;
    protected float lastUpdate;
    protected float lastPacketReceived;

    protected float lastPingTime;

    protected final ConcurrentLinkedQueue<GamePacket> sendQueue = new ConcurrentLinkedQueue<>();
    protected final Queue<AttachmentHandleResult> handlingQueue = new Queue<>();

    protected final ExecutorService virtual = Executors.newVirtualThreadPerTaskExecutor();
    protected final IntMap<AttachmentHandle> handlers = new IntMap<>();
    protected final IntMap<NetworkResponseCallback<?>> callbacks = new IntMap<>();

    private IntMap.Entries<NetworkResponseCallback<?>> entries;
    protected final AtomicBoolean entriesValid = new AtomicBoolean(false);

    public AbstractConnection(Channel channel, GameProtocol protocol, PlayerSP player) {
        this.channel = channel;
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
     * Attach a handler (sync)
     *
     * @param packet   packet
     * @param consumer consumer
     */
    public void attach(int packet, Consumer<GamePacket> consumer) {
        handlers.put(packet, new AttachmentHandle(consumer, true));
    }

    /**
     * Attach a handler (async)
     *
     * @param packet   packet
     * @param consumer consumer
     */
    public void attachAsync(int packet, Consumer<GamePacket> consumer) {
        handlers.put(packet, new AttachmentHandle(consumer, false));
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
                entry.value.callback().handleResponse(packet);
                it.remove();

                wasHandled = true;
                break;
            }
        }

        if (wasHandled) return;

        if (handlers.containsKey(packetId)) {
            handlers.get(packetId).handle(packet);
        } else {
            GameLogging.warn(this, "No registered handler for %d", packetId);
        }
    }

    /**
     * Reload entries if more was added.
     */
    private void loadCallbackEntries() {
        if (!entriesValid.get()) {
            entries = new IntMap.Entries<>(callbacks);
            entriesValid.set(true);
        }
    }

    /**
     * Send a packet immediately but wait for a response
     *
     * @param packet   packet
     * @param callback callback
     * @param <T>      type
     */
    public <T extends GamePacket> void sendImmediatelyWithCallback(GamePacket packet, NetworkCallback<T> callback) {
        final int callbackId = ThreadLocalRandom.current().nextInt(1024, 9999) + callbacks.size + 1;
        sendImmediately(packet);

        callbacks.put(callbackId, new NetworkResponseCallback<>(callback, packet.response()));
        entriesValid.set(false);
    }

    /**
     * Update the server on this players' position
     *
     * @param x        x position
     * @param y        y position
     * @param rotation rotation
     */
    public void updatePosition(float x, float y, float rotation) {
        sendImmediately(new C2SPacketPlayerPosition(x, y, rotation));
    }

    /**
     * Update the server on this players' position
     *
     * @param position position
     * @param rotation rotation
     */
    public void updatePosition(Vector2 position, float rotation) {
        sendImmediately(new C2SPacketPlayerPosition(position.x, position.y, rotation));
    }

    /**
     * Update the server on this players' velocity.
     *
     * @param x        x velocity
     * @param y        y velocity
     * @param rotation rotation
     */
    public void updateVelocity(float x, float y, float rotation) {
        sendImmediately(new C2SPacketPlayerVelocity(x, y, rotation));
    }

    /**
     * Update the server on this players' velocity
     *
     * @param velocity velocity
     * @param rotation rotation
     */
    public void updateVelocity(Vector2 velocity, float rotation) {
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
        this.sendImmediately(new C2SPacketWorldLoaded());
    }

    /**
     * Attempt to join a world
     *
     * @param world    the world
     * @param username the username of this player
     * @param time     current client tick and or time
     */
    public void joinWorld(String world, String username, long time) {
        this.sendImmediately(new C2SPacketJoinWorld(world, username, time));
    }

    /**
     * Attempt to join a world.
     * unlike {@code joinWorld(world, username, time)} this method substitutes time for {@code System.currentTimeMillis}
     *
     * @param world    the world
     * @param username the username of this player
     */
    public void joinWorld(String world, String username) {
        this.sendImmediately(new C2SPacketJoinWorld(world, username, System.currentTimeMillis()));
    }

    /**
     * @return if connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * @return the alloc of this channel for sending packets
     */
    public ByteBufAllocator alloc() {
        return channel.alloc();
    }


    /**
     * Will send the provided {@code packet} to the send queue.
     * Used for situations where priority is low.
     *
     * @param packet the packet
     */
    public void sendToQueue(GamePacket packet) {
        packet.alloc(alloc());
        sendQueue.add(packet);
    }


    /**
     * Send the provided {@code packet} immediately.
     *
     * @param packet the packet
     */
    public void sendImmediately(GamePacket packet) {
        packet.alloc(alloc());
        channel.writeAndFlush(packet);
    }

    /**
     * Update this sync.
     */
    public void updateSync() {
        if (GameManager.hasTimeElapsed(lastUpdate, updateInterval)) {
            virtual.execute(this::flush);

            if (!handlingQueue.isEmpty()) {
                for (Iterator<AttachmentHandleResult> it = handlingQueue.iterator(); it.hasNext(); ) {
                    final AttachmentHandleResult result = it.next();
                    result.handler.accept(result.packet);
                    it.remove();
                }
            }

            updatePing();
            lastUpdate = GameManager.getTick();
        }
    }

    /**
     * Update ping time
     */
    private void updatePing() {
        if (GameManager.hasTimeElapsed(lastPingTime, 2.0f)) {
            sendToQueue(new C2SPacketPing(System.currentTimeMillis()));
            lastPingTime = GameManager.getTick();
        }
    }

    /**
     * Flush the current packet queue.
     */
    public void flush() {
        if (sendQueue.isEmpty()) return;

        for (GamePacket packet = sendQueue.poll(); packet != null; packet = sendQueue.poll()) {
            channel.write(packet);
        }

        channel.flush();
    }

    public void close() {
        channel.close();
    }

    @Override
    public void dispose() {
        virtual.shutdownNow().forEach(r -> GameLogging.info(this, "Cancelled virtual runnable task"));
        flush();
        close();
    }

    /**
     * Packet attachment handler
     */
    protected final class AttachmentHandle {
        final Consumer<GamePacket> handler;
        final boolean isSync;

        AttachmentHandle(Consumer<GamePacket> handler, boolean isSync) {
            this.handler = handler;
            this.isSync = isSync;
        }

        void handle(GamePacket packet) {
            // not ready yet so no benefit from stalling around
            if (!GameManager.game().isGameReady()) {
                handler.accept(packet);
                return;
            }

            if (isSync) {
                handlingQueue.addLast(new AttachmentHandleResult(handler, packet));
            } else {
                virtual.execute(() -> handler.accept(packet));
            }
        }
    }

    /**
     * TODO: POOL?
     *
     * @param handler
     * @param packet
     */
    protected record AttachmentHandleResult(Consumer<GamePacket> handler, GamePacket packet) {
    }

}
