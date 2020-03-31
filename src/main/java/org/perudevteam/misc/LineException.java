package org.perudevteam.misc;

public class LineException extends Exception {
    private int line;

    public LineException(int l, String msg) {
        super(msg);
        line = l;
    }

    public String toString() {
        return "[" + line + "] " + getMessage();
    }
}
