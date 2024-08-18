package me.vrekt.oasis.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.ai.components.AiComponent;
import me.vrekt.oasis.ai.goals.EntityGoal;
import me.vrekt.oasis.ai.goals.EntityMapGoal;
import me.vrekt.oasis.ai.utility.AiVectorUtility;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.combat.DamageType;
import me.vrekt.oasis.combat.EntityDamageAnimator;
import me.vrekt.oasis.entity.component.EntityPropertiesComponent;
import me.vrekt.oasis.entity.component.EntityTextureComponent;
import me.vrekt.oasis.entity.component.EntityTransformComponent;
import me.vrekt.oasis.entity.component.GlobalEntityMapper;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.entity.component.status.EntityStatus;
import me.vrekt.oasis.entity.enemy.EntityEnemy;
import me.vrekt.oasis.entity.enemy.fsm.EntityStateMachine;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.graphics.Drawable;
import me.vrekt.oasis.graphics.Viewable;
import me.vrekt.oasis.gui.input.Cursor;
import me.vrekt.oasis.gui.input.MouseListener;
import me.vrekt.oasis.save.world.entity.EntitySaveState;
import me.vrekt.oasis.utility.ResourceLoader;
import me.vrekt.oasis.utility.collision.CollisionType;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.effects.AreaEffectCloud;
import me.vrekt.shared.network.state.NetworkEntityState;

/**
 * Represents a basic entity within Oasis
 */
public abstract class GameEntity implements MouseListener, Viewable, Drawable, ResourceLoader, Disposable {

    protected String key;
    protected EntityType type;

    protected Entity entity;
    protected Body body;

    protected boolean fixedRotation;
    protected boolean collisionEnabled;

    protected PlayerSP player;
    protected boolean isNearby, inView;
    protected float distanceFromPlayer;
    protected NinePatch gradient;
    protected boolean dynamicSize;
    protected Rectangle bb;

    // the active texture this entity is using to draw itself
    protected TextureRegion activeEntityTexture;
    protected EntityRotation rotation = EntityRotation.DOWN, previousRotation = EntityRotation.LEFT;

    // the world this entity is in
    protected GameWorld worldIn;
    // if entity is in interior
    protected GameWorld parentWorld;

    protected Array<EntityMapGoal> goals = new Array<>();
    protected Bag<CollisionType> collisionTypes = new Bag<>();

    protected Array<AiComponent> aiComponents = new Array<>();
    protected boolean isPaused;
    protected float pauseTime, pauseForTime;

    // entity is queued to be removed from the world.
    protected boolean queueRemoval;

    protected AreaEffectCloud cloudApartOf;
    protected IntMap<EntityStatus> statuses = new IntMap<>();

    protected EntityStateMachine stateMachine;
    protected float physicsScale = 1.0f;

    protected boolean renderWithMap;
    protected String renderAfterLayer;

    protected boolean isNetworked;
    protected boolean hasEnteredMouse;

    // networking interpolation
    // TODO: NET-2 less messy
    protected final Vector2 incomingNetworkPosition = new Vector2();
    protected final Vector2 velocity = new Vector2();
    protected final Vector2 predicted = new Vector2();
    protected final Vector2 lerped = new Vector2();
    protected final Vector2 trajectory = new Vector2();
    protected final Vector2 smoothed = new Vector2();

    public GameEntity() {
        entity = new Entity();
        addComponents();
        setHealth(100.0f);
    }

    public void setWorldIn(GameWorld worldIn) {
        this.worldIn = worldIn;
    }

    /**
     * Load this saved generic entity
     *
     * @param save save
     */
    public void loadSavedEntity(EntitySaveState save) {
        setPosition(save.position());
        setHealth(save.health());
        setMoveSpeed(save.moveSpeed());
        rotation = save.rotation();
    }

    /**
     * @return key, basically the type.
     */
    public String key() {
        return key;
    }

    /**
     * @return type of
     */
    public EntityType type() {
        return type;
    }

    /**
     * @return {@code true} if this entity should be rendered during the map drawing
     */
    public boolean renderWithMap() {
        return renderWithMap;
    }

    /**
     * @return the layer to render after if renderWithMap is true
     */
    public String renderAfterLayer() {
        return renderAfterLayer;
    }

    /**
     * Add default components
     */
    protected void addComponents() {
        entity.add(new EntityPropertiesComponent());
        entity.add(new EntityTransformComponent());
        entity.add(new EntityTextureComponent());
    }

