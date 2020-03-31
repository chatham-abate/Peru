package org.perudevteam.lexer.charlexer;

public class CharSimpleContext {

    public static final CharSimpleContext INITIAL_CONTEXT = new CharSimpleContext(1, 1, 1);

    private int currentLine;
    private int staringLine;
    private int endingLine;

    public CharSimpleContext(int c, int s, int e) {
        currentLine = c;
        staringLine = s;
        endingLine = e;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public int getStartingLine() {
        return staringLine;
    }

    public int getEndingLine() {
        return endingLine;
    }

    public CharSimpleContext withCurrentLine(int c) {
        return new CharSimpleContext(c, staringLine, endingLine);
    }

    public CharSimpleContext withStartingLine(int s) {
        return new CharSimpleContext(currentLine, s, endingLine);
    }

    public CharSimpleContext withEndingLine(int e) {
        return new CharSimpleContext(currentLine, staringLine, e);
    }
}
