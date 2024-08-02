package me.vrekt.oasis.network.connection;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.util.internal.PlatformDependent;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.network.PacketHandler;
import me.vrekt.oasis.network.utility.NetworkValidation;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.utility.logging.ServerLogging;
import me.vrekt.shared.packet.GamePacket;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Handles all the basics of a connection.
 * Handling -> sync or async
 */
public abstract class NetworkConnection implements PacketHandler, Disposable {

    protected final HandlerAttachment notFound;

    protected final Queue<GamePacketAttachment> handlingQueue = PlatformDependent.newMpscQueue();
    protected final Queue<GamePacket> sendQueue = PlatformDependent.newSpscQueue();
    protected final IntMap<HandlerAttachment> handlers = new IntMap<>();

    protected final Channel channel;
    protected final boolean isClient;

    protected boolean isServerPlayerReady;
    protected boolean isConnected;

    protected long lastActive;

    public NetworkConnection(Channel channel, boolean isClient) {
        Preconditions.checkNotNull(channel);
        this.channel = channel;
        this.isClient = isClient;

        notFound = new HandlerAttachment(packet -> {
            if (isClient) {
                GameLogging.info(this, "Unhandled packet %d was not registered.", packet.getId());
            } else {
                ServerLogging.info(this, "Unhandled packet %d was not registered.", packet.getId());
            }
        }, false);
    }

    @Override
    public void handle(GamePacket packet) {
        lastActive = System.currentTimeMillis();
        handlers.get(packet.getId(), notFound).handle(packet);
    }

    /**
     * @return if connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * @return when this connection was last active.
     */
    public long lastActive() {
        return lastActive;
    }

    /**
     * Channel was closed, either unregistered or an exception was thrown.
     *
     * @param ifAny the exception, {@code null} if none.
     */
    public void channelClosed(Throwable ifAny) {
        if (ifAny != null) GameLogging.exceptionThrown(this, "Connection closed with exception", ifAny);
    }

    /**
     * Attach a handler (sync)
     *
     * @param packet   packet
     * @param consumer consumer
     */
    public void attach(int packet, Consumer<GamePacket> consumer) {
        handlers.put(packet, new HandlerAttachment(consumer, true));
    }

    /**
     * Attach a handler (async)
     *
     * @param packet   packet
     * @param consumer consumer
     */
    public void attachAsync(int packet, Consumer<GamePacket> consumer) {
        handlers.put(packet, new HandlerAttachment(consumer, false));
    }

    /**
     * Handles executing each task within the packet handling queue
     * Packets are posted to this queue when they arrive.
     * If the attachment is marked sync, it will be posted to this queue
     * Otherwise it will be handled immediately.
     */
    public void updateHandlingQueue() {
        GamePacketAttachment attachment;
        while ((attachment = handlingQueue.poll()) != null) {
            attachment.handler.accept(attachment.packet);

            attachment.free();
        }
    }

    /**
     * Flush this connection.
     */
    public void flush() {
        if (sendQueue.isEmpty()) return;

        GamePacket packet;
        while ((packet = sendQueue.poll()) != null) {
            channel.write(packet);
        }

        channel.flush();
    }

    /**
     * Disconnect
     */
    public void disconnect() {

    }

    /**
     * Close the channel.
     */
    public void close() {
        channel.close();
        isConnected = false;
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
        Preconditions.checkNotNull(packet);
        if (!NetworkValidation.ensureMainThread())
            throw new UnsupportedOperationException("No main thread @ send queue.");

        packet.alloc(alloc());
        sendQueue.add(packet);
    }

    /**
     * Send the provided {@code packet} immediately.
     *
     * @param packet the packet
     */
    public void sendImmediately(GamePacket packet) {
        Preconditions.checkNotNull(packet);

        packet.alloc(alloc());
        channel.writeAndFlush(packet);
    }

    /**
     * @return virtual async service
     */
    protected ExecutorService virtual() {
        return GameManager.game().executor();
    }

    /**
     * Get a new attachment
     *
     * @param consumer consumer
     * @param packet   packet
     * @return the new attachment
     */
    protected GamePacketAttachment get(Consumer<GamePacket> consumer, GamePacket packet) {
        Preconditions.checkNotNull(consumer);
        Preconditions.checkNotNull(packet);
        final GamePacketAttachment attachment = GamePacketAttachment.POOL.obtain();
        attachment.populate(consumer, packet);
        return attachment;
    }

    @Override
    public void dispose() {
        flush();
        close();
    }

    /**
     * Packet attachment handler
     */
    protected final class HandlerAttachment {
        final Consumer<GamePacket> handler;
        final boolean isSync;

        public HandlerAttachment(Consumer<GamePacket> handler, boolean isSync) {
            Preconditions.checkNotNull(handler);

            this.handler = handler;
            this.isSync = isSync;
        }

        public void handle(GamePacket packet) {
            // if we are the client and the game is not ready, continue on.
            if (isClient && !GameManager.game().isGameReady()) {
                handler.accept(packet);
                return;
            } else if (!isClient && !isServerPlayerReady) {
                // if we are a server player, and we are not in a world yet, continue.
                handler.accept(packet);
                return;
            }

            if (isSync) {
                handlingQueue.offer(get(handler, packet));
            } else {
                virtual().execute(() -> handler.accept(packet));
            }
        }
    }

}
