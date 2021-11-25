package me.vrekt.oasis.item.items.attr;

public enum ItemAttributeType {

    ATK("ATK"),
    LUCK("Luck"),
    CRITICAL_DAMAGE("Crit DMG"),
    BANISHING("Banishing"),
    HP("HP"),
    DEF("DEF"),
    PACIFY("Pacify");


    private final String name;

    ItemAttributeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
