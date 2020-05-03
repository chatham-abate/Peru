package org.perudevteam.ide.text;

import org.perudevteam.charpos.CharPos;

import java.util.Objects;

// Texted is positioned with line and linePosition...
public interface Text {

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
    default Text insertChar(CharPos d, char c) {
        Objects.requireNonNull(d);
        return insertChar(d.getLine(), d.getLinePosition(), c);
    }

    default Text insertChar(int l, int lp, char c) {
        requireValidInsertionLinePosition(l, lp);
        return insertCharUnchecked(l, lp, c);
    }

    Text insertCharUnchecked(int l, int lp, char c);

    // Insert String Methods...
    default Text insertString(CharPos d, String s) {
        Objects.requireNonNull(d);
        return insertString(d.getLine(), d.getLinePosition(), s);
    }

    default Text insertString(int l, int lp, String s) {
        requireValidInsertionLinePosition(l, lp);
        return insertStringUnchecked(l, lp, s);
    }

    Text insertStringUnchecked(int l, int lp, String s);

    // Delete Line Methods...
    default Text deleteLine(int l) {
        requireValidLine(l);
        return deleteLineUnchecked(l);
    }

    Text deleteLineUnchecked(int l);

    // Delete Char Methods...
    default Text deleteChar(CharPos d) {
        Objects.requireNonNull(d);
        return deleteChar(d.getLine(), d.getLinePosition());
    }

    default Text deleteChar(int l, int lp) {
        requireValidLinePosition(l, lp);
        return deleteCharUnchecked(l, lp);
    }

    Text deleteCharUnchecked(int l, int lp);

    // Delete Range Methods...
    default Text deleteRangeInclusive(CharPos s, CharPos e) {
        Objects.requireNonNull(s);
        Objects.requireNonNull(e);
        return deleteRangeInclusiveUnchecked(s.getLine(), s.getLinePosition(), e.getLine(), e.getLinePosition());
    }

    default Text deleteRangeInclusive(int sl, int slp, int el, int elp) {
        requireValidLinePosition(sl, slp);
        requireValidLinePosition(el, elp);

        if (sl > el || (sl == el && slp > elp)) {
            throw new IllegalArgumentException("Non-consecutive range given for deletion.");
        }

        return deleteRangeInclusiveUnchecked(sl, slp, el, elp);
    }

    Text deleteRangeInclusiveUnchecked(int sl, int slp, int el, int elp);
}
