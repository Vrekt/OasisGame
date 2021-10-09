package me.vrekt.oasis.dialog;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.entity.npc.NPCDialog;
import me.vrekt.oasis.world.AbstractWorld;
import org.apache.commons.text.WordUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles rendering and updating dialog within the game
 */
public final class DialogHandler extends InputAdapter {

    // drawing font
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final Camera stageCamera;
    private final AbstractWorld world;
    private final Vector3 translator = new Vector3();

    // options
    private final Map<Rectangle, String> options = new HashMap<>();

    // current dialog
    private NPCDialog.DialogLink dialog;

    // current highlighted option
    private String highlightedOption;
    private boolean optionsPopulated;

    public DialogHandler(BitmapFont font, Camera stageCamera, AbstractWorld world) {
        this.font = font;
        this.layout = new GlyphLayout(font, "");
        this.stageCamera = stageCamera;
        this.world = world;
    }

    public void setDialog(NPCDialog.DialogLink dialog) {
        this.dialog = dialog;
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

        // wrap lines if title is too big.
        this.layout.setText(font, dialog.title);
        if (layout.width > (width - 16f)) {
            dialog.title = WordUtils.wrap(dialog.title, 40);
            this.layout.setText(font, dialog.title);
        }

        this.font.draw(batch, dialog.title, originX + ((width - layout.width) / 2f), originY + height - 16f);

        // set origin Y a little below the title to draw options
        originY += (height - 16f) - (layout.height >= (height / 4f) ? (layout.height * 1.32f) : layout.height * 4f);

        // draw each line
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
            if (this.highlightedOption != null) {
                this.world.advanceNPCDialog(highlightedOption);
                this.highlightedOption = null;
                this.optionsPopulated = false;
                this.options.clear();
                return true;
            }
        }
        return false;
    }
}
