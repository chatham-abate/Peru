package com.github.chathamabate.peru.charpos;

import java.util.Objects;

public class SimpleCharPos implements MappableCharPos<SimpleCharPos> {

    public static SimpleCharPos simpleCharPos(CharPos d) {
        Objects.requireNonNull(d);
        return new SimpleCharPos(d.getLine(), d.getLinePosition());
    }

    public static SimpleCharPos simpleCharPos(int l, int lp) {
        return new SimpleCharPos(l, lp);
    }

    private final int line;
    private final int linePosition;

    protected SimpleCharPos(int l, int lp) {
        line = l;
        linePosition = lp;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getLinePosition() {
        return linePosition;
    }

    @Override
    public SimpleCharPos withPosition(int l, int lp) {
        return new SimpleCharPos(l, lp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleCharPos that = (SimpleCharPos) o;
        return line == that.line &&
                linePosition == that.linePosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, linePosition);
    }

    @Override
    public String toString() {
        return "[" + line + " : " + linePosition + " ]";
    }
}
