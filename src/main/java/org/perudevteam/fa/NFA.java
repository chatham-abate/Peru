package org.perudevteam.fa;

import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

public abstract class NFA<I, IC, O> extends FA<I, IC, O>  {
    // NFA has empty transitions...
    // And multi Transitions...
    // How can this be dealt with...
    // We need an empty transitions data structure and a normal transiitons data struture.
    // Then a to DFA algorithm....

//    private final Array<Map<IC, Set<Integer>>> transitionTable;
//    private final Array<Set<Integer>> epsilonTransitions;

    public NFA(Map<? extends Integer, O> as, Set<IC> ia,
               Array<? extends Map<IC, ? extends Integer>> tt,
               Array<? extends Set<? extends Integer>> ets, boolean withCheck) {
        super(as, ia, withCheck);


    }

    @Override
    protected int getNumberOfStates() {
        return transitionTable.length();
    }
}
