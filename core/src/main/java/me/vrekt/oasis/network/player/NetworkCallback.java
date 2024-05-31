package me.vrekt.oasis.network.player;

import me.vrekt.shared.packet.GamePacket;

/**
 * A response callback from a packet initially sent
 *
 * @param <T> type of
 */
@FunctionalInterface
public interface NetworkCallback<T extends GamePacket> {

    /**
     * Handle the response
     *
     * @param packet the packet
     */
    void handleResponse(GamePacket packet);

}
