package me.vrekt.oasis.utility.collision;

import com.badlogic.gdx.physics.box2d.*;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.utility.logging.GameLogging;

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

        final boolean hasA = fixtureA.getBody().getUserData() != null;
        final boolean hasB = fixtureB.getBody().getUserData() != null;
        if (!hasA || !hasB) {
            GameLogging.info(this, "Unknown collision! a=%s, b=%s", fixtureA.getType(), fixtureB.getType());
            return;
        }

        final Object a = fixtureA.getBody().getUserData();
        final Object b = fixtureB.getBody().getUserData();
        if (a instanceof GameEntity entity && b instanceof CollisionType type) {
            handleEntityCollision(entity, type, contact);
        } else if (b instanceof GameEntity entity && a instanceof CollisionType type) {
            handleEntityCollision(entity, type, contact);
        } else if (a instanceof GameEntity && b instanceof GameEntity) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    /**
     * Handle simple entity collision
     *
     * @param entity  entity
     * @param type    type
     * @param contact contact
     */
    private void handleEntityCollision(GameEntity entity, CollisionType type, Contact contact) {
        if (entity.isCollisionDisabled(type)) {
            contact.setEnabled(false);
        }
    }

}
