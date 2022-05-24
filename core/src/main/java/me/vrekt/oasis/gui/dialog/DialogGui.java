package me.vrekt.oasis.gui.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.gui.Gui;
import org.apache.commons.lang3.StringUtils;

public final class DialogGui extends Gui {

    private final Table rootTable;
    private final Table optionsGroup;
    private final TypingLabel entityNameLabel;
    private final Image entityImage;

    private final TextureRegionDrawable start, middle, end;

    // dialog options and title
    private final TypingLabel dialogTitle;
    private EntityInteractable entity;

    public DialogGui(GameGui gui, Asset asset) {
        super(gui);

        rootTable = new Table();
        rootTable.setVisible(false);
        rootTable.left();

        this.start = new TextureRegionDrawable(asset.get("dialog_picture"));
        this.middle = new TextureRegionDrawable(asset.get("dialog_option"));
        this.end = new TextureRegionDrawable(asset.get("dialog_end_picture"));

        final Table name = new Table();
        name.add(this.entityNameLabel = new TypingLabel("", gui.getSkin(), "medium", Color.BLACK));

        final Table optionsTable = new Table();
        this.optionsGroup = new Table();

        optionsTable.add(name).padBottom(2f);
        optionsTable.row();
        optionsTable.add(optionsGroup);

        gui.createContainer(rootTable).bottom();
        rootTable.add(optionsTable).right().padBottom(4);
        rootTable.row();

        final Table table = new Table();
        table.setBackground(new TextureRegionDrawable(asset.get("interaction_dialog")));
        table.left();

        final Table entity = new Table();
        entity.add(this.entityImage = new Image()).left();
        table.add(entity).padLeft(16);

        table.add(this.dialogTitle = new TypingLabel("DialogTitle", gui.getSkin(), "medium", Color.BLACK)).width(448).padTop(16).padBottom(16).padRight(8).padLeft(8);
        this.dialogTitle.setWrap(true);

        rootTable.add(table);
    }

    /**
     * Set current entity speaking to
     *
     * @param entity the entity speaking to
     */
    public void setShowingDialog(EntityInteractable entity) {
        if (entity.getDialog() == null) {
            hideGui();
            return;
        }

        this.entity = entity;

        this.entityImage.setDrawable(new TextureRegionDrawable(entity.getDialogFace()));
        this.entityNameLabel.setText("{COLOR=BLACK}" + entity.getName());
        // append black color to typing label.
        this.dialogTitle.restart("{COLOR=BLACK}" + parseDialogText(entity.getDialog().title));

        optionsGroup.clear();
        entity.getDialog().options.forEach((key, option) -> {
            optionsGroup.add(createOptionSection(option, key)).padBottom(2f);
            optionsGroup.row();
        });

    }

    private Table createOptionSection(String text, String key) {
        final Table option = new Table();
        final Table bck = new Table();

        bck.setBackground(middle);
        bck.add(new Label(parseDialogText(text), gui.getSkin(), "small", Color.WHITE));

        option.add(new Image(start));
        option.add(bck);
        option.add(new Image(end));

        option.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (entity.advanceDialogStage(key)) {
                    hideGui();
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
    public void showGui() {
        rootTable.setVisible(true);
        isShowing = true;
    }

    @Override
    public void hideGui() {
        rootTable.setVisible(false);
        isShowing = false;
    }

}
