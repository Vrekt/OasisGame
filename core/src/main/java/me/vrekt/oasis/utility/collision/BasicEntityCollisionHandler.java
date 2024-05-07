package me.vrekt.oasis.utility.collision;

import com.badlogic.gdx.physics.box2d.*;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;

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
            if (a instanceof OasisPlayer && b instanceof EntityInteractable) {
                contact.setEnabled(false);
            } else if (a instanceof EntityInteractable && b instanceof OasisPlayer) {
                contact.setEnabled(false);
            }

            // ignore player collisions
            if (fixtureA.getBody().getUserData() instanceof OasisPlayer
                    && fixtureB.getBody().getUserData() instanceof OasisNetworkPlayer) {
                if (((OasisPlayer) fixtureA.getBody().getUserData()).isPlayerCollisionDisabled()
                        || ((OasisNetworkPlayer) fixtureB.getBody().getUserData()).isPlayerCollisionDisabled()) {
                    contact.setEnabled(false);
                }
            }
        }

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
