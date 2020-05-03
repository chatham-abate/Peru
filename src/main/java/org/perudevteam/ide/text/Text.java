package org.perudevteam.ide.text;

import org.perudevteam.charpos.CharPos;

import java.util.Objects;

// Texted is positioned with line and linePosition...
public interface Text {

    // Insert text...
    // remove text...

    default Text insertChar(CharPos d, char c) {
        Objects.requireNonNull(d);
        return insertChar(d.getLine(), d.getLinePosition(), c);
    }

    Text insertChar(int l, int lp, char c);

    default Text insertString(CharPos d, String s) {
        Objects.requireNonNull(d);
        return insertString(d.getLine(), d.getLinePosition(), s);
    }

    Text insertString(int l, int lp, String s);

}
