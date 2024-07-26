package me.vrekt.oasis.network.server.world.obj;

import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;
import me.vrekt.oasis.world.obj.interaction.impl.items.BreakableObjectInteraction;

/**
 * A breakable object within the world.
 */
public final class ServerBreakableWorldObject extends ServerWorldObject {

    private final ItemRarity assignedRarity;

    public ServerBreakableWorldObject(ServerWorld worldIn, AbstractInteractableWorldObject object) {
        super(worldIn, object);

        // should be, just in-case.
        if (object instanceof BreakableObjectInteraction casted) {
            this.assignedRarity = casted.rarity();
        } else {
            this.assignedRarity = ItemRarity.COMMON;
        }
    }

    /**
     * @return rarity of this
     */
    public ItemRarity assignedRarity() {
        return assignedRarity;
    }
}
