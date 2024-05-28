package me.vrekt.oasis.entity.enemy.fsm.states;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.ai.components.AiComponent;
import me.vrekt.oasis.ai.components.AiComponentType;
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

    public AiProcessingState() {
        super(STATE_ID);
    }

    public AiProcessingState populateComponents(AiComponent... components) {
        this.components.addAll(components);
        return this;
    }

    public AiProcessingState using(Processor processor) {
        this.processor = processor;
        return this;
    }

    public AiProcessingState requires(ProcessingRequirement requirement) {
        this.requirement = requirement;
        return this;
    }

    /**
     * Get a component
     * TODO: Maybe return array[]?
     *
     * @param type type
     * @return the component or {@code null} if none
     */
    private AiComponent get(AiComponentType type) {
        for (AiComponent component : components) {
            if (component.type() == type) {
                return component;
            }
        }
        return null;
    }

    @Override
    public void update(float delta) {
        if (!requirement.shouldProcess()) return;
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
