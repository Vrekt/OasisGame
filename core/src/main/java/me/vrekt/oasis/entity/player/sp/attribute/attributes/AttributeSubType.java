package me.vrekt.oasis.entity.player.sp.attribute.attributes;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.gui.GuiManager;

/**
 * Sub types of attributes
 */
public enum AttributeSubType {

    SATISFACTION {
        @Override
        public TextureRegionDrawable get(GuiManager manager) {
            return manager.style().getSatisfactionAttributeIcon();
        }
    }, HEALING {
        @Override
        public TextureRegionDrawable get(GuiManager manager) {
            return manager.style().getHealingAttributeIcon();
        }
    };

    public abstract TextureRegionDrawable get(GuiManager manager);

}
