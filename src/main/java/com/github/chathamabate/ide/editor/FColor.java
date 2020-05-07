package com.github.chathamabate.ide.editor;

import io.vavr.Function1;

import java.awt.*;
import java.util.Objects;

// Functional Color Class...
public class FColor {

    public static final FColor CLEAR = color(0, 0, 0, 0);

    public static FColor color(int r, int g, int b) {
        return new FColor(r, g, b, 255, true);
    }

    public static FColor color(int r, int g, int b, int a) {
        return new FColor(r, g, b, a, true);
    }

    private final int red;
    private final int green;
    private final int blue;
    private final int alpha;

    protected FColor(int r, int g, int b, int a, boolean withCheck) {
        if (withCheck && (
                (r < 0 || r > 255) ||
                        (g < 0 || g > 255) ||
                        (b < 0 || b > 255) ||
                        (a < 0 || a > 255)
        )) {
            throw new IllegalArgumentException("Given invalid color values.");
        }

        red = r;
        green = g;
        blue = b;
        alpha = a;
    }

    public int getRed() {
        return red;
    }

    public FColor withRed(int r) {
        if (r < 0 || r > 255) {
            throw new IllegalArgumentException("Given invalid red value.");
        }

        return new FColor(r, green, blue, alpha, false);
    }

    public FColor mapRed(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withRed(f.apply(red));
    }

    public int getGreen() {
        return green;
    }

    public FColor withGreen(int g) {
        if (g < 0 || g > 255) {
            throw new IllegalArgumentException("Given invalid green value.");
        }

        return new FColor(red, g, blue, alpha, false);
    }

    public FColor mapGreen(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withGreen(f.apply(green));
    }

    public int getBlue() {
        return blue;
    }

    public FColor withBlue(int b) {
        if (b < 0 || b > 255) {
            throw new IllegalArgumentException("Given invalid blue value.");
        }

        return new FColor(red, green, b, alpha, false);
    }

    public FColor mapBlue(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withBlue(f.apply(blue));
    }

    public int getAlpha() {
        return alpha;
    }

    public FColor withAlpha(int a) {
        if (a < 0 || a > 255) {
            throw new IllegalArgumentException("Given invalid alpha value.");
        }

        return new FColor(red, green, blue, a, false);
    }

    public FColor mapAlpha(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withAlpha(f.apply(alpha));
    }

    public Color toAWT() {
        return new Color(red, green, blue, alpha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FColor fColor = (FColor) o;
        return red == fColor.red &&
                green == fColor.green &&
                blue == fColor.blue &&
                alpha == fColor.alpha;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }
}
