package me.vrekt.oasis.item.tools;

import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.item.Item;

public final class TutorialWand extends Item {

    public static final String TEXTURE = "wand2";
    public static final String NAME = "Tutorial Wand";

    public TutorialWand() {
        super("Arcane Wand");
        setDescription("A tutorial wand for Tutorial Island.");
        setItemId(1);
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.texture = asset.get("wand2");
    }

}
