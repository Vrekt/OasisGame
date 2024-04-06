package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.ai.SeekAndPursuePlayer;
import me.vrekt.oasis.entity.ai.utilities.PlayerSteeringLocation;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.npc.EntityEnemy;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Mainly used for testing combat features!
 */
public final class TutorialCombatDummy extends EntityEnemy {

    public TutorialCombatDummy(String name, Vector2 position, OasisPlayer player, OasisWorld worldIn, OasisGame game) {
        super(name, position, player, worldIn, game, EntityNPCType.DUMMY);
        setAngle(1.0f);

        this.ai = new SeekAndPursuePlayer(this, player, new PlayerSteeringLocation(player), position);
        setIgnoreCollision(true);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (!getVelocity().isZero()) {
            draw(batch, animationComponent.playWalkingAnimation(entityRotation.ordinal(), delta));
        } else {
            if (currentRegionState != null) {
                draw(batch, currentRegionState);
            }
        }
    }

    @Override
    public void setEntityRotation(EntityRotation entityRotation) {
        super.setEntityRotation(entityRotation);
    }

    @Override
    public void damage(float tick, float amount, float knockback, boolean isCritical) {
        super.damage(tick, amount, knockback, isCritical);

        float knockbackModifierX = 0.0f;
        float knockbackModifierY = 0.0f;
        if (player.getPlayerRotation() == EntityRotation.LEFT) {
            knockbackModifierX = getPosition().x * -knockback;
        } else if (player.getPlayerRotation() == EntityRotation.RIGHT) {
            knockbackModifierX = getPosition().x * knockback;
        } else if (player.getPlayerRotation() == EntityRotation.UP) {
            knockbackModifierY = getPosition().y * knockback;
        } else if (player.getPlayerRotation() == EntityRotation.DOWN) {
            knockbackModifierY = getPosition().y * -knockback;
        }

        body.applyLinearImpulse(knockbackModifierX, knockbackModifierY, getPosition().x, getPosition().y, true);
    }

    private void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, body.getPosition().x, body.getPosition().y, getScaledWidth(), getScaledHeight());
    }

    @Override
    public void load(Asset asset) {
        super.load(asset);
        setUseAnimations();

        putRegion("healer_walking_up_idle", asset.get("healer_walking_up_idle"));
        putRegion("healer_walking_down_idle", asset.get("healer_walking_down_idle"));
        putRegion("healer_walking_left_idle", asset.get("healer_walking_left_idle"));
        putRegion("healer_walking_right_idle", asset.get("healer_walking_right_idle"));
        putRegion("facing_down", asset.get("mavia_facing_down"));
        currentRegionState = getRegion("healer_walking_down_idle");

        // up, down, left, right
        animationComponent.registerWalkingAnimation(0, 0.25f, asset.get("healer_walking_up", 1), asset.get("healer_walking_up", 2));
        animationComponent.registerWalkingAnimation(1, 0.25f, asset.get("healer_walking_down", 1), asset.get("healer_walking_down", 2));
        animationComponent.registerWalkingAnimation(2, 0.25f, asset.get("healer_walking_left", 1), asset.get("healer_walking_left", 2));
        animationComponent.registerWalkingAnimation(3, 0.25f, asset.get("healer_walking_right", 1), asset.get("healer_walking_right", 2));

        this.bounds = new Rectangle(getPosition().x, getPosition().y, getScaledWidth(), getScaledHeight());
        setSize(currentRegionState.getRegionWidth(), currentRegionState.getRegionHeight(), OasisGameSettings.SCALE);

        createBoxBody(gameWorldIn.getEntityWorld());
    }

    @Override
    public void update(float v) {
        super.update(v);

        if (ai != null)
            ai.update(v);

        speakable = false;
    }

    @Override
    public boolean advanceDialogStage(String option) {
        return false;
    }

    @Override
    public boolean advanceDialogStage() {
        return false;
    }
}
