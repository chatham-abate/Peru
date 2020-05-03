package org.perudevteam.ide.text;

import io.vavr.collection.*;

import java.util.Objects;

// Simple Text will hold a sequence of strings... That's it...
public class SimpleText implements Text<SimpleText> {

    private static final SimpleText SINGLE_LINE = new SimpleText(Vector.of(""));

    public static SimpleText singleLine() {
        return SINGLE_LINE;
    }

    public static SimpleText of(String text) {
        Objects.requireNonNull(text);
        return new SimpleText(sliceLines(text));
    }

    public static SimpleText of(Seq<String> lines) {
        Objects.requireNonNull(lines);
        lines.forEach(Objects::requireNonNull);

        return new SimpleText(lines);
    }

    static Vector<String> sliceLines(String text) {
        Queue<String> lines = Queue.empty();
        StringBuilder currLine = new StringBuilder();

        for (char c: text.toCharArray()) {
            if (c == '\n') {
                lines = lines.append(currLine.toString());
                currLine = new StringBuilder();
            } else {
                currLine.append(c);
            }
        }

        lines = lines.append(currLine.toString());
        return Vector.ofAll(lines);
    }

    private final Seq<String> lines;

    protected SimpleText(Seq<String> ls) {
        lines = ls;
    }

    @Override
    public int numberOfLines() {
        return lines.length();
    }

    @Override
    public String getLineUnchecked(int l) {
        return lines.get(l);
    }

    @Override
    public SimpleText insertCharUnchecked(int l, int lp, char c) {
        if (c == '\n') {
            Seq<String> newLines = lines;
            // Slice off suffix.
            String suffix = newLines.get(l).substring(lp);
            return new SimpleText(newLines.update(l, s -> s.substring(0, lp)).insert(l + 1, suffix));
        }

        return new SimpleText(lines.update(l, s -> s.substring(0, lp) + c + s.substring(lp)));
    }

    @Override
    public SimpleText insertStringUnchecked(int l, int lp, String s) {
        Seq<String> lineInsertions = sliceLines(s);

        if (lineInsertions.length() > 1) {
            String suffix = lines.get(l).substring(lp);

            // Take out suffix.
            Seq<String> newLines = lines.update(l, r -> r.substring(0, lp) + lineInsertions.get(0));

            for (int i = 1; i < lineInsertions.length(); i++) {
                newLines = newLines.insert(l + i, lineInsertions.get(i));
            }

            newLines = newLines.update(l + lineInsertions .length() - 1, r -> r + suffix);

            return new SimpleText(newLines);
        }

        return new SimpleText(lines.update(l, r -> r.substring(0, lp) + s + r.substring(lp)));
    }

    @Override
    public SimpleText breakLineUnchecked(int l) {
        Seq<String> newLines = lines.update(l - 1, s -> s + lines.get(l))
                .removeAt(l);
        return new SimpleText(newLines);
    }

    @Override
    public SimpleText deleteCharUnchecked(int l, int lp) {
        return new SimpleText(
                lines.update(l, s -> s.substring(0, lp) + s.substring(lp + 1)));
    }

    @Override
    public SimpleText deleteRangeInclusiveUnchecked(int sl, int slp, int el, int elp) {
        if (sl == el) {
            return new SimpleText(
                    lines.update(sl, s -> s.substring(0, slp) + s.substring(elp + 1)));
        }

        String suffix = lines.get(el).substring(elp + 1);
        Seq<String> newLines = lines;

        // First remove excess characters on the starting line... add suffix from ending line...
        newLines = newLines.update(sl, s -> s.substring(0, slp) + suffix);

        // Remove all deleted lines.
        for (int delLine = sl + 1; delLine <= el; delLine++) {
            newLines = newLines.removeAt(sl + 1);
        }

        return new SimpleText(newLines);
    }

    @Override
    public String toString() {
        return lines.foldLeft("", (x, y) -> x + "\n" + y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleText that = (SimpleText) o;
        return lines.equals(that.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lines);
    }
}
