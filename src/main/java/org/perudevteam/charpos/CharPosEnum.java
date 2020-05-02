package org.perudevteam.charpos;

import io.vavr.Function1;
import org.perudevteam.parser.Tokenized;

import java.util.Objects;

public class CharPosEnum<T extends Enum<T>> extends CharPosValue<T> implements Tokenized<T> {

    public static <T extends Enum<T>> CharPosEnum<T> charPosEnum(CharPos d, T v) {
        Objects.requireNonNull(d);
        return new CharPosEnum<>(d.getLine(), d.getLinePosition(), v);
    }

    public static <T extends Enum<T>> Function1<CharPos, CharPosEnum<T>> enumBuilder(T v) {
        return d -> charPosEnum(d, v);
    }

    public static <T extends Enum<T>> CharPosEnum<T> charPosEnum(int l, int lp, T v) {
        return new CharPosEnum<>(l, lp, v);
    }

    protected CharPosEnum(int l, int lp, T v) {
        super(l, lp, v);
    }
    
    @Override
    public CharPosEnum<T> withLine(int l) {
        return new CharPosEnum<>(l, getLinePosition(), getValue());
    }

    @Override
    public CharPosEnum<T> withLinePosition(int lp) {
        return new CharPosEnum<>(getLine(), lp, getValue());
    }

    @Override
    public CharPosEnum<T> withPosition(CharPos d) {
        Objects.requireNonNull(d);
        return new CharPosEnum<>(d.getLine(), d.getLinePosition(), getValue());
    }

    public CharPosEnum<T> withValue(T v) {
        return new CharPosEnum<>(getLine(), getLinePosition(), v);
    }

    @Override
    public CharPosEnum<T> mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLine(f.apply(getLine()));
    }

    @Override
    public CharPosEnum<T> mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLinePosition(f.apply(getLinePosition()));
    }

    @Override
    public CharPosEnum<T> mapPosition(Function1<? super CharPos, ? extends CharPos> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }

    public CharPosEnum<T> mapValue(Function1<? super T, ? extends T> f) {
        Objects.requireNonNull(f);
        return withValue(f.apply(getValue()));
    }

    @Override
    public T getTokenType() {
        return getValue();
    }
}
