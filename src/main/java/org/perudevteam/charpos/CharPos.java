package org.perudevteam.charpos;

import io.vavr.Function1;

import java.util.Objects;

/**
 * This interface describes some Object which holds some line position information.
 * <br>
 * Specifically, The line number of the Object and the position of the Object on that line.
 */
public interface CharPos {
    static CharPos charPos(int l, int lp) {
        return new CharPos() {
            @Override
            public int getLine() {
                return l;
            }

            @Override
            public int getLinePosition() {
                return lp;
            }

            @Override
            public CharPos withLine(int l) {
                return CharPos.charPos(l, lp);
            }

            @Override
            public CharPos withLinePosition(int lp) {
                return CharPos.charPos(l, lp);
            }

            @Override
            public int hashCode() {
                return Objects.hash(l, lp);
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == null) return false;
                if (obj.getClass() != this.getClass()) return false;
                CharPos that = (CharPos) obj;
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

    CharPos withLine(int l);

    CharPos withLinePosition(int lp);

    default CharPos withPosition(CharPos d) {
        Objects.requireNonNull(d);
        return d;
    }

    default CharPos mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLine(f.apply(getLine()));
    }

    default CharPos mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLinePosition(f.apply(getLinePosition()));
    }

    default CharPos mapPosition(Function1<? super CharPos, ? extends CharPos> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }
}
