package me.vrekt.oasis.item.weapons;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;

/**
 * Initial tutorial item
 */
public final class FrostbittenAvernicItem extends ItemWeapon {

    public static final int ID = 4;
    public static final String TEXTURE = "frostbitten_avernic";
    public static final String NAME = "Frostbitten Avernic";

    public FrostbittenAvernicItem() {
        super(NAME, ID, "A special blade with magical abilities.");
        this.baseDamage = 1.5f;
    }

    @Override
    public void loadItemAsset(Asset asset) {
        this.sprite = new Sprite(asset.get(TEXTURE));
    }

}
