package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.world.GameWorld;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends EntitySpeakable {

    protected final OasisGame game;
    protected EntityNPCType type;

    public EntityInteractable(String name, Vector2 position, PlayerSP player, GameWorld worldIn, OasisGame game, EntityNPCType type) {
        super(player);

        setName(name);
        setAngle(EntityRotation.DOWN.ordinal());
        setPosition(position, true);
        setNearby(false);

        this.worldIn = worldIn;
        this.game = game;
        this.type = type;
    }

    public EntityNPCType getType() {
        return type;
    }


    /**
     * Update active texture based on entity rotation
     */
    protected void updateRotationTextureState() {
        if (previousRotation != rotation) {
            if (hasTexturePart(rotation)) activeEntityTexture = getTexturePart(rotation.name());
            previousRotation = rotation;
        }
    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public EntityInteractable asInteractable() {
        return this;
    }
}
