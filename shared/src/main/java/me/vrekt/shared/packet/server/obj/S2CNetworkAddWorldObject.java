package me.vrekt.shared.packet.server.obj;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.shared.packet.GamePacket;

import java.util.HashMap;
import java.util.Map;

/**
 * A network object that should be added.
 */
public final class S2CNetworkAddWorldObject extends GamePacket {

    public static final int PACKET_ID = 3000_10;

    private WorldNetworkObject object;

    public S2CNetworkAddWorldObject(ByteBuf buffer) {
        super(buffer);
    }

    public S2CNetworkAddWorldObject(WorldNetworkObject object) {
        Preconditions.checkNotNull(object);
        this.object = object;
    }

    public WorldNetworkObject object() {
        return object;
    }

    @Override
    public int getId() {
        return PACKET_ID;
    }

    @Override
    public void encode() {
        writeId();

        buffer.writeInt(object.type().ordinal());
        buffer.writeBoolean(object.key() != null);
        if (object.key() != null) writeString(object.key());
        writeVector2(object.position().x, object.position().y);
        writeVector2(object.size().x, object.size().y);
        buffer.writeInt(object.objectId());

        // if the object has a map object.
        final boolean mapped = object.mapObject() != null;
        buffer.writeBoolean(mapped);

        if (mapped) {
            final MapObject mp = object.mapObject();
            // copy properties into another map for writing.
            final Map<String, Object> props = new HashMap<>();
            mp.getProperties()
                    .getKeys()
                    .forEachRemaining(key -> props.put(key, mp.getProperties().get(key)));

            // total size of all properties.
            buffer.writeInt(props.size());
            // write each property and key
            props.forEach(this::writeValue);
        }
    }

    /**
     * Write key and value pair
     *
     * @param key key
     * @param v   v
     */
    private void writeValue(String key, Object v) {
        writeString(key);
        write(v);
    }

    /**
     * Write the object value
     *
     * @param v v
     */
    private void write(Object v) {
        switch (v) {
            case Integer i -> {
                writeType(ObjectValueType.INT);
                buffer.writeInt(i);
            }
            case Float a -> {
                writeType(ObjectValueType.FLOAT);
                buffer.writeFloat(a);
            }
            case String s -> {
                writeType(ObjectValueType.STRING);
                writeString(s);
            }
            case Boolean b -> {
                writeType(ObjectValueType.BOOL);
                buffer.writeBoolean(b);
            }
            case null, default -> GameLogging.warn(this, "Unsupported object type! %s", object.getClass().getName());
        }
    }

    /**
     * Write object type.
     *
     * @param type type of
     */
    private void writeType(ObjectValueType type) {
        buffer.writeInt(type.ordinal());
    }

    @Override
    public void decode() {
        final int ordinal = buffer.readInt();
        // type
        final WorldInteractionType type = WorldInteractionType.values()[ordinal];
        final boolean keyed = buffer.readBoolean();
        // key, if any.
        String key = null;
        if (keyed) {
            key = readString();
        }

        // read pos + size
        final float x = buffer.readFloat();
        final float y = buffer.readFloat();
        final Vector2 position = new Vector2(x, y);

        final float w = buffer.readFloat();
        final float h = buffer.readFloat();
        final Vector2 size = new Vector2(w, h);

        // read map object if any.
        final int id = buffer.readInt();
        final boolean mapped = buffer.readBoolean();
        MapObject mp;

        if (mapped) {
            final MapProperties properties = new MapProperties();
            final int length = buffer.readInt();
            for (int i = 0; i < length; i++) {
                final String propertiesKey = readString();
                final int index = buffer.readInt();
                final ObjectValueType propertyType = ObjectValueType.values()[index];
                readInto(properties, propertiesKey, propertyType);
            }
            mp = new MapObject();
            mp.getProperties().putAll(properties);
            properties.clear();
        } else {
            mp = null;
        }

        object = new WorldNetworkObject(type, key, position, size, id, mp);
    }

    /**
     * Read property into map
     *
     * @param properties properties map
     * @param key        key
     * @param type       type
     */
    private void readInto(MapProperties properties, String key, ObjectValueType type) {
        switch (type) {
            case INT -> properties.put(key, buffer.readInt());
            case FLOAT -> properties.put(key, buffer.readFloat());
            case STRING -> properties.put(key, readString());
            case BOOL -> properties.put(key, buffer.readBoolean());
        }
    }

    private enum ObjectValueType {
        INT, FLOAT, STRING, BOOL
    }
}
