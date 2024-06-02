package me.vrekt.oasis.entity.component.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class EntityAnimation {

    private Animation<TextureRegion> moving;
    private float movingAnimationTime;
    private TextureRegion[] hurting;

    void moving(Animation<TextureRegion> animation) {
        this.moving = animation;
        this.moving.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void setHurting(TextureRegion[] hurting) {
        this.hurting = hurting;
    }

    TextureRegion animateMoving(float deltaTime) {
        movingAnimationTime += deltaTime;
        return moving.getKeyFrame(movingAnimationTime);
    }

    TextureRegion animateHurting() {
        return hurting[moving.getKeyFrameIndex(movingAnimationTime)];
    }

}
