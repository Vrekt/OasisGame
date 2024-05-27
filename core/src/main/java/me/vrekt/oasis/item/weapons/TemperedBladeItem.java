package me.vrekt.oasis.item.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.component.facing.EntityRotation;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.draw.AnimationRendererConfig;
import me.vrekt.oasis.item.draw.ItemAnimationRenderer;
import me.vrekt.oasis.item.utility.ItemDescriptor;

public final class TemperedBladeItem extends ItemWeapon {

    public static final String KEY = "oasis:enchanted_violet_sword";
    public static final String NAME = "Tempered Blade";
    public static final String TEXTURE = "enchanted_violet";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    private ParticleEffect swingEffect;

    public TemperedBladeItem() {
        super(Items.TEMPERED_BLADE, KEY, NAME, "A blade with a razor sharp edge.");

        this.baseDamage = 1.5f;
        this.swingCooldown = GameManager.secondsToTicks(0.25f);
        this.swingTime = .25f;
        this.criticalHitChance = 15.0f;
        this.criticalHitDamage = 6.5f;
        this.knockbackMultiplier = 3.5f;
        this.range = 0.8f;
        this.rarity = ItemRarity.VOID;
        this.isStackable = false;
    }

    @Override
    public void load(Asset asset) {
        final AnimationRendererConfig config = new AnimationRendererConfig()
                .rotation(EntityRotation.UP, 0.55f, 0.45f)
                .rotation(EntityRotation.DOWN, 0.45f, 0.45f)
                .rotation(EntityRotation.LEFT, 0.45f, 0.45f)
                .rotation(EntityRotation.RIGHT, 0.0f, 0.0f);

        this.renderer = new ItemAnimationRenderer(asset.get(TEXTURE), this, config);

        this.swingEffect = new ParticleEffect();
        this.swingEffect.load(Gdx.files.internal("world/asset/particles/swordparticle"), asset.getAtlasAssets());
        this.swingEffect.start();

        this.bounds = new Rectangle();

        //   this.animator = new TemperedBladeWeaponAnimation(this, 1.0f);
        //  this.animator.initializeAnimation(Animation.PlayMode.LOOP,
        //          asset.get("slash", 1),
        //          asset.get("slash", 2),
        //          asset.get("slash", 3),
        //          asset.get("slash", 4));
    }

    @Override
    public void update(float delta, EntityRotation rotation) {
        super.update(delta, rotation);
        if (!isSwinging) swingEffect.reset();
    }
}
