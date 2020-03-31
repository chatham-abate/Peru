package org.perudevteam.lexeralpha;

import io.vavr.Function1;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestTableDFA {
    @Test
    public void testBasics() {
        Function1<Integer, Option<Integer>> cl = i -> Option.of(i % 2);
        Array<Option<String>> as = Array.of(Option.of("Good!"), Option.none());

        Array<Option<Integer>> r1 = Array.of(Option.of(1), Option.none());
        Array<Option<Integer>> r2 = Array.of(Option.none(), Option.of(0));

        TableDFA<Integer, String> dfa = TableDFA.tableDFA(cl, as, Array.of(r1, r2));

        assertFalse(dfa.getOutcome(0).isEmpty());
        assertTrue(dfa.getOutcome(1).isEmpty());

        assertEquals(1, dfa.nextState(0, 0).get());
        assertTrue(dfa.nextState(1, 0).isEmpty());

    }
}
