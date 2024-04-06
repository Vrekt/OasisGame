package me.vrekt.oasis.gui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TextraLabel;
import com.github.tommyettinger.textra.TypingLabel;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.artifact.Artifact;
import org.apache.commons.lang3.StringUtils;

public final class HudGui extends Gui {

    private final OasisPlayer player;

    // FPS, Ping, other information
    private final VisTable debugTable;
    private final VisTable classIconTable;
    private final VisTable playerArtifactsTable;
    private final VisTable playerHealthTable;
    private final VisTable itemHintTable;

    private final VisTable dungeonIntroTable;
    private final VisLabel dungeonIntroLabel;

    private final VisTable missingItemWarningTable;
    private final VisImage missingItemImage;

    private final VisImage playerClassImage;
    private final VisLabel fpsLabel, pingLabel;
    private TypingLabel hintLabel;

    private final VisImage hintItemImage;
    private final VisLabel hintItemLabel;
    private float lastHintTick, lastIntroTick, lastWarningTick, hintDuration;

    private final ArtifactSlot[] slots = new ArtifactSlot[3];

    public HudGui(GameGui gui, Asset asset) {
        super(gui, asset);

        this.player = gui.getGame().getPlayer();
        this.isShowing = false;
        playerClassImage = new VisImage();
        fpsLabel = new VisLabel("", new Label.LabelStyle(asset.getMedium(), Color.WHITE));
        pingLabel = new VisLabel("", new Label.LabelStyle(asset.getMedium(), Color.WHITE));
        debugTable = initializeDebugTable();
        classIconTable = initializeClassIconTable(gui);
        playerArtifactsTable = initializePlayerArtifactsTable();

        playerHealthTable = initializePlayerHealthTable();
        dungeonIntroLabel = new VisLabel("", new Label.LabelStyle(gui.getLarge(), Color.WHITE));
        dungeonIntroTable = initializeDungeonIntroTable();

        missingItemImage = new VisImage();
        missingItemWarningTable = initializeMissingItemWarningTable();

        hintItemImage = new VisImage();
        hintItemLabel = new VisLabel("(1)", new Label.LabelStyle(gui.getMedium(), Color.WHITE));
        itemHintTable = initializeHintTable();

    }

    @Override
    public void update() {
        if (OasisGameSettings.SHOW_FPS && !fpsLabel.isVisible()) {
            fpsLabel.setVisible(true);
            pingLabel.setVisible(true);
        } else if (!OasisGameSettings.SHOW_FPS && fpsLabel.isVisible()) {
            fpsLabel.setVisible(false);
            pingLabel.setVisible(false);
        }

        if (fpsLabel.isVisible()) {
            fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        }
        if (pingLabel.isVisible()) {
            pingLabel.setText("Ping: " + gui.getGame().getPlayer().getServerPingTime());
        }

        // update player artifact slots


        updateAlphaActions();
    }

    /**
     * Update alpha actions for various GUI elements
     */
    private void updateAlphaActions() {
        final float now = GameManager.getCurrentGameWorldTick();
        if (itemHintTable.getColor().a == 1 && (hintDuration != 0.0 && now - lastHintTick >= hintDuration)) {
            itemHintTable.addAction(Actions.fadeOut(1f));
        }

        // fade warning table
        if (missingItemWarningTable.getColor().a == 1 && (now - lastWarningTick) >= 1.5f)
            missingItemWarningTable.addAction(Actions.fadeOut(1f));

        // fade intro table
        if (dungeonIntroTable.getColor().a == 1 && (now - lastIntroTick) >= 2.5f)
            dungeonIntroTable.addAction(Actions.fadeOut(1f));
    }

    private VisTable initializeDebugTable() {
        final VisTable fpsTable = new VisTable(true);
        fpsTable.setVisible(false);
        fpsTable.top().left();
        fpsTable.add(fpsLabel).left();
        fpsTable.row();
        fpsTable.add(pingLabel).left();
        fpsLabel.setVisible(false);

        gui.createContainer(fpsTable).top().left();

        return fpsTable;
    }

