package me.vrekt.oasis.network.utility;

import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.GamePacket;

/**
 * Handles validation of packets
 */
public final class GameValidation {

    private static final String TAG = "NetworkUtility";
    public static long mainThreadId;

    /**
     * Ensure whatever is calling is on the main thread
     *
     * @return {@code true} if so
     */
    public static boolean ensureMainThread() {
        if (Thread.currentThread().threadId() != mainThreadId) {
            GameLogging.warn("NetworkValidation", "Not in main thread! thread=%s", Thread.currentThread().getName());
            return false;
        }
        return true;
    }

    /**
     * Ensure whatever is calling is on the main thread
     */
    public static void ensureMainThreadOrThrow() {
        if (Thread.currentThread().threadId() != mainThreadId) {
            throw new UnsupportedOperationException("Main thread bugcheck.");
        }
    }

    /**
     * Ensure on the correct provided thread ID
     *
     * @param threadId thread ID
     */
    public static void ensureOnThread(long threadId) {
        if (Thread.currentThread().threadId() != threadId)
            throw new UnsupportedOperationException();
    }

    /**
     * Ensure the player is in the world
     *
     * @param player player
     * @return {@code true} if so
     */
    public static boolean ensureInWorld(PlayerSP player, GamePacket information) {
        final boolean result = player.isInWorld() && player.getWorldState() != null;
        if (!result) {
            GameLogging.warn(TAG, "Unexpected packet when the player was not in a world! id=%d", information.getId());
        }
        return result;
    }

    /**
     * Ensure the player is in the world
     *
     * @param player player
     * @return {@code true} if so
     */
    public static boolean ensureInWorld(PlayerSP player) {
        final boolean result = player.isInWorld() && player.getWorldState() != null;
        if (!result) {
            GameLogging.warn(TAG, "Unexpected packet when the player was not in a world!");
        }
        return result;
    }

    /**
     * Ensure the entity ID sent is not ourselves, this indicates a server side bug
     *
     * @param player   player
     * @param entityId entity  ID
     * @return {@code true} if the entity ID is not ours.
     */
    public static boolean ensureValidEntityId(PlayerSP player, int entityId) {
        final boolean result = player.entityId() != entityId;
        if (!result) {
            GameLogging.warn(TAG, "The server sent the same entity ID as local-player, this is a bug. id=%d", entityId);
        }
        return result;
    }


}
