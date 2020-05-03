package org.perudevteam.charpos;

import io.vavr.Function1;
import org.perudevteam.lexer.charlexer.CharSimpleContext;

import java.util.Objects;

public class CharPosValue<T> implements MappableCharPos<CharPosValue<T>>  {

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
    public CharPosValue<T> withPosition(int l, int lp) {
        return new CharPosValue<>(line, linePosition, value);
    }

    public T getValue() {
        return value;
    }

    public CharPosValue<T> withValue(T v) {
        return new CharPosValue<T>(getLine(), getLinePosition(), v);
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

    @Override
    public String toString() {
        return "[ " + line + " : " + linePosition + "]" + " (" + value + ")";
    }
}
