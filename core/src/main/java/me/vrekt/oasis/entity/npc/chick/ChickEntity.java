package me.vrekt.oasis.entity.npc.chick;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import me.vrekt.oasis.entity.npc.chick.states.ChickDrinkAndPeckState;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.utility.collision.CollisionType;
import me.vrekt.oasis.world.GameWorld;

/**
 * Chick
 */
public final class ChickEntity extends GameEntity {

    private static final float GOAL_CHANCE = 0.5f;
    private static final float REWARD_CHANCE = 0.1f;
    private static final int MAX_REWARDS = 2;

    public static final String ENTITY_KEY = "oasis:chick";
    public static final String NAME = "Chick";

    private EntityAnimationComponent animationComponent;
    private AiWalkToGoalsComponent component;

    private ChickDrinkAndPeckState state;
    private int rewardsGiven;

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

        disableCollisionFor(CollisionType.MAP_BOUNDS);
        disableCollisionFor(CollisionType.PLAYER);
        disableCollisionFor(CollisionType.OTHER_ENTITY);

        addTexturePart(EntityRotation.UP, asset.get("chick_walking_up_idle"), false);
        addTexturePart(EntityRotation.DOWN, asset.get("chick_walking_down_idle"), false);
        addTexturePart(EntityRotation.LEFT, asset.get("chick_walking_left_idle"), true);
        addTexturePart(EntityRotation.RIGHT, asset.get("chick_walking_right_idle"), false);
        createBB(activeEntityTexture.getRegionWidth() / 2f, activeEntityTexture.getRegionHeight() / 2f);

        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        final EntityAnimationBuilder builder = new EntityAnimationBuilder(asset)
                .moving(EntityRotation.LEFT, 0.4f, "chick_walking_left", 3)
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

        createRectangleBody(worldIn.boxWorld(), new Vector2(1, 1));
        component = new AiWalkToGoalsComponent(this);
        // every 120 ticks
        component.setPathingInterval(120);
        component.setMaxLinearSpeed(1.0f);
        component.setMaxLinearAcceleration(1.0f);
        addAiComponent(component);

        // splashing animation when chick is drinking
        final Animation<TextureRegion> splash = new Animation<>(0.1f,
                asset.get("chick_drink_splash", 1),
                asset.get("chick_drink_splash", 2),
                asset.get("chick_drink_splash", 3));
        splash.setPlayMode(Animation.PlayMode.LOOP);

        // pecking grass animation
        final Animation<TextureRegion> peck = new Animation<>(0.15f,
                asset.get("chick_peck", 1),
                asset.get("chick_peck", 2),
                asset.get("chick_peck", 3));
        peck.setPlayMode(Animation.PlayMode.LOOP);

        state = new ChickDrinkAndPeckState(this);
        state.setPeckAnimation(peck);
        state.setSplashAnimation(splash);
    }

    @Override
    public EntityMapGoal registerGoal(EntityGoal goal, Vector2 position, EntityRotation rotation) {
        final EntityMapGoal g = super.registerGoal(goal, position, rotation);

        // register the general goal point for pecking
        component.addGoalPoint(g, gp -> {
            // face goal rotation
            if (rotation != null) setRotation(rotation);

            // random chance to peck, or just sit there.
            if (MathUtils.randomBoolean(GOAL_CHANCE)) {
                state.setActiveAnimation(getPeckingAnimation(), gp.goal());
                // randomize a bit
                state.setStateTime(MathUtils.random(2.5f, 4.5f));
                stateMachine.enter(state);

                // random chance for the player to get a reward
                if (goal != EntityGoal.DRINK && MathUtils.randomBoolean(REWARD_CHANCE) && rewardsGiven <= MAX_REWARDS) {
                    worldIn.spawnWorldDrop(pickRandomReward(), 1, getPosition().cpy().add(0.25f, 0.25f));
                    rewardsGiven++;
                }
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
        if (stateMachine.isInState(state)) {
            if (state.isFinished()) {
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
                setVelocity(0, 0, true);
            }
        }

        rotation = component.getFacingDirection();
        updateRotationTextureState();
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (stateMachine.isInState(state) && state.isActive()) {
            state.render(batch, delta);
        } else {
            if (!isMoving()) {
                drawCurrentPosition(batch, activeEntityTexture);
            } else {
                drawCurrentPosition(batch, animationComponent.animateMoving(rotation, delta));
            }
        }
    }

    /**
     * Pick a random award
     */
    private Items pickRandomReward() {
        final int reward = MathUtils.random(0, 2);
        return switch (reward) {
            case 0 -> Items.LOCK_PICK;
            case 1 -> Items.PIG_HEART;
            case 2 -> Items.QUICKSTEP_ARTIFACT;
            default -> Items.NO_ITEM;
        };
    }

}
