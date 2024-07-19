package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.component.status.EntitySpeakableStatus;
import me.vrekt.oasis.entity.component.status.MiraObjectiveStatus;
import me.vrekt.oasis.entity.dialog.EntityDialogueLoader;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.utility.collision.CollisionType;
import me.vrekt.oasis.world.GameWorld;

/**
 * Shop keeper within Mycelia
 */
public final class MiraEntity extends EntityInteractable {

    public static final String ENTITY_KEY = "oasis:mira";
    public static final String NAME = "Mira";

    public MiraEntity(GameWorld world, Vector2 position, OasisGame game) {
        super(NAME, position, game.getPlayer(), world, game);
        this.key = ENTITY_KEY;
        this.type = EntityType.MIRA;

        // we want to draw behind some layers, so it doesn't look weird.
        this.renderWithMap = true;
        this.renderAfterLayer = "TheGroundObjects";
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);

        disableCollisionFor(CollisionType.OTHER_ENTITY);
        disableCollisionFor(CollisionType.MAP_BOUNDS);

        parentWorld = worldIn;

        addTexturePart("face", asset.get("mira_face"));
        addTexturePart("idle", asset.get("mira_idle"), true);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());
        createRectangleBody(worldIn.boxWorld(), new Vector2(0.55f, 0.88f));

        dialogue = EntityDialogueLoader.load("assets/dialog/mira_dialog.json");
        dialogue.setOwner(this);

        dialogue.addTaskHandler("mira:show_items", this::showPlayerItems);

        activeEntry = dialogue.getEntry("mira:dialog_stage_0").getEntry();
        ((EntitySpeakableStatus) getStatus(EntitySpeakableStatus.STATUS_ID)).offset(0.25f, 0.0f);
    }

    @Override
    public void mapRender(SpriteBatch batch, float delta) {
        batch.draw(activeEntityTexture, body.getPosition().x, body.getPosition().y,
                activeEntityTexture.getRegionWidth() * OasisGameSettings.SCALE,
                activeEntityTexture.getRegionHeight() * OasisGameSettings.SCALE);
    }

    @Override
    public TextureRegion getDialogFace() {
        return getTexturePart("face");
    }

    @Override
    public void speak(boolean speakingTo) {
        super.speak(speakingTo);
    }

    /**
     * Show the player the shop GUI
     */
    private void showPlayerItems() {
        addStatus(new MiraObjectiveStatus(this, game.getAsset()));

        GameManager.getTaskManager().schedule(() -> {
            this.speak(false);
            GameManager.getGuiManager().hideGui(GuiType.DIALOG);
        }, 3.55f);
    }

}
