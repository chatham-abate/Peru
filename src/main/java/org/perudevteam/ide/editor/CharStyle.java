package org.perudevteam.ide.editor;

import java.util.Objects;

public class CharStyle {
    public static CharStyle style(FColor fg) {
        return new CharStyle(false, false, fg, FColor.CLEAR, FColor.CLEAR, true);
    }

    public static CharStyle style(boolean i, boolean b, FColor fg) {
        return new CharStyle(i, b, fg, FColor.CLEAR, FColor.CLEAR, true);
    }

    public static CharStyle style(boolean i, boolean b, FColor fg, FColor bg, FColor un) {
        return new CharStyle(i, b, fg, bg, un, true);
    }

    // Style.
    private final boolean italicize;
    private final boolean bold;

    // Colors.
    private final FColor foreground;
    private final FColor background;
    private final FColor underline;

    protected CharStyle(boolean i, boolean b, FColor fg, FColor bg, FColor un, boolean withCheck) {
        if (withCheck) {
            Objects.requireNonNull(fg);
            Objects.requireNonNull(bg);
            Objects.requireNonNull(un);
        }

        italicize = i;
        bold = b;
        foreground = fg;
        background = bg;
        underline = un;
    }


    public boolean italicize() {
        return italicize;
    }

    public CharStyle withItalicsFlag(boolean i) {
        return new CharStyle(i, bold, foreground, background, underline, false);
    }

    public boolean bold() {
        return bold;
    }

    public CharStyle withBoldFlag(boolean b) {
        return new CharStyle(italicize, b, foreground, background, underline, false);
    }

    public FColor getForeground() {
        return foreground;
    }

    public CharStyle withForeground(FColor fg) {
        Objects.requireNonNull(fg);
        return new CharStyle(italicize, bold, fg, background, underline, false);
    }

    public FColor getBackground() {
        return background;
    }

    public CharStyle withBackground(FColor bg) {
        Objects.requireNonNull(bg);
        return new CharStyle(italicize, bold, foreground, bg, underline, false);
    }

    public FColor getUnderline() {
        return underline;
    }

    public CharStyle withUnderLine(FColor un) {
        Objects.requireNonNull(un);
        return new CharStyle(italicize, bold, foreground, background, un, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharStyle charStyle = (CharStyle) o;
        return italicize == charStyle.italicize &&
                bold == charStyle.bold &&
                foreground.equals(charStyle.foreground) &&
                background.equals(charStyle.background) &&
                underline.equals(charStyle.underline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(italicize, bold, foreground, background, underline);
    }
}
