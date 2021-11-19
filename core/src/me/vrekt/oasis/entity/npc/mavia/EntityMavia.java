package me.vrekt.oasis.entity.npc.mavia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.entity.drawing.Rotation;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.entity.render.EntityAnimationRenderer;
import me.vrekt.oasis.quest.type.QuestType;
import me.vrekt.oasis.world.AbstractWorld;
import me.vrekt.oasis.world.interior.Interior;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

/**
 * Mavia.
 */
public final class EntityMavia extends EntityInteractable {

    // mavias house entrance Y.
    private float interiorEntranceY;

    public EntityMavia(float x, float y, OasisGame game, AbstractWorld worldIn) {
        super("Mavia", x, y, game, worldIn);

        this.dialog = new MaviaDialog();
        this.dialog.starting = "mavia_option_1";
        this.dialog.ending = "mavia_option_0";

        this.dialogSection = this.dialog.getStarting();
        this.display = game.getAsset().getAssets().findRegion("mavia_face");
        this.type = EntityNPCType.MAVIA;
    }

    public void setInteriorEntrance(float interiorEntranceY) {
        this.interiorEntranceY = interiorEntranceY;
    }

    @Override
    public boolean nextOrEnd(String option) {
        if (dialog.isEnd(option)) {
            setSpeakingTo(false);
            return true;
        } else if (option.equalsIgnoreCase("mavia_option_next_0")) {
            // first stage, meet mavia inside, dialog is also finished.
            questManager.getQuest(QuestType.HUNNEWELL).setQuestInformation("Follow Mavia inside her house.");
            worldIn.getGui().getQuest().startTrackingQuest(questManager.getQuest(QuestType.HUNNEWELL));

            setSpeakingTo(false);
            isMoving = true;

            dialogSection = dialog.sections.get("mavia_option_11");
            this.drawDialogAnimationTile = false;
            return true;
        }

        dialogSection = dialog.sections.get(option);
        return false;
    }

    @Override
    public void update(Player player, float delta) {
        this.speakable = distance <= 2f;
        super.update(player, delta);

        if (isMoving) {
            this.speakable = false;
            this.position.y += delta;
            if (position.y > interiorEntranceY) {
                worldIn.removeInteractableEntityFromWorld(this);
                worldIn.getInterior(Interior.MAVIA_HOUSE).setEnterable(true);
                isMoving = false;
                speakable = true;
                return;
            }
            renderer.update(Rotation.FACING_UP, true);
        }
    }

    @Override
    public void render(SpriteBatch batch, float scale) {
        if (isMoving()) {
            renderer.render(Gdx.graphics.getDeltaTime(), position.x, position.y, batch);
        } else {
            super.render(batch, scale);
        }
    }

    @Override
    public void load(Asset asset) {
        this.entityTexture = asset.getAssets().findRegion("mavia_facing_down");
        this.width = entityTexture.getRegionWidth();
        this.height = entityTexture.getRegionHeight();

        this.renderer = new EntityAnimationRenderer(asset.getAssets(), Rotation.FACING_UP, width * GlobalGameRenderer.SCALE, height * GlobalGameRenderer.SCALE, true);
        renderer.load();

        this.rotations.put(Rotation.FACING_UP, "mavia_facing_down");
        this.rotations.put(Rotation.FACING_DOWN, "mavia_facing_down");
        this.rotations.put(Rotation.FACING_LEFT, "mavia_facing_down");
        this.rotations.put(Rotation.FACING_RIGHT, "mavia_facing_down");
    }
}
