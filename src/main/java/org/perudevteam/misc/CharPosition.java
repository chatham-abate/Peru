package org.perudevteam.misc;

import io.vavr.Function1;

import java.util.Objects;

/**
 * This interface describes some Object which holds some line position information.
 * <br>
 * Specifically, The line number of the Object and the position of the Object on that line.
 */
public interface CharPosition {

    static CharPosition position(int l, int lp) {
        return new CharPosition() {
            @Override
            public int getLine() {
                return l;
            }

            @Override
            public int getLinePosition() {
                return lp;
            }

            @Override
            public CharPosition withLine(int l) {
                return CharPosition.position(l, lp);
            }

            @Override
            public CharPosition withLinePosition(int lp) {
                return CharPosition.position(l, lp);
            }

            @Override
            public int hashCode() {
                return Objects.hash(l, lp);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) return false;
                if (obj.getClass() != this.getClass()) return false;
                CharPosition that = (CharPosition) obj;
                return that.getLine() == getLine() && that.getLinePosition() == getLinePosition();
            }
        };
    }

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

    CharPosition withLine(int l);

    CharPosition withLinePosition(int lp);

    default CharPosition withPosition(CharPosition d) {
        Objects.requireNonNull(d);
        return d;
    }

    default CharPosition mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLine(f.apply(getLine()));
    }

    default CharPosition mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLinePosition(f.apply(getLinePosition()));
    }

    default CharPosition mapPosition(Function1<? super CharPosition, ? extends CharPosition> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }
}
