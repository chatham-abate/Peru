package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.perudevteam.lexer.LinearContext;

import java.util.Objects;

public class CharLinearContext extends CharSimpleContext implements LinearContext<CharLinearContext> {

    public static final CharLinearContext INIT_LINEAR_CONTEXT =
            new CharLinearContext(HashMap.empty(), 0, PositionData.INIT_POSITION, PositionData.INIT_POSITION);

    private final int absolutePosition;
    private final Map<Integer, Set<Integer>> failMap;

    public CharLinearContext(Map<Integer, ? extends Set<? extends Integer>> fm,
                             int ap, PositionData l, PositionData lp) {
        super(l, lp);

        absolutePosition = ap;
        failMap = fm.map((p, st) -> Tuple.of(p, Set.narrow(st)));
    }

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
    public CharLinearContext withPreErrors(Map<Integer, ? extends Integer> preErrors) {
        Map<Integer, Set<Integer>> preErrorSets = preErrors.mapValues(HashSet::of);
        Map<Integer, Set<Integer>> newFailMap = failMap.merge(preErrorSets,
                Set::addAll);

        return new CharLinearContext(newFailMap, absolutePosition,
                getLine(), getLinePosition());
    }

    /*
     * Updated Overridden methods...
     */

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
