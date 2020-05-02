package org.perudevteam.charpos;

import io.vavr.Function1;

import java.util.Objects;

public class CharPosValue<T> implements CharPos {

    public static <T> CharPosValue<T> charPosValue(CharPos d, T v) {
        Objects.requireNonNull(d);
        return new CharPosValue<>(d.getLine(), d.getLinePosition(), v);
    }

    public static <T> CharPosValue<T> charPosValue(int l, int lp, T v) {
        return new CharPosValue<>(l, lp, v);
    }

    public static <T> Function1<CharPos, CharPosValue<T>> valueBuilder(T v) {
        return d -> charPosValue(d, v);
    }

    private final int line;
    private final int linePosition;
    private final T value;

    protected CharPosValue(int l, int lp, T v) {
        Objects.requireNonNull(v);

        line = l;
        linePosition = lp;
        value = v;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getLinePosition() {
        return linePosition;
    }

    public T getValue() {
        return value;
    }

    @Override
    public CharPosValue<T> withLine(int l) {
        return new CharPosValue<>(l, linePosition, value);
    }

    @Override
    public CharPosValue<T> withLinePosition(int lp) {
        return new CharPosValue<>(line, lp, value);
    }

    @Override
    public CharPosValue<T> withPosition(CharPos d) {
        Objects.requireNonNull(d);
        return new CharPosValue<>(d.getLine(), d.getLinePosition(), value);
    }

    public CharPosValue<T> withValue(T v) {
        return new CharPosValue<>(line, linePosition, v);
    }

    @Override
    public CharPosValue<T> mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLine(f.apply(line));
    }

    @Override
    public CharPosValue<T> mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLinePosition(f.apply(linePosition));
    }

    @Override
    public CharPosValue<T> mapPosition(Function1<? super CharPos, ? extends CharPos> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }

    public CharPosValue<T> mapValue(Function1<? super T, ? extends T> f) {
        Objects.requireNonNull(f);
        return withValue(f.apply(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharPosValue<?> that = (CharPosValue<?>) o;
        return line == that.line &&
                linePosition == that.linePosition &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, linePosition, value);
    }
}
