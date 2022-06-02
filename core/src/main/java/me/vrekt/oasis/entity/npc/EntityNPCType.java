package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.npc.tutorial.MaviaTutorial;
import me.vrekt.oasis.world.OasisWorld;

/**
 * All NPC entities within the game
 */
public enum EntityNPCType {

    /**
     * Mavia is a tutorial entity on Tutorial World.
     */
    @SuppressWarnings("unchecked")
    MAVIA {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world) {
            return (T) new MaviaTutorial("Mavia", position, game.getPlayer(), world, game);
        }
    };

    public abstract <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world);

    /**
     * Find the type of map object
     *
     * @param object the object
     * @return the entity or {@code  null} if not found
     */
    public static EntityNPCType findType(MapObject object) {
        try {
            return valueOf(object.getProperties().get("entity_type", null, String.class));
        } catch (Exception error) {
            return null;
        }
    }

}
