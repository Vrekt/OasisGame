package me.vrekt.oasis.classes;

public abstract class AbstractPlayerClass {

    protected final ClassType classType;

    public AbstractPlayerClass(ClassType classType) {
        this.classType = classType;
    }

    public ClassType getClassType() {
        return classType;
    }
}
