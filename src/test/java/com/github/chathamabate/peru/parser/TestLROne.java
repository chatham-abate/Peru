package com.github.chathamabate.peru.parser;

import com.github.chathamabate.peru.parser.grammar.CFGrammar;
import com.github.chathamabate.peru.parser.grammar.Production;
import com.github.chathamabate.peru.parser.lrone.LROneItem;
import com.github.chathamabate.peru.parser.lrone.LROneTable;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.*;

public class TestLROne {

    enum NT {
        A, B, C, D
    }

    enum T {
        E, F, G, H
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
     * Test LR(1) table errors.
     */

    // Ambiguous Grammar 1
    private static final CFGrammar<NT, T, Production<NT, T>> A_G1 = new CFGrammar<>(NT.A, List.of(
            new Production<NT, T>(NT.A, List.of(left(NT.B))),
            new Production<NT, T>(NT.A, List.of(left(NT.C))),
            new Production<NT, T>(NT.B, List.of(right(T.E))),
            new Production<NT, T>(NT.C, List.of(right(T.E)))
    ));

    // Ambiguous Grammar 2
    private static final CFGrammar<NT, T, Production<NT, T>> A_G2 = new CFGrammar<>(NT.A, List.of(
            new Production<NT, T>(NT.A, List.of(left(NT.B))),
            new Production<NT, T>(NT.A, List.of(left(NT.B), right(T.E))),
            new Production<NT, T>(NT.B, List.empty()),
            new Production<NT, T>(NT.B, List.of(right(T.E)))
    ));

    @Test
    void testAmbiguousLROneTables() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LROneTable<>(A_G1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new LROneTable<>(A_G2);
        });
    }
}
