package org.perudevteam.ide.editor;

import io.vavr.Function1;
import org.perudevteam.charpos.CharPos;
import org.perudevteam.charpos.MappableCharPos;

import java.util.Objects;

public interface Cursor<C extends Cursor<C>> extends MappableCharPos<C> {

    int getTokenIndex();

    int getTokenPosition();

    C withTokenIndexAndPosition(int ti, int tp);

    default C withTokenIndexAndPosition(Cursor<?> d) {
        Objects.requireNonNull(d);
        return withTokenIndexAndPosition(d.getTokenIndex(), d.getTokenPosition());
    }

    default C withTokenIndex(int ti) {
        return withTokenIndexAndPosition(ti, getTokenPosition());
    }

    default C withTokenPosition(int tp) {
        return withTokenIndexAndPosition(getTokenIndex(), tp);
    }

    default C mapTokenIndex(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withTokenIndexAndPosition(f.apply(getTokenIndex()), getTokenPosition());
    }

    default C mapTokenPosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withTokenIndexAndPosition(getTokenIndex(), f.apply(getTokenPosition()));
    }

    default C mapTokenIndexAndPosition(Function1<? super Cursor<?>, ? extends Cursor<?>> f) {
        Objects.requireNonNull(f);
        return withTokenIndexAndPosition(f.apply(this));
    }
}
