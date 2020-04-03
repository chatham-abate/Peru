package org.perudevteam.parser;

import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.junit.jupiter.api.*;
import org.perudevteam.parser.production.Production;

import static org.junit.jupiter.api.Assertions.*;
import static io.vavr.control.Either.*;

public class TestCFGrammar {

    enum NT {
        A, B, C, D
    }

    enum T {
        E, F, G, H
    }

    /*
     * Production.
     */

    @Test
    void testProductionErrors() {
        assertThrows(NullPointerException.class, () -> {
            new Production<>(NT.A, null);
        });

        assertThrows(NullPointerException.class, () -> {
            new Production<>(NT.A, List.of(null, right(T.F)));
        });

        assertThrows(NullPointerException.class, () -> {
            new Production<NT, T>(NT.A, List.of(left(null)));
        });

        assertThrows(NullPointerException.class, () -> {
            new Production<NT, T>(NT.A, List.of(right(null)));
        });
    }

    @Test
    void testWithRule() {
        Production<NT, T> prod = new Production<>(NT.A, List.of(
           right(T.E), left(NT.A), left(NT.B)
        ));

        // Terminal Symbol switch.
        Production<NT, NT> prod2 = prod.withRule(List.of(
           right(NT.A)
        ));

        Production<NT, T> prod3 = prod.withSource(NT.B);

        assertNotEquals(prod, prod2);
        assertNotEquals(prod, prod3);
        assertNotEquals(prod2, prod3);

        assertThrows(NullPointerException.class, () -> {
            prod.withRule(null);
        });

        assertThrows(NullPointerException.class, () -> {
           prod.withSource(null);
        });
    }

    /*
     * LR(1) Item Tests.
     */

    @Test
    void testLROneItemErrors() {
        Production<NT, T> prod = new Production<>(NT.A, List.of(right(T.E), right(T.F)));
        LROneItem<NT, T, Production<NT, T>> lrOne = new LROneItem<>(1, prod);

        assertThrows(IllegalArgumentException.class, () -> {
            lrOne.withProduction(prod.withRule(List.empty()));
        });

        lrOne.shiftCursor();

        assertThrows(IllegalArgumentException.class, () -> {
            lrOne.shiftCursor().shiftCursor();
        });
    }

    /*
     * Simple Grammar Tests.
     */

    @Test
    void testGrammarBasics() {
        Production<NT, T> prod1 = new Production<>(NT.A, List.of(
                left(NT.B), right(T.E)
        ));

        Production<NT, T> prod2 = new Production<>(NT.B, List.of(
                right(T.F)
        ));

        Production<NT, T> prod3 = prod1.withSource(NT.C).withRule(List.of(
                left(NT.B), right(T.F), left(NT.C), left(NT.A)
        ));

        assertThrows(IllegalArgumentException.class, () -> {
           new CFGrammar<>(NT.C, List.of(prod1, prod2));
        });

        assertThrows(IllegalArgumentException.class, () -> {
           new CFGrammar<>(NT.C, List.of(prod3, prod2));
        });

        Production<NT, T> prod4 = new Production<>(NT.A, List.of(
                left(NT.B), right(T.E)
        ));

        Production<NT, T> prod5 = new Production<>(NT.B, List.of(
                left(NT.A), right(T.E)
        ));

        assertThrows(IllegalArgumentException.class, () -> {
            new CFGrammar<>(NT.A, List.of(prod4));
        });

        CFGrammar g = new CFGrammar<>(NT.A, List.of(prod4, prod5));
        assertEquals(HashSet.of(NT.B, NT.A), g.getNonTerminalsUsed());
        assertEquals(HashSet.of(T.E), g.getTerminalsUsed());
    }

    /*
     * Firsts Sets Tests.
     */

    private static final CFGrammar<NT, T, Production<NT, T>> G1 = new CFGrammar<>(NT.A, List.of(
            new Production<>(NT.A, List.of(right(T.E)))
    ));

    private static final FirstSets<NT, T> F1 = new FirstSets<>(G1);

    private static final CFGrammar<NT, T, Production<NT, T>> G2 = new CFGrammar<>(NT.A, List.of(
            new Production<NT, T>(NT.A, List.of(left(NT.B), left(NT.C))),
            new Production<>(NT.B, List.of(right(T.E))),
            new Production<>(NT.C, List.of(right(T.F)))
    ));

    private static final FirstSets<NT, T> F2 = new FirstSets<>(G2);

    private static final CFGrammar<NT, T, Production<NT, T>> G3 = G2.withProduction(new Production<>(
            NT.B, List.empty()
    ));

    private static final FirstSets<NT, T> F3 = new FirstSets<>(G3);

    private static final CFGrammar<NT, T, Production<NT, T>> G4 = G3.withProduction(new Production<>(
            NT.C, List.empty()
    ));

    private static final FirstSets<NT, T> F4 = new FirstSets<>(G4);

    @Test
    void testFirstsSets() {
        assertEquals(HashSet.of(T.E), F1.getFirstSet(NT.A));

        assertEquals(HashSet.of(T.E), F2.getFirstSet(NT.A));
        assertEquals(HashSet.of(T.F), F2.getFirstSet(NT.C));
        assertFalse(F2.mayBeEmpty(NT.A));

        assertEquals(HashSet.of(T.E, T.F), F3.getFirstSet(NT.A));
        assertTrue(F3.mayBeEmpty(NT.B));

        assertTrue(F4.mayBeEmpty(NT.A) && F4.mayBeEmpty(NT.B) && F4.mayBeEmpty(NT.C));

        Tuple2<Boolean, Set<T>> ruleTuple = F4.getFirstSet(List.of(left(NT.A), right(T.G), right(T.H)));
        assertFalse(ruleTuple._1);
        assertEquals(HashSet.of(T.E, T.F, T.G), ruleTuple._2);
    }
}
