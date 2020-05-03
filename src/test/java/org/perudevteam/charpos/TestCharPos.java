package org.perudevteam.charpos;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.perudevteam.TestingUtil;

import static org.perudevteam.charpos.EnumCharPos.*;
import static org.perudevteam.charpos.SimpleCharPos.*;

public class TestCharPos {
    private static final Seq<Tuple2<SimpleCharPos, SimpleCharPos>> SIMPLE_EQUALITIES = Array.of(
            Tuple.of(simpleCharPos(1, 3), simpleCharPos(0, 0)
                    .mapLine(l -> l + 1).mapLinePosition(lp -> lp + 3)),
            Tuple.of(simpleCharPos(1, 2), simpleCharPos(1, 2)),
            Tuple.of(simpleCharPos(1, 2), simpleCharPos(0, 0).withPosition(1, 2)),
            Tuple.of(simpleCharPos(1, 2), simpleCharPos(0, 1).withLine(1).withLinePosition(2))
    );

    @TestFactory
    Seq<DynamicTest> testSimpleEqualities() {
        return TestingUtil.testEqualities(SIMPLE_EQUALITIES);
    }

    private static final Seq<Tuple2<ValueCharPos<Integer>, ValueCharPos<Integer>>> VALUE_EQUALITIES = Array.of(
            Tuple.of(charPosValue(0, 0, 1), charPosValue(0,0, 0).withValue(1)),
            Tuple.of(charPosValue(1, 2, 1), charPosValue(0, 0, 1).withPosition(1, 2)),
            Tuple.of(charPosValue(1, 3, 2), charPosValue(1, 0, 0).withLinePosition(3).withValue(2)),
            Tuple.of(charPosValue(1, 2, 3), charPosValue(0, 0, 0)
                    .withLine(1).withLinePosition(2).withValue(3))
    );

    @TestFactory
    Seq<DynamicTest> testValueEqualities() {
        return TestingUtil.testEqualities(VALUE_EQUALITIES);
    }

    enum TestEnum {
        VAL1,
        VAL2,
        VAL3
    }

    private static final Seq<Tuple2<EnumCharPos<TestEnum>, EnumCharPos<TestEnum>>> ENUM_EQUALITIES = Array.of(
            Tuple.of(charPosEnum(0,0, TestEnum.VAL1),
                    charPosEnum(0, 0, TestEnum.VAL2).withValue(TestEnum.VAL1)),
            Tuple.of(charPosEnum(1, 2, TestEnum.VAL3),
                    charPosEnum(0, 0, TestEnum.VAL1).withPosition(1, 2).withValue(TestEnum.VAL3))
    );

    @TestFactory
    Seq<DynamicTest> testEnumEqualities() {
        return TestingUtil.testEqualities(ENUM_EQUALITIES);
    }
}
