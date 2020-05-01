package org.perudevteam.ide.editor;

import io.vavr.Function1;
import org.perudevteam.misc.CharPosition;

import java.util.Objects;

public class TokenStyleData implements CharPosition {

    // Position.
    private final int line;
    private final int linePosition;

    // Style.
    private final boolean italicize;
    private final boolean bold;

    // Colors.
    private final FColor foreground;
    private final FColor background;
    private final FColor underline;


    protected TokenStyleData(int l, int lp, boolean i, boolean b, FColor fg, FColor bg, FColor un, boolean withCheck) {
        if (withCheck) {
            Objects.requireNonNull(fg);
            Objects.requireNonNull(bg);
            Objects.requireNonNull(un);
        }

        line = l;
        linePosition = lp;

        italicize = i;
        bold = b;

        foreground = fg;
        background = bg;
        underline = un;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getLinePosition() {
        return linePosition;
    }

    @Override
    public TokenStyleData withLine(int l) {
        return new TokenStyleData(l, linePosition, italicize, bold, foreground, background, underline, false);
    }

    @Override
    public TokenStyleData withLinePosition(int lp) {
        return new TokenStyleData(line, lp, italicize, bold, foreground, background, underline, false);
    }

    @Override
    public TokenStyleData withPosition(CharPosition d) {
        Objects.requireNonNull(d);
        return new TokenStyleData(d.getLine(), d.getLinePosition(), italicize,
                bold, foreground, background, underline, false);
    }

    @Override
    public TokenStyleData mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLine(f.apply(line));
    }

    @Override
    public TokenStyleData mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLinePosition(f.apply(linePosition));
    }

    @Override
    public TokenStyleData mapPosition(Function1<? super CharPosition, ? extends CharPosition> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }

    // Now for style characteristics...

    public boolean italicize() {
        return italicize;
    }

    public TokenStyleData withItalicsFlag(boolean i) {
        return new TokenStyleData(line, linePosition, i, bold, foreground, background, underline, false);
    }

    public boolean bold() {
        return bold;
    }

    public TokenStyleData withBoldFlag(boolean b) {
        return new TokenStyleData(line, linePosition, italicize, b, foreground, background, underline, false);
    }

    public FColor getForeground() {
        return foreground;
    }

    public TokenStyleData withForeground(FColor fg) {
        Objects.requireNonNull(fg);
        return new TokenStyleData(line, linePosition, italicize, bold, fg, background, underline, false);
    }

    public FColor getBackground() {
        return background;
    }

    public TokenStyleData withBackground(FColor bg) {
        Objects.requireNonNull(bg);
        return new TokenStyleData(line, linePosition, italicize, bold, foreground, bg, underline, false);
    }

    public FColor getUnderline() {
        return underline;
    }

    public TokenStyleData withUnderLine(FColor un) {
        Objects.requireNonNull(un);
        return new TokenStyleData(line, linePosition, italicize, bold, foreground, background, un, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenStyleData that = (TokenStyleData) o;
        return line == that.line &&
                linePosition == that.linePosition &&
                italicize == that.italicize &&
                bold == that.bold &&
                foreground.equals(that.foreground) &&
                background.equals(that.background) &&
                underline.equals(that.underline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, linePosition, italicize, bold, foreground, background, underline);
    }
}
