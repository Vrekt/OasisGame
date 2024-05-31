package me.vrekt.oasis.gui.guis.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.IntMap;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.*;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.entity.player.sp.attribute.Attribute;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiManager;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.artifact.Artifact;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Main game user interface.
 */
public final class GameHudGui extends Gui {

    // avoid allocating strings over and over
    // may or may not matter much, but regardless.
    private static final String FPS = "FPS: ";
    private static final String PING = "Ping: ";
    private static final String MSPT = "MSPT: ";
    private static final String MS = "ms";
    private final StringBuilder builder;

    private final PlayerSP player;

    // store artifact components
    private final LinkedList<ArtifactComponentSlot> artifactComponentSlots = new LinkedList<>();
    private final LinkedList<AttributeComponentSlot> attributeComponentSlots = new LinkedList<>();

    private final VisLabel debugComponentText;
    private final TypingLabel hintComponentText;

    private final VisTable hintComponent;
    private final VisTable attributeComponent;

    private float lastHintTime, currentHintDuration;

    private final List<VisTable> components = new ArrayList<>();
    private final LinkedList<HotbarComponentSlot> hotbarIconComponents = new LinkedList<>();

    private boolean hintVisibilityOverridden, hintPaused;
    private HotbarComponentSlot selectedSlot;

    public GameHudGui(GuiManager guiManager) {
        super(GuiType.HUD, guiManager);

        this.player = guiManager.getGame().getPlayer();

        debugComponentText = new VisLabel();
        debugComponentText.setStyle(guiManager.getStyle().getMediumWhite());

        hintComponentText = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumWhite());

        rootTable.setVisible(true);
        rootTable.setFillParent(true);

        initializeDebugComponent();
        initializeArtifactComponent();
        hintComponent = initializeHintComponent();
        initializeHotbarComponent();
        attributeComponent = initializeAttributeComponents();

