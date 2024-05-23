package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.EntitySteerable;
import me.vrekt.oasis.ai.components.AiComponent;
import me.vrekt.oasis.ai.utility.SimpleVectorLocation;
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

    protected Array<AiComponent> aiComponents = new Array<>();
    protected boolean isPaused;
    protected float pauseTime, pauseForTime;

    protected final OasisGame game;
    protected EntityNPCType type;

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

    /**
     * Add an AI component
     */
    protected void addAiComponent(AiComponent component) {
        aiComponents.add(component);
    }

    /**
     * Update AI components if it is not paused
     *
     * @param delta the graphics** delta time
     */
    protected void updateAi(float delta) {
        if (!isPaused) {
            GdxAI.getTimepiece().update(delta);
            for (AiComponent component : aiComponents) {
                component.update(GdxAI.getTimepiece().getDeltaTime());
            }
        } else {
            isPaused = !GameManager.hasTimeElapsed(pauseTime, pauseForTime);
        }
    }

    /**
     * Pause AI for X seconds
     *
     * @param seconds seconds
     */
    protected void pauseFor(float seconds) {
        isPaused = true;
        pauseTime = GameManager.getTick();
        pauseForTime = seconds;
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
