package org.perudevteam.parser;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.control.Either;
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

    @Test
    void testProductionErrors() {
        assertThrows(NullPointerException.class, () -> {
            new Production<>(NT.A, null);
        });

        assertThrows(NullPointerException.class, () -> {
            new Production<>(NT.A, List.of(null));
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
}
