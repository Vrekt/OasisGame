package me.vrekt.oasis.network.connection.client;

import com.badlogic.gdx.utils.Pool;
import com.google.common.base.Preconditions;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.GamePacket;

import java.util.function.Consumer;

/**
 * Simple builder for dispatching packets with callbacks.
 * <p>
 * The pool is probably not required, but as the game increasing in complexity
 * I'm expecting more objects to be created more frequently.
 */
public final class NetworkCallback implements Pool.Poolable {

    private static final Pool<NetworkCallback> DISPATCH_POOL = new Pool<>(3) {
        @Override
        protected NetworkCallback newObject() {
            return new NetworkCallback();
        }
    };

    /**
     * Create a new immediate send packet builder
     *
     * @param of the packet
     * @return this
     */
    public static NetworkCallback immediate(GamePacket of) {
        Preconditions.checkNotNull(of);
        return DISPATCH_POOL.obtain().of(of, true);
    }

    GamePacket of;
    int responseId;
    private boolean immediate;

    private long timeoutMs;
    long timeCreated;
    private boolean sync;

    private Runnable timeoutAction;
    private Consumer<GamePacket> acceptor;

    /**
     * Set the packet and priority
     *
     * @param of        packet
     * @param immediate if should be sent now
     * @return this
     */
    private NetworkCallback of(GamePacket of, boolean immediate) {
        this.of = of;
        this.immediate = immediate;
        return this;
    }

    public NetworkCallback waitFor(int packetId) {
        this.responseId = packetId;
        return this;
    }

    /**
     * How long in ms with no response until timed out.
     *
     * @param ms ms
     * @return this
     */
    public NetworkCallback timeoutAfter(long ms) {
        this.timeoutMs = ms;
        return this;
    }

    /**
     * Will execute the callback sync.
     *
     * @return this
     */
    public NetworkCallback sync() {
        this.sync = true;
        return this;
    }

    /**
     * Will execute the callback async.
     *
     * @return this
     */
    public NetworkCallback async() {
        this.sync = false;
        return this;
    }

    /**
     * Run an action if it timed out
     *
     * @param action action
     * @return this
     */
    public NetworkCallback ifTimedOut(Runnable action) {
        this.timeoutAction = action;
        return this;
    }

    /**
     * Provide the consumer for the final packet response
     *
     * @param apply consumer
     * @return this
     */
    public NetworkCallback accept(Consumer<GamePacket> apply) {
        this.acceptor = apply;
        return this;
    }

    /**
     * Send the packet.
     * When invoked this object is no longer use able and is recycled.
     */
    public void send() {
        if (immediate) {
            GameManager.getPlayer().getConnection().sendImmediatelyWithCallback(this);
        } else {
            GameLogging.warn(this, "Not implemented yet!");
        }
    }

    /**
     * @param now current time in ms
     * @return {@code true} if this callback has expired.
     */
    boolean isTimedOut(long now) {
        return now - timeCreated >= timeoutMs;
    }

    /**
     * Run the timeout action if there is one.
     */
    void timeout() {
        if (timeoutAction == null) return;

        if (sync) {
            GameManager.executeOnMainThread(timeoutAction);
        } else {
            timeoutAction.run();
        }
    }

    /**
     * Run the callback
     *
     * @param packet packet
     */
    public void run(GamePacket packet) {
        if (sync) {
            GameManager.executeOnMainThread(() -> acceptor.accept(packet));
        } else {
            acceptor.accept(packet);
        }
    }

    /**
     * Free this object
     * Must be invoked manually.
     */
    void free() {
        DISPATCH_POOL.free(this);
    }

    @Override
    public void reset() {
        of = null;
        immediate = false;
        timeoutMs = 0;
        sync = false;
    }
}
