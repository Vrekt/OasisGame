package me.vrekt.oasis.utility.collision;

import com.badlogic.gdx.physics.box2d.*;
import me.vrekt.oasis.entity.npc.EntityEnemy;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;

/**
 * Basis collision listener
 */
public final class BasicPlayerCollisionHandler implements ContactListener {

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

            // ignore enemy collision
            if (a instanceof OasisPlayer && b instanceof EntityEnemy) {
                if (((EntityEnemy) b).isIgnoreCollision()) {
                    contact.setEnabled(false);
                }
            } else if (a instanceof EntityEnemy && b instanceof OasisPlayer) {
                if (((EntityEnemy) a).isIgnoreCollision()) {
                    contact.setEnabled(false);
                }
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
