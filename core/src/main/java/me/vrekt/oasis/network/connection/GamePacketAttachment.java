package me.vrekt.oasis.network.connection;

import com.badlogic.gdx.utils.Pool;
import me.vrekt.shared.packet.GamePacket;

import java.util.function.Consumer;

/**
 * Wraps the packet handler and relevant packet for handling.
 */
public final class GamePacketAttachment implements Pool.Poolable {

    static final Pool<GamePacketAttachment> POOL = new Pool<>() {
        @Override
        protected GamePacketAttachment newObject() {
            return new GamePacketAttachment();
        }
    };

    public Consumer<GamePacket> handler;
    public GamePacket packet;

    private GamePacketAttachment() {

    }

    /**
     * Populate this attachment
     *
     * @param handler handler
     * @param packet  packet
     */
    void populate(Consumer<GamePacket> handler, GamePacket packet) {
        this.handler = handler;
        this.packet = packet;
    }

    void free() {
        POOL.free(this);
    }

    @Override
    public void reset() {
        this.handler = null;
        this.packet = null;
    }
}
