package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import org.perudevteam.charpos.CharPos;

import static org.perudevteam.charpos.CharPos.*;

/**
 * The context of a simple character lexer.
 * @see org.perudevteam.lexer.charlexer.CharSimpleDLexer
 */
public class CharSimpleContext implements CharPos {

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
    private final PositionData lineData;

    /**
     * Data on the lexer's position within its current line.
     */
    private final PositionData linePositionData;

    /**
     * Constructor.
     *
     * @param l The Context's line data.
     * @param lp The Context's line position data.
     */
    public CharSimpleContext(PositionData l, PositionData lp) {
       lineData = l;
       linePositionData = lp;
    }

    /**
     * Get the context's line data.
     *
     * @return The line data.
     */
    public PositionData getLineData() {
        return lineData;
    }

    /**
     * Get the context's line position data.
     *
     * @return The line position data.
     */
    public PositionData getLinePositionData() {
        return linePositionData;
    }

    /**
     * Set the context's line data.
     *
     * @param l The new line data.
     * @return The new context.
     */
    public CharSimpleContext withLineData(PositionData l) {
       return new CharSimpleContext(l, linePositionData);
    }

    /**
     * Map the line data of this context.
     *
     * @param m The mapping function.
     * @return The new context.
     */
    public CharSimpleContext mapLineData(Function1<? super PositionData, ? extends  PositionData> m) {
       return new CharSimpleContext(m.apply(lineData), linePositionData);
    }

    /**
     * Set the line position data of the context.
     *
     * @param lp The line position data.
     * @return The new context.
     */
    public CharSimpleContext withLinePositionData(PositionData lp) {
       return new CharSimpleContext(lineData, lp);
    }

    /**
     * Map the line position data of the context.
     *
     * @param m The mapping function.
     * @return The new context.
     */
    public CharSimpleContext mapLinePositionData(Function1<? super PositionData, ? extends PositionData> m) {
       return new CharSimpleContext(lineData, m.apply(linePositionData));
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
       return new CharSimpleContext(lm.apply(lineData), lpm.apply(linePositionData));
    }

    @Override
    public int getLine() {
        return lineData.getStarting();
    }

    @Override
    public int getLinePosition() {
        return linePositionData.getStarting();
    }

    @Override
    public CharPos withLine(int l) {
        return charPos(l, linePositionData.getStarting());
    }

    @Override
    public CharPos withLinePosition(int lp) {
        return charPos(lineData.getStarting(), lp);
    }
}
