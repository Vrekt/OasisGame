package me.vrekt.oasis.utility.collision;

import com.badlogic.gdx.physics.box2d.*;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.player.mp.NetworkPlayer;
import me.vrekt.oasis.entity.player.sp.PlayerSP;

/**
 * Basis collision listener
 */
public final class BasicEntityCollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getBody().getUserData() != null
                && fixtureB.getBody().getUserData() != null) {
            final Object a = fixtureA.getBody().getUserData();
            final Object b = fixtureB.getBody().getUserData();

            // ignore interactable collision
            if (a instanceof PlayerSP && b instanceof GameEntity) {
                contact.setEnabled(false);
            } else if (a instanceof GameEntity && b instanceof PlayerSP) {
                contact.setEnabled(false);
            }

            // ignore player collisions
            if (fixtureA.getBody().getUserData() instanceof PlayerSP
                    && fixtureB.getBody().getUserData() instanceof NetworkPlayer) {
                if (!((PlayerSP) fixtureA.getBody().getUserData()).isCollisionEnabled()
                        || !((NetworkPlayer) fixtureB.getBody().getUserData()).isCollisionEnabled()) {
                    contact.setEnabled(false);
                }
            }
        }

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
