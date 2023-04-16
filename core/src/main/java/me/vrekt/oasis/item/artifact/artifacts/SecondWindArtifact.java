package me.vrekt.oasis.item.artifact.artifacts;

import com.badlogic.gdx.graphics.g2d.Sprite;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.item.artifact.Artifact;

public final class SecondWindArtifact extends Artifact {

    public SecondWindArtifact() {
        super("Second Wind", "Allows you to heal yourself.");
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get("secondwind_artifact"));
    }

    @Override
    public boolean apply(OasisPlayerSP player, float tick) {
        return false;
    }

    @Override
    public void expire(OasisPlayerSP player) {

    }

    @Override
    protected void update(OasisPlayerSP player, float tick) {

    }
}
