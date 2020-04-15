package org.perudevteam.misc;

import org.perudevteam.lexer.charlexer.CharData;

import java.util.Objects;

public class LineException extends Exception {
    private final int line;
    private final int linePosition;

    public LineException(Positioned d, String msg) {
        super(msg);
        Objects.requireNonNull(d);
        line = d.getLine();
        linePosition = d.getLinePosition();
    }

    public LineException(int l, int lp, String msg) {
        super(msg);
        line = l;
        linePosition = lp;
    }

    public int getLine() {
        return line;
    }

    public int getLinePosition() {
        return linePosition;
    }

    public String toString() {
        return "[" + line + " : " + linePosition + "] " + getMessage();
    }
}
