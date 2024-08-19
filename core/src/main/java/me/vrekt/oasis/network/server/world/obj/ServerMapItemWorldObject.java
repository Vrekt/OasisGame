package me.vrekt.oasis.network.server.world.obj;

import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.network.server.world.ServerWorld;
import me.vrekt.oasis.world.obj.interaction.impl.items.MapItemInteraction;

/**
 * A dropped item
 */
public final class ServerMapItemWorldObject extends ServerWorldObject {

    private final Items item;
    private final int amount;

    public ServerMapItemWorldObject(ServerWorld worldIn, MapItemInteraction object) {
        super(worldIn, object);

        this.item = object.item().type();
        this.amount = object.item().amount();
    }

    /**
     * @return the item type
     */
    public Items item() {
        return item;
    }

    /**
     * @return the amount
     */
    public int amount() {
        return amount;
    }
}
