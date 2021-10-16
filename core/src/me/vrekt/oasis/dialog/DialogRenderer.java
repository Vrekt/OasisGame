package me.vrekt.oasis.dialog;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.dialog.entity.EntityDialogSection;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import org.apache.commons.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles rendering and updating dialog within the game
 */
public final class DialogRenderer extends InputAdapter {

    // drawing font
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final Camera stageCamera;
    private final Vector3 translator = new Vector3();

    // options
    private final Map<Rectangle, String> options = new HashMap<>();

    // current dialog
    private EntityInteractable entity;
    private EntityDialogSection dialog;

    // current highlighted option
    private String highlightedOption;
    private boolean optionsPopulated;

    // npc face display
    private TextureRegion display;

    // when dialog runs out of space
    private Rectangle nextButton;
    private boolean hasNextPage, hoverNextPage, renderDialogOptions;

    public DialogRenderer(BitmapFont font, Camera stageCamera) {
        this.font = font;
        this.layout = new GlyphLayout(font, "");
        this.stageCamera = stageCamera;
    }

    /**
     * Set current dialog set to use
     *
     * @param dialog dialog
     */
    public void setDialogToUse(EntityInteractable entity, EntityDialogSection dialog, TextureRegion texture) {
        this.entity = entity;
        this.dialog = dialog;
        this.display = texture;
    }

    /**
     * Render the current dialog
     *
     * @param batch   batch
     * @param originX origin X
     * @param originY origin Y
     * @param width   image width
     * @param height  image height
     */
    public void renderDialog(Batch batch, float originX, float originY, float width, float height) {
        font.setColor(Color.BLACK);

        // padding
        originX += 16f;
        originY += height;

        // width, height of character face
        final float dw = display.getRegionWidth();
        final float dh = display.getRegionHeight();
        renderCharacterDisplay(batch, originX, originY, height, dw, dh);

        // reduce space taken from character display
        final float paddingY = layout.height * 1.5f;
        originX += dw;
        width -= 32 + dw;

        if (renderDialogOptions) {
            // indicates only options should be drawn here and not the title.
            originY -= paddingY;
            renderDialogOptions(batch, originX, originY, width);
        } else {
            // title and options should be drawn.
            this.layout.setText(font, dialog.title);
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
                this.nextButton = new Rectangle(originX + width - (layout.width * 1.2f), (originY - titleHeight * 1.25f) - layout.height, layout.width, layout.height);
            } else {
                originY -= (layout.height) * 2f;
                renderDialogOptions(batch, originX, originY, width);
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
     */
    private void renderCharacterDisplay(Batch batch, float originX, float originY, float height, float dw, float dh) {
        // draw NPC character and their name
        layout.setText(font, entity.getName());
        final float paddingY = layout.height * 1.5f;

        font.draw(batch, "Mavia", originX + (dw - layout.width) / 2f, originY - paddingY);
        batch.draw(display, originX, originY - ((height / 2f) + (dh / 2f)));
    }

    /**
     * Render all dialog options
     *
     * @param batch   batch
     * @param originX ox
     * @param originY oy
     * @param width   width
     */
    private void renderDialogOptions(Batch batch, float originX, float originY, float width) {
        for (Map.Entry<String, String> entry : dialog.options.entrySet()) {
            this.layout.setText(font, entry.getValue());
            if (this.highlightedOption != null
                    && this.highlightedOption.equals(entry.getKey())) {
                this.font.setColor(Color.GRAY);
            } else {
                this.font.setColor(Color.BLACK);
            }

            this.font.draw(batch, entry.getValue(), originX + ((width - layout.width) / 2f), originY);
            if (!optionsPopulated)
                this.options.put(new Rectangle(originX + ((width - layout.width) / 2f), originY - layout.height, layout.width, layout.height), entry.getKey());
            originY -= (layout.height * 2f);
        }

        this.optionsPopulated = true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        stageCamera.unproject(translator.set(screenX, screenY, 0.0f));
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
            } else if (this.highlightedOption != null) {
                // advance to next dialog stage
                entity.nextDialog(highlightedOption);

                // reset current stage
                highlightedOption = null;
                optionsPopulated = false;
                this.renderDialogOptions = false;
                options.clear();
                return true;
            }
        }
        return false;
    }
}
