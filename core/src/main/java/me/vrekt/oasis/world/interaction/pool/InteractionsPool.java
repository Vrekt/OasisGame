package me.vrekt.oasis.world.interaction.pool;

import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.world.interaction.Interaction;

/**
 * Manages interaction pools.
 */
public final class InteractionsPool extends Pool<Interaction> {

    @Override
    protected Interaction newObject() {
        return new Interaction();
    }
}
