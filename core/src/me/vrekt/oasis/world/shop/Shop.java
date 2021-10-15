package me.vrekt.oasis.world.shop;

import com.badlogic.gdx.math.Rectangle;
import me.vrekt.oasis.entity.player.local.Player;
import me.vrekt.oasis.ui.world.GameWorldInterface;

/**
 * Represents a shop/store where you can buy things
 */
public final class Shop {

    private final Rectangle bounds;
    private boolean near;

    public Shop(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void update(Player player) {
        if (player.getPosition().dst2(bounds.x, bounds.y) <= 10) {
            this.near = true;
        } else {
            this.near = false;
        }
    }

    public void interact(Player player, GameWorldInterface ui) {
        if (near) {
            ui.showShop();
        }
    }

}
