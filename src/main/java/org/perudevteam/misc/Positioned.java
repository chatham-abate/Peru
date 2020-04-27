package org.perudevteam.misc;

/**
 * This interface describes some Object which holds some line position information.
 * <br>
 * Specifically, The line number of the Object and the position of the Object on that line.
 */
public interface Positioned {

    /**
     * Get the line number of the Object.
     *
     * @return The line number.
     */
    int getLine();

    /**
     * Get the line position of the Object.
     *
     * @return The line position.
     */
    int getLinePosition();
}
