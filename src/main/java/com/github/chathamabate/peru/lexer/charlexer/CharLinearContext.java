package com.github.chathamabate.peru.lexer.charlexer;

import com.github.chathamabate.peru.lexer.LinearDLexer;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.collection.*;
import com.github.chathamabate.peru.lexer.LinearContext;

/**
 * The context used for a {@link CharLinearDLexer}.
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
     * @see LinearDLexer
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
                getLineData(), getLinePositionData());
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
                getLineData(), getLinePositionData());
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
                getLineData(), getLinePositionData());
    }

    @Override
    public CharLinearContext withLineData(PositionData l) {
        return new CharLinearContext(failMap, absolutePosition, l, getLinePositionData());
    }

    @Override
    public CharLinearContext mapLineData(Function1<? super PositionData, ? extends  PositionData> m) {
        return new CharLinearContext(failMap, absolutePosition, m.apply(getLineData()), getLinePositionData());
    }

    @Override
    public CharLinearContext withLinePositionData(PositionData lp) {
        return new CharLinearContext(failMap, absolutePosition, getLineData(), lp);
    }

    @Override
    public CharLinearContext mapLinePositionData(Function1<? super PositionData, ? extends PositionData> m) {
        return new CharLinearContext(failMap, absolutePosition, getLineData(), m.apply(getLinePositionData()));
    }

    @Override
    public CharLinearContext map(Function1<? super PositionData, ? extends  PositionData> lm,
                                 Function1<? super PositionData, ? extends  PositionData> lpm) {
        return new CharLinearContext(failMap, absolutePosition, lm.apply(getLineData()), lpm.apply(getLinePositionData()));
    }
}
