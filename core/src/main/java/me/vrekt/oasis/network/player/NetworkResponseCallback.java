package me.vrekt.oasis.network.player;

import me.vrekt.oasis.GameManager;
import me.vrekt.shared.packet.GamePacket;

/**
 * Represents a network callback.
 * The connection sender will register a handler with the callback that waits for the provided {@code waitResponsePacketId}
 *
 * @param callback             callback
 * @param waitResponsePacketId the packet to wait for
 * @param timeout              timeout delay
 * @param synchronize          if sync executor should be used
 * @param timeCreated          when the callback was registered
 * @param <T>                  type of
 */
public record NetworkResponseCallback<T extends GamePacket>(NetworkCallback<T> callback,
                                                            Runnable timeoutHandler,
                                                            int waitResponsePacketId,
                                                            long timeout,
                                                            boolean synchronize,
                                                            long timeCreated) {

    /**
     * Run the callback
     *
     * @param packet packet
     */
    void run(GamePacket packet) {
        if (synchronize) {
            GameManager.executeOnMainThread(() -> callback.handleResponse(packet));
        } else {
            callback.handleResponse(packet);
        }
    }

    /**
     * Execute timed out runnable
     */
    void timeOut() {
        if (synchronize) {
            GameManager.executeOnMainThread(timeoutHandler);
        } else {
            timeoutHandler.run();
        }
    }

    /**
     * @return {@code true} if this callback has expired.
     */
    boolean isTimedOut() {
        return System.currentTimeMillis() - timeCreated >= timeout;
    }

}
