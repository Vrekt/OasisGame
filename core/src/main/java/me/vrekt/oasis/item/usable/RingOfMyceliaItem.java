package me.vrekt.oasis.item.usable;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.gui.GuiType;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;
import me.vrekt.oasis.world.tutorial.MyceliaWorld;
import me.vrekt.oasis.world.tutorial.NewGameWorld;

/**
 * Used to enter the {@link MyceliaWorld}
 * <p>
 * NOTE: UNUSED
 */
public final class RingOfMyceliaItem extends ItemUsable {

    public static final String KEY = "oasis:ring_of_mycelia";
    public static final String NAME = "Ring Of Mycelia";
    public static final String DESCRIPTION = "A ring with a special fungus growing from it.";
    public static final String TEXTURE = "ring_of_mycelia";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public RingOfMyceliaItem() {
        super(Items.RING_OF_MYCELIA, KEY, NAME, DESCRIPTION);

        this.isStackable = false;
        this.rarity = ItemRarity.VOID;
        this.scaleSize = 2.0f;
        this.inventoryTag = "Teleport";
    }

    @Override
    public boolean isUsable(PlayerSP player) {
        return !player.isInInteriorWorld() && player.getWorldState() instanceof NewGameWorld;
    }

    @Override
    public void load(Asset asset) {
        this.sprite = asset.get(TEXTURE);
    }

    @Override
    public void use(PlayerSP player) {
        GameManager.gui().hideGui(GuiType.INVENTORY);
    }
}
