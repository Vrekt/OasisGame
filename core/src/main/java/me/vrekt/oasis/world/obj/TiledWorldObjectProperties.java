package me.vrekt.oasis.world.obj;

import com.badlogic.gdx.maps.MapObject;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;

/**
 * Properties for loading
 */
public final class TiledWorldObjectProperties {

    public final boolean hasCollision, interactable, offsetX, offsetY;
    public final float interactionRange, sizeX, sizeY;
    public final String key, texture;
    public final WorldInteractionType interactionType;

    public TiledWorldObjectProperties(MapObject object) {
        hasCollision = TiledMapLoader.ofBoolean(object, "hasCollision");
        interactable = TiledMapLoader.ofBoolean(object, "interactable");
        interactionRange = TiledMapLoader.ofFloat(object, "interaction_range", 3.5f);
        key = TiledMapLoader.ofString(object, "key");

        if (interactable) {
            interactionType = WorldInteractionType.ofOrNone(TiledMapLoader.ofString(object, "interaction_type"));
        } else {
            interactionType = WorldInteractionType.NONE;
        }

        texture = TiledMapLoader.ofString(object, "texture");
        sizeX = TiledMapLoader.ofFloat(object, "size_x", 1.0f);
        sizeY = TiledMapLoader.ofFloat(object, "size_y", 1.0f);
        offsetX = TiledMapLoader.ofBoolean(object, "offset_x");
        offsetY = TiledMapLoader.ofBoolean(object, "offset_y");
    }

}
