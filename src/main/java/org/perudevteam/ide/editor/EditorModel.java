package org.perudevteam.ide.editor;

import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import org.perudevteam.misc.Positioned;

public abstract class EditorModel {

    // Cursor movement functions...
    public abstract EditorModel moveCursorLeft();
    public abstract EditorModel moveCursorRight();
    public abstract EditorModel moveCursorUp();
    public abstract EditorModel moveCursorDown();

    // Editor Text functions...
    public abstract Seq<Tuple2<String, StyleData>> getTokens();
    public abstract EditorModel insertChar(char c);
    public abstract EditorModel deleteChar();
    public abstract EditorModel insertString(String s);
    public abstract EditorModel restyle();      // Restyle text... (With lexer/parser)

    // Utility functions...
    public abstract EditorModel undo();
    public abstract EditorModel redo();

    public abstract EditorModel cut();
    public abstract EditorModel copy();
    public abstract EditorModel paste();

    public abstract EditorModel withSelectionMode(boolean on);
}
