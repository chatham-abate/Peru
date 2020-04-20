package org.perudevteam.fa;

import io.vavr.collection.*;

public final class FAutomatonUtil {
    private FAutomatonUtil() {
        // Should never be initialized.
    }

    public static <I, IC, O> Array<Set<Integer>> buildEpsilonStar(NFAutomaton<I, IC, O> nfa) {
        return reachableSets(nfa.getEpsilonTransitions());
    }

    // No Checks... hence why this is package private.
    static Array<Set<Integer>> reachableSets(Array<? extends Set<? extends Integer>> graph) {
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


    // SAME here, no null checks.
    // We want some NFA to DFA Code here...
    // what we need is the e* sets... and the normal map...
    // The normal will be an array of maps which nmap some input class to some set of integers...
    // Don't care about normal e transitions here, just e* transitions
    static <IC> Array<Map<IC, Integer>> buildDFATransTable(
            Array<? extends Map<? extends IC, ? extends Set<? extends Integer>>> nfaTransTable,
            Array<? extends Set<? extends Integer>> eStar) {
        // FINISH LATER.
        return null;
    }
}
