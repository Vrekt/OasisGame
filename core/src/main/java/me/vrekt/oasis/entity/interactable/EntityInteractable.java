package me.vrekt.oasis.entity.interactable;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.save.Loadable;
import me.vrekt.oasis.save.Savable;
import me.vrekt.oasis.save.world.entity.EntitySaveState;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;

/**
 * Represents an NPC within the game
 */
public abstract class EntityInteractable extends EntitySpeakable implements
        Savable<EntitySaveState>,
        Loadable<EntitySaveState> {

    protected final OasisGame game;

    public EntityInteractable(String name, Vector2 position, PlayerSP player, GameWorld worldIn, OasisGame game) {
        super(player);

        setName(name);
        getTransformComponent().position.set(position);
        setNearby(false);

        this.rotation = EntityRotation.DOWN;
        this.worldIn = worldIn;
        this.game = game;
    }

    @Override
    public void load(EntitySaveState save, Gson gson) {
        setPosition(save.position());
        rotation = save.rotation();

        final JsonObject data = save.data();
        // if dialog was not implemented yet for the saved entity
        if (data != null && dialogue != null) {
            setActiveEntry(dialogue.setStageAndUpdate(
                    data.get("dialogue_entry_key").getAsString(),
                    data.get("dialogue_stage_index").getAsInt()));
        }
    }

    @Override
    public EntitySaveState save(JsonObject to, Gson gson) {
        to.addProperty("dialogue_entry_key", dialogue.getActiveEntryKey());
        to.addProperty("dialogue_stage_index", dialogue.index());
        return new EntitySaveState(this, to);
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
