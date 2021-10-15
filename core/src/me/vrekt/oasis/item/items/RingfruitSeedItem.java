package me.vrekt.oasis.item.items;

import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemAtlasType;

public final class RingfruitSeedItem extends Item {

    public RingfruitSeedItem(int amount) {
        super("Ringfruit Seed", "ringfruit_seeds", ItemAtlasType.SEEDS);
        this.description = "Seeds to a Ringfruit plant.";
        this.amount = amount;
    }

}
