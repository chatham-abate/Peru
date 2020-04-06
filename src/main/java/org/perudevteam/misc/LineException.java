package org.perudevteam.misc;

public class LineException extends Exception {
    private int line;
    private int linePosition;

    public LineException(int l, int lp, String msg) {
        super(msg);
        line = l;
        linePosition = lp;
    }

    public String toString() {
        return "[" + line + " : " + linePosition + "] " + getMessage();
    }
}
