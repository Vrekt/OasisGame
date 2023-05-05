package me.vrekt.oasis.item;

public enum ItemRarity {

    BASIC("Basic", "[LIGHT_GRAY]"), COMSIC("Cosmic", "[azure]"), VOID("Void", "[PURPLE]"), DIVINE("Divine", "[butter]");

    private final String rarityName, colorName;

    ItemRarity(String rarityName, String color) {
        this.rarityName = rarityName;
        this.colorName = color;
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
}