    private VisTable initializeClassIconTable(GameGui gui) {
        final VisTable classIconTable = new VisTable(true);
        classIconTable.setVisible(false);
        classIconTable.left();
        classIconTable.add(playerClassImage).size(48, 48);
        gui.createContainer(classIconTable).bottom().left().pad(8);
        return classIconTable;
    }

    private VisTable initializePlayerHealthTable() {

        final VisTable health = new VisTable(true);
        health.setVisible(false);
        health.bottom();

        final VisTable bk = new VisTable(true);
        bk.setBackground(new TextureRegionDrawable(asset.get("healthbar")));
        bk.add(new VisLabel("HP", new Label.LabelStyle(gui.getMedium(), Color.WHITE)));

        health.add(bk);
        gui.createContainer(health).bottom().padBottom(8);
        return health;
    }

    private VisTable initializeDungeonIntroTable() {
        final VisTable introTable = new VisTable(true);
        introTable.setVisible(false);
        introTable.center();
        introTable.add(dungeonIntroLabel);

        gui.createContainer(introTable).center();
        return introTable;
    }

    private VisTable initializeHintTable() {
        final VisTable hintTable = new VisTable();
        hintTable.setVisible(false);

        final Table hint = new Table();
        hint.setBackground(gui.getStyles().getTheme());

        hint.add(this.hintLabel = new TypingLabel("HintLabel", new Label.LabelStyle(asset.getMedium(), Color.WHITE)))
                .width(448)
                .padBottom(16)
                .padRight(8)
                .padLeft(8);
        this.hintLabel.setWrap(true);
        hintTable.add(hint);

//        gui.createContainer(hintTable).top().padTop(2);

        return hintTable;
    }

    private VisTable initializeMissingItemWarningTable() {
        final VisTable warningTable = new VisTable();
        warningTable.setVisible(false);
        warningTable.bottom().padBottom(16);

        final Table warning = new Table();
        warning.setBackground(gui.getStyles().getTheme());
        warning.add(new TextraLabel("[YELLOW](!) [WHITE]You need a: ", new Label.LabelStyle(asset.getMedium(), Color.WHITE))).top();
        warning.add(missingItemImage);
        warningTable.add(warning);

        gui.createContainer(warningTable).bottom().padBottom(16);

        return warningTable;
    }

    /**
     * Show the dungeon introduction
     *
     * @param text the text
     */
    public void showDungeonIntroduction(String text) {
        this.dungeonIntroLabel.setText(text);
        this.dungeonIntroTable.setVisible(true);
        this.dungeonIntroTable.getColor().a = 0;
        this.dungeonIntroTable.addAction(Actions.fadeIn(1.5f));
        lastIntroTick = GameManager.getCurrentGameWorldTick();
    }

    public void stopDungeonIntroduction() {
        dungeonIntroTable.setVisible(false);
    }

    private VisTable initializePlayerArtifactsTable() {
        final VisTable playerArtifactsTable = new VisTable(true);
        playerArtifactsTable.setVisible(true);
        playerArtifactsTable.left();

        final TextureRegionDrawable slot = new TextureRegionDrawable(asset.get("theme"));
        for (int i = 0; i < 3; i++) {
            createArtifactContainer(slot, playerArtifactsTable, i);
        }

        gui.createContainer(playerArtifactsTable).bottom().left().padLeft(8).padBottom(64);
        return playerArtifactsTable;
    }

    private VisTable initializeItemHintTable() {
        final VisTable root = new VisTable(true);
        final VisTable hint = new VisTable(true);
        root.setVisible(false);
        hint.setBackground(new TextureRegionDrawable(asset.get("hintdropdown")));
        hint.add(new VisLabel("You received ", new Label.LabelStyle(gui.getMedium(), Color.WHITE)));
        root.add(hint).size(256, 32);
        hint.add(hintItemImage).size(32, 32).padBottom(6).padTop(6);
        hint.add(hintItemLabel);

        gui.createContainer(root).bottom().padBottom(16);
        return root;
    }

