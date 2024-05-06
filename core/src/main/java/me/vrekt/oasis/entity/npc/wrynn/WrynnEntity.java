package me.vrekt.oasis.entity.npc.wrynn;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.ai.EntityLocation;
import me.vrekt.oasis.ai.SteeringEntity;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityAnimationComponent;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.entity.interactable.EntityInteractable;
import me.vrekt.oasis.entity.npc.EntityNPCType;
import me.vrekt.oasis.entity.npc.wrynn.dialog.WrynnDialog;
import me.vrekt.oasis.entity.player.sp.OasisPlayer;
import me.vrekt.oasis.utility.hints.PlayerHints;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Wrynn.
 */
public final class WrynnEntity extends EntityInteractable {

    private boolean hintShown;
    // entity AI
    private SteeringEntity steering;
    private EntityLocation seekingLocation;

    private long lastMoveTime;
    private Vector2 target;
    private boolean moveToTarget;
    private boolean wantsNextPath;
    private boolean facingDown = false;

    private EntityAnimationComponent animationComponent;

    public WrynnEntity(String name, Vector2 position, OasisPlayer player, OasisWorld worldIn, OasisGame game, EntityNPCType type) {
        super(name, position, player, worldIn, game, type);

        entityDialog = WrynnDialog.create();
        dialog = entityDialog.getStarting();
        rotation = EntityRotation.DOWN;
        lastRotation = EntityRotation.DOWN;
    }

    public boolean wantsNextPath() {
        return wantsNextPath;
    }

    public void setNextPathPoint(Vector2 point) {
        seekingLocation.getPosition().set(point);
        wantsNextPath = false;

        GameLogging.info(this, "New path: %s", point);
    }

    @Override
    public void load(Asset asset) {
        putRegion("face", asset.get("wrynn_face"));
        putRegion(EntityRotation.DOWN.name(), asset.get("wrynn_facing_down"));
        putRegion(EntityRotation.UP.name(), asset.get("wrynn_facing_up"));
        putRegion(EntityRotation.LEFT.name(), asset.get("wrynn_facing_left"));

        animationComponent = new EntityAnimationComponent();
        entity.add(animationComponent);

        animationComponent.registerWalkingAnimation(EntityRotation.LEFT, 0.4f, asset.get("wrynn_walking_left", 1), asset.get("wrynn_walking_left", 2));
        animationComponent.registerWalkingAnimation(EntityRotation.DOWN, 0.4f, asset.get("wrynn_walking_down", 1), asset.get("wrynn_walking_down", 2));

        this.dialogFaceAsset = "face";
        currentRegionState = getRegion(EntityRotation.UP.name());

        setSize(currentRegionState.getRegionWidth(), currentRegionState.getRegionHeight(), OasisGameSettings.SCALE);
        this.bounds = new Rectangle(getPosition().x, getPosition().y, getScaledWidth(), getScaledHeight());

        dialogFrames[0] = asset.get("dialog", 1);
        dialogFrames[1] = asset.get("dialog", 2);
        dialogFrames[2] = asset.get("dialog", 3);
        createBoxBody(gameWorldIn.getEntityWorld());

        steering = new SteeringEntity(this, body);
        steering.setMaxLinearSpeed(2.0f);
        steering.setMaxLinearAcceleration(2.85f);

        seekingLocation = new EntityLocation();

        // final RayConfiguration<Vector2> rayConfiguration = new SingleRayConfiguration<>(steering, 10.0f);
        //        final RaycastCollisionDetector<Vector2> detector = new Box2dRaycastCollisionDetector(gameWorldIn.getEntityWorld());
        //        final SteeringBehavior<Vector2> collisionAvoidance = new RaycastObstacleAvoidance<>(steering, rayConfiguration, detector, 5.0f);
        //
        //        final Seek<Vector2> seeking = new Seek<>(steering, seekingLocation);
        //        final PrioritySteering<Vector2> tree = new PrioritySteering<>(steering, 0.001f);

        final Arrive<Vector2> arrive = new Arrive<>(steering, seekingLocation);
        arrive.setTimeToTarget(0.01f);
        arrive.setArrivalTolerance(0.01f);
        arrive.setDecelerationRadius(2f);
        steering.setBehavior(arrive);

        //  final Seek<Vector2> seeking = new Seek<>(steering, seekingLocation);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        // update region state based on rotation changes
        if (lastRotation != rotation) {
            currentRegionState = getRegion(rotation.name());
            lastRotation = rotation;
        }

        if (currentRegionState != null) {
            // only draw walking animations if velocity is basically zero.
            if (steering.isVelocityZeroWithinTolerance()) {
                draw(batch, currentRegionState);
            } else {
                draw(batch, animationComponent.playWalkingAnimation(rotation.ordinal(), delta));
            }
        }
    }

    private void draw(SpriteBatch batch, TextureRegion region) {
        batch.draw(region, body.getPosition().x, body.getPosition().y, getScaledWidth(), getScaledHeight());
    }

    @Override
    public void update(float v) {
        super.update(v);

        if (lastMoveTime == 0 || (System.currentTimeMillis() - lastMoveTime >= 15000)) {
            wantsNextPath = true;
            lastMoveTime = System.currentTimeMillis();
        }

        // only update the steering if we are not within the target tolerance
        // avoids the steering constantly making small corrections
        // TODO: Maybe just fine tune later but for now it doesn't even work.
        if (!steering.isWithinTarget(1.5f, seekingLocation)) {
            steering.update(v);

            // update rotation
            rotation = steering.getDirectionMoving();
        } else {
            // make sure we stop fully now.
            body.setLinearVelocity(0, 0);
        }


       /* if (steering.isWithinTarget(1.0f, seekingLocation)) {
            // we are at the target so stop moving for now.
            body.setLinearVelocity(0.0f, 0.0f);
            facingDown = false;
        } else {
            if(body.getLinearVelocity().y < 0) {
                facingDown = true;
            } else {
                facingDown = false;
            }
            GameLogging.info(this, "updating");
            steering.update(v);
        }*/

      /*  if (lastMoveTime == 0 || (System.currentTimeMillis() - lastMoveTime >= 2500)
                && !moveToTarget) {
            target = body.getPosition().add(MathUtils.random(-3f, 3f), MathUtils.random(-3f, 3f));
            moveToTarget = true;
            lastMoveTime = System.currentTimeMillis();

            GameLogging.info(this, "Move to target %s", target);
        }

        if (moveToTarget) {
            final float rad = MathUtils.atan2(target.x - body.getPosition().x, target.y - body.getPosition().y);
            final float cos = MathUtils.cos(rad);
            final float sin = MathUtils.sin(rad);
            body.setLinearVelocity(cos, sin);

            GameLogging.info(this, "Dist %s", target.dst2(body.getPosition()));

            if (target.dst2(body.getPosition()) <= 0.5f) {
                moveToTarget = false;
            }

        }*/

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

    @Override
    public void setSpeakingTo(boolean speakingTo) {
        super.setSpeakingTo(speakingTo);

        if (speakingTo && !hintShown) {
            // show player hint about how to interact with the dialog system
            game.guiManager.getHudComponent().showPlayerHint(PlayerHints.DIALOG_TUTORIAL_HINT, 12.0f);
            hintShown = true;
        }
    }
}
