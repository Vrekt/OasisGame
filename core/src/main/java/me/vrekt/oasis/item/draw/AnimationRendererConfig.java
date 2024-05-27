package me.vrekt.oasis.item.draw;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.entity.component.facing.EntityRotation;

public final class AnimationRendererConfig {

    private final IntMap<Applicator> applicatorIntMap = new IntMap<>();

    public AnimationRendererConfig() {

    }

    public AnimationRendererConfig idle(EntityRotation rotation, float angle) {
        return this;
    }

    public AnimationRendererConfig rotation(EntityRotation rotation, float x, float y) {
        applicatorIntMap.put(rotation.ordinal(), vector2 -> vector2.add(x, y));
        return this;
    }

    public void manipulate(EntityRotation rotation, Vector2 position) {
        applicatorIntMap.get(rotation.ordinal()).manipulate(position);
    }

    private interface Applicator {
        void manipulate(Vector2 vector2);
    }

}
