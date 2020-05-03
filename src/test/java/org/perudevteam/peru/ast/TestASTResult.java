package org.perudevteam.peru.ast;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.TestFactory;
import org.perudevteam.TestingUtil;
import org.perudevteam.charpos.SimpleCharPos;

import static org.perudevteam.peru.ast.ASTResult.*;

import static org.perudevteam.peru.base.BaseValue.*;

public class TestASTResult {

    @Test
    void testBasics() {
        assertTrue(empty().isEmpty() && !empty().isPositioned());
        assertEquals(1, position(1, 2).getLine());
        assertNotEquals(position(1, 2), fullResult(1, 2, ofInt(10)));
    }

    private static final Seq<Tuple2<ASTResult, ASTResult>> EQUALITIES = Array.of(
            Tuple.of(empty(), empty()),
            Tuple.of(fullResult(1,2, ofInt(10)), fullResult(1,2 , ofInt(10))),
            Tuple.of(position(1, 3), position(0,0).withLinePosition(3).withLine(1)),
            Tuple.of(position(4, 3), position(2,0).mapLine(l -> l * 2).mapLinePosition(lp -> lp + 3)),
            Tuple.of(position(0, 0), position(1, 2).mapPosition(
                    d -> SimpleCharPos.simpleCharPos(d.getLine() - 1, d.getLinePosition() - 2))),
            Tuple.of(value(ofInt(1)).withLine(1), fullResult(1, 0, ofInt(1))),
            Tuple.of(value(ofInt(10)).withLinePosition(10), position(0, 10).withValue(ofInt(10)))
    );

    @TestFactory
    Seq<DynamicTest> testEqualASTs() {
        return TestingUtil.testTuples(EQUALITIES, Assertions::assertEquals);
    }
}
