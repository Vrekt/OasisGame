package me.vrekt.oasis.gui.guis.other;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.magic.MagicSpell;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.Styles;
import me.vrekt.oasis.item.usable.MagicBookItem;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

/**
 * Magic book GUI
 * <p>
 * TODO LIST:
 * - Cooldowns
 */
public final class MagicBookGui extends Gui {

    private static final String TOOLTIP_DEFAULT = "Unlock this spell to discover its ability.";
    private static final String LOCK_ACTOR_NAME = "LOCK";

    private final TypingLabel header, description;
    private final LinkedList<SpellContainer> containers = new LinkedList<>();

    private final VisImageTextButton castButton;

    private MagicSpell selectedSpell;

    public MagicBookGui(GuiManager guiManager) {
        super(GuiType.MAGIC_BOOK, guiManager);

        rootTable.setVisible(false);
        rootTable.setFillParent(true);
        rootTable.setBackground(drawable("arcana_codex_ui"));

        final VisTable left = new VisTable(true);
        left.top().padTop(52).padLeft(84);

        final VisTable right = new VisTable(true);

        right.top().padTop(36).padLeft(36).left();

        final TextureRegionDrawable drawable = drawable("spell_slot");
        final TextureRegionDrawable lock = drawable("lock");
        for (int i = 0; i < 3; i++) {
            // adapter from InventoryGui class
            final VisImage slot = new VisImage(drawable);
            final VisImage item = new VisImage();
            item.setOrigin(16 / 2f, 16 / 2f);

            final VisTable itemTable = new VisTable(false);
            itemTable.add(item).size(32, 32);

            final VisImage locked = new VisImage(lock);
            locked.setName(LOCK_ACTOR_NAME);

            final Stack overlay = new Stack();
            overlay.add(locked);
            overlay.getColor().a = 0.55f;

            overlay.add(slot);
            overlay.add(itemTable);

            final Tooltip tooltip = new Tooltip.Builder(TOOLTIP_DEFAULT)
                    .style(Styles.getTooltipStyle())
                    .target(overlay)
                    .build();

            tooltip.setAppearDelayTime(0.25f);

            final SpellContainer container = new SpellContainer(overlay, item, locked, tooltip);
            this.containers.add(container);

            registerClickListener(overlay, i);

            slot.setColor(Color.WHITE);
            left.add(overlay).size(48, 48).padLeft(2);
        }

        header = new TypingLabel(StringUtils.EMPTY, Styles.getLargeBlack());
        description = new TypingLabel(StringUtils.EMPTY, Styles.getMediumWhiteMipMapped());

        header.setVisible(false);
        header.setWrap(true);
        header.setWidth(150);
        header.setColor(Color.BLACK);

        description.setVisible(false);
        description.setWrap(true);
        description.setWidth(175);
        description.setColor(Color.valueOf("#13146C"));

        castButton = new VisImageTextButton("Cast", Styles.getImageTextButtonStyle());

        right.add(header).padTop(6).left();
        right.row();
        right.add(description)
                .width(175)
                .padTop(8)
                .left();
        right.row();
        right.add(castButton).left().padTop(16);
        handleCastButton(castButton);

        // Add split pane for left and right tables
        final VisSplitPane pane = new VisSplitPane(left, right, false);
        // only allow elements to be touched, since split pane
        // is programming choice for easier UI, the split pane should not be used
        pane.setTouchable(Touchable.enabled);
        rootTable.add(pane).fill().expand();

        guiManager.addGui(rootTable);
    }

    /**
     * Show the GUI for a specified spell book
     *
     * @param item item
     */
    public void showItem(MagicBookItem item) {
        containers.forEach(SpellContainer::reset);
        castButton.setVisible(false);

        for (int i = 0; i < item.spells().size(); i++) {
            final SpellContainer container = containers.get(i);
            final MagicSpell spell = item.spells().get(i);
            final boolean castable = spell.isCastable(guiManager.player());
            if (spell.isUnlocked() && castable) {
                container.container.getColor().a = 1.0f;
                container.container.removeActor(container.lock);
            } else {
                if (castable) {
                    container.reset();
                }
            }

            this.selectedSpell = spell;
            container.setSpell(spell, castable);
            container.item.setDrawable(new TextureRegionDrawable(spell.icon()));
        }

        show();
    }

    /**
     * Register spell click listeners
     *
     * @param container container
     * @param index     index
     */
    private void registerClickListener(Actor container, int index) {
        container.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final MagicSpell spell = containers.get(index).spell;
                if (spell != null && spell.isUnlocked() && spell.isCastable(guiManager.player())) {
                    header.setVisible(true);
                    header.setText(spell.name());
                    header.restart();

                    description.setVisible(true);
                    description.setText(spell.description());
                    description.restart();

                    castButton.setVisible(true);
                }

            }
        });
    }

    /**
     * Handle the clicking of the cast button
     *
     * @param button button
     */
    private void handleCastButton(Actor button) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedSpell != null && selectedSpell.isCastable(guiManager.player())) {
                    selectedSpell.cast(guiManager.player());
                }
            }
        });
    }

    @Override
    public void show() {
        super.show();

        guiManager.hideGui(GuiType.INVENTORY);
        rootTable.setVisible(true);

        GameManager.playSound(Sounds.OPEN_MAGIC_BOOK, 0.11f, 1.0f, 0.0f);
    }

    @Override
    public void hide() {
        super.hide();

        header.setVisible(false);
        description.setVisible(false);

        rootTable.setVisible(false);
    }

    private static final class SpellContainer {

        private final Stack container;
        private final VisImage item, lock;
        private final Tooltip tooltip;

        private MagicSpell spell;

        public SpellContainer(Stack container, VisImage item, VisImage lock, Tooltip tooltip) {
            this.container = container;
            this.item = item;
            this.lock = lock;
            this.tooltip = tooltip;
        }

        /**
         * Show feedback this spell cannot be used right now
         */
        private void resetNotCastable() {
            tooltip.setText("You cannot use this spell right now.");
            container.getColor().a = 0.55f;
        }

        private void reset() {
            item.setDrawable((Drawable) null);
            spell = null;
            if (container.findActor(LOCK_ACTOR_NAME) == null) {
                container.add(lock);
                container.getColor().a = 0.55f;
            }

            tooltip.setText(TOOLTIP_DEFAULT);
        }

        /**
         * Set the active spell for this slot container
         *
         * @param spell spell
         */
        public void setSpell(MagicSpell spell, boolean castable) {
            if (!castable) {
                resetNotCastable();
            } else {
                tooltip.setText(spell.name());
                this.spell = spell;
            }
        }
    }

}
