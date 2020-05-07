package com.github.chathamabate.peru.charpos;

import com.github.chathamabate.peru.parser.Tokenized;
import io.vavr.Function1;

import java.util.Objects;

public class EnumCharPos<T extends Enum<T>> implements Tokenized<T>, MappableCharPos<EnumCharPos<T>> {
    public static <T extends Enum<T>> EnumCharPos<T> charPosEnum(CharPos d, T v) {
        Objects.requireNonNull(d);
        return new EnumCharPos<>(d.getLine(), d.getLinePosition(), v);
    }

    public static <T extends Enum<T>> Function1<CharPos, EnumCharPos<T>> enumBuilder(T v) {
        return d -> charPosEnum(d, v);
    }

    public static <T extends Enum<T>> EnumCharPos<T> charPosEnum(int l, int lp, T v) {
        return new EnumCharPos<>(l, lp, v);
    }

    private final int line;
    private final int linePosition;
    private final T value;

    protected EnumCharPos(int l, int lp, T v) {
        Objects.requireNonNull(v);

        line = l;
        linePosition = lp;
        value = v;
    }

    @Override
    public EnumCharPos<T> withPosition(int l, int lp) {
        return new EnumCharPos<>(l, lp, value);
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

    public EnumCharPos<T> withValue(T v) {
        Objects.requireNonNull(v);
        return new EnumCharPos<>(line, linePosition, v);
    }

    public EnumCharPos<T> mapValue(Function1<? super T, ? extends T> f) {
        Objects.requireNonNull(f);
        return new EnumCharPos<>(line, linePosition, f.apply(value));
    }

    @Override
    public T getTokenType() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumCharPos<?> that = (EnumCharPos<?>) o;
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