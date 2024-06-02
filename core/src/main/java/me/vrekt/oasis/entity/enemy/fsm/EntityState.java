package me.vrekt.oasis.entity.enemy.fsm;

public enum EntityState {

    AI(0);

    private final int id;

    EntityState(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
