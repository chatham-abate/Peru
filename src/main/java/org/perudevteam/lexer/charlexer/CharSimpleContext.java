package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;

/**
 * The context of a simple character lexer.
 * @see org.perudevteam.lexer.charlexer.CharSimpleDLexer
 */
public class CharSimpleContext {

    /**
     * Preset initial context for any simple character lexer.
     */
    public static final CharSimpleContext INIT_SIMPLE_CONTEXT = new CharSimpleContext(
            PositionData.INIT_POSITION,
            PositionData.INIT_POSITION
    );

    /**
     * Data on the lexer's line number.
     */
    private final PositionData line;

    /**
     * Data on the lexer's position within its current line.
     */
    private final PositionData linePosition;

    /**
     * Constructor.
     *
     * @param l The Context's line data.
     * @param lp The Context's line position data.
     */
    public CharSimpleContext(PositionData l, PositionData lp) {
       line = l;
       linePosition = lp;
    }

    /**
     * Get the context's line data.
     *
     * @return The line data.
     */
    public PositionData getLine() {
        return line;
    }

    /**
     * Get the context's line position data.
     *
     * @return The line position data.
     */
    public PositionData getLinePosition() {
        return linePosition;
    }

    /**
     * Set the context's line data.
     *
     * @param l The new line data.
     * @return The new context.
     */
    public CharSimpleContext withLine(PositionData l) {
       return new CharSimpleContext(l, linePosition);
    }

    /**
     * Map the line data of this context.
     *
     * @param m The mapping function.
     * @return The new context.
     */
    public CharSimpleContext mapLine(Function1<? super PositionData, ? extends  PositionData> m) {
       return new CharSimpleContext(m.apply(line), linePosition);
    }

    /**
     * Set the line position data of the context.
     *
     * @param lp The line position data.
     * @return The new context.
     */
    public CharSimpleContext withLinePosition(PositionData lp) {
       return new CharSimpleContext(line, lp);
    }

    /**
     * Map the line position data of the context.
     *
     * @param m The mapping function.
     * @return The new context.
     */
    public CharSimpleContext mapLinePosition(Function1<? super PositionData, ? extends PositionData> m) {
       return new CharSimpleContext(line, m.apply(linePosition));
    }

    /**
     * Map the line data and the line position data of this context.
     *
     * @param lm The line data mapper.
     * @param lpm The line position data mapper.
     * @return The new context.
     */
    public CharSimpleContext map(Function1<? super PositionData, ? extends  PositionData> lm,
                                 Function1<? super PositionData, ? extends  PositionData> lpm) {
       return new CharSimpleContext(lm.apply(line), lpm.apply(linePosition));
    }
}
