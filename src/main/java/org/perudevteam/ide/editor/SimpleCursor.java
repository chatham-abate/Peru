package org.perudevteam.ide.editor;

import org.perudevteam.charpos.SimpleCharPos;

import java.util.Objects;

public class SimpleCursor implements Cursor<SimpleCursor> {

    public static SimpleCursor simpleCursor(int l, int lp, int ti, int tp) {
        return new SimpleCursor(l, lp, ti, tp);
    }

    private final int line;
    private final int linePosition;
    private final int tokenIndex;
    private final int tokenPosition;

    protected SimpleCursor(int l, int lp, int ti, int tp) {
        line = l;
        linePosition = lp;
        tokenIndex = ti;
        tokenPosition = tp;
    }

    @Override
    public int getTokenIndex() {
        return tokenIndex;
    }

    @Override
    public int getTokenPosition() {
        return tokenPosition;
    }

    @Override
    public SimpleCursor withTokenIndexAndPosition(int ti, int tp) {
        return new SimpleCursor(line, linePosition, ti, tp);
    }

    @Override
    public SimpleCursor withPosition(int l, int lp) {
        return new SimpleCursor(l, lp, tokenIndex, tokenPosition);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleCursor that = (SimpleCursor) o;
        return line == that.line &&
                linePosition == that.linePosition &&
                tokenIndex == that.tokenIndex &&
                tokenPosition == that.tokenPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, linePosition, tokenIndex, tokenPosition);
    }
}
