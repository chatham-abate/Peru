package org.perudevteam.charpos;

import io.vavr.Function1;

import java.util.Objects;

public interface MappableCharPos<CP extends MappableCharPos<CP>> extends CharPos {
    CP withPosition(int l, int lp);

    default CP withPosition(CharPos d) {
        Objects.requireNonNull(d);
        return withPosition(d.getLine(), d.getLinePosition());
    }

    default CP withLine(int l) {
        return withPosition(l, getLinePosition());
    }

    default CP withLinePosition(int lp) {
        return withPosition(getLine(), lp);
    }

    default CP mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(getLine()), getLinePosition());
    }

    default CP mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withPosition(getLine(), f.apply(getLinePosition()));
    }

    default CP mapPosition(Function1<? super CharPos, ? extends CharPos> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }
}
