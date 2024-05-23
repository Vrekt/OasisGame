package me.vrekt.oasis.entity.enemy;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import me.vrekt.oasis.combat.CombatDamageAnimator;
import me.vrekt.oasis.entity.Entity;

/**
 * An enemy entity
 */
public abstract class EntityEnemy extends Entity {

    protected final Rectangle bounds;
    protected float inaccuracy, hostileRange, attackSpeed;
    protected float attackStrength;

    protected final Vector3 worldPosition = new Vector3(),
            screenPosition = new Vector3();

    protected final CombatDamageAnimator animator;

    protected EntityEnemyType type;

    public EntityEnemy(EntityEnemyType type) {
        super(true);
        bounds = new Rectangle();

        this.type = type;
        this.animator = new CombatDamageAnimator();
    }

    /**
     * @return type of
     */
    public EntityEnemyType getType() {
        return type;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void update(float v) {
        animator.update(v);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {

    }

    @Override
    public void damage(float tick, float amount, float knockback, boolean isCritical) {
        animator.accumulateDamage(amount, rotation, isCritical);
        super.damage(amount);
    }

    @Override
    public float damage(float amount) {
        animator.accumulateDamage(amount, rotation, false);
        return super.damage(amount);
    }

    /**
     * Render damage amount animations
     *
     * @param batch       the batch
     * @param font        the font
     * @param worldCamera world cam
     * @param guiCamera   gui cam
     */
    public void renderDamageAmountAnimation(SpriteBatch batch, BitmapFont font, Camera worldCamera, Camera guiCamera) {
        worldPosition.set(worldCamera.project(worldPosition.set(body.getPosition().x + 0.1f, body.getPosition().y + 0.25f, 0.0f)));
        screenPosition.set(guiCamera.project(worldPosition));
        animator.drawAccumulatedDamage(batch, font, screenPosition.x, screenPosition.y, getWidth());
    }

    /* protected final Vector3 worldPosition = new Vector3();
    protected final Vector3 screenPosition = new Vector3();
    protected final CombatDamageAnimator animator;

    protected Body body;

    protected EntityRotation entityRotation;
    protected EntityRotation lastRotation;

    protected Box2dSteeringAgent ai;
    protected EntityAnimationComponent animationComponent;

    protected boolean ignoreCollision;

    public EntityEnemy(String name, Vector2 position, OasisPlayer player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);
        this.animator = new CombatDamageAnimator();
        this.speakable = false;
        this.isEnemy = true;
        this.setDrawDialogAnimationTile(false);
        this.lastRotation = EntityRotation.UP;
        this.entityRotation = EntityRotation.DOWN;
    }

    @Override
    public void load(Asset asset) {
        this.gradient = new NinePatch(asset.get("health_gradient"), 0, 0, 0, 0);
    }

    public void createBoxBody(World world) {
        final BodyDef definition = new BodyDef();
        final FixtureDef fixture = new FixtureDef();

        definition.type = BodyDef.BodyType.DynamicBody;
        definition.fixedRotation = false;
        definition.position.set(getPosition());

        body = world.createBody(definition);
        PolygonShape shape;

        shape = new PolygonShape();
        shape.setAsBox(getScaledWidth() / 2.0F, getScaledHeight() / 2.0F);
        fixture.shape = shape;
        fixture.density = 1.0f;

        body.createFixture(fixture);
        body.setUserData(this);
        shape.dispose();
    }

    public void setIgnoreCollision(boolean ignoreCollision) {
        this.ignoreCollision = ignoreCollision;
    }

    public boolean isIgnoreCollision() {
        return ignoreCollision;
    }

    @Override
    public Body getBody() {
        return body;
    }

    public void setUseAnimations() {
        entity.add(animationComponent = new EntityAnimationComponent());
    }

    public void setEntityRotation(EntityRotation entityRotation) {
        this.entityRotation = entityRotation;
    }

    *//**
     * "
     * TODO
     *
     * @param rotation rotation of local player
     * @return {@code  true} if the player is facing this entity
     *//*
    public boolean isFacingEntity(float rotation) {
        return true;
    }

    @Override
    public void damage(float tick, float amount, float knockback, boolean isCritical) {
        this.animator.accumulateDamage(amount, game.getPlayer().getPlayerRotation(), isCritical);
        setHealth(getHealth() - amount);
    }

    @Override
    public void update(float v) {
        super.update(v);

        bounds.set(getPosition().x, getPosition().y, getScaledWidth(), getScaledHeight());
        animator.update(v);
    }

    @Override
    public Vector2 getPosition() {
        if (body == null) {
            return super.getPosition();
        }
        return body.getPosition();
    }

    @Override
    public Vector2 getVelocity() {
        if (body == null) {
            return super.getVelocity();
        }
        return body.getLinearVelocity();
    }

    public void drawDamageIndicator(SpriteBatch batch) {
        if (animator.hasDamage()) {
            worldPosition.set(game.getRenderer().getCamera().project(worldPosition.set(getPosition().x + 1.0f, getPosition().y + 2.5f, 0.0f)));
            screenPosition.set(game.getGuiManager().getStage().getCamera().project(worldPosition));
            animator.drawAccumulatedDamage(batch, game.getAsset().getBoxy(), screenPosition.x, screenPosition.y, getWidth());
        }
    }*/

}
