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

    private Animation<TextureRegion> breakingAnimation;
    private float animationTime;
    private boolean isBreaking;
    private boolean unlucky;

    public BreakableObjectInteraction(GameWorld world, MapObject object) {
        super(WorldInteractionType.BREAKABLE_OBJECT);

        this.rarity = ItemRarity.valueOf(TiledMapLoader.ofString(object, "rarity"));
        this.handleMouseState = false;
        this.isCombatInteraction = true;

        // load breaking animation
        final String anim = TiledMapLoader.ofString(object, "animation");
        breakSound = Sounds.valueOf(TiledMapLoader.ofString(object, "sound"));
        volume = TiledMapLoader.ofFloat(object, "volume", 1.0f);

        if (anim != null) {
            final float animationTime = TiledMapLoader.ofFloat(object, "animation_time", 0.1335f);
            final TextureRegion[] frames = new TextureRegion[TiledMapLoader.ofInt(object, "animation_frames", 3)];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = world.getGame().getAsset().get(anim, i + 1);
            }
            breakingAnimation = new Animation<>(animationTime, frames);
        }
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        if (isBreaking || unlucky) {

            batch.draw(breakingAnimation.getKeyFrame(animationTime), position.x, position.y, size.x, size.y);
            animationTime += delta;

            // finally, remove the interaction
            if (breakingAnimation.isAnimationFinished(animationTime)) {
                world.removeInteraction(this);
                isBreaking = false;
            }
        } else {
            super.render(batch, delta);
        }
    }

    @Override
    public void interact() {
        if (isBreaking || unlucky) return;

        if (breakingAnimation != null) {
            isBreaking = true;
        }

        GameManager.playSound(breakSound, volume, 0.88f, 0.0f);
        final boolean isUnlucky = MathUtils.randomBoolean(UNLUCKY_CHANCE);
        if (isUnlucky) {
            // no item for you, still play the animation
            unlucky = true;
            disable();
            return;
        }

        final int amount = MathUtils.randomBoolean(MULTIPLE_ITEM_CHANCE) ? MathUtils.random(1, 3) : 1;
        final Item item = ItemRegistry.createRandomItemWithRarity(rarity, amount);
        world.spawnWorldDrop(item, position.cpy().add(0.25f, 0.25f));
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
