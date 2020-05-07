package com.github.chathamabate.peru.preset.base;


import static org.junit.jupiter.api.Assertions.*;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.Tuple4;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class TestBaseOperatorSet {

    private static final Seq<Tuple4<BaseOperator, BaseValue, BaseValue, BaseValue>> BINOP_TESTS =
    List.of(
            Tuple.of(BaseOperator.PLUS, BaseValue.ofInt(10), BaseValue.ofFloat(10.0f), BaseValue.ofFloat(20.0f)),
            Tuple.of(BaseOperator.MINUS, BaseValue.ofByte((byte)5), BaseValue.ofByte((byte)2), BaseValue.ofByte((byte)3)),
            Tuple.of(BaseOperator.PLUS, BaseValue.ofString("A"), BaseValue.ofString("B"), BaseValue.ofString("AB")),
            Tuple.of(BaseOperator.AND, BaseValue.ofBoolean(true), BaseValue.ofBoolean(false), BaseValue.ofBoolean(false)),
            Tuple.of(BaseOperator.POWER, BaseValue.ofShort((short)2), BaseValue.ofLong(3), BaseValue.ofLong(8)),
            Tuple.of(BaseOperator.LT, BaseValue.ofInt(10), BaseValue.ofLong(100), BaseValue.ofBoolean(true)),
            Tuple.of(BaseOperator.GT_EQ, BaseValue.ofCharacter('a'), BaseValue.ofCharacter('a'), BaseValue.ofBoolean(true)),
            Tuple.of(BaseOperator.GT, BaseValue.ofInt(90), BaseValue.ofInt(100), BaseValue.ofBoolean(false)),
            Tuple.of(BaseOperator.EQ, BaseValue.ofCharacter('a'), BaseValue.ofCharacter('a'), BaseValue.ofBoolean(true)),
            Tuple.of(BaseOperator.LT, BaseValue.ofShort((short)100), BaseValue.ofCharacter('a'), BaseValue.ofBoolean(false)),
            Tuple.of(BaseOperator.PLUS, BaseValue.ofCharacter('a'), BaseValue.ofByte((byte)1), BaseValue.ofCharacter('b')),
            Tuple.of(BaseOperator.EQ, BaseValue.ofSequence(List.empty()), BaseValue.ofSequence(List.of(BaseValue.ofInt(1))), BaseValue.ofBoolean(false)),
            Tuple.of(BaseOperator.PLUS, BaseValue.ofInt(1), BaseValue.ofString("a"), BaseValue.ofString("1a")),
            Tuple.of(BaseOperator.MINUS, BaseValue.ofCharacter('B'), BaseValue.ofInt(1), BaseValue.ofCharacter('A')),
            Tuple.of(BaseOperator.MODULO, BaseValue.ofInt(10), BaseValue.ofInt(3), BaseValue.ofInt(1)),
            Tuple.of(BaseOperator.MODULO, BaseValue.ofDouble(10), BaseValue.ofInt(3), BaseValue.ofDouble(1))
    );

    @TestFactory
    Seq<DynamicTest> testBinaryOperatorBasics() {
        return BINOP_TESTS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(),
                () -> {
                    Try<BaseValue> result = BaseOperatorSetUtil.BASE_OPERATOR_SET.tryApplyBinary(tuple._1, tuple._2, tuple._3);
                    assertTrue(result.isSuccess());
                    assertEquals(tuple._4, result.get());
                }
        ));
    }

    private static final Seq<Tuple3<BaseOperator, BaseValue, BaseValue>> UNOP_TESTS = List.of(
            Tuple.of(BaseOperator.NOT, BaseValue.ofBoolean(true), BaseValue.ofBoolean(false)),
            Tuple.of(BaseOperator.MINUS, BaseValue.ofInt(1), BaseValue.ofInt(-1)),
            Tuple.of(BaseOperator.PLUS, BaseValue.ofDouble(1.0), BaseValue.ofDouble(1.0))
    );

    @TestFactory
    Seq<DynamicTest> testUnaryOperatorBasics() {
        return UNOP_TESTS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(), () -> {
            Try<BaseValue> result = BaseOperatorSetUtil.BASE_OPERATOR_SET.tryApplyUnary(tuple._1, tuple._2);
            assertTrue(result.isSuccess());
            assertEquals(tuple._3, result.get());
        }));
    }

    private static final Seq<Tuple3<BaseOperator, BaseValue, BaseValue>> BINOP_ERRORS = List.of(
            Tuple.of(BaseOperator.PLUS, BaseValue.ofMap(HashMap.empty()), BaseValue.ofMap(HashMap.empty())),
            Tuple.of(BaseOperator.LT, BaseValue.ofInt(1), BaseValue.ofString("D")),
            Tuple.of(BaseOperator.EQ, BaseValue.ofBoolean(true), BaseValue.ofInt(23)),
            Tuple.of(BaseOperator.TIMES, BaseValue.ofString("a"), BaseValue.ofString("b"))
    );

    @TestFactory
    Seq<DynamicTest> testBinaryOperatorErrors() {
        return BINOP_ERRORS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(), () -> {
            Try<BaseValue> errorResult = BaseOperatorSetUtil.BASE_OPERATOR_SET.tryApplyBinary(tuple._1, tuple._2, tuple._3);
            assertTrue(errorResult.isFailure());
        }));
    }

    private static final Seq<Tuple2<BaseOperator, BaseValue>> UNOP_ERRORS = List.of(
            Tuple.of(BaseOperator.MINUS, BaseValue.ofString("1")),
            Tuple.of(BaseOperator.NOT, BaseValue.ofInt(1))
    );

    @TestFactory
    Seq<DynamicTest> testUnaryOperatorErrors() {
        return UNOP_ERRORS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(), () -> {
            Try<BaseValue> errorResult = BaseOperatorSetUtil.BASE_OPERATOR_SET.tryApplyUnary(tuple._1, tuple._2);
            assertTrue(errorResult.isFailure());
        }));
    }
}
