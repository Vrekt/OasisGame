package me.vrekt.oasis.item.tools;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

/**
 * Probably will be removed in the future
 */
public final class TutorialWand extends Item {

    public static final int ID = 1;
    public static final String TEXTURE = "wand2";
    public static final String NAME = "Tutorial Wand";

    public TutorialWand() {
        super(NAME, ID, "A tutorial wand for Tutorial Island.");
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get(TEXTURE);
    }

}
