package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.utility.SimpleVectorLocation;
import me.vrekt.oasis.ai.EntitySteerable;
import me.vrekt.oasis.ai.components.AiComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.world.GameWorld;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends EntitySpeakable {

    // entity AI steering
    protected EntitySteerable entitySteering;
    protected SimpleVectorLocation location;

    protected final OasisGame game;
    protected EntityNPCType type;
    protected Rectangle bounds;

    public EntityInteractable(String name, Vector2 position, PlayerSP player, GameWorld worldIn, OasisGame game, EntityNPCType type) {
        super(player);

        setName(name);
        setAngle(EntityRotation.DOWN.ordinal());
        setBodyPosition(position, getAngle(), true);
        setNearby(false);

        this.worldIn = worldIn;
        this.game = game;
        this.type = type;
    }

    public EntityNPCType getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Add arrival AI component
     */
    protected void addAiComponent(AiComponent component) {

    }

    /**
     * Update active texture based on entity rotation
     */
    protected void updateRotationTextureState() {
        if (previousRotation != rotation) {
            activeEntityTexture = getTexturePart(rotation.name());
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
