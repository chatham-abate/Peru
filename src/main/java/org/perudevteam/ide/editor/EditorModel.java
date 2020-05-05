package org.perudevteam.ide.editor;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.perudevteam.charpos.CharPos;
import org.perudevteam.charpos.MappableCharPos;
import org.perudevteam.charpos.SimpleCharPos;
import org.perudevteam.ide.text.Text;

public abstract class EditorModel<T extends Text<T>,
        MCP extends MappableCharPos<MCP>,
        EM extends EditorModel<T, MCP, EM>> {

    private final MCP cursor;
    private final Option<MCP> selectionStart;

    private final T underlyingText;
    private final Seq<Tuple2<String, CharStyle>> stylizedTokens;

    private final Function1<String, Seq<Tuple2<String, CharStyle>>> stylist;

    protected EditorModel(MCP c, Option<MCP> ss, T ut, Seq<Tuple2<String, CharStyle>> st,
                          Function1<String, Seq<Tuple2<String, CharStyle>>> stl) {
        cursor = c;
        selectionStart = ss;
        underlyingText = ut;
        stylizedTokens = st;
        stylist = stl;
    }

    public MCP getCursor() {
        return cursor;
    }

    public Option<MCP> getSelectionStart() {
        return selectionStart;
    }

    public T getUnderlyingText() {
        return underlyingText;
    }

    public Seq<Tuple2<String, CharStyle>> getStylizedTokens() {
        return stylizedTokens;
    }

    // Movement methods...
//
//    public EM cursorUp() {
//
//    }
//
//    public EM cursorDown() {
//
//    }
//
//    public EM cursorLeft() {
//
//    }
//
//    public EM cursorRight() {
//
//    }


}
