package org.perudevteam.lexer.charlexer;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.perudevteam.lexer.LinearContext;

public class CharLinearContext extends CharSimpleContext implements LinearContext<CharLinearContext> {

    public static final CharLinearContext INIT_LINEAR_CONTEXT =
            new CharLinearContext(HashMap.empty(), 0, 1, 1, 1);

    private int absolutePosition;
    private Map<Integer, Set<Integer>> failMap;

    public CharLinearContext(Map<Integer, ? extends Set<? extends Integer>> fm,
                             int ap, int c, int s, int e) {
        super(c, s, e);
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
                getCurrentLine(), getStartingLine(), getEndingLine());
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
                getCurrentLine(), getStartingLine(), getEndingLine());
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
                getCurrentLine(), getStartingLine(), getEndingLine());
    }

    /*
     * Updated Overridden methods...
     */

    public CharLinearContext withCurrentLine(int c) {
        return new CharLinearContext(failMap, absolutePosition,
                c, getStartingLine(), getEndingLine());
    }

    public CharLinearContext withStartingLine(int s) {
        return new CharLinearContext(failMap, absolutePosition,
                getCurrentLine(), s, getEndingLine());
    }

    public CharLinearContext withEndingLine(int e) {
        return new CharLinearContext(failMap, absolutePosition,
                getCurrentLine(), getStartingLine(), e);
    }
}
