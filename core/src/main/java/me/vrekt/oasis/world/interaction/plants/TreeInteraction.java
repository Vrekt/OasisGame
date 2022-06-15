package me.vrekt.oasis.world.interaction.plants;

import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.world.interaction.Interaction;

/**
 * Interaction in general for trees
 */
public final class TreeInteraction extends Interaction {

    public TreeInteraction() {
        this.interactable = true;
        this.updateDistance = 20f;
        this.interactionDistance = 7f;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void interact() {
        super.interact();
        environment.destroy();

        // TODO: Animations

        this.interactable = false;
        this.interactedWith = false;
        Pools.free(this);
    }

    @Override
    public String getCursor() {
        return "ui/tree_cursor.png";
    }
}
