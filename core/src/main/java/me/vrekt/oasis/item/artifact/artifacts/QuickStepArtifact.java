package me.vrekt.oasis.item.artifact.artifacts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.artifact.Artifact;

/**
 * Player move speed modifier
 */
public final class QuickStepArtifact extends Artifact {

    private float moveSpeed, tick;

    public QuickStepArtifact() {
        super("Quickstep", "Allows you to move around the environment faster.");
        this.artifactDuration = 1.5f;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get("quickstep_artifact"));

        this.artifactParticle = new Sprite(asset.get("quickstep_artifact_effect_arrow"));
        this.artifactParticle.setColor(0, 148, 255, 1.0f);
    }

    @Override
    public boolean apply(OasisPlayerSP player, float tick) {
        if (this.isApplied || (tick - this.tick) < artifactCooldown) {
            return false;
        }
        this.moveSpeed = player.getMoveSpeed();
        player.setMoveSpeed(player.getMoveSpeed() + (artifactLevel * 0.75f));

        createEffect(player);
        artifactParticle.setPosition(player.getInterpolated().x, player.getInterpolated().y);

        this.tick = tick;
        this.isApplied = true;
        return true;
    }

    @Override
    public void expire(OasisPlayerSP player) {
        player.setMoveSpeed(moveSpeed);
        this.isApplied = false;
    }

    @Override
    public void update(OasisPlayerSP player, float tick) {
        if (tick - this.tick >= artifactDuration) {
            this.expire(player);
        }

        artifactParticle.setPosition(player.getInterpolated().x, player.getInterpolated().y);
    }
}
