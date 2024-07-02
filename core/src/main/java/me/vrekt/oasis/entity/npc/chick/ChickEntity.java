package me.vrekt.oasis.entity.npc.chick;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.components.AiWalkToGoalsComponent;
import me.vrekt.oasis.ai.goals.EntityGoal;
import me.vrekt.oasis.ai.goals.EntityMapGoal;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.EntityType;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.entity.component.animation.AnimationType;
import me.vrekt.oasis.entity.component.animation.EntityAnimation;
import me.vrekt.oasis.entity.component.animation.EntityAnimationBuilder;
import me.vrekt.oasis.entity.component.animation.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.enemy.fsm.EntityStateMachine;
import me.vrekt.oasis.entity.npc.chick.states.ChickPeckingState;
import me.vrekt.oasis.world.GameWorld;

/**
 * Chick
 */
public final class ChickEntity extends GameEntity {

    private static final float PECKING_CHANCE = 0.5f;
    public static final String ENTITY_KEY = "oasis:chick";
    public static final String NAME = "Chick";

    private EntityAnimationComponent animationComponent;
    private AiWalkToGoalsComponent component;

    private ChickPeckingState peckingState;

    public ChickEntity(GameWorld world, Vector2 position, OasisGame game) {
        this.worldIn = world;
        this.key = ENTITY_KEY;
        this.type = EntityType.CHICK;
        this.player = game.getPlayer();
        this.stateMachine = new EntityStateMachine(this);

        setName(NAME);
        setPosition(position.x, position.y, false);
    }

    @Override
    public void load(Asset asset) {
        this.parentWorld = worldIn;

        addTexturePart(EntityRotation.UP, asset.get("chick_walking_up_idle"), false);
        addTexturePart(EntityRotation.DOWN, asset.get("chick_walking_down_idle"), false);
        addTexturePart(EntityRotation.LEFT, asset.get("chick_walking_left_idle"), true);
        addTexturePart(EntityRotation.RIGHT, asset.get("chick_walking_right_idle"), false);
        createBB(activeEntityTexture.getRegionWidth(), activeEntityTexture.getRegionHeight());

        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        final EntityAnimationBuilder builder = new EntityAnimationBuilder(asset)
                .moving(EntityRotation.LEFT, 0.4f, "chick_walking_left", 2)
                .add(animationComponent)
                .moving(EntityRotation.DOWN, 0.4f, "chick_walking_down", 2)
                .add(animationComponent)
                .moving(EntityRotation.RIGHT, 0.4f, "chick_walking_right", 2)
                .add(animationComponent)
                .moving(EntityRotation.UP, 0.4f, "chick_walking_up", 2)
                .add(animationComponent);

        builder.animation(AnimationType.PECKING_DOWN, 0.15f, "chick_pecking_down", 3)
                .add(animationComponent)
                .animation(AnimationType.PECKING_UP, 0.15f, "chick_pecking_up", 3)
                .add(animationComponent)
                .animation(AnimationType.PECKING_LEFT, 0.15f, "chick_pecking_left", 3)
                .add(animationComponent)
                .animation(AnimationType.PECKING_RIGHT, 0.15f, "chick_pecking_right", 3)
                .add(animationComponent);

        builder.dispose();

        createBoxBody(worldIn.boxWorld());
        component = new AiWalkToGoalsComponent(this);
        // every 120 ticks
        component.setPathingInterval(120);
        component.setMaxLinearSpeed(1.0f);
        component.setMaxLinearAcceleration(1.0f);
        // attempt to stop weird bobbing movement, incomplete still WIP
        component.steering().setMovementTolerance(0.001f, 0.001f);
        addAiComponent(component);

        peckingState = new ChickPeckingState(this);
        // the pecking state lasts for 4.5 seconds
        peckingState.setStateTime(4.5f);
    }

    @Override
    public EntityMapGoal registerGoal(EntityGoal goal, Vector2 position) {
        final EntityMapGoal g = super.registerGoal(goal, position);

        // register the general goal point for pecking
        component.addGoalPoint(g, gp -> {
            // random chance to peck, or just sit there.
            if (MathUtils.randomBoolean(PECKING_CHANCE)) {
                peckingState.setActiveAnimation(getPeckingAnimation());
                stateMachine.enter(peckingState);
            }
        });
        return g;
    }

    /**
     * Get the right pecking animation from the current entity rotation
     *
     * @return animation
     */
    private EntityAnimation getPeckingAnimation() {
        return switch (rotation) {
            case UP -> animationComponent.get(AnimationType.PECKING_UP);
            case DOWN -> animationComponent.get(AnimationType.PECKING_DOWN);
            case LEFT -> animationComponent.get(AnimationType.PECKING_LEFT);
            case RIGHT -> animationComponent.get(AnimationType.PECKING_RIGHT);
        };
    }

    @Override
    public void update(float delta) {
        if (stateMachine.isInState(peckingState)) {
            if (peckingState.isFinished()) {
                // assign a new goal immediately
                component.resume();
                stateMachine.exit();
                return;
            }

            // otherwise, update pecking state
            stateMachine.update(delta);
        } else {
            updateAi(delta);
            if (component.isAtGoal()) {
                // full-stop
                body.setLinearVelocity(0, 0);
            }
            // TODO: Remove this, better velocity updating
            setVelocity(body.getLinearVelocity(), false);
        }

        rotation = component.getFacingDirection();
        updateRotationTextureState();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (stateMachine.isInState(peckingState) && peckingState.isActive()) {
            peckingState.render(batch, delta);
        } else {
            if (!isMoving()) {
                drawCurrentPosition(batch, activeEntityTexture);
            } else {
                drawCurrentPosition(batch, animationComponent.animateMoving(rotation, delta));
            }
        }
    }
}
