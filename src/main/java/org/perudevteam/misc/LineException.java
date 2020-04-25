package org.perudevteam.misc;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import org.perudevteam.lexer.charlexer.CharData;

import javax.sound.sampled.Line;
import java.util.Objects;

public class LineException extends Exception implements Positioned {

    public static <T> Try<T> matchLineEx(Try<? extends T> tryValue,
                                         Function1<? super T, ? extends T> valueMap,
                                         Function1<? super LineException, ? extends LineException> exMap) {
        return MiscHelpers.throwMatch(LineException.class, tryValue, valueMap, exMap);
    }

    public static <T> Try<T> mapLineExIfNeeded(Try<? extends T> tryValue,
                                               Function1<? super Throwable, ? extends LineException> exMap) {
        return MiscHelpers.mapCauseIfNeeded(LineException.class, tryValue, exMap);
    }

    public static LineException lineEx(Positioned d, String msg) {
        Objects.requireNonNull(d);
        return new LineException(d.getLine(), d.getLinePosition(), msg);
    }

    public static LineException lineEx(int l, int lp, String msg) {
        return new LineException(l, lp, msg);
    }

    private final int line;
    private final int linePosition;

    private LineException(int l, int lp, String msg) {
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
    public String toString() {
        return "[" + line + " : " + linePosition + "] " + getMessage();
    }

    public LineException withLine(int l) {
        return new LineException(l, linePosition, getMessage());
    }

    public LineException withLinePosition(int lp) {
        return new LineException(line, lp, getMessage());
    }

    public LineException withPosition(Positioned d) {
        return new LineException(d.getLine(), d.getLinePosition(), getMessage());
    }

    public LineException withMessage(String msg) {
        return new LineException(line, linePosition, getMessage());
    }
}
