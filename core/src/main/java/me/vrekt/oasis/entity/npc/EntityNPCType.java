package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.wrynn.WrynnEntity;
import me.vrekt.oasis.entity.npc.tutorial.MaviaTutorial;
import me.vrekt.oasis.entity.npc.tutorial.TutorialCombatDummy;
import me.vrekt.oasis.world.OasisWorld;
import me.vrekt.shared.entities.EntityType;

/**
 * All NPC entities within the game
 */
@SuppressWarnings("unchecked")
public enum EntityNPCType {

    /**
     * Mavia is a tutorial entity on Tutorial World.
     */
    MAVIA {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world) {
            return (T) new MaviaTutorial("Mavia", position, game.getPlayer(), world, game);
        }
    },
    DUMMY {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world) {
            return (T) new TutorialCombatDummy("CombatDummy", position, game.getPlayer(), world, game);
        }
    },
    WRYNN {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world) {
            return (T) new WrynnEntity("Wrynn", position, game.getPlayer(), world, game, EntityNPCType.WRYNN);
        }
    },

    INVALID {
        @Override
        public <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world) {
            return null;
        }
    };

    public abstract <T extends EntityInteractable> T create(Vector2 position, OasisGame game, OasisWorld world);

    public static EntityNPCType typeOfServer(EntityType type) {
        switch (type) {
            case INVALID:
                return INVALID;
            case TUTORIAL_COMBAT_DUMMY:
                return DUMMY;
        }
        return INVALID;
    }

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
