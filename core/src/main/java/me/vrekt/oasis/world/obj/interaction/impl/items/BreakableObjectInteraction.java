package me.vrekt.oasis.world.obj.interaction.impl.items;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.ItemRegistry;
import me.vrekt.oasis.item.weapons.ItemWeapon;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.obj.interaction.WorldInteractionType;
import me.vrekt.oasis.world.obj.interaction.impl.AbstractInteractableWorldObject;

/**
 * Player can break pots/containers with a sword and get a random item
 */
public final class BreakableObjectInteraction extends AbstractInteractableWorldObject {

    // player got unlucky, no item
    private static final float UNLUCKY_CHANCE = 0.15f;
    private static final float MULTIPLE_ITEM_CHANCE = 0.2f;
    private final ItemRarity rarity;
    private final Sounds breakSound;
    private final float volume;
    private final float offset;

    private Animation<TextureRegion> breakingAnimation;
    private float animationTime;
    private boolean isBreaking;
    private boolean unlucky;

    private boolean networkAnimation;

    public BreakableObjectInteraction(GameWorld world, MapObject object) {
        super(WorldInteractionType.BREAKABLE_OBJECT);

        this.rarity = ItemRarity.valueOf(TiledMapLoader.ofString(object, "rarity"));
        this.handleMouseState = false;
        this.isCombatInteraction = true;
        this.shouldSave = false;

        // load breaking animation
        final String anim = TiledMapLoader.ofString(object, "animation");
        breakSound = Sounds.valueOf(TiledMapLoader.ofString(object, "sound"));
        volume = TiledMapLoader.ofFloat(object, "volume", 1.0f);
        offset = TiledMapLoader.ofFloat(object, "item_drop_offset", 0.25f);

        if (anim != null) {
            final float animationTime = TiledMapLoader.ofFloat(object, "animation_time", 0.1335f);
            final TextureRegion[] frames = new TextureRegion[TiledMapLoader.ofInt(object, "animation_frames", 3)];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = world.getGame().getAsset().get(anim, i + 1);
            }
            breakingAnimation = new Animation<>(animationTime, frames);
        }
    }

    /**
     * @return assigned rarity
     */
    public ItemRarity rarity() {
        return rarity;
    }

    /**
     * Start animating, triggered by network + self.
     */
    public void animate(boolean networkAnimation) {
        isBreaking = true;
        this.networkAnimation = networkAnimation;
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (isBreaking || unlucky) {
            batch.draw(breakingAnimation.getKeyFrame(animationTime), position.x, position.y, size.x, size.y);
            animationTime += delta;

            // finally, remove the interaction
            if (breakingAnimation.isAnimationFinished(animationTime)) {
                isBreaking = false;

                // if we ourselves executed this action, go ahead.
                if (!networkAnimation) {
                    world.removeInteraction(this);
                    broadcastDestroyed();
                } else {
                    // we didn't so, hide since the animation is finished, on our side anyway.
                    this.hide();
                }
            }
        } else {
            super.render(batch, delta);
        }
    }

    @Override
    public void interact() {
        if (isBreaking || unlucky) return;

        if (breakingAnimation != null) {
            broadcastAnimation();
            animate(false);
        }

        GameManager.playSound(breakSound, volume, 0.88f, 0.0f);

        // the server will decide this.
        // if we are the local client, we can decide, which is probably dangerous.
        if (!isNetworkPlayer()) {
            final boolean isUnlucky = MathUtils.randomBoolean(UNLUCKY_CHANCE);
            if (isUnlucky) {
                // no item for you, still play the animation
                unlucky = true;
                disable();
                return;
            }

            final int amount = MathUtils.randomBoolean(MULTIPLE_ITEM_CHANCE) ? MathUtils.random(1, 3) : 1;
            final Item item = ItemRegistry.createRandomItemWithRarity(rarity, amount);
            world.spawnWorldDrop(item, position.cpy().add(offset, offset));
        }
    }

    @Override
    public void reset() {
        super.reset();

        animationTime = 0.0f;
        isBreaking = false;
        unlucky = false;
    }

    /**
     * Check if the player hit this pot
     *
     * @param weapon weapon
     * @return {@code true} if so
     */
    public boolean playerHit(ItemWeapon weapon) {
        return weapon.getBounds().overlaps(bounds);
    }
}
