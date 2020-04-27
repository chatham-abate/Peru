package org.perudevteam.misc;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import java.util.Objects;
import static org.perudevteam.misc.LineException.*;

/**
 * A <b>TraceException</b> is an <b>Exception</b> which holds a sequence of <b>LineExceptions</b>.
 * This class is supposed to be used for error handling in some custom language which may throw nested errors.
 * <br>
 * Think a stack of function calls. When an error occurs at some level in the stack, this class allows us to
 * store all calls in a single <b>Exception</b>.
 */
public class TraceException extends Exception implements Positioned {

    /**
     * Match some <b>Try</b>.
     * <br>
     * If the <b>Try</b> is a <b>Success</b>, map its internal value.
     * <br>
     * If the <b>Try</b> is a <b>Failure</b> containing a <b>TraceException</b>, map the <b>TraceException</b>
     * to some other <b>TraceException</b>.
     * <br>
     * Otherwise, the <b>Try</b> must be a <b>Failure</b> containing some other <b>Throwable</b>. In this case,
     * throw an error.
     * @see org.perudevteam.misc.MiscHelpers
     *
     * @param tryValue The given <b>Try</b>.
     * @param valueMap The function for mapping the <b>Try</b>'s internal value.
     * @param exMap The function for mapping the <b>Try</b>'s internal <b>TraceException</b>.
     * @param <T> The given <b>Try</b>'s internal type.
     * @param <U> The resulting <b>Try</b>'s internal type.
     * @return The matched <b>Try</b>.
     */
    public static <T, U> Try<U> matchTraceEx(Try<? extends T> tryValue,
                                          Function1<? super T, ? extends U> valueMap,
                                          Function1<? super TraceException, ? extends TraceException> exMap) {
        return MiscHelpers.throwMatch(TraceException.class, tryValue, valueMap, exMap);
    }

    /**
     * Map the cause of a <b>Try</b> if and only if the <b>Try</b> is a <b>Failure</b>
     * containing some non <b>TraceException</b>.
     *
     * @param tryValue The given <b>Try</b>.
     * @param exMap The cause mapping function.
     * @param <T> The <b>Try</b>'s internal type.
     * @return The mapped <b>Try</b>.
     */
    public static <T> Try<T> mapTraceExIfNeeded(Try<? extends T> tryValue,
                                                Function1<? super Throwable, ? extends TraceException> exMap) {
        return MiscHelpers.mapCauseIfNeeded(TraceException.class, tryValue, exMap);
    }

    /**
     * A Singleton <b>TraceException</b> containing an empty sequence of <b>LineException</b>s.
     */
    private static final TraceException EMPTY_TRACE = new TraceException(List.empty());

    /**
     * Get the Singleton empty trace.
     *
     * @return The empty trace.
     */
    public static TraceException emptyTrace() {
        return EMPTY_TRACE;
    }

    /**
     * This <b>TraceException</b>'s sequence of <b>LineException</b>s.
     */
    private final Seq<LineException> trace;


    /**
     * Construct a <b>TraceException</b> with some sequence of <b>LineException</b>s.
     *
     * @param t The sequence of <b>LineException</b>s.
     */
    private TraceException(Seq<? extends LineException> t) {
        super();
        Objects.requireNonNull(t);
        t.forEach(Objects::requireNonNull);

        trace = Seq.narrow(t);
    }

    /**
     * Get the first <b>LineException</b> in this <b>TraceException</b>'s sequence of
     * <b>LineException</b>s.
     * <br>
     * An error is thrown if this <b>TraceException</b> is empty.
     *
     * @return The head of this <b>TraceException</b>.
     */
    private LineException headException() {
        if (trace.isEmpty()) {
            throw new NullPointerException("Exception has no trace.");
        }

        return trace.head();
    }

    @Override
    public String getMessage() {
        return headException().getMessage();
    }

    @Override
    public int getLine() {
        return headException().getLine();
    }

    @Override
    public int getLinePosition() {
        return headException().getLinePosition();
    }

    @Override
    public String toString() {
        if (trace.isEmpty()) {
            return "";
        }

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(trace.head().toString());
        trace.tail().forEach(ex -> strBuilder.append("\n  ").append(ex));

        return strBuilder.toString();
    }

    /**
     * Prepend a <b>LineException</b> onto the front of this <b>TraceException</b>'s
     * sequence of <b>LineException</b>s.
     *
     * @param e The <b>LineException</b> to push.
     * @return The new <b>TraceException</b>.
     */
    public TraceException push(LineException e) {
        return new TraceException(trace.prepend(e));
    }

    /**
     * Prepend a new <b>LineException</b> onto the front of this <b>TraceException</b>'s
     * sequence of <b>LineException</b>s.
     *
     * @param d The position data of the new <b>LineException</b>.
     * @param msg The message of the new <b>LineException</b>.
     * @return The new <b>TraceException</b>.
     */
    public TraceException push(Positioned d, String msg) {
        return new TraceException(trace.prepend(lineEx(d, msg)));
    }

    /**
     * Prepend a new <b>LineException</b> onto the front of this <b>TraceException</b>'s
     * sequence of <b>LineException</b>s.
     *
     * @param l The line number of the new <b>LineException</b>.
     * @param lp The line position of the new <b>LineException</b>.
     * @param msg The message of the new <b>LineException</b>.
     * @return The new <b>TraceException</b>.
     */
    public TraceException push(int l, int lp, String msg) {
        return new TraceException(trace.prepend(lineEx(l, lp, msg)));
    }
}
