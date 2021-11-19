package me.vrekt.oasis.item;

import me.vrekt.oasis.utilities.loading.Loadable;

public final class ItemManager implements Loadable {

    private boolean loaded;

    public ItemManager() {

    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void setLoaded() {
        loaded = true;
    }
}
