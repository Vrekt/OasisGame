package me.vrekt.shared.codec;

import me.vrekt.shared.packet.GamePacket;

/**
 * Server -> client packet handler
 */
public interface S2CPacketHandler {
    /**
     * Handle the packet
     *
     * @param packet the packet
     */
    void handle(GamePacket packet);

}
