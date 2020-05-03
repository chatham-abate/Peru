package org.perudevteam.misc;

import io.vavr.Function1;
import io.vavr.control.Try;
import org.perudevteam.charpos.CharPos;
import org.perudevteam.charpos.MappableCharPos;

import java.util.Objects;

/**
 * Class representing an exception which occurred on some line at some some position on that line.
 */
public class LineException extends Exception implements MappableCharPos<LineException> {

    /**
     * Given a <b>Try</b>.
     * <br>
     * If the <b>Try</b> is a success, map its internal value.
     * <br>
     * If the <b>Try</b> is a failure containing a <b>LineException</b>, map the <b>LineException</b>.
     * <br>
     * Otherwise, if the <b>Try</b> is a failure containing some other type of <b>Throwable</b>,
     * throw an error.
     * @see org.perudevteam.misc.MiscHelpers
     *
     * @param tryValue The <b>Try</b>.
     * @param valueMap The function for mapping the successful <b>Try</b>'s internal value.
     * @param exMap The function for mapping the <b>Try</b>'s internal <b>LineException</b>.
     * @param <T> The internal type of the given <b>Try</b>.
     * @param <U> The internal type of the resulting <b>Try</b>.
     * @return The mapped <b>Try</b>.
     */
    public static <T, U> Try<U> matchLineEx(Try<? extends T> tryValue,
                                         Function1<? super T, ? extends U> valueMap,
                                         Function1<? super LineException, ? extends LineException> exMap) {
        return MiscHelpers.throwMatch(LineException.class, tryValue, valueMap, exMap);
    }

    /**
     * Given a <b>Try</b>.
     * <br>
     * If the <b>Try</b> is a <b>Success</b> or a <b>Failure</b> containing a <b>LineException</b>,
     * simply return the <b>Try</b>.
     * <br>
     * Otherwise, the <b>Try</b> must be a <b>Failure</b> containing a non <b>LineException</b>. Map this
     * <b>Throwable</b> to a <b>LineException</b>.
     *
     * @param tryValue The <b>Try</b>.
     * @param exMap The <b>Throwable</b> mapping function.
     * @param <T> The internal type of the given <b>Try</b>.
     * @return The mapped <b>Try</b>.
     */
    public static <T> Try<T> mapLineExIfNeeded(Try<? extends T> tryValue,
                                               Function1<? super Throwable, ? extends LineException> exMap) {
        return MiscHelpers.mapCauseIfNeeded(LineException.class, tryValue, exMap);
    }

    /**
     * Static helper for creating a <b>LineException</b> with some given position data and message.
     *
     * @param d The position data.
     * @param msg The message.
     * @return The created <b>LineException</b>.
     */
    public static LineException lineEx(CharPos d, String msg) {
        Objects.requireNonNull(d);
        return new LineException(d.getLine(), d.getLinePosition(), msg);
    }

    /**
     * Static helper for creating a <b>LineException</b> with some given position data and message.
     *
     * @param l The line number.
     * @param lp The line position.
     * @param msg The message.
     * @return The new <b>LineException</b>.
     */
    public static LineException lineEx(int l, int lp, String msg) {
        return new LineException(l, lp, msg);
    }

    /**
     * The line number of the <b>LineException</b>.
     */
    private final int line;

    /**
     * The line position of the <b>LineException</b>.
     */
    private final int linePosition;

    /**
     * Construct a new <b>LineException</b>.
     *
     * @param l The line number of the <b>LineException</b>.
     * @param lp The line position of the <b>LineException</b>.
     * @param msg The message of the <b>LineException</b>.
     */
    protected LineException(int l, int lp, String msg) {
        super(msg);
        line = l;
        linePosition = lp;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getLinePosition() {
        return linePosition;
    }

    @Override
    public LineException withPosition(int l, int lp) {
        return new LineException(l, lp, getMessage());
    }

    @Override
    public String toString() {
        return "[" + line + " : " + linePosition + "] " + getMessage();
    }

    /**
     * Set the message of this <b>LineException</b>.
     *
     * @param msg The message.
     * @return The new <b>LineException</b>.
     */
    public LineException withMessage(String msg) {
        return new LineException(line, linePosition, getMessage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineException that = (LineException) o;
        return line == that.line &&
                linePosition == that.linePosition && getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, linePosition, getMessage());
    }
}
