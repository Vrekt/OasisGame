package me.vrekt.oasis.item.weapons;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

public final class FrostbittenAvernicItem extends Item {

    public static final int ID = 4;
    public static final String TEXTURE = "frostbitten_avernic";
    public static final String NAME = "Frostbitten Avernic";

    public FrostbittenAvernicItem() {
        super(NAME, ID, "A special blade with magical abilities.");
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get(TEXTURE);
    }
}
