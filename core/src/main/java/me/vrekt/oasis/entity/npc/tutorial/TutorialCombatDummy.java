package me.vrekt.oasis.entity.npc.tutorial;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.npc.EntityEnemy;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;

public final class TutorialCombatDummy extends EntityEnemy {

    public TutorialCombatDummy(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game) {
        super(name, position, player, worldIn, game, EntityNPCType.DUMMY);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        super.render(batch, delta);
    }

    @Override
    public void load(Asset asset) {
        putRegion("facing_up", asset.get("mavia_facing_up"));
        putRegion("facing_down", asset.get("mavia_facing_down"));

        currentRegionState = getRegion("facing_down");
        setSize(currentRegionState.getRegionWidth(), currentRegionState.getRegionHeight(), OasisGameSettings.SCALE);

        this.bounds = new Rectangle(getPosition().x, getPosition().y, getWidthScaled(), getHeightScaled());
    }

    @Override
    public void update(float v) {
        super.update(v);
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
