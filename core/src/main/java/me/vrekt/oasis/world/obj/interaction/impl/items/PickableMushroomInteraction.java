package me.vrekt.oasis.world.obj.interaction.impl.items;

import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Pickable mushroom typically found in {@link me.vrekt.oasis.world.tutorial.MyceliaWorld}
 */
public final class PickableMushroomInteraction extends AbstractInteractableWorldObject {

    public PickableMushroomInteraction() {
        super(WorldInteractionType.PICKABLE_MUSHROOM);
    }

}
