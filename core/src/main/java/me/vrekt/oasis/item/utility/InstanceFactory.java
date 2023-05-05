package me.vrekt.oasis.item.utility;

public interface InstanceFactory<T> {

    /**
     * Create a new instance
     *
     * @return the type
     */
    T newItem();

}
