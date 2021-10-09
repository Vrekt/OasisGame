package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gdx.lunar.entity.LunarEntity;
import me.vrekt.oasis.asset.Asset;

public abstract class Entity extends LunarEntity {

    public Entity(int entityId) {
        super(entityId);
    }

    /**
     * Load the NPC assets
     *
     * @param asset asset
     */
    public abstract void loadNPC(Asset asset);

    /**
     * Render the entity
     *
     * @param batch batch
     * @param scale world scale
     */
    public abstract void render(SpriteBatch batch, float scale);

}
