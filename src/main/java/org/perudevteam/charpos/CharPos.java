package org.perudevteam.charpos;


import java.util.Objects;

public interface CharPos {
    int getLine();

    int getLinePosition();

    default boolean isSameAs(CharPos d) {
        Objects.requireNonNull(d);
        return isSameAs(d.getLine(), d.getLinePosition());
    }

    default boolean isSameAs(int l, int lp) {
        return getLine() == l && getLinePosition() == lp;
    }

    default boolean isBefore(CharPos d) {
        Objects.requireNonNull(d);
        return isBefore(d.getLine(), d.getLinePosition());
    }

    default boolean isBefore(int l, int lp) {
        return getLine() < l ||
                (getLine() == l && getLinePosition() < lp);
    }

    default boolean isAfter(CharPos d) {
        Objects.requireNonNull(d);
        return isAfter(d.getLine(), d.getLinePosition());
    }

    default boolean isAfter(int l, int lp) {
        return getLine() > l ||
                (getLine() == l && getLinePosition() > lp);
    }
}
