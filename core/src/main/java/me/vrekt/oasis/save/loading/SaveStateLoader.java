package me.vrekt.oasis.save.loading;

public interface SaveStateLoader<T> {

    void loadFromSave(T state);

}
