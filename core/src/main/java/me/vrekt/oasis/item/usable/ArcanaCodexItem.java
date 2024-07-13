package me.vrekt.oasis.item.usable;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.asset.game.Asset;
import me.vrekt.oasis.entity.player.magic.MyceliaGatewaySpell;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.item.ItemRarity;
import me.vrekt.oasis.item.Items;
import me.vrekt.oasis.item.utility.ItemDescriptor;

/**
 * Basic spell-book given by Lyra
 */
public final class ArcanaCodexItem extends MagicBookItem {

    public static final String KEY = "oasis:arcana_codex";
    public static final String NAME = "Arcana's Codex";
    public static final String DESCRIPTION = "A simple spell book with a few useful casts.";
    public static final String TEXTURE = "arcana_codex";
    public static final ItemDescriptor DESCRIPTOR = new ItemDescriptor(TEXTURE, NAME);

    public ArcanaCodexItem() {
        super(Items.ARCANA_CODEX, KEY, NAME, DESCRIPTION);

        this.rarity = ItemRarity.UN_COMMON;
        this.inventoryTag = "Open";
        this.dropScale = true;
        this.itemDropScale = 2.0f;

        spells.add(new MyceliaGatewaySpell());
    }

    @Override
    public void load(Asset asset) {
        this.spells.forEach(spell -> spell.load(asset));
        this.sprite = asset.get(TEXTURE);
    }

    @Override
    public void use(PlayerSP player) {
        GameManager.getGuiManager().getMagicBookComponent().showItem(this);
    }

}
