package org.perudevteam.fa;

import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;

public final class FAutomatonUtil {
    private FAutomatonUtil() {
        // Should never be initialized.
    }

    public static <I, IC, O> Array<Set<Integer>> buildEpsilonStar(NFAutomaton<I, IC, O> nfa) {
        return reachableSets(nfa.getEpsilonTransitions());
    }

    public static Array<Set<Integer>> reachableSets(Array<? extends Set<? extends Integer>> graph) {
        Array<Set<Integer>> reachableSets = Array.rangeBy(0, graph.length(), 1).map(HashSet::of);

        int oldSize = 0;
        int newSize = graph.length();

        while (newSize - oldSize > 0) {
            oldSize = newSize;
            newSize = 0;

            Array<Set<Integer>> tempReachableSets = Array.fill(graph.length(), HashSet.empty());

            for (int node = 0; node < graph.length(); node++) {
                Set<Integer> nodeTempReachableSet = reachableSets.get(node);
                for (int neighbor: graph.get(node)) {
                    nodeTempReachableSet = nodeTempReachableSet.addAll(reachableSets.get(neighbor));
                }

                newSize += nodeTempReachableSet.length();
                tempReachableSets = tempReachableSets.update(node, nodeTempReachableSet);
            }

            reachableSets = tempReachableSets;
        }

        return reachableSets;
    }
}
