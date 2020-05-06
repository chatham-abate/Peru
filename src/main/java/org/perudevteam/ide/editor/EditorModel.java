package org.perudevteam.ide.editor;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public class EditorModel {
    private final Cursor cursor;
    private final Option<Cursor> selectionStart;

    private final Seq<Tuple2<String, TokenStyle>> tokens;
    private final Stylist stylist;

    protected EditorModel(Cursor c, Option<Cursor> ss, Seq<Tuple2<String, TokenStyle>> ts,
                          Stylist st) {
        cursor = c;
        selectionStart = ss;

        tokens = ts;
        stylist = st;
    }

    protected Cursor requireValidCursor(Cursor c) {
        if (c.getTokenIndex() < 0 || tokens.length() <= c.getTokenIndex()) {
            throw new IndexOutOfBoundsException("Invalid token index " + c.getTokenIndex() + ".");
        }

        int posUpperBoundExc = tokens.get(c.getTokenIndex())._1.length();

        if (c.getTokenIndex() == tokens.length() - 1) {
            posUpperBoundExc++;
        }

        if (c.getTokenPosition() < 0 || posUpperBoundExc <= c.getTokenPosition()) {
            throw new IndexOutOfBoundsException("Invalid token position " + c.getTokenPosition() + ".");
        }

        return c;
    }

    public EditorModel jumpTo(Cursor c) {
        return new EditorModel(requireValidCursor(c), selectionStart, tokens, stylist);
    }

    
}
