package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import org.perudevteam.misc.Positioned;
import org.perudevteam.parser.Tokenized;

import java.util.Objects;

/**
 * This holds the data extracted by this package's character lexers.
 * <br>
 * Specifically this class holds the successful lexeme's category (as an <b>Enum</b>), line number,
 * and line position.
 * <br>
 * Line position refers to the position of the lexeme on its given line.
 *
 * @param <T> The <b>Enum</b> category type of a successful lexeme.
 */
public class CharData<T extends Enum<T>> implements Tokenized<T>, Positioned {

    /**
     * Returns a function used for producing a <b>CharData</b> instance
     * from a {@link org.perudevteam.lexer.charlexer.CharSimpleContext}.
     *
     * @param tokenType The category of the function's resulting <b>CharData</b>.
     * @param <T> The category <b>Enum</b> type.
     * @return The function.
     */
    public static <T extends Enum<T>> Function1<CharSimpleContext, CharData<T>> dataBuilder(T tokenType) {
        return context -> new CharData<>(tokenType, context);
    }

    /**
     * The <b>Enum</b> category for the lexeme associated with this <b>CharData</b>.
     */
    private final T type;

    /**
     * The associated lexeme's line number.
     */
    private final int line;

    /**
     * The associated lexeme's line position.
     */
    private final int linePosition;

    /**
     * Build a <b>CharData</b> instance given the successful lexeme's category and the lexer's current
     * context.
     *
     * @param t The category <b>Enum</b>.
     * @param context The current context.
     */
    public CharData(T t, CharSimpleContext context) {
        this(t, context, true);
    }

    protected CharData(T t, CharSimpleContext context, boolean withCheck) {
        if (withCheck) {
            Objects.requireNonNull(t);
            Objects.requireNonNull(context);
        }

        type = t;
        line = context.getLine().getStarting();
        linePosition = context.getLinePosition().getStarting();
    }

    public CharData(T t, Positioned d) {
        this(t, d, true);
    }

    protected CharData(T t, Positioned d, boolean withCheck) {
        if (withCheck) {
            Objects.requireNonNull(t);
            Objects.requireNonNull(d);
        }

        type = t;
        line = d.getLine();
        linePosition = d.getLinePosition();
    }

    /**
     * Build a <b>CharData</b> instance given the successful lexeme's category and position data.
     *
     * @param t The category <b>Enum</b>.
     * @param l The lexeme's line number.
     * @param lp The lexeme's line position.
     */
    public CharData(T t, int l, int lp) {
        this(t, l, lp, true);
    }

    protected CharData(T t, int l, int lp, boolean withCheck) {
        if (withCheck) {
            Objects.requireNonNull(t);
        }

        type = t;
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
    public CharData<T> withPosition(Positioned d) {
        Objects.requireNonNull(d);
        return new CharData<>(type, d, false);
    }

    @Override
    public CharData<T> mapLine(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return new CharData<>(type, f.apply(line), linePosition, false);
    }

    @Override
    public CharData<T> mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        Objects.requireNonNull(f);
        return new CharData<>(type, line, f.apply(linePosition), false);
    }

    @Override
    public CharData<T> mapPosition(Function1<? super Positioned, ? extends Positioned> f) {
        Objects.requireNonNull(f);
        return withPosition(f.apply(this));
    }

    @Override
    public CharData<T> withLine(int l) {
        return new CharData<>(type, l, linePosition);
    }

    @Override
    public CharData<T> withLinePosition(int lp) {
        return new CharData<>(type, line, linePosition);
    }

    /**
     * Set the <b>Enum</b> category associated with this <b>CharData</b>.
     *
     * @param t The <b>Enum</b> category.
     * @param <OT> The potentially different category type.
     * @return The new <b>CharData</b>.
     */
    public <OT extends Enum<OT>> CharData<OT> withType(OT t) {
        return new CharData<>(t, line, linePosition);
    }

    @Override
    public T getTokenType() {
        return type;
    }

    @Override
    public String toString() {
        return "[" + line + " : " + linePosition + " : " + type.name() + "]";
    }

    /*
     * Generated hashCode and equals.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharData<?> charData = (CharData<?>) o;
        return line == charData.line &&
                linePosition == charData.linePosition &&
                type.equals(charData.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, line, linePosition);
    }
}
