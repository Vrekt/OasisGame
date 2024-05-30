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
            return manager.getStyle().getSatisfactionAttributeIcon();
        }
    }, HEALING {
        @Override
        public TextureRegionDrawable get(GuiManager manager) {
            return manager.getStyle().getHealingAttributeIcon();
        }
    };

    public abstract TextureRegionDrawable get(GuiManager manager);

}
