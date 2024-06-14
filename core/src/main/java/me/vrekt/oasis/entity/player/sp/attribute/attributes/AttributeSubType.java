package me.vrekt.oasis.entity.player.sp.attribute.attributes;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.vrekt.oasis.gui.Styles;

/**
 * Sub types of attributes
 */
public enum AttributeSubType {

    SATISFACTION {
        @Override
        public TextureRegionDrawable get() {
            return Styles.getSatisfactionAttributeIcon();
        }
    }, HEALING {
        @Override
        public TextureRegionDrawable get() {
            return Styles.getHealingAttributeIcon();
        }
    };

    public abstract TextureRegionDrawable get();

}
