package org.perudevteam.charpos;

import io.vavr.Function1;
import org.perudevteam.parser.Tokenized;

import java.util.Objects;

public class EnumCharPos<T extends Enum<T>> extends ValueCharPos<T> implements Tokenized<T> {
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

    protected EnumCharPos(int l, int lp, T v) {
        super(l, lp, v);
    }

    @Override
    public EnumCharPos<T> withPosition(int l, int lp) {
        return new EnumCharPos<>(l, lp, getValue());
    }

    @Override
    public EnumCharPos<T> withValue(T v) {
        return new EnumCharPos<>(getLine(), getLinePosition(), v);
    }

    @Override
    public EnumCharPos<T> mapValue(Function1<? super T, ? extends T> f) {
        return withValue(f.apply(getValue()));
    }

    @Override
    public T getTokenType() {
        return getValue();
    }
}
