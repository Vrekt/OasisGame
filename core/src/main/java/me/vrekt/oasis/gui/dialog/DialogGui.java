package me.vrekt.oasis.gui.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.tommyettinger.textra.TypingLabel;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.npc.EntitySpeakable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.utility.logging.Logging;
import org.apache.commons.lang3.StringUtils;

public final class DialogGui extends Gui {

    private final Table rootTable;
    private final Table optionsGroup;
    private final TypingLabel entityNameLabel;
    private final Image entityImage, keybindImage;

    private final TextureRegionDrawable start, middle, end;

    // dialog options and title
    private final TypingLabel dialogTitle;
    private EntitySpeakable entity;

    // time until reset key image
    private boolean isPressed;
    private long lastPress;

    public DialogGui(GameGui gui, Asset asset) {
        super(gui, asset);

        rootTable = new Table();
        rootTable.setVisible(false);
        rootTable.left();

        this.start = new TextureRegionDrawable(asset.get("dialog_picture"));
        this.middle = new TextureRegionDrawable(asset.get("dialog_option"));
        this.end = new TextureRegionDrawable(asset.get("dialog_end_picture"));

        final Table name = new Table();
        final Table key = new Table();
        key.right().bottom();
        key.add(keybindImage = new Image(asset.get("fkey"))).size(32, 32);

        name.add(this.entityNameLabel = new TypingLabel("", new Label.LabelStyle(asset.getMedium(), Color.WHITE)));

        final Table optionsTable = new Table();
        this.optionsGroup = new Table();
        optionsTable.add(optionsGroup);

        gui.createContainer(rootTable).bottom();
        // TODO: Might cause bugs will check later
        rootTable.add(name).left().padBottom(12);
        rootTable.add(optionsTable).right().padBottom(4);
        rootTable.row();

        final Table table = new Table();
        table.setBackground(gui.getStyles().getTheme());
        table.left();

        final Table entity = new Table();
        entity.add(this.entityImage = new Image()).left();
        table.add(entity).padLeft(16);

        table.add(this.dialogTitle = new TypingLabel("DialogTitle", new Label.LabelStyle(asset.getMedium(), Color.WHITE)))
                .width(448)
                .padBottom(16)
                .padRight(8)
                .padLeft(8);
        this.dialogTitle.setWrap(true);
        table.add(key).right().bottom();

        rootTable.add(table).padBottom(8);
    }

    /**
     * Update key-press for skipping dialog
     */
    public void updateKeyPress() {
        keybindImage.setDrawable(new TextureRegionDrawable(asset.get("fkeypressed")));
        lastPress = System.currentTimeMillis();
        isPressed = true;
    }

    @Override
    public void update() {
        if (isPressed && (System.currentTimeMillis() - lastPress) >= 100) {
            keybindImage.setDrawable(new TextureRegionDrawable(asset.get("fkey")));
            isPressed = false;
        }
    }

    /**
     * Set current entity speaking to
     *
     * @param entity the entity speaking to
     */
    public void setShowingDialog(EntitySpeakable entity) {
        if (entity == null || entity.getDialog() == null) {
            Logging.warn(this, "Failed to show dialog: " + (entity == null) + " -> entity " + (entity == null ? "[no entity]" : (entity.getDialog() == null)) + " -> dialog");
            hide();
            return;
        }

        this.entity = entity;

        this.entityImage.setDrawable(new TextureRegionDrawable(entity.getDialogFace()));
        this.entityNameLabel.setText("{COLOR=WHITE}" + entity.getName());
        // append black color to typing label.
        this.dialogTitle.restart("{COLOR=WHITE}" + parseDialogText(entity.getDialog().getTitle()));

        optionsGroup.clear();
        if (entity.getDialog().hasOptions()) {
            entity.getDialog().getOptions().forEach((key, option) -> {
                optionsGroup.add(createOptionSection(option, key)).padBottom(2f);
                optionsGroup.row();
            });
        }
    }

    private Table createOptionSection(String text, String key) {
        final Table option = new Table();
        final Table bck = new Table();

        bck.setBackground(middle);
        bck.add(new Label(parseDialogText(text), gui.getSkin(), "smaller", Color.WHITE));

        option.add(new Image(start));
        option.add(bck);
        option.add(new Image(end));

        option.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (entity.advanceDialogStage(key)) {
                    // no more dialog so stop speaking
                    entity.setSpeakingTo(false);
                    hide();
                } else {
                    setShowingDialog(entity);
                }
                return true;
            }
        });

        return option;
    }

    private String parseDialogText(String text) {
        return StringUtils.replace(text, "{playerName}", gui.getGame().getPlayer().getName());
    }

    @Override
    public void resize(int width, int height) {
        rootTable.invalidateHierarchy();
    }

    @Override
    public void show() {
        gui.hideGui(GuiType.HUD);
        rootTable.setVisible(true);
        isShowing = true;
    }

    @Override
    public void hide() {
        rootTable.setVisible(false);
        isShowing = false;
    }

}
