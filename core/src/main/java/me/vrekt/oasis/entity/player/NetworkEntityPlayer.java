package me.vrekt.oasis.entity.player;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;
import me.vrekt.oasis.graphics.Renderable;

/**
 * A multiplayer player entity
 */
public abstract class NetworkEntityPlayer extends LunarNetworkEntityPlayer implements Renderable {

    // describes the view/renderable stuff
    protected final Vector3 view = new Vector3(0, 0, 0);
    protected boolean inView;

    public NetworkEntityPlayer(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public NetworkEntityPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }

    @Override
    public boolean isInView(Camera camera) {
        inView = Renderable.isInViewExtended(view, getX(), getY(), camera.frustum);
        return inView;
    }

}
