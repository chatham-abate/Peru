package org.perudevteam.ide.editor;

import io.vavr.Function1;
import org.perudevteam.misc.CharPosition;

import java.util.Objects;

public class CharStyleData implements CharPosition {

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


    protected CharStyleData(int l, int lp, boolean i, boolean b, FColor fg, FColor bg, FColor un, boolean withCheck) {
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
    public CharStyleData withLine(int l) {
        return new CharStyleData(l, linePosition, italicize, bold, foreground, background, underline, false);
    }

    @Override
    public CharStyleData withLinePosition(int lp) {
        return new CharStyleData(line, lp, italicize, bold, foreground, background, underline, false);
    }

    @Override
    public CharStyleData withPosition(CharPosition d) {
        Objects.requireNonNull(d);
        return new CharStyleData(d.getLine(), d.getLinePosition(), italicize,
                bold, foreground, background, underline, false);
    }

    @Override
    public CharStyleData mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLine(f.apply(line));
    }

    @Override
    public CharStyleData mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return withLinePosition(f.apply(linePosition));
    }

    @Override
    public CharStyleData mapPosition(Function1<? super CharPosition, ? extends CharPosition> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }

    // Now for style characteristics...

    public boolean italicize() {
        return italicize;
    }

    public CharStyleData withItalicsFlag(boolean i) {
        return new CharStyleData(line, linePosition, i, bold, foreground, background, underline, false);
    }

    public boolean bold() {
        return bold;
    }

    public CharStyleData withBoldFlag(boolean b) {
        return new CharStyleData(line, linePosition, italicize, b, foreground, background, underline, false);
    }

    public FColor getForeground() {
        return foreground;
    }

    public CharStyleData withForeground(FColor fg) {
        Objects.requireNonNull(fg);
        return new CharStyleData(line, linePosition, italicize, bold, fg, background, underline, false);
    }

    public FColor getBackground() {
        return background;
    }

    public CharStyleData withBackground(FColor bg) {
        Objects.requireNonNull(bg);
        return new CharStyleData(line, linePosition, italicize, bold, foreground, bg, underline, false);
    }

    public FColor getUnderline() {
        return underline;
    }

    public CharStyleData withUnderLine(FColor un) {
        Objects.requireNonNull(un);
        return new CharStyleData(line, linePosition, italicize, bold, foreground, background, un, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharStyleData that = (CharStyleData) o;
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
