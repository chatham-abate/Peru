package org.perudevteam.ide.editor;

import org.perudevteam.misc.Positioned;

public abstract class EditorModel implements Positioned {

    // Cursor movement functions...
    public abstract EditorModel moveCursorLeft();
    public abstract EditorModel moveCursorRight();
    public abstract EditorModel moveCursorUp();
    public abstract EditorModel moveCursorDown();

    // Editor Text functions...
    public abstract String getEditorText();
    public abstract EditorModel insertChar(char c);
    public abstract EditorModel insertString(String s);

    // Utility functions...
    public abstract EditorModel undo();
    public abstract EditorModel redo();

    public abstract EditorModel cut();
    public abstract EditorModel copy();
    public abstract EditorModel paste();

    public abstract EditorModel withSelectionMode(boolean on);
}
