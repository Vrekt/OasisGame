package me.vrekt.oasis.network.player;

import me.vrekt.shared.packet.GamePacket;

/**
 * Represents a network callback.
 * The connection sender will register a handler with the callback that waits for the provided {@code waitResponsePacketId}
 *
 * @param callback             callback
 * @param waitResponsePacketId the packet to wait for
 * @param <T>                  type of
 */
public record NetworkResponseCallback<T extends GamePacket>(NetworkCallback<T> callback, int waitResponsePacketId) {

}
