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
import me.vrekt.oasis.entity.dialog.EntityDialogueLoader;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.utility.collision.CollisionType;
import me.vrekt.oasis.world.GameWorld;

/**
 * Misc entity that is seen fishing in the tutorial world
 */
public final class ThaliaEntity extends EntityInteractable {

    public static final String ENTITY_KEY = "oasis:thalia";
    public static final String NAME = "Thalia";

    private int fishingIndex = 1;
    private float lastFishFrameChange = 1;
    private boolean drawFishing;

    public ThaliaEntity(GameWorld world, Vector2 position, OasisGame game) {
        super(NAME, position, game.player(), world, game);
        this.key = ENTITY_KEY;
        this.type = EntityType.THALIA;
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);

        disableCollisionFor(CollisionType.OTHER_ENTITY);
        disableCollisionFor(CollisionType.MAP_BOUNDS);

        parentWorld = worldIn;

        addTexturePart("face", asset.get("thalia_face"));
        addTexturePart("fishing_1", asset.get("thalia_fishing", 1), true);
        addTexturePart("fishing_2", asset.get("thalia_fishing", 2));
        addTexturePart("fishing_3", asset.get("thalia_fishing", 3));

        // don't interfere with status drawing EM-139
        createBBNoSize(activeEntityTexture.getRegionWidth() / 2f, activeEntityTexture.getRegionHeight() / 2f);
        setSize(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight(), OasisGameSettings.SCALE);
        createRectangleBody(worldIn.boxWorld(), new Vector2(1.5f, 1.5f));

        dialogue = EntityDialogueLoader.loadSync("assets/dialog/thalia_dialog.json");
        dialogue.setOwner(this);

        activeEntry = dialogue.getEntry("thalia:dialog_stage_0").getEntry();
        ((EntitySpeakableStatus) getStatus(EntitySpeakableStatus.STATUS_ID)).offset(1.25f, -0.5f);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        super.render(batch, delta);

        if (GameManager.hasTimeElapsed(lastFishFrameChange, 0.2f) && drawFishing) {
            fishingIndex++;
            lastFishFrameChange = GameManager.tick();
            if (fishingIndex >= 3) fishingIndex = 3;
            activeEntityTexture = getTexturePart("fishing_" + fishingIndex);
        }

        // we want to wait till the player can see us a bit before we animate ourselves
        if (inView && !drawFishing) {
            drawFishing = GameManager.hasTimeElapsed(1, 1.0f);
        }

        batch.draw(activeEntityTexture, body.getPosition().x, body.getPosition().y,
                activeEntityTexture.getRegionWidth() * OasisGameSettings.SCALE,
                activeEntityTexture.getRegionHeight() * OasisGameSettings.SCALE);
    }

    @Override
    public TextureRegion getDialogFace() {
        return getTexturePart("face");
    }
}
