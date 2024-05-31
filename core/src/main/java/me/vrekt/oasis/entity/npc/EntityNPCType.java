package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.wrynn.WrynnEntity;
import me.vrekt.oasis.world.GameWorld;

/**
 * All NPC entities within the game
 */
@SuppressWarnings("unchecked")
public enum EntityNPCType {

    WRYNN {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, GameWorld world) {
            return (T) new WrynnEntity("Wrynn", position, game.getPlayer(), world, game, EntityNPCType.WRYNN);
        }
    },

    INVALID {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, GameWorld world) {
            return null;
        }
    };

    public abstract <T extends EntityInteractable> T create(Vector2 position, OasisGame game, GameWorld world);

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
