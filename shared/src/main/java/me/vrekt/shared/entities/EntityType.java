package me.vrekt.shared.entities;

public enum EntityType {

    INVALID(-1), TUTORIAL_COMBAT_DUMMY(0);

    private final int entityTypeId;

    EntityType(int entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    public int getEntityTypeId() {
        return entityTypeId;
    }

    public static EntityType of(int id) {
        switch (id) {
            case 0:
                return TUTORIAL_COMBAT_DUMMY;
        }
        return INVALID;
    }

}
