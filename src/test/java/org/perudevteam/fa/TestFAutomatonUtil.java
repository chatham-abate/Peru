package org.perudevteam.fa;

import io.vavr.collection.Array;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.junit.jupiter.api.Test;

import static org.perudevteam.fa.FAutomatonUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestFAutomatonUtil {

    private static final Array<Set<Integer>> TEST_GRAPH = Array.of(
            HashSet.of(1),
            HashSet.of(2),
            HashSet.of(1),
            HashSet.empty(),
            HashSet.of(2, 3)
    );

    private static final Array<Set<Integer>> EXPECTED_REACHABLES = Array.of(
            HashSet.of(0, 1, 2),
            HashSet.of(1, 2),
            HashSet.of(1, 2),
            HashSet.of(3),
            HashSet.of(1, 2, 3, 4)
    );

    @Test
    void testReachableSets() {
        assertEquals(EXPECTED_REACHABLES, reachableSets(TEST_GRAPH));
    }
}
