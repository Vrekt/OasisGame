package me.vrekt.oasis.world.network;

import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.packet.server.player.S2CPacketPlayers;

/**
 * Handles validation of packets
 */
public final class NetworkValidation {

    private static final String TAG = "NetworkUtility";

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
     * Ensure the player is in the correct world for the packet
     *
     * @param playerSP player
     * @param packet   packet
     * @return {@code true} if so
     */
    public static boolean ensureWorldContext(PlayerSP playerSP, S2CPacketPlayers packet) {
        final boolean world = playerSP.isInWorld();
        final boolean interior = playerSP.isInInteriorWorld();

        final boolean result = world && (interior
                ? playerSP.getInteriorWorldIn().type().name().equals(packet.worldKey())
                : playerSP.getWorldState().getWorldName().equals(packet.worldKey()));

        if (!result) {
            GameLogging.warn(TAG, "Invalid world context for create players packet! key=%s, interior=%b", packet.worldKey(), packet.interior());
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
