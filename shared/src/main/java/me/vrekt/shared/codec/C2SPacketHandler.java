package me.vrekt.shared.codec;

import me.vrekt.shared.packet.GamePacket;

/**
 * Client -> Server packet handler
 */
public interface C2SPacketHandler {

    /**
     * Handle the packet
     *
     * @param packet the packet
     */
    void handle(GamePacket packet);
}
