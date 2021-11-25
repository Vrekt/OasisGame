package me.vrekt.oasis.item.items.weapons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.vrekt.oasis.asset.Asset;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.item.Item;
import me.vrekt.oasis.item.items.ItemRarity;
import me.vrekt.oasis.item.items.attr.ItemAttributeType;
import me.vrekt.oasis.world.renderer.GlobalGameRenderer;

public final class PrototypeTimepiercerWeapon extends Item {

    private int animationIndex;
    private long lastTick;

    public PrototypeTimepiercerWeapon(Asset asset) {
        super("Prototype\nTimepiercer", "prototype_timepiercer", ItemRarity.EPIC);
        this.texture = asset.getAssets().findRegion(this.textureName);
        this.animation = new Animation<>(.1f,
                asset.getAssets().findRegion(textureName + "_swing", 1),
                asset.getAssets().findRegion(textureName + "_swing", 2),
                asset.getAssets().findRegion(textureName + "_swing", 3));
        this.animation.setPlayMode(Animation.PlayMode.LOOP);

        // default attrs for this weapon.
        this.addAttribute(ItemAttributeType.ATK, 32);
        this.addAttribute(ItemAttributeType.LUCK, 1);
        this.description = "A blade crafted from the ancestors of Hunnewell.";
    }

    @Override
    public void updateAnimation(long tick, float delta) {
        if (lastTick == 0) {
            lastTick = tick;
            animationIndex = 0;
        } else if (tick - lastTick >= 5) {
            lastTick = tick;
            animationIndex++;
        }

        if (animationIndex > 2) {
            animationIndex = 0;
            lastTick = 0;
        }

        // check if item is done being used
        if (tick - this.inUseTime >= useTime) {
            animationIndex = 0;
            this.lastTick = 0;
            this.isUsing = false;
        }
    }

    @Override
    public void renderAnimation(SpriteBatch batch, Player player) {
        final TextureRegion frame = animation.getKeyFrames()[animationIndex];
        batch.draw(frame,
                player.getX() - 0.2f,
                player.getY() - 0.2f,
                frame.getRegionWidth() * (GlobalGameRenderer.SCALE / 2f),
                frame.getRegionHeight() * (GlobalGameRenderer.SCALE) / 2f);
    }
}