    public void setPhysicsScale(float scale) {
        this.physicsScale = scale;
    }

    /**
     * Register a goal for this entity
     *
     * @param goal     goal
     * @param position position
     * @param rotation rotation entity should be to execute this goal
     */
    public EntityMapGoal registerGoal(EntityGoal goal, Vector2 position, EntityRotation rotation) {
        final EntityMapGoal g = new EntityMapGoal(goal, position, rotation);
        goals.add(g);
        return g;
    }

    /**
     * Disable collision with another entity or object
     *
     * @param type type
     */
    public void disableCollisionFor(CollisionType type) {
        collisionTypes.add(type);
    }

    /**
     * Check if collision with the type is disabled
     *
     * @param type type
     * @return {@code true} if so
     */
    public boolean isCollisionDisabled(CollisionType type) {
        return collisionTypes.contains(type);
    }

    /**
     * Update active texture based on entity rotation
     */
    protected void updateRotationTextureState() {
        if (previousRotation != rotation) {
            if (hasTexturePart(rotation)) activeEntityTexture = getTexturePart(rotation);
            previousRotation = rotation;
        }
    }

    /**
     * @return the entities unique ID
     */
    public int entityId() {
        return getPropertiesComponent().entityId;
    }

    /**
     * @param entityId the entities unique ID
     */
    public void setEntityId(int entityId) {
        getPropertiesComponent().entityId = entityId;
    }

    /**
     * @return the name of this entity
     */
    public String name() {
        return getPropertiesComponent().entityName;
    }

    /**
     * @param name the name of this entity
     */
    public void setName(String name) {
        getPropertiesComponent().entityName = name;
    }

    /**
     * Set properties within {@link EntityPropertiesComponent}
     *
     * @param name the entity name
     * @param id   the entity unique ID
     */
    public void setProperties(String name, int id) {
        getPropertiesComponent().setProperties(id, name);
    }

    /**
     * Set if this entity has fixed rotation
     * This will only apply if the {@code getBodyHandler} is not {@code null}
     *
     * @param rotation state
     */
    public void setHasFixedRotation(boolean rotation) {
        this.fixedRotation = rotation;
    }

    /**
     * disable collision with entities and players
     */
    public void disableCollision() {
        this.collisionEnabled = false;
    }

    /**
     * @return {@code true} if collision is enabled
     */
    public boolean isCollisionEnabled() {
        return this.collisionEnabled;
    }

    /**
     * @return the width of this entity
     */
    public float getWidth() {
        return getPropertiesComponent().width;
    }

    /**
     * @return the height of this entity
     */
    public float getHeight() {
        return getPropertiesComponent().height;
    }

    /**
     * @return the scaled width defined with {@code setSize}
     */
    public float getScaledWidth() {
        return getPropertiesComponent().getScaledWidth();
    }

    /**
     * @return {@code  getHeight} * {@code getWorldScale}
     */
    public float getScaledHeight() {
        return getPropertiesComponent().getScaledHeight();
    }

    /**
     * Set size and world/entity scaling
     *
     * @param width  width
     * @param height height
     * @param scale  scale
     */
    public void setSize(float width, float height, float scale) {
        getPropertiesComponent().setEntitySize(width, height, scale);
    }

    /**
     * @return size as vector
     */
    public Vector3 getSizeVector() {
        return new Vector3(getPropertiesComponent().width, getPropertiesComponent().height, getPropertiesComponent().scaling);
    }

    /**
     * @return the movement speed of this entity
     */
    public float getMoveSpeed() {
        return getPropertiesComponent().getSpeed();
    }

    /**
     * @param speed the movement speed of this entity
     */
    public void setMoveSpeed(float speed) {
        getPropertiesComponent().speed = speed;
    }

    /**
     * @return health of this entity defaulted to {@code  100.0f}
     */
    public float getHealth() {
        return getPropertiesComponent().health;
    }

    /**
     * @param health health of this entity
     */
    public void setHealth(float health) {
        getPropertiesComponent().health = health;
    }

    /**
     * Heal this entity by the provided amount
     *
     * @param amount the amount to heal by
     * @return the entities new health
     */
    public float heal(float amount) {
        return getPropertiesComponent().health += amount;
    }

    /**
     * Damage this entity by the provided amount
     *
     * @param amount the amount to damage by
     * @return the entities new health
     */
    public float damage(float amount) {
        return getPropertiesComponent().health -= amount;
    }

