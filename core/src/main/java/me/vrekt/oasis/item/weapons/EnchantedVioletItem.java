package me.vrekt.oasis.item.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.component.EntityRotation;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.utility.ItemDescriptor;

public final class EnchantedVioletItem extends ItemWeapon {

    public static final int ID = 4;
    public static final String TEXTURE = "enchanted_violet";
    public static final String NAME = "Enchanted Violet";

    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    private ParticleEffect swingEffect;

    public EnchantedVioletItem() {
        super(NAME, ID, "An enchanted dagger dipped in violet.");
        this.baseDamage = 1.5f;
        this.swingCooldown = 0.25f;
        this.swingTime = .25f;
        this.criticalHitChance = 15.0f;
        this.criticalHitDamage = 6.5f;
        this.knockbackMultiplier = 3.5f;
        this.range = 0.8f;
        this.rarity = ItemRarity.VOID;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = new Sprite(asset.get(TEXTURE));
        this.sprite.setScale(2.0f);
        this.sprite.setSize(sprite.getRegionWidth() * OasisGameSettings.SCALE, sprite.getRegionHeight() * OasisGameSettings.SCALE);
        this.swipe = new Sprite(asset.get("swipetest", 2));

        this.swingEffect = new ParticleEffect();
        this.swingEffect.load(Gdx.files.internal("world/asset/swordparticle"), asset.getAtlasAssets());
        this.swingEffect.start();

        this.bounds = new Rectangle();

        this.animator = new ItemWeaponAnimator(this, 0.05f);
        this.animator.initializeAnimation(Animation.PlayMode.LOOP,
                asset.get("slash", 1),
                asset.get("slash", 2),
                asset.get("slash", 3),
                asset.get("slash", 4));
    }

    @Override
    public void update(float delta, EntityRotation rotation) {
        super.update(delta, rotation);
        if (!isSwinging) swingEffect.reset();
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (sprite != null) {
            this.draw(batch, sprite.getWidth(), sprite.getHeight(), sprite.getRotation());
        }

        if (animator.isAnimating()) {
            final TextureRegion region = animator.getFrame();
            if (region != null) {
                batch.draw(region,
                        animator.getPosition().x, animator.getPosition().y,
                        0.0f, 0.0f,
                        region.getRegionWidth(), region.getRegionHeight(),
                        OasisGameSettings.SCALE, OasisGameSettings.SCALE,
                        animator.getAnimationAngle());
            }
        }
    }


}
