package me.vrekt.oasis.utilities.array;

import com.badlogic.gdx.utils.Array;
import me.vrekt.oasis.entity.npc.EntityInteractable;

public final class EntityComparableArray extends Array<EntityInteractable> {

    public EntityComparableArray() {
        items = new EntityInteractable[128];
    }

    public boolean contains(EntityInteractable value) {
        EntityInteractable[] items = this.items;
        int i = size - 1;

        while (i >= 0) {
            if (items[i--].getUniqueId() == value.getUniqueId()) return true;
        }
        return false;
    }

    public void removeValue(EntityInteractable value) {
        EntityInteractable[] items = this.items;
        for (int i = 0, n = size; i < n; i++) {
            if (value.getUniqueId() == (items[i].getUniqueId())) {
                removeIndex(i);
                return;
            }
        }
    }
}
