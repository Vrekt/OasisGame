package me.vrekt.oasis.gui.rewrite.guis.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.gui.rewrite.Gui;
import me.vrekt.oasis.gui.rewrite.GuiManager;
import me.vrekt.oasis.item.artifact.Artifact;
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

    private final OasisPlayer player;

    // store artifact components
    private final ArtifactComponentSlot[] artifactSlots;

    private final VisLabel debugComponentText;
    private final TypingLabel hintComponentText;

    private final VisTable hintComponent;

    private float lastHintTime, currentHintDuration;

    private final List<VisTable> components = new ArrayList<>();
    private final LinkedList<HotbarComponentSlot> hotbarIconComponents = new LinkedList<>();

    public GameHudGui(GuiManager guiManager) {
        super(GuiType.HUD, guiManager);

        this.player = guiManager.getGame().getPlayer();

        debugComponentText = new VisLabel();
        debugComponentText.setStyle(guiManager.getStyle().getMediumWhite());

        hintComponentText = new TypingLabel(StringUtils.EMPTY, guiManager.getStyle().getMediumWhite());

        rootTable.setVisible(true);
        rootTable.setFillParent(true);

        artifactSlots = new ArtifactComponentSlot[3];

        initializeDebugComponent();
        initializeArtifactComponent();
        hintComponent = initializeHintComponent();
        initializeHotbarComponent();

        builder = new StringBuilder();
        this.updateInterval = 1000;
    }

    @Override
    public void update() {
        final float now = GameManager.getCurrentGameWorldTick();
        for (int i = 0; i < player.getArtifacts().size(); i++) {
            final Artifact artifact = player.getArtifacts().get(i);
            if (artifact != null) artifactSlots[i].update(artifact);
        }

        player.getInventory().getSlots().forEach((slot, item) -> {
//            if (item.isHotbarItem() && !hotbarIconComponents.get(slot).isUpdated()) {
          //      hotbarIconComponents.get(slot).setItemInSlot(new TextureRegionDrawable(item.getItem().getSprite()));
          //  }
        });

        updatePlayerHintComponent(now);
    }

    public void hotbarItemSelected(int slot) {
        hotbarIconComponents.get(0).setSelected();
    }

    private void updatePlayerHintComponent(float now) {
        // ensure hint component is visible, not indefinite (0) and hint has expired.
        if (hintComponent.getColor().a == 1.0f
                && (currentHintDuration != 0.0f
                && now - lastHintTime >= currentHintDuration)) {
            hintComponent.addAction(Actions.sequence(Actions.fadeOut(1.0f), Actions.visible(false)));
        }
    }

    public void showPlayerHint(String text) {
        showPlayerHint(text, 0.0f);
    }

    public void showPlayerHint(String text, float duration) {
        final float now = GameManager.getCurrentGameWorldTick();
        // don't show the hint if one is already active
        // TODO: Maybe in the future some method to check if any hints, if so expire?
        if (lastHintTime != 0.0f && now - lastHintTime < 2.5f) {
            return;
        }

        currentHintDuration = duration;
        hintComponent.setVisible(true);
        fadeIn(hintComponent, 1.0f);
        hintComponentText.setText(text);
        hintComponentText.restart();
        lastHintTime = now;
    }

    public void showArtifactAbilityUsed(int slot, float cooldown) {
        artifactSlots[slot].activate(cooldown);
    }

    /**
     * Initialize separate ui component for debug stats.
     */
    private void initializeDebugComponent() {
        final VisTable debugTable = new VisTable();
        debugTable.setVisible(false);
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

        artifactSlots[index] = slot;
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
        for (int i = 0; i < 6; i++) {
            final VisTable slotTable = new VisTable();
            slotTable.add(new VisLabel(Integer.toString((i + 1)), guiManager.getStyle().getMediumWhite()));
            final Stack stack = new Stack();

            final VisImage slot = new VisImage(guiManager.getStyle().getTheme());
            final VisImage item = new VisImage();
            hotbarIconComponents.add(new HotbarComponentSlot(slot, item));

            slot.setColor(Color.WHITE);
            stack.add(slot);
            stack.add(item);

            slotTable.row();
            slotTable.add(stack).size(48, 48);
            table.add(slotTable).padLeft(2);
        }

        guiManager.addGui(table);
        components.add(table);
    }

    @Override
    public void timedUpdate(long currentTime) {
        builder.setLength(0);
        builder.append(FPS)
                .append(Gdx.graphics.getFramesPerSecond())
                .append(StringUtils.SPACE)
                .append(PING)
                .append(guiManager.getGame().getPlayer().getServerPingTime())
                .append(StringUtils.SPACE)
                .append(MSPT)
                .append(guiManager.getGame().getServer().getGameServer().getWorldTickTime())
                .append(MS);
        debugComponentText.setText(builder.toString());
    }

    @Override
    public void show() {
        super.show();
        rootTable.setVisible(true);
        components.forEach(table -> table.setVisible(true));
    }

    @Override
    public void hide() {
        super.hide();
        rootTable.setVisible(false);
        components.forEach(table -> table.setVisible(false));
    }

    private static final class HotbarComponentSlot {
        private final VisImage slot, item;

        public HotbarComponentSlot(VisImage slot, VisImage item) {
            this.slot = slot;
            this.item = item;
        }

        public boolean isUpdated() {
            return item.getDrawable() != null;
        }

        public void setSelected() {
            slot.setColor(Color.GRAY);
        }

        public void reset() {
            slot.setColor(Color.WHITE);
        }

        public void setItemInSlot(TextureRegionDrawable texture) {
            item.setDrawable(texture);
        }

        public void removeItemFromSlot() {
            item.setDrawable((Drawable) null);
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
         * Update this component
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

        void activate(float cooldown) {
            artifactImageComponent.getColor().a = 0.0f;
            artifactImageComponent.addAction(Actions.fadeIn(cooldown));
        }

    }

}