    /**
     * Damage this entity and log it
     *
     * @param amount amount
     * @param type   type
     */
    public void damage(float amount, DamageType type) {
        damage(amount);

        getWorldState().registerEntityDamage(this, amount, type);
    }

    /**
     * Render UI components and other things
     *
     * @param worldCamera camera
     * @param guiCamera   stage camera
     * @param batch       batch
     * @param animator    animator
     */
    public void postRender(Camera worldCamera, Camera guiCamera, SpriteBatch batch, EntityDamageAnimator animator) {
        for (EntityStatus status : statuses.values()) {
            if (status.isPostRender()) status.postRender(batch, worldCamera, guiCamera);
        }
    }

    /**
     * @return {@code true} if this entity is dead
     */
    public boolean isDead() {
        return getHealth() <= 0;
    }

    /**
     * @return entity should not be updated
     */
    public boolean queuedForRemoval() {
        return queueRemoval;
    }

    /**
     * Entity should be removed next tick
     */
    public void queueForRemoval() {
        this.queueRemoval = true;
    }

    /**
     * @return unused: always return 0.0f for box2d
     */
    public float getAngle() {
        return 0.0f;
    }

    /**
     * @return position of this entity, defaulted to {@code 0.0f,0.0f}
     */
    public Vector2 getPosition() {
        return body == null ? Vector2.Zero : body.getPosition();
    }

    /**
     * Check if this entity is within the point
     *
     * @param point     point
     * @param tolerance <= tolerance
     * @return {@code true} if so
     */
    public boolean within(Vector2 point, float tolerance) {
        return getPosition().dst2(point) <= tolerance;
    }

    /**
     * @return x position
     */
    public float getX() {
        return getPosition().x;
    }

    /**
     * @return y position
     */
    public float getY() {
        return getPosition().y;
    }

    /**
     * Set the position of this entity
     *
     * @param position the position
     */
    public void setPosition(Vector2 position) {
        if (body != null) body.setTransform(position, getAngle());
    }

    /**
     * Set the position of this entity
     *
     * @param x x position
     * @param y y position
     */
    public void setPosition(float x, float y) {
        if (body != null) body.setTransform(x, y, getAngle());
    }

    /**
     * Interpolate from the network, works by interpolating velocity required
     * to reach the end destination point.
     *
     * @param state       the network state
     * @param delta       gdx delta
     * @param networkTime time since the packet was sent
     */
    public void networkInterpolate(NetworkEntityState state, float delta, float networkTime) {
        // A network frame is sent every 50ms, compensate for that
        networkTime = (networkTime / 50f);

        velocity.set(body.getLinearVelocity());
        // attempt to predict where the entity will be
        predicted.set(body.getPosition()).mulAdd(velocity, delta * networkTime);
        incomingNetworkPosition.set(state.x(), state.y());

        final float factor = Math.min(0.5f * delta / networkTime, 1.0f);
        // lerp between that position and actual server position
        lerped.set(predicted).lerp(incomingNetworkPosition, factor);
        // final smoothing and trajectory calculation
        trajectory.set(lerped).sub(body.getPosition()).scl(1f / delta);
        smoothed.set(velocity).lerp(trajectory, 0.25f);
        setVelocity(smoothed);
    }

    /**
     * Teleport this entity to the given position
     *
     * @param x x
     * @param y y
     */
    public void teleport(float x, float y) {
        body.setTransform(x, y, 0.0f);
    }

    /**
     * Set if this entity is networked, if so, no AI components will be loaded.
     */
    public void setNetworked(boolean state) {
        this.isNetworked = state;
    }

    /**
     * @return previous position of this entity used for interpolation
     */
    public Vector2 getPreviousPosition() {
        return GlobalEntityMapper.transform.get(entity).previous;
    }

    /**
     * Usually set right before updating an entities current position
     *
     * @param position set previous position
     */
    public void setPreviousPosition(Vector2 position) {
        getPreviousPosition().set(position);
    }

    /**
     * @return interpolated position for drawing or anything else that requires smoothing
     */
    public Vector2 getInterpolatedPosition() {
        return GlobalEntityMapper.transform.get(entity).interpolated;
    }

    /**
     * @param x x position
     * @param y y position
     */
    public void setInterpolatedPosition(float x, float y) {
        getInterpolatedPosition().set(x, y);
    }

    /**
     * Interpolate the position of this entity
     */
    public void interpolatePosition() {
        interpolatePosition(Interpolation.linear, 1.0f);
    }