    /**
     * Create a container for the players artifact slots
     *
     * @param background the background slot
     * @param parent     the parent
     */
    private void createArtifactContainer(TextureRegionDrawable background, VisTable parent, int index) {
        final VisImage slot = new VisImage(background);
        final VisTable table = new VisTable(true);
        final ArtifactSlot artifact = new ArtifactSlot(new VisImage());
        table.add(artifact.artifactImage).size(32, 32);
        final Stack stack = new Stack(slot, new Container<Table>(table));
        parent.add(stack).size(48, 48);
        parent.row().padTop(-6);

        slots[index] = artifact;
    }

    /**
     * Set the class icon for the HUD
     *
     * @param icon the icon string from assets
     */
    public void setClassIcon(String icon) {
        playerClassImage.setDrawable(new TextureRegionDrawable(asset.get(icon)));
    }

    /**
     * Show a warning displaying an item is required
     *
     * @param texture the texture of the item required
     */
    public void showMissingItemWarning(String texture) {
        final float now = GameManager.getCurrentGameWorldTick();
        if (now - lastWarningTick < 2.5f) {
            return;
        }

        missingItemImage.setDrawable(new TextureRegionDrawable(asset.get(texture)));
        itemHintTable.setVisible(false);

        lastWarningTick = now;
        missingItemWarningTable.setVisible(true);
        missingItemWarningTable.getColor().a = 0.0f;
        missingItemWarningTable.addAction(Actions.fadeIn(1f));
    }

    /**
     * Display an item has been collected via a notification.
     * Used mainly for quests and tutorial purposes.
     *
     * @param item the item
     */
    public void showItemCollected(Item item) {
        final float now = GameManager.getCurrentGameWorldTick();
        if (now - lastHintTick < 2.5f) {
            return;
        }

        itemHintTable.setVisible(true);
        itemHintTable.getColor().a = 0;
        itemHintTable.addAction(Actions.fadeIn(1f));

        hintItemImage.setDrawable(new TextureRegionDrawable(item.getSprite()));
        hintItemLabel.setText("" + item.getAmount());
        lastHintTick = now;
    }

    public void showHintWithNoFade(String text) {
        showHint(text, 0.0f);
    }

    public void expireCurrentHint() {
        itemHintTable.addAction(Actions.fadeOut(.5f));
        itemHintTable.setVisible(false);
    }

    public void showHint(String text, float duration) {
        final float now = GameManager.getCurrentGameWorldTick();
        if (lastHintTick != 0.0f && now - lastHintTick < 2.5f) {
            return;
        }

        this.hintDuration = duration;

        itemHintTable.setVisible(true);
        itemHintTable.getColor().a = 0.0f;
        itemHintTable.addAction(Actions.fadeIn(1f));
        hintLabel.setText(text);
        hintLabel.restart();
        lastHintTick = now;
    }


    public void artifactUsed(int slot, float cooldown) {
        slots[slot].artifactImage.getColor().a = 0.0f;
        slots[slot].artifactImage.addAction(Actions.fadeIn(cooldown));
    }

    @Override
    public void show() {
        super.show();
        debugTable.setVisible(true);
        classIconTable.setVisible(true);
        playerArtifactsTable.setVisible(true);
        playerHealthTable.setVisible(true);
    }

    @Override
    public void hide() {
        super.hide();
        debugTable.setVisible(false);
        classIconTable.setVisible(false);
        playerArtifactsTable.setVisible(false);
        playerHealthTable.setVisible(false);
        itemHintTable.setVisible(false);
        missingItemWarningTable.setVisible(false);
        dungeonIntroTable.setVisible(false);
    }

    /**
     * Contains information about an artifact slot
     */
    private final class ArtifactSlot {
        private final VisImage artifactImage;
        private Artifact artifactSlot;

        public ArtifactSlot(VisImage artifactImage) {
            this.artifactImage = artifactImage;
        }

        void updateIfRequired(Artifact artifact) {
            if (this.artifactSlot == null ||
                    !StringUtils.equals(this.artifactSlot.getName(), artifact.getName())) {
                // no artifact or different, update.
                this.artifactSlot = artifact;
                this.artifactImage.setDrawable(new TextureRegionDrawable(artifact.getSprite()));
            }
        }

    }

}
