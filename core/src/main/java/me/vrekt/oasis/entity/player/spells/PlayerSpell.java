package me.vrekt.oasis.entity.player.spells;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.components.config.EntityConfigurationComponent;
import lunar.shared.entity.components.drawing.EntityTextureComponent;
import lunar.shared.entity.components.position.EntityPositionComponent;
import lunar.shared.entity.components.position.EntityVelocityComponent;
import lunar.shared.entity.player.LunarEntity;
import lunar.shared.entity.player.LunarEntityPlayer;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;
import me.vrekt.oasis.entity.npc.animation.EntityTextured;

/**
 * Represents a player magic spell
 */
public abstract class PlayerSpell extends EntityTextured {

    public PlayerSpell() {
        super(false);

        entity.add(new EntityTextureComponent());
        entity.add(new EntityPositionComponent());
        entity.add(new EntityVelocityComponent());
        entity.add(new EntityConfigurationComponent());
    }

    @Override
    public void update(float v) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> lunarWorld, float v, float v1) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> lunarWorld) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInWorld(LunarWorld<P, N, E> lunarWorld) {

    }

    @Override
    public void render(SpriteBatch batch, float delta) {

    }
}
