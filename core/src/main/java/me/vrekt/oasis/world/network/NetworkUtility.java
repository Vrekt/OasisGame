package me.vrekt.oasis.world.network;

import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.GamePacket;

public final class NetworkUtility {

    private static final String TAG = "NetworkUtility";

    /**
     * Ensure the player is in the world
     *
     * @param player player
     * @return {@code true} if so
     */
    public static boolean ensureWorldState(PlayerSP player, GamePacket information) {
        final boolean result = player.isInWorld() && player.getWorldState() != null;
        if (!result) {
            GameLogging.warn(TAG, "Unexpected packet when the player was not in a world! id=%d", information.getId());
        }
        return result;
    }

    public static boolean ensureValidEntityId(PlayerSP player, int entityId) {
        final boolean result = player.entityId() != entityId;
        if (!result) {
            GameLogging.warn(TAG, "The server sent the same entity ID as local-player, this is a bug. id=%d", entityId);
        }
        return result;
    }



}
