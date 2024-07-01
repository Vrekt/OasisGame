package me.vrekt.oasis.gui.guis.inventory.style;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.game.Resource;

/**
 * Handles item slot rarity styles
 */
public final class ItemSlotStyles {

    private final NinePatchDrawable normal, normalDown;
    private final NinePatchDrawable common, commonDown, cosmic, cosmicDown;
    private final NinePatchDrawable vd, voidDown;

    public ItemSlotStyles(Asset asset) {
        final NinePatch normalPatch = new NinePatch(asset.get(Resource.UI, "theme"), 4, 4, 4, 4);
        final NinePatch normalDownPatch = new NinePatch(asset.get(Resource.UI, "theme_down"), 4, 4, 4, 4);

        this.normal = new NinePatchDrawable(normalPatch);
        this.normalDown = new NinePatchDrawable(normalDownPatch);

        final NinePatch commonPatch = new NinePatch(asset.get(Resource.UI, "common_slot_rarity"), 4, 4, 4, 4);
        final NinePatch commonDownPatch = new NinePatch(asset.get(Resource.UI, "common_slot_rarity_down"), 4, 4, 4, 4);
        final NinePatch cosmicPatch = new NinePatch(asset.get(Resource.UI, "cosmic_slot_rarity"), 4, 4, 4, 4);
        final NinePatch cosmicDownPatch = new NinePatch(asset.get(Resource.UI, "cosmic_slot_rarity_down"), 4, 4, 4, 4);

        final NinePatch vd = new NinePatch(asset.get(Resource.UI, "void_slot_rarity"), 4, 4, 4, 4);
        final NinePatch vdDown = new NinePatch(asset.get(Resource.UI, "void_slot_rarity_down"), 4, 4, 4, 4);

        this.common = new NinePatchDrawable(commonPatch);
        this.commonDown = new NinePatchDrawable(commonDownPatch);
        this.cosmic = new NinePatchDrawable(cosmicPatch);
        this.cosmicDown = new NinePatchDrawable(cosmicDownPatch);
        this.vd = new NinePatchDrawable(vd);
        this.voidDown = new NinePatchDrawable(vdDown);
    }

    /**
     * @return normal style
     */
    public NinePatchDrawable normal() {
        return normal;
    }

    /**
     * @return normal mouse over style
     */
    public NinePatchDrawable normalDown() {
        return normalDown;
    }

    /**
     * @return the common style
     */
    public NinePatchDrawable common() {
        return common;
    }

    /**
     * @return common style, mouse over
     */
    public NinePatchDrawable commonDown() {
        return commonDown;
    }

    /**
     * @return the cosmic style
     */
    public NinePatchDrawable cosmic() {
        return cosmic;
    }

    /**
     * @return the cosmic mouse over style
     */
    public NinePatchDrawable cosmicDown() {
        return cosmicDown;
    }

    /**
     * @return void rarity normal
     */
    public NinePatchDrawable vd() {
        return vd;
    }

    /**
     * @return mouse over style
     */
    public NinePatchDrawable voidDown() {
        return voidDown;
    }
}
