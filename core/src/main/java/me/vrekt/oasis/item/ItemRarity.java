package me.vrekt.oasis.item;

public enum ItemRarity {

    BASIC("Basic", "[LIGHT_GRAY]", null), COSMIC("Cosmic", "[azure]", null), VOID("Void", "[PURPLE]", "void_rarity"), DIVINE("Divine", "[butter]", null);

    private final String rarityName, colorName, texture;

    ItemRarity(String rarityName, String color, String texture) {
        this.rarityName = rarityName;
        this.colorName = color;
        this.texture = texture;
    }

    public String getRarityName() {
        return rarityName;
    }

    public String getColoredRarityName() {
        return colorName + rarityName;
    }

    public String getColorName() {
        return colorName;
    }

    public String getTexture() {
        return texture;
    }
}
