package me.vrekt.shared.packet.client.interior;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.shared.packet.GamePacket;
import me.vrekt.shared.protocol.Packets;

/**
 * Player finished loading an interior
 * At this point start syncing entities and objects
 */
public final class C2SInteriorLoaded extends GamePacket {

    private Interior type;

    public C2SInteriorLoaded(ByteBuf buffer) {
        super(buffer);
    }

    public C2SInteriorLoaded(Interior type) {
        Preconditions.checkNotNull(type);
        this.type = type;
    }

    /**
     * @return the interior type loaded.
     */
    public Interior type() {
        return type;
    }

    @Override
    public int getId() {
        return Packets.C2S_INTERIOR_LOADED;
    }

    @Override
    public void encode() {
        writeId();
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void decode() {
        if (buffer.isReadable(4)) type = Interior.of(buffer.readInt());
    }
}
