package org.perudevteam.ide.editor.sprite;

import io.vavr.Function1;

import java.awt.*;
import java.util.Objects;

// Test Comment.
public abstract class Sprite<T extends Sprite<T>> {

    private final int xPosition;
    private final int yPosition;

    protected Sprite(int x, int y) {
        xPosition = x;
        yPosition = y;
    }

    public int getXPosition(){
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public abstract T withPosition(int x, int y);

    public T withXPosition(int x) {
        return withPosition(x, yPosition);
    }

    public T mapXPosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(xPosition), yPosition);
    }

    public T withYPosition(int y) {
        return withPosition(xPosition, y);
    }

    public T mapYPosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withPosition(xPosition, f.apply(yPosition));
    }

    public abstract void render(Graphics g);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprite<?> sprite = (Sprite<?>) o;
        return xPosition == sprite.xPosition &&
                yPosition == sprite.yPosition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xPosition, yPosition);
    }
}
