package org.perudevteam.parser;

import io.vavr.collection.*;
import org.junit.jupiter.api.Test;
import org.perudevteam.misc.SeqHelpers;
import org.perudevteam.parser.grammar.CFGrammar;
import org.perudevteam.parser.grammar.Production;
import org.perudevteam.parser.lrone.FirstSets;
import org.perudevteam.parser.lrone.LROneItem;
import org.perudevteam.parser.lrone.LROneTable;

import static io.vavr.control.Either.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Manual Tests on Parentheses grammar.
 */
public class TestParenGrammar {

    enum NT {
        GOAL, LIST, PAIR
    }

    enum T {
        LEFT, RIGHT
    }

    private static final Production<NT, T>
            PROD1 = new Production<NT, T>(NT.GOAL, List.of(left(NT.LIST))),
            PROD2 = new Production<NT, T>(NT.LIST, List.of(left(NT.LIST), left(NT.PAIR))),
            PROD3 = new Production<NT, T>(NT.LIST, List.of(left(NT.PAIR))),
            PROD4 = new Production<>(NT.PAIR, List.of(right(T.LEFT), left(NT.PAIR), right(T.RIGHT))),
            PROD5 = new Production<>(NT.PAIR, List.of(right(T.LEFT), right(T.RIGHT)));

    private static final CFGrammar<NT, T, Production<NT, T>> G = new CFGrammar<>(NT.GOAL, List.of(
            PROD1, PROD2, PROD3, PROD4, PROD5
    ));

    private static final FirstSets<NT, T> F = new FirstSets<>(G);

    private static final Set<LROneItem<NT, T, Production<NT, T>>> CC_0 = LROneItem.closureSet(G, F, HashSet.of(
            new LROneItem<>(0, PROD1)
    ));

    private static final LROneTable<NT, T, Production<NT, T>> LRONE_TABLE = new LROneTable<>(G);

    @Test
    void testCCSize() {
        assertEquals(12, LRONE_TABLE.getCC().length());
    }

    // @Test
    void testParenGrammar() {
        System.out.println("G : ");
        System.out.println(G);
        System.out.println();
        System.out.println("CC_0 : ");
        LROneItem.closureSet(G, F, CC_0).forEach(System.out::println);
    }

    // @Test
    void testParenLROneTableCC() {
        LROneTable<NT, T, Production<NT, T>> lroneTable = new LROneTable<>(G);
        Array<Set<LROneItem<NT, T, Production<NT, T>>>> cc = lroneTable.getCC();

        for (int i = 0; i < cc.length(); i++) {
            System.out.println("CC " + i + " : ");
            cc.get(i).forEach(System.out::println);
            System.out.println();
        }
    }

    // @Test
    void testLROneTableAction() {
        System.out.println(LRONE_TABLE.actionTableString());
        System.out.println(LRONE_TABLE.gotoTableString());
    }
}
