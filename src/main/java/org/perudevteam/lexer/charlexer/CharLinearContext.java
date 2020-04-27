package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.collection.*;
import org.perudevteam.lexer.LinearContext;

import java.util.Objects;

/**
 * The context used for a {@link org.perudevteam.lexer.charlexer.CharLinearDLexer}.
 */
public class CharLinearContext extends CharSimpleContext implements LinearContext<CharLinearContext> {

    /**
     * An initial linear context to used by any linear character lexer.
     */
    public static final CharLinearContext INIT_LINEAR_CONTEXT =
            new CharLinearContext(HashMap.empty(), 0, PositionData.INIT_POSITION, PositionData.INIT_POSITION);

    /**
     * The absolute position of this context's lexer.
     * @see LinearContext#getAbsolutePosition()
     */
    private final int absolutePosition;

    /**
     * This map holds all previously seen errors states of the lexer after this context's absolute position.
     * It maps absolute positions the set of states which inevitably result in error while lexing.
     * @see org.perudevteam.lexer.LinearDLexer
     */
    private final Map<Integer, Set<Integer>> failMap;

    /**
     * Construct a new character linear context.
     *
     * @param fm The error cache.
     * @param ap The absolute position.
     * @param l The line data.
     * @param lp The line position data.
     */
    public CharLinearContext(Map<Integer, ? extends Set<? extends Integer>> fm,
                             int ap, PositionData l, PositionData lp) {
        super(l, lp);

        absolutePosition = ap;
        failMap = fm.map((p, st) -> Tuple.of(p, Set.narrow(st)));
    }

    /**
     * Get the size of the error cache of this context.
     *
     * @return The integer size of the error cache.
     */
    int getMapSize() { return failMap.size(); }

    @Override
    public int getAbsolutePosition() {
        return absolutePosition;
    }

    @Override
    public CharLinearContext withAbsolutePosition(int absPosition) {
        return new CharLinearContext(failMap, absPosition,
                getLine(), getLinePosition());
    }

    @Override
    public CharLinearContext dropPreErrorsBefore(int absPosition) {
        Map<Integer, Set<Integer>> cleanMap = failMap;

        for (Integer key: failMap.keySet()) {
            if (key < absPosition) {
                cleanMap = cleanMap.remove(key);
            }
        }

        return new CharLinearContext(cleanMap, absolutePosition,
                getLine(), getLinePosition());
    }

    @Override
    public boolean isPreError(int absPosition, int state) {
        return failMap.containsKey(absPosition) &&
                failMap.get(absPosition).get().contains(state);
    }

    @Override
    public CharLinearContext withPreErrors(Map<? extends Integer, ? extends Integer> preErrors) {
        Map<Integer, Set<Integer>> preErrorSets = Map.<Integer, Integer>narrow(preErrors).mapValues(HashSet::of);
        Map<Integer, Set<Integer>> newFailMap = failMap.merge(preErrorSets,
                Set::addAll);

        return new CharLinearContext(newFailMap, absolutePosition,
                getLine(), getLinePosition());
    }

    @Override
    public CharLinearContext withLine(PositionData l) {
        return new CharLinearContext(failMap, absolutePosition, l, getLinePosition());
    }

    @Override
    public CharLinearContext mapLine(Function1<? super PositionData, ? extends  PositionData> m) {
        return new CharLinearContext(failMap, absolutePosition, m.apply(getLine()), getLinePosition());
    }

    @Override
    public CharLinearContext withLinePosition(PositionData lp) {
        return new CharLinearContext(failMap, absolutePosition, getLine(), lp);
    }

    @Override
    public CharLinearContext mapLinePosition(Function1<? super PositionData, ? extends PositionData> m) {
        return new CharLinearContext(failMap, absolutePosition, getLine(), m.apply(getLinePosition()));
    }

    @Override
    public CharLinearContext map(Function1<? super PositionData, ? extends  PositionData> lm,
                                 Function1<? super PositionData, ? extends  PositionData> lpm) {
        return new CharLinearContext(failMap, absolutePosition, lm.apply(getLine()), lpm.apply(getLinePosition()));
    }
}
