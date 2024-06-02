package me.vrekt.oasis.entity.enemy.fsm.states.ai;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.ai.components.AiComponent;
import me.vrekt.oasis.entity.enemy.fsm.ProcessingState;
import me.vrekt.oasis.entity.enemy.fsm.processing.ProcessingRequirement;
import me.vrekt.oasis.entity.enemy.fsm.processing.Processor;

/**
 * Handles processing the AI
 */
public final class AiProcessingState extends ProcessingState implements Pool.Poolable {

    private static final int STATE_ID = 0;

    private final Array<AiComponent> components = new Array<>();
    private Processor processor;
    private ProcessingRequirement requirement;
    private Runnable otherwiseAction;

    public AiProcessingState() {
        super(STATE_ID);
    }

    public AiProcessingState populateComponents(AiComponent... components) {
        this.components.addAll(components);
        return this;
    }

    public AiProcessingState processor(Processor processor) {
        this.processor = processor;
        return this;
    }

    public AiProcessingState requires(ProcessingRequirement requirement) {
        this.requirement = requirement;
        return this;
    }

    public AiProcessingState otherwise(Runnable action) {
        this.otherwiseAction = action;
        return this;
    }

    /**
     * Allow outside velocity to influence
     *
     * @param emptyVelocity if velocity should be set to 0
     */
    public void handleOutsideVelocityInfluence(boolean emptyVelocity) {
        for (AiComponent component : components) {
            component.pause(emptyVelocity);
        }
    }

    /**
     * Resume
     */
    public void resumeNormalVelocityInfluence() {
        for (AiComponent component : components) {
            component.resume();
        }
    }

    @Override
    public void update(float delta) {
        if (requirement != null && !requirement.shouldProcess()) {
            if (otherwiseAction != null) otherwiseAction.run();
            return;
        }

        if (processor != null) processor.update(delta);
        for (AiComponent component : components) {
            component.update(delta);
        }
    }

    @Override
    public void reset() {
        components.clear();
    }
}
