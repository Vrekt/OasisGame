package me.vrekt.oasis.entity.player.magic;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.asset.sound.Sounds;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.world.interior.InteriorWorldType;

/**
 * Teleports the player to the {@link me.vrekt.oasis.world.interior.other.MyceliaWorld}
 */
public final class MyceliaGatewaySpell extends MagicSpell {

    private static final String NAME = "Mycelic Gateway";
    private static final String DESCRIPTION = "Teleports you to a mycelic world.";
    private static final float COOLDOWN = 25f;

    public MyceliaGatewaySpell() {
        super(NAME, DESCRIPTION, COOLDOWN);
    }

    @Override
    public void load(Asset asset) {
        this.icon = asset.get("mycelia_spell");
    }

    @Override
    public void cast(PlayerSP player) {
        GameManager.getGuiManager().hideGui(GuiType.MAGIC_BOOK);
        GameManager.playSound(Sounds.TELEPORT, 0.15f, -1.0f, 0.0f);
        player.getWorldState().enterInterior(InteriorWorldType.MYCELIA_WORLD);
    }
}
