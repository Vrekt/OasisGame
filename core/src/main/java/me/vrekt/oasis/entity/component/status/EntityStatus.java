package me.vrekt.oasis.entity.component.status;

public enum EntityStatus {

    THINKING("entity_question");

    private final String resource;
    EntityStatus(String resource) {
        this.resource = resource;
    }

    public String resource() {
        return resource;
    }
}
