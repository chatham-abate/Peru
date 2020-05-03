package org.perudevteam.ide.text;

import io.vavr.collection.Seq;

import java.util.Objects;

// Simple Text will hold a sequence of strings... That's it...
public class SimpleText implements Text {

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


    // MUST DEAL WITH NEWLINE CHARACTERS!!!!
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
        String[] lineInsertions = s.split("\n");

        if (lineInsertions.length > 1) {
            String suffix = lines.get(l).substring(lp);
            Seq<String> newLines = lines;

            // Take out suffix.
            newLines = lines.update(l, r -> r.substring(0, lp));

            // TODO Finish This ALGO....
        }

        return new SimpleText(lines.update(l, r -> r.substring(0, lp) + s + r.substring(lp)));
    }

    @Override
    public SimpleText deleteLineUnchecked(int l) {
        return new SimpleText(lines.removeAt(l));
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

        Seq<String> newLines = lines;

        // First remove excess characters on the starting line...
        newLines = newLines.update(sl, s -> s.substring(0, slp));

        // Now for the middle lines (If they exist.)
        for (int midLine = sl + 1; midLine < el; midLine++) {
            newLines = newLines.removeAt(sl + 1);
        }

        if (elp == newLines.get(sl + 1).length() - 1) {
            // Delete the ending line.
            newLines = newLines.removeAt(sl + 1);
        } else {
            // Trim the ending line.
            newLines = newLines.update(sl + 1, s -> s.substring(elp + 1));
        }

        return new SimpleText(newLines);
    }
}
