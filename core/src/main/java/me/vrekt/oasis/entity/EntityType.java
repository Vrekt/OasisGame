package me.vrekt.oasis.entity;

/**
 * All entities within the game
 */
public enum EntityType {

    WRYNN(Type.INTERACTABLE),
    THALIA(Type.INTERACTABLE),
    LYRA(Type.INTERACTABLE),
    ROACH(Type.ENEMY),
    FISH(Type.GENERIC),
    CHICK(Type.GENERIC);

    private final Type type;

    EntityType(Type type) {
       this.type = type;
    }

    /**
     * @return if the type is generic
     */
    public boolean generic() {
        return type == Type.GENERIC;
    }

    /**
     * @return if the type is enemy
     */
    public boolean enemy() {
        return type == Type.ENEMY;
    }

    /**
     * @return if the type is interactable
     */
    public boolean interactable() {
        return type == Type.INTERACTABLE;
    }

    private enum Type {
        ENEMY, INTERACTABLE, GENERIC
    }

}
