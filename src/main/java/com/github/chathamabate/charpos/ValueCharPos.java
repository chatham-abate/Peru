package com.github.chathamabate.charpos;

import io.vavr.Function1;

import java.util.Objects;

public class ValueCharPos<T> implements MappableCharPos<ValueCharPos<T>>  {

    public static <T> ValueCharPos<T> charPosValue(CharPos d, T v) {
        Objects.requireNonNull(d);
        return new ValueCharPos<>(d.getLine(), d.getLinePosition(), v);
    }

    public static <T> ValueCharPos<T> charPosValue(int l, int lp, T v) {
        return new ValueCharPos<>(l, lp, v);
    }

    public static <T> Function1<CharPos, ValueCharPos<T>> valueBuilder(T v) {
        return d -> charPosValue(d, v);
    }

    private final int line;
    private final int linePosition;
    private final T value;

    protected ValueCharPos(int l, int lp, T v) {
        Objects.requireNonNull(v);

        value = v;
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
    public ValueCharPos<T> withPosition(int l, int lp) {
        return new ValueCharPos<>(l, lp, value);
    }

    public T getValue() {
        return value;
    }

    public ValueCharPos<T> withValue(T v) {
        return new ValueCharPos<T>(getLine(), getLinePosition(), v);
    }

    public ValueCharPos<T> mapValue(Function1<? super T, ? extends T> f) {
        Objects.requireNonNull(f);
        return withValue(f.apply(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueCharPos<?> that = (ValueCharPos<?>) o;
        return line == that.line &&
                linePosition == that.linePosition &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, linePosition, value);
    }

    @Override
    public String toString() {
        return "[" + line + " : " + linePosition + "]" + " (" + value + ")";
    }
}
