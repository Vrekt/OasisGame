package me.vrekt.oasis.entity.npc;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.combat.CombatDamageAnimator;
import me.vrekt.oasis.entity.ai.agent.BasicSteeringAgent;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.world.OasisWorld;

public abstract class EntityEnemy extends EntityDamageable {
    protected final Vector3 worldPosition = new Vector3();
    protected final Vector3 screenPosition = new Vector3();
    protected final CombatDamageAnimator animator;

    protected Body body;

    protected EntityRotation entityRotation;
    protected EntityRotation lastRotation;

    protected BasicSteeringAgent ai;
    protected EntityAnimationComponent animationComponent;

    protected boolean ignoreCollision;

    public EntityEnemy(String name, Vector2 position, OasisPlayerSP player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);
        this.animator = new CombatDamageAnimator();
        this.speakable = false;
        this.isEnemy = true;
        this.setDrawDialogAnimationTile(false);
        this.lastRotation = EntityRotation.UP;
        this.entityRotation = EntityRotation.DOWN;
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
        shape.setAsBox(getWidthScaled() / 2.0F, getHeightScaled() / 2.0F);
        fixture.shape = shape;
        fixture.density = 1.0f;

        body.createFixture(fixture);
        body.setUserData(this);
        body.setLinearDamping(1.0f);
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

    public EntityRotation getEntityRotation() {
        return entityRotation;
    }

    public void setEntityRotation(EntityRotation entityRotation) {
        this.entityRotation = entityRotation;
    }

    public EntityRotation getLastRotation() {
        return lastRotation;
    }

    public void setLastRotation(EntityRotation lastRotation) {
        this.lastRotation = lastRotation;
    }

    /**
     * "
     * TODO
     *
     * @param rotation rotation of local player
     * @return {@code  true} if the player is facing this entity
     */
    public boolean isFacingEntity(float rotation) {
        return true;
    }

    @Override
    public void damage(float tick, float amount, float knockback, boolean isCritical) {
        this.animator.accumulateDamage(amount, game.getPlayer().getRotation(), isCritical);
        this.health -= amount;
    }

    @Override
    public void update(float v) {
        super.update(v);

        bounds.set(getPosition().x, getPosition().y, getWidthScaled(), getHeightScaled());
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
            screenPosition.set(game.getGui().getCamera().project(worldPosition));
            animator.drawAccumulatedDamage(batch, game.getAsset().getBoxy(), screenPosition.x, screenPosition.y, getWidth(), getHeight());
        }
    }

}
