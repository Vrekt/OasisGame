package me.vrekt.oasis.save.world;

import com.google.gson.*;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.save.entity.EntitySaveProperties;
import me.vrekt.oasis.world.GameWorld;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorldSaveProperties {

    // the actual world for saving
    private transient GameWorld world;

    // world map
    private transient String worldMap;
    private transient String worldName;

    // list of entities in this world state
    private final transient List<EntitySaveProperties> entities = new ArrayList<>();

    public WorldSaveProperties(GameWorld world) {
        this.world = world;
    }

    public WorldSaveProperties() {
    }

    public List<EntitySaveProperties> getEntities() {
        return entities;
    }

    public String getWorldMap() {
        return worldMap;
    }

    public String getWorldName() {
        return worldName;
    }

    public static final class WorldSaver implements JsonSerializer<WorldSaveProperties> {
        @Override
        public JsonElement serialize(WorldSaveProperties src, Type typeOfSrc, JsonSerializationContext context) {
            final GameWorld world = src.world;
            final JsonObject base = new JsonObject();

            base.addProperty("worldIn", Asset.TUTORIAL_WORLD);
            base.addProperty("worldName", world.getWorldName());
            final JsonArray entities = new JsonArray();
            final EntitySaveProperties save = new EntitySaveProperties();

            // iterate through all entities in the world and save their states
            for (GameEntity entity : world.entities().values()) {
                save.reset(entity);
                entities.add(context.serialize(save));
            }

            base.add("entities", entities);
            return base;
        }
    }

    public static final class WorldLoader implements JsonDeserializer<WorldSaveProperties> {
        @Override
        public WorldSaveProperties deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final WorldSaveProperties save = new WorldSaveProperties();
            final JsonObject base = json.getAsJsonObject();

            // load map name
            save.worldMap = base.get("worldIn").getAsString();
            save.worldName = base.get("worldName").getAsString();

            // load all entities within the world
            final JsonArray entities = base.getAsJsonArray("entities");
            for (JsonElement element : entities) {
                final JsonObject object = element.getAsJsonObject();
                save.entities.add(context.deserialize(object, EntitySaveProperties.class));
            }
            return save;
        }
    }

}
