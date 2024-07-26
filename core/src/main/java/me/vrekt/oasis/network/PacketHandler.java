package me.vrekt.oasis.network;

import me.vrekt.shared.packet.GamePacket;

@FunctionalInterface
public interface PacketHandler {

    /**
     * Handle the packet
     *
     * @param packet the packet
     */
    void handle(GamePacket packet);

}
