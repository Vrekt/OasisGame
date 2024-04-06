package me.vrekt.oasis.item.artifact.artifacts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.item.artifact.Artifact;

public final class FusionArtifact extends Artifact {

    public FusionArtifact() {
        super("artifact:fusion", "Fusion", "Allows you to combine two elements in one attack.");
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get("fusion_artifact"));
    }

    @Override
    public boolean apply(OasisPlayer player, float tick) {
        return false;
    }

    @Override
    public void expire(OasisPlayer player) {

    }

    @Override
    protected void update(OasisPlayer player, float tick) {

    }
}
