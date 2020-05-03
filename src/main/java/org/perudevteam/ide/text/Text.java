package org.perudevteam.ide.text;

import org.perudevteam.charpos.CharPos;

import java.util.Objects;

// Texted is positioned with line and linePosition...
public interface Text<T extends Text<T>> {

    // Get Line Methods...
    int numberOfLines();

    default void requireValidLine(int l) {
        if (l >= numberOfLines() || l < 0) {
            throw new IndexOutOfBoundsException("Invalid line index " + l + ".");
        }
    }

    default String getLine(int l) {
        requireValidLine(l);
        return getLineUnchecked(l);
    }

    String getLineUnchecked(int l);

    // Position Validation Methods...
    default void requireValidLinePosition(int l, int lp) {
        int lineLength = getLine(l).length();
        if (lp >= lineLength || lp < 0) {
            throw new IndexOutOfBoundsException("Invalid line position index " + lp + ".");
        }
    }

    default void requireValidInsertionLinePosition(int l, int lp) {
        int lineLength = getLine(l).length();
        if (lp < 0 || lp > lineLength) {
            throw new IndexOutOfBoundsException("Invalid line position index for insertion " + lp + ".");
        }
    }

    // Insert Char Methods...
    default T insertChar(CharPos d, char c) {
        Objects.requireNonNull(d);
        return insertChar(d.getLine(), d.getLinePosition(), c);
    }

    default T insertChar(int l, int lp, char c) {
        requireValidInsertionLinePosition(l, lp);
        return insertCharUnchecked(l, lp, c);
    }

    T insertCharUnchecked(int l, int lp, char c);

    // Insert String Methods...
    default T insertString(CharPos d, String s) {
        Objects.requireNonNull(d);
        return insertString(d.getLine(), d.getLinePosition(), s);
    }

    default T insertString(int l, int lp, String s) {
        requireValidInsertionLinePosition(l, lp);
        return insertStringUnchecked(l, lp, s);
    }

    T insertStringUnchecked(int l, int lp, String s);

    default T breakLine(int l) {
        if (l <= 0 || l >= numberOfLines()) {
            throw new IndexOutOfBoundsException("Invalid break line index");
        }

        return breakLineUnchecked(l);
    }

    T breakLineUnchecked(int l);

    // Delete Char Methods...
    default T deleteChar(CharPos d) {
        Objects.requireNonNull(d);
        return deleteChar(d.getLine(), d.getLinePosition());
    }

    default T deleteChar(int l, int lp) {
        requireValidLinePosition(l, lp);
        return deleteCharUnchecked(l, lp);
    }

    T deleteCharUnchecked(int l, int lp);

    // Delete Range Methods...
    default T deleteRangeInclusive(CharPos s, CharPos e) {
        Objects.requireNonNull(s);
        Objects.requireNonNull(e);
        return deleteRangeInclusiveUnchecked(s.getLine(), s.getLinePosition(), e.getLine(), e.getLinePosition());
    }

    default T deleteRangeInclusive(int sl, int slp, int el, int elp) {
        requireValidLinePosition(sl, slp);
        requireValidLinePosition(el, elp);

        if (sl > el || (sl == el && slp > elp)) {
            throw new IllegalArgumentException("Non-consecutive range given for deletion.");
        }

        return deleteRangeInclusiveUnchecked(sl, slp, el, elp);
    }

    T deleteRangeInclusiveUnchecked(int sl, int slp, int el, int elp);
}
