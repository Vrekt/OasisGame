package me.vrekt.oasis.entity.enemy.fsm;

import com.badlogic.gdx.utils.Pools;
import me.vrekt.oasis.entity.GameEntity;

public final class EntityStateMachine {

    private final GameEntity owner;
    private ProcessingState state;

    public EntityStateMachine(GameEntity owner) {
        this.owner = owner;
    }

    public void initial(ProcessingState state) {
        this.state = state;
        this.state.enter();
    }

    public void enterObtained(Class<ProcessingState> obtainable) {
        enter(Pools.obtain(obtainable));
    }

    public void enter(ProcessingState state) {
        if (this.state != null) this.state.exit();

        state.enter();
        this.state = state;
    }

    public void update(float delta) {
        if (state != null) state.update(delta);
    }

    public boolean isInSameState(int other) {
        return state.processingStateId == other;
    }

    public void stop() {
        this.state = null;
    }

}