    /**
     * Interpolate the position of this entity
     *
     * @param interpolation interpolation method to use
     * @param alpha         alpha to use for 'smoothing'
     */
    public void interpolatePosition(Interpolation interpolation, float alpha) {
        final Vector2 previous = getPreviousPosition();
        final Vector2 current = body.getPosition();
        setInterpolatedPosition(interpolation.apply(previous.x, current.x, alpha), interpolation.apply(previous.y, current.y, alpha));
    }

    /**
     * @return current linear velocity of this entity
     */
    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    /**
     * @param velocity the linear velocity
     */
    public void setVelocity(Vector2 velocity) {
        if (body != null) body.setLinearVelocity(velocity);
    }

    /**
     * @param x linear X velocity
     * @param y linear Y velocity
     */
    public void setVelocity(float x, float y) {
        if (body != null) body.setLinearVelocity(x, y);
    }

    /**
     * Get rotation from velocity
     *
     * @return rotation
     */
    protected EntityRotation rotationFromVelocity() {
        return AiVectorUtility.velocityToDirection(getVelocity());
    }

    /**
     * load the AI of this entity
     */
    public void loadAi() {

    }

    /**
     * @return the box2d {@link Body} or {@code  null} if none yet
     */
    public Body body() {
        return body;
    }

    /**
     * Set the body
     *
     * @param body the body
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * The ashley {@link Entity} of this entity
     *
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the transform component of this entity
     */
    public EntityTransformComponent getTransformComponent() {
        return GlobalEntityMapper.transform.get(entity);
    }

    /**
     * @return the properties component of this entity
     */
    public EntityPropertiesComponent getPropertiesComponent() {
        return GlobalEntityMapper.properties.get(entity);
    }

    /**
     * @return texture component of this entity
     */
    public EntityTextureComponent getTextureComponent() {
        return GlobalEntityMapper.texture.get(entity);
    }

    public Rectangle bb() {
        return bb;
    }

    /**
     * Create BB
     *
     * @param w w
     * @param h h
     */
    protected void createBB(float w, float h) {
        if (body == null) {
            bb = new Rectangle(
                    getTransformComponent().position.x,
                    getTransformComponent().position.y,
                    w * OasisGameSettings.SCALE,
                    h * OasisGameSettings.SCALE);
        } else {
            bb = new Rectangle(getX(), getY(), w * OasisGameSettings.SCALE, h * OasisGameSettings.SCALE);
        }
        setSize(w, h, OasisGameSettings.SCALE);
    }

    /**
     * Create BB
     *
     * @param w w
     * @param h h
     */
    protected void createBBNoSize(float w, float h) {
        if (body == null) {
            bb = new Rectangle(
                    getTransformComponent().position.x,
                    getTransformComponent().position.y,
                    w * OasisGameSettings.SCALE,
                    h * OasisGameSettings.SCALE);
        } else {
            bb = new Rectangle(getX(), getY(), w * OasisGameSettings.SCALE, h * OasisGameSettings.SCALE);
        }
    }

    /**
     * @return origin center of the player x
     */
    public float centerX() {
        return getPosition().x + (getScaledWidth() / 2f);
    }

    /**
     * @return origin center of the player y
     */
    public float centerY() {
        return getPosition().y + (getScaledHeight() / 2f);
    }

    public EntityRotation rotation() {
        return rotation;
    }