        builder = new StringBuilder();
        this.updateInterval = 1f;
    }

    @Override
    public void update() {
        final float now = GameManager.getTick();
        for (int i = 0; i < player.getArtifacts().size; i++) {
            final Artifact artifact = player.getArtifacts().get(i);
            if (artifact != null) artifactComponentSlots.get(i).update(artifact);
        }

        // TODO: Fix this nasty shit
        for (IntMap.Entry<Item> entry : player.getInventory().getItems()) {
            if (entry.value == null) continue;

            if (player.getInventory().isHotbar(entry.key)
                    && hotbarIconComponents.get(entry.key).replaceItem(entry.value)) {
                hotbarIconComponents.get(entry.key).setItemInSlot(entry.value);
            }
        }

        // update attribute states
        // cannot use forEach "iterator cannot be nested"
        for (AttributeComponentSlot component : attributeComponentSlots) {
            component.update();
        }

        updatePlayerHintComponent(now);
    }

    /**
     * Set a hotbar slot was selected
     *
     * @param slot the slot
     */
    public void hotbarItemSelected(int slot) {
        // reset current slot only if we have one and its active
        if (selectedSlot != null && hotbarIconComponents.indexOf(selectedSlot) != slot) selectedSlot.reset();
        // update selected slot
        selectedSlot = hotbarIconComponents.get(slot);
        selectedSlot.setSelected();

        // TODO: Mechanics still being decided
        if (selectedSlot.item instanceof ItemWeapon weapon) {
            player.equipItem(weapon);
        }

    }

    /**
     * Remove an item from the hotbar slot
     * TODO: Maybe in the future just detect this with update
     * TODO: but, since its removed physically from the list it won't be iterated next update to be detected.
     *
     * @param slot the slot
     */
    public void hotbarItemRemoved(int slot) {
        hotbarIconComponents.get(slot).removeItemFromSlot();
    }

    /**
     * Update the hint alpha actions
     *
     * @param now current tick
     */
    private void updatePlayerHintComponent(float now) {
        if (hintPaused) {
            // reset hint time since its being paused
            // we don't want it to instantly expire
            // when the hint is resumed
            lastHintTime = now;
        } else {
            // ensure hint component is visible, not indefinite (0) and hint has expired.
            if (hintComponent.getColor().a == 1.0f
                    && currentHintDuration != 0.0f
                    && now - lastHintTime >= currentHintDuration
                    && hintComponentText.hasEnded()) {
                hintComponent.addAction(Actions.sequence(Actions.fadeOut(1.0f), Actions.visible(false)));
            }
        }
    }

    public void showPlayerHint(String text) {
        showPlayerHint(text, 0);
    }

    /**
     * Show a player hint
     *
     * @param text     the text hint
     * @param duration the duration in ticks
     */
    public void showPlayerHint(String text, float duration) {
        if (!hintComponent.isVisible()) {
            hintComponent.getColor().a = 0.0f;
            hintComponent.setVisible(true);
            hintVisibilityOverridden = true;
            hintPaused = false;
        }

        final float now = GameManager.getTick();
        // don't show the hint if one is already active
        // TODO: Maybe in the future some method to check if any hints, if so expire?
        if (lastHintTime != 0.0f && now - lastHintTime < 32 /* 1.5ish seconds */) {
            return;
        }

        currentHintDuration = duration;
        hintComponent.setVisible(true);
        fadeIn(hintComponent, 1.0f);
        hintComponentText.setText(text);
        hintComponentText.restart();
        lastHintTime = now;
    }

    /**
     * @return {@code true} if a hint is currently being shown
     */
    public boolean isHintActive() {
        return hintComponent.isVisible() && hintComponent.getColor().a > 0.0f;
    }

    /**
     * Pauses the current hint and hides the element.
     */
    public void pauseCurrentHint() {
        hintComponent.setVisible(false);
        hintPaused = true;
    }

    /**
     * Resumes the current hint and shows the element
     */
    public void resumeCurrentHint() {
        hintComponent.setVisible(true);
        hintPaused = false;
    }

    /**
     * Clear hints
     */
    public void clearHints() {
        hintComponent.setVisible(false);
    }

    /**
     * Show the artifact apply effect
     *
     * @param slot     the slot
     * @param cooldown the cooldown
     */
    public void showArtifactAbilityUsed(int slot, float cooldown) {
        artifactComponentSlots.get(slot).activate(cooldown);
    }

    /**
     * Initialize separate ui component for debug stats.
     */
    private void initializeDebugComponent() {
        final VisTable debugTable = new VisTable();
        debugTable.setVisible(true);
        debugTable.top().left();
        debugTable.add(debugComponentText).top().left();
        guiManager.addGui(debugTable);
        components.add(debugTable);
    }

    /**
     * Initialize artifact ui component
     */
    private void initializeArtifactComponent() {
        final VisTable artifactComponentTable = new VisTable();
        artifactComponentTable.setVisible(true);
        artifactComponentTable.bottom().left().padLeft(8).padBottom(24);

        final TextureRegionDrawable slot = new TextureRegionDrawable(guiManager.getAsset().get("theme"));
        for (int i = 0; i < 3; i++) {
            createArtifactContainer(slot, artifactComponentTable, i);
        }

        guiManager.addGui(artifactComponentTable);
        components.add(artifactComponentTable);
    }


    /**
     * Create separate containers for each artifact slot
     *
     * @param theme  the theme
     * @param parent the parent owner
     * @param index  the current index iterating
     */
    private void createArtifactContainer(TextureRegionDrawable theme, VisTable parent, int index) {
        final VisImage background = new VisImage(theme);
        final VisTable table = new VisTable(true);
        final VisImage icon = new VisImage();
        final ArtifactComponentSlot slot = new ArtifactComponentSlot(icon);
        table.add(icon).size(32, 32);

        final Stack stack = new Stack(background, new Container<Table>(table));
        parent.add(stack).size(48, 48);
        parent.row().padTop(4);

        artifactComponentSlots.add(index, slot);
    }

    /**
     * Initialize hint components
     *
     * @return the owner of this component
     */
    private VisTable initializeHintComponent() {
        final VisTable table = new VisTable();
        table.setVisible(false);
        table.top();

        final VisTable hint = new VisTable();
        hint.setBackground(guiManager.getStyle().getTheme());
        hint.add(new VisImage(guiManager.getAsset().get("hint_icon"))).padLeft(4f).padRight(4f).padBottom(1f);
        hint.add(hintComponentText)
                .width(448)
                .padBottom(16)
                .padRight(8)
                .padLeft(8);
        hintComponentText.setWrap(true);
        table.add(hint);

        guiManager.addGui(table);
        components.add(table);
        return table;
    }

    /**
     * Initialize hotbar components
     */
    private void initializeHotbarComponent() {
        final VisTable table = new VisTable();
        table.bottom().padBottom(8);

        // 6 hotbar slots
        final Label.LabelStyle style = new Label.LabelStyle(guiManager.getSmallFont(), Color.LIGHT_GRAY);

        for (int i = 0; i < 6; i++) {
            // adapter from InventoryGui class
            final VisImage slot = new VisImage(guiManager.getStyle().getTheme());
            final VisImage item = new VisImage();
            item.setOrigin(16 / 2f, 16 / 2f);

            final VisTable itemTable = new VisTable(false);
            itemTable.add(item).size(32, 32);
            final Stack overlay = new Stack(slot, itemTable);

            final VisTable slotNumber = new VisTable(true);
            final VisLabel slotNumberLabel = new VisLabel(Integer.toString(i + 1), style);
            slotNumber.top().left();
            slotNumber.add(slotNumberLabel).top().left().padLeft(2);
            overlay.add(slotNumber);

            hotbarIconComponents.add(new HotbarComponentSlot(slot, item));

            slot.setColor(Color.WHITE);
            table.add(overlay).size(48, 48).padLeft(2);
        }

        guiManager.addGui(table);
        components.add(table);
    }

    /**
     * Initialize attribute components
     *
     * @return the root table
     */
    private VisTable initializeAttributeComponents() {
        final VisTable attributeComponentTable = new VisTable();

        attributeComponentTable.setVisible(false);
        attributeComponentTable.bottom().right().padRight(8).padBottom(24);

        for (int i = 0; i < 3; i++) createAttributeContainer(attributeComponentTable);

        guiManager.addGui(attributeComponentTable);
        components.add(attributeComponentTable);
        return attributeComponentTable;
    }

    /**
     * Creates an attribute container for the attribute icon
     *
     * @param parent parent table
     */
    private void createAttributeContainer(VisTable parent) {
        final VisImage icon = new VisImage();
        icon.setVisible(false);

        parent.add(icon).size(36, 36);
        parent.row();

        attributeComponentSlots.add(new AttributeComponentSlot(icon));
    }

    /**
     * Show an attribute icon component
     *
     * @param attribute the attribute
     */
    public void showAttribute(Attribute attribute) {
        attributeComponent.setVisible(true);

        for (AttributeComponentSlot component : attributeComponentSlots) {
            if (!component.isActive) {
                component.show(attribute);
                break;
            }
        }
    }

    /**
     * Hide the components table if no attributes are active anymore.
     */
    private void hideAttributeComponentsIfNoneActive() {
        boolean visible = false;
        for (AttributeComponentSlot component : attributeComponentSlots) {
            if (component.isActive) {
                visible = true;
                break;
            }
        }
        if (!visible) attributeComponent.setVisible(false);
    }

    @Override
    public void timedUpdate(float tick) {
        builder.setLength(0);
        builder.append(FPS)
                .append(Gdx.graphics.getFramesPerSecond())
                .append(StringUtils.SPACE);

        if (GameManager.game().isMultiplayer()) {
            builder.append(PING)
                    .append(guiManager.getGame().getConnectionHandler().getPingMs())
                    .append(StringUtils.SPACE);
        }

        if (guiManager.getGame().isLocalMultiplayer()) {
           /* builder.append(MSPT)
                    .append(guiManager.getGame().getServer().getGameServer().getWorldTickTime())
                    .append(MS);*/
        }
        debugComponentText.setText(builder.toString());
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
        components.forEach(table -> {
            if (table != hintComponent) table.setVisible(true);
        });
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
        components.forEach(table -> {
            if (table == hintComponent) {
                // override hint visibility if this GUI
                // was hidden for whatever reason
                // but hint still needs to be shown.
                if (!hintVisibilityOverridden) table.setVisible(false);
            } else {
                table.setVisible(false);
            }
        });
    }

    private static final class HotbarComponentSlot {

        private final VisImage slot, itemIcon;
        private Item item;

        HotbarComponentSlot(VisImage slot, VisImage itemIcon) {
            this.slot = slot;
            this.itemIcon = itemIcon;
        }

        /**
         * @param other comparing
         * @return {@code true} if the incoming item should replace what's in this slot
         */
        public boolean replaceItem(Item other) {
            if (this.item == null) return true;
            return !this.item.compare(other);
        }

        public void setSelected() {
            slot.setColor(Color.GRAY);
        }

        public void reset() {
            slot.setColor(Color.WHITE);
        }

        public void setItemInSlot(Item item) {
            itemIcon.setDrawable(new TextureRegionDrawable(item.sprite()));
            this.item = item;
        }

        public void removeItemFromSlot() {
            itemIcon.setDrawable((Drawable) null);
            this.item = null;
        }
    }

    /**
     * Store basic information about an artifact slot.
     */
    private final class ArtifactComponentSlot {
        private final VisImage artifactImageComponent;
        private final Tooltip tooltip;
        private Artifact artifact;

        public ArtifactComponentSlot(VisImage artifactImageComponent) {
            this.artifactImageComponent = artifactImageComponent;
            this.tooltip = new Tooltip.Builder(StringUtils.EMPTY)
                    .target(artifactImageComponent)
                    .style(GameHudGui.this.guiManager.getStyle().getTooltipStyle())
                    .build();
        }

        /**
         * Update this component only if changed or new
         *
         * @param artifact the current artifact iterating
         */
        void update(Artifact artifact) {
            if (this.artifact == null || !this.artifact.is(artifact)) {
                this.artifact = artifact;
                artifactImageComponent.setDrawable(new TextureRegionDrawable(artifact.getSprite()));
                tooltip.setText(artifact.getName());
            }
        }

        /**
         * Activate the hud effect for this slot
         *
         * @param cooldown cooldown until active again
         */
        void activate(float cooldown) {
            artifactImageComponent.getColor().a = 0.0f;
            artifactImageComponent.addAction(Actions.fadeIn(cooldown));
        }

    }

    private final class AttributeComponentSlot {
        private Attribute activeAttribute;
        private final VisImage icon;
        private boolean isActive;

        AttributeComponentSlot(VisImage icon) {
            this.icon = icon;
        }

        void show(Attribute attribute) {
            this.activeAttribute = attribute;

            icon.getColor().a = 0.0f;
            icon.addAction(Actions.fadeIn(1.0f));
            icon.setDrawable(activeAttribute.subType().get(guiManager));
            icon.setVisible(true);
            isActive = true;
        }

        void expire() {
            icon.addAction(
                    Actions.sequence(
                            Actions.fadeOut(1.0f),
                            Actions.visible(false),
                            Actions.run(GameHudGui.this::hideAttributeComponentsIfNoneActive))
            );
            activeAttribute = null;
            isActive = false;
        }

        void update() {
            if (activeAttribute == null) return;

            if (activeAttribute.isExpired()) {
                expire();
            }
        }
    }

}
