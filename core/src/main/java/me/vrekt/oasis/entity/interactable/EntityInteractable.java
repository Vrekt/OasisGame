package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.world.entity.InteractableEntitySave;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends EntitySpeakable implements Savable<InteractableEntitySave> {

    protected final OasisGame game;

    public EntityInteractable(String name, Vector2 position, PlayerSP player, GameWorld worldIn, OasisGame game) {
        super(player);

        setName(name);
        setAngle(EntityRotation.DOWN.ordinal());
        getTransformComponent().position.set(position);
        setNearby(false);

        this.worldIn = worldIn;
        this.game = game;
    }

    @Override
    public void load(InteractableEntitySave save) {
        setPosition(save.position());
        rotation = save.rotation();

        // if dialog was not implemented yet for the saved entity
        if (dialogue != null)
            setActiveEntry(dialogue.setStageAndUpdate(save.dialogueStage(), save.dialogueStageIndex()));
    }

    @Override
    public EntityInteractable asInteractable() {
        return this;
    }

    /**
     * Transfer this entity
     *
     * @param interior interior
     */
    public void transfer(GameWorldInterior interior) {
        worldIn.transferEntityTo(this, interior);
    }

}
