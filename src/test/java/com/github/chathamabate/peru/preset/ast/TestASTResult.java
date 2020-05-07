package com.github.chathamabate.peru.preset.ast;

import com.github.chathamabate.peru.TestingUtil;
import com.github.chathamabate.peru.preset.base.BaseValue;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.TestFactory;
import com.github.chathamabate.peru.charpos.SimpleCharPos;

public class TestASTResult {

    @Test
    void testBasics() {
        assertTrue(ASTResult.empty().isEmpty() && !ASTResult.empty().isPositioned());
        Assertions.assertEquals(1, ASTResult.position(1, 2).getLine());
        Assertions.assertNotEquals(ASTResult.position(1, 2), ASTResult.fullResult(1, 2, BaseValue.ofInt(10)));
    }

    private static final Seq<Tuple2<ASTResult, ASTResult>> EQUALITIES = Array.of(
            Tuple.of(ASTResult.empty(), ASTResult.empty()),
            Tuple.of(ASTResult.fullResult(1,2, BaseValue.ofInt(10)), ASTResult.fullResult(1,2 , BaseValue.ofInt(10))),
            Tuple.of(ASTResult.position(1, 3), ASTResult.position(0,0).withLinePosition(3).withLine(1)),
            Tuple.of(ASTResult.position(4, 3), ASTResult.position(2,0).mapLine(l -> l * 2).mapLinePosition(lp -> lp + 3)),
            Tuple.of(ASTResult.position(0, 0), ASTResult.position(1, 2).mapPosition(
                    d -> SimpleCharPos.simpleCharPos(d.getLine() - 1, d.getLinePosition() - 2))),
            Tuple.of(ASTResult.value(BaseValue.ofInt(1)).withLine(1), ASTResult.fullResult(1, 0, BaseValue.ofInt(1))),
            Tuple.of(ASTResult.value(BaseValue.ofInt(10)).withLinePosition(10), ASTResult.position(0, 10).withValue(BaseValue.ofInt(10)))
    );

    @TestFactory
    Seq<DynamicTest> testEqualASTs() {
        return TestingUtil.testTuples(EQUALITIES, Assertions::assertEquals);
    }
}
