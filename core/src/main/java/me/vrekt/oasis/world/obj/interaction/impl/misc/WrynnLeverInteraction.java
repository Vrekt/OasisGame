package me.vrekt.oasis.world.obj.interaction.impl.misc;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.questing.quests.QuestType;
import me.vrekt.oasis.world.interior.InteriorWorldType;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Unlocks the basement
 */
public final class WrynnLeverInteraction extends AbstractInteractableWorldObject {

    private static final String KEY = "wrynn:lever";
    private boolean state;
    private boolean pressed;

    public WrynnLeverInteraction() {
        super(WorldInteractionType.NONE, KEY);
        this.interactionDelay = 0.5f;
        disable();
    }

    @Override
    public void interact() {
        GameManager.playSound(Sounds.LEVER_CLICK, 1.0f, 1.0f, 0.0f);
        lastInteraction = GameManager.getTick();

        state = !state;
        final TextureRegion region = world.getGame().getAsset().get(state ? "lever_on" : "lever_off");
        setTextureAndSize(region);

        if (!pressed && parentWorld.hasSimpleObject("oasis:basement_gate")) {
            parentWorld.findInteriorByType(InteriorWorldType.WRYNN_BASEMENT).setEnterable(true);
            parentWorld.removeSimpleObject("oasis:basement_gate");

            world.player().getQuestManager().advanceQuest(QuestType.A_NEW_HORIZON);
            pressed = true;
        }
    }
}
