package org.perudevteam.ide.editor;

import io.vavr.Function1;
import org.perudevteam.parser.Tokenized;

import java.util.Objects;

public class TokenStyle {
    public static TokenStyle style(FColor fg) {
        return style(false, false, fg, FColor.CLEAR, FColor.CLEAR, "");
    }

    public static TokenStyle style(boolean i, boolean b, FColor fg) {
        return style(i, b, fg, FColor.CLEAR, FColor.CLEAR, "");
    }

    public static TokenStyle style(boolean i, boolean b, FColor fg, FColor bg, FColor un, String tt) {
        Objects.requireNonNull(fg);
        Objects.requireNonNull(bg);
        Objects.requireNonNull(un);
        Objects.requireNonNull(tt);

        return new TokenStyle(i, b, fg, bg, un, tt);
    }

    // Style.
    private final boolean italicize;
    private final boolean bold;

    // Colors.
    private final FColor foreground;
    private final FColor background;
    private final FColor underline;

    // Tool Tip.
    private final String toolTip;

    protected TokenStyle(boolean i, boolean b, FColor fg, FColor bg, FColor un, String tt) {
        italicize = i;
        bold = b;
        foreground = fg;
        background = bg;
        underline = un;
        toolTip = tt;
    }

    public boolean italicize() {
        return italicize;
    }

    public TokenStyle withItalicsFlag(boolean i) {
        return new TokenStyle(i, bold, foreground, background, underline, toolTip);
    }

    public boolean bold() {
        return bold;
    }

    public TokenStyle withBoldFlag(boolean b) {
        return new TokenStyle(italicize, b, foreground, background, underline, toolTip);
    }

    public FColor getForeground() {
        return foreground;
    }

    public TokenStyle withForeground(FColor fg) {
        Objects.requireNonNull(fg);
        return new TokenStyle(italicize, bold, fg, background, underline, toolTip);
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
        return new TokenStyle(italicize, bold, foreground, bg, underline, toolTip);
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
        return new TokenStyle(italicize, bold, foreground, background, un, toolTip);
    }

    public TokenStyle mapUnderline(Function1<? super FColor, ? extends FColor> f) {
        Objects.requireNonNull(f);
        return withUnderline(f.apply(underline));
    }

    public String getToolTip() {
        return toolTip;
    }

    public TokenStyle withToolTip(String tt) {
        Objects.requireNonNull(tt);
        return new TokenStyle(italicize, bold, foreground, background, underline, tt);
    }

    public TokenStyle mapToolTip(Function1<? super String, ? extends String> f) {
        Objects.requireNonNull(f);
        return withToolTip(f.apply(toolTip));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenStyle tokenStyle = (TokenStyle) o;
        return italicize == tokenStyle.italicize &&
                bold == tokenStyle.bold &&
                foreground.equals(tokenStyle.foreground) &&
                background.equals(tokenStyle.background) &&
                underline.equals(tokenStyle.underline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(italicize, bold, foreground, background, underline);
    }
}
