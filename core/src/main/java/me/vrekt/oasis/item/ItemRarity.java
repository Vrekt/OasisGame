package me.vrekt.oasis.item;

public enum ItemRarity {

    COMMON("Common", "[#472B08]", "common_rarity"),
    UN_COMMON("Uncommon", "[#00540D]", "uncommon_rarity"),
    COSMIC("Cosmic", "[#10334A]", null),
    VOID("Void", "[PURPLE]", "void_rarity");

    private final String rarityName, colorName, texture;

    ItemRarity(String rarityName, String color, String texture) {
        this.rarityName = rarityName;
        this.colorName = color;
        this.texture = texture;
    }

    public String getRarityName() {
        return rarityName;
    }

    public String getColorName() {
        return colorName;
    }

    public String getTexture() {
        return texture;
    }
}
