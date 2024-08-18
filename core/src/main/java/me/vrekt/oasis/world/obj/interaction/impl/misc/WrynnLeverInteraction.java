package me.vrekt.oasis.world.obj.interaction.impl.misc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.save.world.obj.WorldObjectSaveState;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.MouseableAbstractInteractableWorldObject;

/**
 * Unlocks the basement
 */
public final class WrynnLeverInteraction extends MouseableAbstractInteractableWorldObject {

    private static final String KEY = "wrynn:lever";
    private boolean state;
    private boolean pressed;
    private String activeTexture = "lever_off";

    public WrynnLeverInteraction() {
        super(WorldInteractionType.WRYNN_LEVER, KEY);
        this.interactionDelay = 0.5f;
        this.saveSerializer = true;

        setSize(16 * OasisGameSettings.SCALE, 16 * OasisGameSettings.SCALE);
        disable();
    }

    @Override
    public void interact() {
        GameManager.playSound(Sounds.LEVER_CLICK, 1.0f, 1.0f, 0.0f);
        lastInteraction = GameManager.tick();

        state = !state;
        activeTexture = state ? "lever_on" : "lever_off";
        final TextureRegion region = world.getGame().asset().get(activeTexture);
        setTextureAndSize(activeTexture, region);

        if (!pressed && parentWorld.hasSimpleObject("oasis:basement_gate")) {
            parentWorld.findInteriorByType(InteriorWorldType.WRYNN_BASEMENT).setEnterable(true);
            parentWorld.removeSimpleObject("oasis:basement_gate");

            world.player().getQuestManager().advanceQuest(QuestType.A_NEW_HORIZON);
            pressed = true;
        }
    }

    @Override
    public WorldObjectSaveState save(JsonObject to, Gson gson) {
        to.addProperty("state", state);
        to.addProperty("pressed", pressed);
        to.addProperty("active_texture", activeTexture);
        return new WorldObjectSaveState(world, this, to);
    }

    @Override
    public void load(WorldObjectSaveState save, Gson gson) {
        if (save.data() != null) {
            this.state = save.data().get("state").getAsBoolean();
            this.pressed = save.data().get("pressed").getAsBoolean();
            this.activeTexture = save.data().get("active_texture").getAsString();
            setTextureAndSize(activeTexture, GameManager.asset().get(activeTexture));
        }
    }
}
