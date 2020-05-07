package com.github.chathamabate.peru.ide.editor;

import io.vavr.Function1;

import java.util.Objects;

public class Cursor {

    public static Cursor cursor(int ti, int tp) {
        return new Cursor(ti, tp);
    }

    private final int tokenIndex;
    private final int tokenPosition;

    protected Cursor(int ti, int tp) {
        tokenIndex = ti;
        tokenPosition = tp;
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public int getTokenPosition() {
        return tokenPosition;
    }

    public Cursor withTokenIndex(int ti) {
        return new Cursor(ti, tokenPosition);
    }

    public Cursor mapTokenIndex(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return new Cursor(f.apply(tokenIndex), tokenPosition);
    }

    public Cursor withTokenPosition(int tp) {
        return new Cursor(tokenIndex, tp);
    }

    public Cursor mapTokenPosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return new Cursor(tokenIndex, f.apply(tokenPosition));
    }
}
