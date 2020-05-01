package org.perudevteam.misc;

import io.vavr.Function1;

import java.util.Objects;

/**
 * This interface describes some Object which holds some line position information.
 * <br>
 * Specifically, The line number of the Object and the position of the Object on that line.
 */
public interface Positioned {
    /**
     * Get the line number of the Object.
     *
     * @return The line number.
     */
    int getLine();

    /**
     * Get the line position of the Object.
     *
     * @return The line position.
     */
    int getLinePosition();

    Positioned withLine(int l);

    Positioned withLinePosition(int lp);

    default Positioned withPosition(Positioned d) {
        Objects.requireNonNull(d);
        return d;
    }

    default Positioned mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLine(f.apply(getLine()));
    }

    default Positioned mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLinePosition(f.apply(getLinePosition()));
    }

    default Positioned mapPosition(Function1<? super Positioned, ? extends Positioned> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }
}
