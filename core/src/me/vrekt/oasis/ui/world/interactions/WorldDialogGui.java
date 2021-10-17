package me.vrekt.oasis.ui.world.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.dialog.entity.EntityDialogSection;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.ui.world.Gui;
import me.vrekt.oasis.ui.world.WorldGui;
import org.apache.commons.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

public final class WorldDialogGui extends Gui {

    private final Vector3 translator = new Vector3(0, 0, 0);
    private final Vector2 coordinates = new Vector2(0, 0);

    // options
    private final Map<Rectangle, String> options = new HashMap<>();

    // current dialog
    private final Image dialogImage;
    private EntityInteractable entity;
    private EntityDialogSection dialog;

    // current highlighted option
    private String highlightedOption;
    private boolean optionsPopulated;

    // npc face display
    private TextureRegion display;

    // when dialog runs out of space
    private Rectangle nextButton;
    private boolean dialogInitialized;
    private boolean hasNextPage, hoverNextPage, renderDialogOptions;

    public WorldDialogGui(WorldGui gui) {
        super(gui);

        final TextureRegion dialog = gui.getAsset().getAtlas(Asset.INTERACTIONS).findRegion("interaction_dialog");
        this.dialogImage = new Image(dialog);
        this.dialogImage.setVisible(false);

        // add the image to the stack.
        gui.addElementToStack(dialogImage, dialog.getRegionWidth(), dialog.getRegionHeight(),
                Gdx.graphics.getHeight() - (dialog.getRegionHeight() * 2.5f));
    }

    /**
     * Set current dialog set to use
     *
     * @param dialog dialog
     */
    public void setDialogToRender(EntityInteractable entity, EntityDialogSection dialog, TextureRegion texture) {
        this.entity = entity;
        this.dialog = dialog;
        this.display = texture;
    }

    @Override
    public void render(BitmapFont font, BitmapFont big, Batch batch, GlyphLayout layout) {
        font.setColor(Color.BLACK);

        if (!dialogInitialized) {
            coordinates.set(0, 0);
            dialogImage.localToStageCoordinates(coordinates);
            dialogInitialized = true;
        }

        float originX = coordinates.x;
        float originY = coordinates.y;
        float width = dialogImage.getWidth();
        float height = dialogImage.getHeight();

        // padding
        originX += 16f;
        originY += height;

        // width, height of character face
        final float dw = display.getRegionWidth();
        final float dh = display.getRegionHeight();
        renderCharacterDisplay(batch, originX, originY, height, dw, dh, font, layout);

        // reduce space taken from character display
        final float paddingY = layout.height * 1.5f;
        originX += dw;
        width -= 32 + dw;

        if (renderDialogOptions) {
            // indicates only options should be drawn here and not the title.
            originY -= paddingY;
            renderDialogOptions(batch, originX, originY, width, font, layout);
        } else {
            // title and options should be drawn.
            layout.setText(font, dialog.title);
            if (layout.width > width - 16f) {
                dialog.title = WordUtils.wrap(dialog.title, 30);
            }

            layout.setText(font, dialog.title);
            font.draw(batch, dialog.title, originX + (width - layout.width) / 2f, originY - paddingY);

            // check if the title runs out of space for options
            if (layout.height > height - (layout.height)) {
                final float titleHeight = layout.height;
                // put options on next page.
                layout.setText(font, "Next >>>");

                if (hoverNextPage) {
                    font.setColor(Color.GRAY);
                } else {
                    font.setColor(Color.BLACK);
                }
                font.draw(batch, "Next >>>", originX + width - (layout.width * 1.2f), originY - titleHeight * 1.25f);

                // put next button in list
                this.hasNextPage = true;
                if (this.nextButton == null)
                    this.nextButton = new Rectangle(originX + width - (layout.width * 1.2f), (originY - titleHeight * 1.25f) - layout.height, layout.width, layout.height);
            } else {
                originY -= (layout.height) * 2f;
                renderDialogOptions(batch, originX, originY, width, font, layout);
            }
        }
    }

    /**
     * render the character display and their name
     *
     * @param batch   batch
     * @param originX ox
     * @param originY oy
     * @param height  height
     * @param dw      texture width
     * @param dh      texture height
     * @param font    the font
     * @param layout  layout
     */
    private void renderCharacterDisplay(Batch batch, float originX, float originY, float height, float dw, float dh, BitmapFont font, GlyphLayout layout) {
        // draw NPC character and their name
        layout.setText(font, entity.getName());
        final float paddingY = layout.height * 1.5f;

        font.draw(batch, "Mavia", originX + (dw - layout.width) / 2f, originY - paddingY);
        batch.draw(display, originX, originY - ((height / 2f) + (dh / 2f)));
    }

    /**
     * Render all dialog options
     * TODO: Fix options in wrong spot after screen is resized
     *
     * @param batch   batch
     * @param originX ox
     * @param originY oy
     * @param width   width
     * @param font    font
     * @param layout  layout
     */
    private void renderDialogOptions(Batch batch, float originX, float originY, float width, BitmapFont font, GlyphLayout layout) {
        for (Map.Entry<String, String> entry : dialog.options.entrySet()) {
            layout.setText(font, entry.getValue());
            if (this.highlightedOption != null
                    && this.highlightedOption.equals(entry.getKey())) {
                font.setColor(Color.GRAY);
            } else {
                font.setColor(Color.BLACK);
            }

            font.draw(batch, entry.getValue(), originX + ((width - layout.width) / 2f), originY);
            if (!optionsPopulated) {
                this.options.put(new Rectangle(originX + ((width - layout.width) / 2f), originY - layout.height, layout.width, layout.height), entry.getKey());
            }
            originY -= (layout.height * 2f);
        }

        this.optionsPopulated = true;
    }

    @Override
    public void show() {
        dialogInitialized = false;
        dialogImage.setVisible(true);
    }

    @Override
    public void resize() {
        coordinates.set(0, 0);
        dialogImage.localToStageCoordinates(coordinates);
        dialogInitialized = false;

        // invalidate options
        optionsPopulated = false;
        options.clear();
        nextButton = null;
    }

    @Override
    public void hide() {
        dialogImage.setVisible(false);

        highlightedOption = null;
        optionsPopulated = false;
        renderDialogOptions = false;
    }

    @Override
    public boolean isShowing() {
        return dialogImage.isVisible();
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        gui.getStage().getCamera().unproject(translator.set(screenX, screenY, 0.0f));
        if (hasNextPage) {
            if (nextButton.contains(translator.x, translator.y)) {
                this.hoverNextPage = true;
                return true;
            } else {
                this.hoverNextPage = false;
            }
        }

        for (Map.Entry<Rectangle, String> entry : options.entrySet()) {
            if (entry.getKey().contains(translator.x, translator.y)) {
                this.highlightedOption = entry.getValue();
                return true;
            }
        }

        this.highlightedOption = null;
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer == Input.Buttons.LEFT) {
            if (this.hasNextPage && hoverNextPage) {
                // go to next page with options
                this.renderDialogOptions = true;
                this.hasNextPage = false;
                this.hoverNextPage = false;
                nextButton = null;
            } else if (this.highlightedOption != null) {
                // advance to next dialog stage
                final boolean result = entity.nextDialog(highlightedOption);
                if (result) {
                    this.hide();
                    return true;
                } else {
                    entity.nextDialog(highlightedOption);
                    highlightedOption = null;
                    renderDialogOptions = false;
                    optionsPopulated = false;
                    options.clear();
                    nextButton = null;
                }

                setDialogToRender(entity, entity.getDialogSection(), entity.getDisplay());
                return true;
            }
        }
        return false;
    }

}
