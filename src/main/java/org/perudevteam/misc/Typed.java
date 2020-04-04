package org.perudevteam.misc;

public interface Typed<T extends Enum<T>> {
    T getType();
}
