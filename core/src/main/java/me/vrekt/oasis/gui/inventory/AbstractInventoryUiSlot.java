package me.vrekt.oasis.gui.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisLabel;
import me.vrekt.oasis.item.Item;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains basic information about what is in a slot for the GUI(s)
 */
public abstract class AbstractInventoryUiSlot {

    protected final Image imageItem;
    protected final Tooltip tooltip;
    protected Item item;
    protected boolean occupied;
    protected Stack stack;

    protected VisLabel amountLabel;

    public AbstractInventoryUiSlot(Stack stack, Image item, VisLabel amountLabel, Tooltip.TooltipStyle style) {
        this.imageItem = item;
        this.stack = stack;
        this.amountLabel = amountLabel;
        tooltip = new Tooltip.Builder("Empty Slot").style(style).target(stack).build();
        tooltip.setAppearDelayTime(0.35f);
    }

    protected void reset() {
        this.occupied = false;
        this.imageItem.setDrawable(null);
        this.tooltip.setText("Empty Slot");
        this.item = null;

        amountLabel.setText(StringUtils.EMPTY);
        amountLabel.setVisible(false);
    }

    protected void setStackableState() {
        if (item.isStackable()) {
            amountLabel.setText(String.valueOf(item.getAmount()));
            amountLabel.setVisible(true);
        } else if (!item.isStackable() && amountLabel.isVisible()) {
            amountLabel.setVisible(false);
        }
    }

    protected void setItem(Item item) {
        this.item = item;

        this.imageItem.setDrawable(new TextureRegionDrawable(item.getTexture()));
        this.imageItem.setScale(item.getSprite().getScaleX(), item.getSprite().getScaleY());
        this.amountLabel.setText(String.valueOf(item.getAmount()));
        this.tooltip.setText(item.getItemName());
        this.occupied = true;
    }

    protected abstract void removeItem();

}
