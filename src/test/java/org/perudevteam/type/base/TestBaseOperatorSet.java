package org.perudevteam.type.base;


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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import static org.perudevteam.type.base.BaseOperatorSetUtil.*;
import static org.perudevteam.type.base.BaseValue.*;
import static org.perudevteam.type.base.BaseOperator.*;

public class TestBaseOperatorSet {

    private static final Seq<Tuple4<BaseOperator, BaseValue, BaseValue, BaseValue>> BINOP_TESTS =
    List.of(
            Tuple.of(PLUS, ofInt(10), ofFloat(10.0f), ofFloat(20.0f)),
            Tuple.of(MINUS, ofByte((byte)5), ofByte((byte)2), ofByte((byte)3)),
            Tuple.of(PLUS, ofString("A"), ofString("B"), ofString("AB")),
            Tuple.of(AND, ofBoolean(true), ofBoolean(false), ofBoolean(false)),
            Tuple.of(POWER, ofShort((short)2), ofLong(3), ofLong(8)),
            Tuple.of(LT, ofInt(10), ofLong(100), ofBoolean(true)),
            Tuple.of(GT_EQ, ofCharacter('a'), ofCharacter('a'), ofBoolean(true)),
            Tuple.of(GT, ofInt(90), ofInt(100), ofBoolean(false)),
            Tuple.of(EQ, ofCharacter('a'), ofCharacter('a'), ofBoolean(true)),
            Tuple.of(LT, ofShort((short)100), ofCharacter('a'), ofBoolean(false)),
            Tuple.of(PLUS, ofCharacter('a'), ofByte((byte)1), ofCharacter('b')),
            Tuple.of(EQ, ofSequence(List.empty()), ofSequence(List.of(ofInt(1))), ofBoolean(false)),
            Tuple.of(PLUS, ofInt(1), ofString("a"), ofString("1a")),
            Tuple.of(MINUS, ofCharacter('B'), ofInt(1), ofCharacter('A'))
    );

    @TestFactory
    Seq<DynamicTest> testBinaryOperatorBasics() {
        return BINOP_TESTS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(),
                () -> {
                    Try<BaseValue> result = BASE_OPERATOR_SET.tryApplyBinary(tuple._1, tuple._2, tuple._3);
                    assertTrue(result.isSuccess());
                    assertEquals(tuple._4, result.get());
                }
        ));
    }

    private static final Seq<Tuple3<BaseOperator, BaseValue, BaseValue>> UNOP_TESTS = List.of(
            Tuple.of(NOT, ofBoolean(true), ofBoolean(false)),
            Tuple.of(MINUS, ofInt(1), ofInt(-1)),
            Tuple.of(PLUS, ofDouble(1.0), ofDouble(1.0))
    );

    @TestFactory
    Seq<DynamicTest> testUnaryOperatorBasics() {
        return UNOP_TESTS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(), () -> {
            Try<BaseValue> result = BASE_OPERATOR_SET.tryApplyUnary(tuple._1, tuple._2);
            assertTrue(result.isSuccess());
            assertEquals(tuple._3, result.get());
        }));
    }

    private static final Seq<Tuple3<BaseOperator, BaseValue, BaseValue>> BINOP_ERRORS = List.of(
            Tuple.of(PLUS, ofMap(HashMap.empty()), ofMap(HashMap.empty())),
            Tuple.of(LT, ofInt(1), ofString("D")),
            Tuple.of(EQ, ofBoolean(true), ofInt(23)),
            Tuple.of(TIMES, ofString("a"), ofString("b"))
    );

    @TestFactory
    Seq<DynamicTest> testBinaryOperatorErrors() {
        return BINOP_ERRORS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(), () -> {
            Try<BaseValue> errorResult = BASE_OPERATOR_SET.tryApplyBinary(tuple._1, tuple._2, tuple._3);
            assertTrue(errorResult.isFailure());
        }));
    }

    private static final Seq<Tuple2<BaseOperator, BaseValue>> UNOP_ERRORS = List.of(
            Tuple.of(MINUS, ofString("1")),
            Tuple.of(NOT, ofInt(1))
    );

    @TestFactory
    Seq<DynamicTest> testUnaryOperatorErrors() {
        return UNOP_ERRORS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(), () -> {
            Try<BaseValue> errorResult = BASE_OPERATOR_SET.tryApplyUnary(tuple._1, tuple._2);
            assertTrue(errorResult.isFailure());
        }));
    }
}