    /**
     * @param rotation rotation
     */
    public void setRotation(EntityRotation rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the current world state of this entity
     */
    public GameWorld getWorldState() {
        return worldIn;
    }

    /**
     * @return {@code true} if this entity is in a world
     */
    public boolean isInWorld() {
        return worldIn != null || parentWorld != null;
    }

    /**
     * @return {@code true} if velocity is not zero with 0.01f
     */
    protected boolean isMoving() {
        return !getVelocity().isZero(0.01f);
    }

    @Override
    public boolean within(Vector3 mouse) {
        return isMouseInEntityBounds(mouse);
    }

    @Override
    public boolean clicked(Vector3 mouse) {
        return false;
    }

    @Override
    public Cursor enter(Vector3 mouse) {
        hasEnteredMouse = true;
        return Cursor.DEFAULT;
    }

    @Override
    public boolean hasEntered() {
        return hasEnteredMouse;
    }

    @Override
    public void exit(Vector3 mouse) {
        hasEnteredMouse = false;
    }

    /**
     * Update this game entity
     *
     * @param delta delta
     */
    public void update(float delta) {
        for (EntityStatus status : statuses.values()) {
            if (!status.isPostRender()) status.update(delta);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        for (EntityStatus status : statuses.values()) {
            if (!status.isPostRender()) status.render(batch, delta);
        }
    }

    /**
     * Map rendering
     *
     * @param batch batch
     * @param delta delta
     */
    public void mapRender(SpriteBatch batch, float delta) {

    }

    /**
     * Called when this entity will stop updating (outside update distance)
     */
    public void stopUpdating() {
        setVelocity(0, 0);
    }

    /**
     * Add an AI component
     */
    protected void addAiComponent(AiComponent component) {
        aiComponents.add(component);
    }

    /**
     * Update AI components if it is not paused
     *
     * @param delta the graphics** delta time
     */
    protected void updateAi(float delta) {
        if (!isPaused) {
            for (AiComponent component : aiComponents) {
                component.update(GdxAI.getTimepiece().getDeltaTime());
            }
        } else {
            isPaused = !GameManager.hasTimeElapsed(pauseTime, pauseForTime);
        }
    }

    /**
     * Pause AI for X seconds
     *
     * @param seconds seconds
     */
    protected void pauseFor(float seconds) {
        isPaused = true;
        pauseTime = GameManager.tick();
        pauseForTime = seconds;
    }

    /**
     * Add a texture part
     *
     * @param rotation rotation key
     * @param texture  the texture
     */
    protected void addTexturePart(EntityRotation rotation, TextureRegion texture, boolean initial) {
        entity.getComponent(EntityTextureComponent.class).textureRegions.put(rotation.name(), texture);
        if (initial) activeEntityTexture = texture;
    }

    /**
     * Add a texture part
     *
     * @param name    the name
     * @param texture the texture
     */
    protected void addTexturePart(String name, TextureRegion texture) {
        entity.getComponent(EntityTextureComponent.class).textureRegions.put(name, texture);
    }

    /**
     * Add a texture part
     *
     * @param name    the name
     * @param texture the texture
     */
    protected void addTexturePart(String name, TextureRegion texture, boolean initial) {
        entity.getComponent(EntityTextureComponent.class).textureRegions.put(name, texture);
        if (initial) activeEntityTexture = texture;
    }

    /**
     * Get a texture part
     *
     * @param name the name of the texture
     * @return the texture
     */
    protected TextureRegion getTexturePart(String name) {
        return entity.getComponent(EntityTextureComponent.class).textureRegions.get(name);
    }

    /**
     * Get a texture part
     *
     * @param rotation rotation key
     * @return the texture
     */
    protected TextureRegion getTexturePart(EntityRotation rotation) {
        return entity.getComponent(EntityTextureComponent.class).textureRegions.get(rotation.name());
    }

    protected boolean hasTexturePart(EntityRotation rotation) {
        return entity.getComponent(EntityTextureComponent.class).textureRegions.containsKey(rotation.name());
    }

    /**
     * Add status of this entity
     * Basically draws something above their head to describe what they may be doing or thinking
     *
     * @param status the status
     */
    protected void addStatus(EntityStatus status) {
        statuses.put(status.id(), status);
    }

    protected <T extends EntityStatus> T getStatus(int id) {
        return (T) statuses.get(id);
    }

    /**
     * Render the health bar of this entity
     * Usually only used for enemies.
     *
     * @param batch the batch
     */
    public void renderHealthBar(SpriteBatch batch) {
        if (getHealth() <= 0 || gradient == null) return;

        final float width = (getHealth() / 100.0f * getWidth()) * OasisGameSettings.SCALE;
        gradient.draw(batch, getX(), getY() + (getScaledHeight() + 0.1f), width, 3.0f * OasisGameSettings.SCALE);
    }

    /**
     * Check if this entity was clicked on
     *
     * @param clicked the vector3 click
     * @return {@code  true} if so
     */
    public boolean isMouseInEntityBounds(Vector3 clicked) {
        return clicked.x > getX() && clicked.x < (getX() + getScaledWidth()) && clicked.y > getY() && clicked.y < (getY() + getScaledHeight());
    }

    /**
     * @return the interactable entity
     */
    public EntityInteractable asInteractable() {
        return null;
    }

    public boolean isInteractableEntity() {
        return this instanceof EntityInteractable;
    }

    public EntityEnemy asEnemy() {
        return null;
    }

    /**
     * @return {@code true} if this entity is nearby
     */
    public boolean nearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;

        if (!isNearby) {
            statuses.values().forEach(EntityStatus::exitStatus);
        }
    }

    public void setDistanceToPlayer(float distance) {
        if (this instanceof EntityInteractable) {
            this.distanceFromPlayer = distance;
        }
    }

    public float getDistanceFromPlayer() {
        return distanceFromPlayer;
    }

    /**
     * Set the cloud this entity is in
     *
     * @param cloudApartOf cloud apart of
     */
    public void setCloudApartOf(AreaEffectCloud cloudApartOf) {
        this.cloudApartOf = cloudApartOf;
    }

    /**
     * @return the effect cloud if this entity is in one
     */
    public AreaEffectCloud cloudApartOf() {
        return cloudApartOf;
    }

    /**
     * Check if we are inside an {@link me.vrekt.oasis.world.effects.AreaEffectCloud}
     */
    public void checkAreaEffects() {
        worldIn.checkAreaEffects(this);
    }

    /**
     * Draw the current position
     * If dynamic sizing is true, will automatically resize the bb.
     *
     * @param batch  batch
     * @param region texture
     */
    public void drawCurrentPosition(SpriteBatch batch, TextureRegion region) {
        if (dynamicSize && previousRotation != rotation) {
            setSize(region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE, OasisGameSettings.SCALE);
        }

        batch.draw(region, body.getPosition().x, body.getPosition().y, region.getRegionWidth() * OasisGameSettings.SCALE, region.getRegionHeight() * OasisGameSettings.SCALE);
    }

    /**
     * Create a regular rectangle collision body
     *
     * @param world the world
     */
    public void createRectangleBody(World world, Vector2 center) {
        final BodyDef definition = new BodyDef();
        final FixtureDef fixture = new FixtureDef();

        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = false;
        definition.position.set(getTransformComponent().position);

        body = world.createBody(definition);

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(getScaledWidth() / 2.0F, getScaledHeight() / 2.0F, center, 0.0f);

        fixture.shape = shape;
        fixture.density = 0.1f;

        body.createFixture(fixture);
        body.setUserData(this);
        shape.dispose();
    }

    /**
     * Create a circle collision body
     *
     * @param world   the world
     * @param flipped if the origin of the circle should be flipped Y axis
     */
    public void createCircleBody(World world, boolean flipped) {
        final BodyDef definition = new BodyDef();
        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = false;
        definition.position.set(getTransformComponent().position);

        final CircleShape shape = createCircleCollisionShape(flipped);
        final FixtureDef fixture = createCircleCollisionFixture(shape);
        if (world.isLocked()) {
            throw new UnsupportedOperationException("Locked world! Look into this.");
        }

        body = world.createBody(definition);
        body.createFixture(fixture);
        body.setUserData(this);

        shape.dispose();
    }

    /**
     * Create a circle shape for the entity collision, mostly players.
     *
     * @param flipped if the origin of the circle is flipped Y axis
     * @return the new shape, must be disposed of
     */
    protected CircleShape createCircleCollisionShape(boolean flipped) {
        final CircleShape shape = new CircleShape();

        // origin position of the circle, for certain maps we are flipped
        final Vector2 origin = flipped ? new Vector2(0, -1) : new Vector2(0, 0);
        origin.x += getScaledWidth() / 2f;
        origin.y += getScaledHeight() / 2f;

        shape.setRadius(((getScaledWidth() * physicsScale / 2f + getScaledHeight() * physicsScale / 2f) / 2f));
        shape.setPosition(origin);
        return shape;
    }

    /**
     * Create a circle fixture for the final body
     *
     * @param shape the shape
     * @return the new fixture
     */
    protected FixtureDef createCircleCollisionFixture(CircleShape shape) {
        final FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.density = 0.0f;
        fixture.friction = 1.0f;
        return fixture;
    }

    /**
     * Remove from active world state
     */
    public void removeFromWorld() {
        if (!isInWorld()) return;

        if (body != null) {
            getWorldState().boxWorld().destroyBody(body);
            body = null;
        }
    }

    @Override
    public boolean isInView(Camera camera) {
        return inView = camera.frustum.pointInFrustum(getX(), getY(), 0.0f);
    }

    @Override
    public void dispose() {
        activeEntityTexture = null;
        worldIn = null;
        parentWorld = null;
        player = null;
        gradient = null;
        bb = null;
        statuses.clear();
        aiComponents.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GameEntity e) {
            return entityId() == e.entityId();
        } else {
            return false;
        }
    }

}
