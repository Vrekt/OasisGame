package me.vrekt.oasis.item.tools;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

public final class TutorialWand extends Item {

    public TutorialWand() {
        super("Arcane Wand");
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.getAssets2().findRegion("wand2");
    }

}
