package org.perudevteam.lexer.charlexer;

/**
 * A class holding abstract information about something's position.
 */
public class PositionData {

    /**
     * A preset initial Position Data instance. (All positions with value 0)
     */
    public static final PositionData INIT_POSITION = new PositionData(0, 0, 0);

    /**
     * The starting position.
     */
    private final int starting;

    /**
     * The ending position.
     */
    private final int ending;

    /**
     * The current position.
     */
    private final int current;

    /**
     * Constructor.
     *
     * @param s The starting position.
     * @param e The ending position.
     * @param c The current position.
     */
    public PositionData(int s, int e, int c) {
        starting = s;
        ending = e;
        current = c;
    }

    /**
     * Get the starting position.
     *
     * @return The starting position.
     */
    public int getStarting() {
        return starting;
    }

    /**
     * Get the ending position.
     *
     * @return The ending position.
     */
    public int getEnding() {
        return ending;
    }

    /**
     * Get the current position.
     *
     * @return The current position.
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Set the starting position.
     *
     * @param s The new starting position.
     * @return A new <b>PositionData</b> containing the given starting position.
     */
    public PositionData withStarting(int s) {
        return new PositionData(s, ending, current);
    }

    /**
     * Set the ending position.
     *
     * @param e The new ending position.
     * @return A new <b>PositionData</b> containing the given ending position.
     */
    public PositionData withEnding(int e) {
        return new PositionData(starting, e, current);
    }

    /**
     * Set the current position.
     *
     * @param c The new current position.
     * @return A new <b>PositionData</b> containing the given current position.
     */
    public PositionData withCurrent(int c) {
        return new PositionData(starting, ending, c);
    }

    @Override
    public String toString() {
        return "(s:" + starting + ", e:" + ending + ", c:" + current + ")";
    }
}
