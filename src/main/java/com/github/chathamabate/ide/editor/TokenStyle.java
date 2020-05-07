package com.github.chathamabate.ide.editor;

import io.vavr.Function1;

import java.awt.*;
import java.util.Objects;

public class TokenStyle {

    public static TokenStyle style(Font f, FColor fg) {
        return style(f, fg, FColor.CLEAR, FColor.CLEAR);
    }

    public static TokenStyle style(Font f, FColor fg, FColor bg, FColor un) {
        Objects.requireNonNull(f);
        Objects.requireNonNull(fg);
        Objects.requireNonNull(bg);
        Objects.requireNonNull(un);

        return new TokenStyle(f, fg, bg, un);
    }

    // Font.
    private final Font font;

    // Colors.
    private final FColor foreground;
    private final FColor background;
    private final FColor underline;

    protected TokenStyle(Font f, FColor fg, FColor bg, FColor un) {
        font = f;
        foreground = fg;
        background = bg;
        underline = un;
    }

    public Font getFont() {
        return font;
    }

    public TokenStyle withFont(Font f) {
        Objects.requireNonNull(f);
        return new TokenStyle(f, foreground, background, underline);
    }

    public TokenStyle mapFont(Function1<? super Font, ? extends Font> f) {
        Objects.requireNonNull(f);
        return withFont(f.apply(font));
    }

    public FColor getForeground() {
        return foreground;
    }

    public TokenStyle withForeground(FColor fg) {
        Objects.requireNonNull(fg);
        return new TokenStyle(font, fg, background, underline);
    }

    public TokenStyle mapForeground(Function1<? super FColor, ? extends FColor> f) {
        Objects.requireNonNull(f);
        return withForeground(f.apply(foreground));
    }

    public FColor getBackground() {
        return background;
    }

    public TokenStyle withBackground(FColor bg) {
        Objects.requireNonNull(bg);
        return new TokenStyle(font, foreground, bg, underline);
    }

    public TokenStyle mapBackground(Function1<? super FColor, ? extends FColor> f) {
        Objects.requireNonNull(f);
        return withBackground(f.apply(background));
    }

    public FColor getUnderline() {
        return underline;
    }

    public TokenStyle withUnderline(FColor un) {
        Objects.requireNonNull(un);
        return new TokenStyle(font, foreground, background, un);
    }

    public TokenStyle mapUnderline(Function1<? super FColor, ? extends FColor> f) {
        Objects.requireNonNull(f);
        return withUnderline(f.apply(underline));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenStyle that = (TokenStyle) o;
        return font.equals(that.font) &&
                foreground.equals(that.foreground) &&
                background.equals(that.background) &&
                underline.equals(that.underline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(font, foreground, background, underline);
    }
}
