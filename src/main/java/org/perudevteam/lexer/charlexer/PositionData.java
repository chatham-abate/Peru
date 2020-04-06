package org.perudevteam.lexer.charlexer;

public class PositionData {
    public static final PositionData INIT_POSITION = new PositionData(1, 1, 1);

    private int starting;
    private int ending;
    private int current;

    public PositionData(int s, int e, int c) {
        starting = s;
        ending = e;
        current = c;
    }

    public int getStarting() {
        return starting;
    }

    public int getEnding() {
        return ending;
    }

    public int getCurrent() {
        return current;
    }

    public PositionData withStarting(int s) {
        return new PositionData(s, ending, current);
    }

    public PositionData withEnding(int e) {
        return new PositionData(starting, e, current);
    }

    public PositionData withCurrent(int c) {
        return new PositionData(starting, ending, c);
    }

    @Override
    public String toString() {
        return "(s:" + starting + ", e:" + ending + ", c:" + current + ")";
    }
}
