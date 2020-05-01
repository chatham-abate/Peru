package org.perudevteam.peru.ast;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.TestFactory;
import org.perudevteam.misc.Positioned;
import org.perudevteam.peru.ast.ASTResult;
import static org.perudevteam.peru.ast.ASTResult.*;

import static org.perudevteam.peru.base.BaseValue.*;

public class TestASTResult {

    @Test
    void testBasics() {
        assertTrue(empty().isEmpty() && !empty().isPositioned());
        assertEquals(1, positioned(1, 2).getLine());
        assertNotEquals(positioned(1, 2), fullResult(1, 2, ofInt(10)));
    }

    private static final Seq<Tuple2<ASTResult, ASTResult>> EQUALITIES = Array.of(
            Tuple.of(empty(), empty()),
            Tuple.of(fullResult(1,2, ofInt(10)), fullResult(1,2 , ofInt(10))),
            Tuple.of(positioned(1, 3), positioned(0,0).withLinePosition(3).withLine(1)),
            Tuple.of(positioned(4, 3), positioned(2,0).mapLine(l -> l * 2).mapLinePosition(lp -> lp + 3)),
            Tuple.of(positioned(0, 0), positioned(1, 2).mapPosition(p -> p.withLine(0).withLinePosition(0))),
            Tuple.of(valued(ofInt(1)).withLine(1), fullResult(1, 0, ofInt(1)))
    );

    @TestFactory
    Seq<DynamicTest> testEqualASTs() {
        return EQUALITIES.map(tuple -> DynamicTest.dynamicTest(tuple._1 + " = " + tuple._2,
                () -> assertEquals(tuple._1, tuple._2)
        ));
    }
}
