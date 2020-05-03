package org.perudevteam.charpos;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.perudevteam.TestingUtil;

import static org.perudevteam.charpos.SimpleCharPos.*;

public class TestCharPos {
    private static final Seq<Tuple2<SimpleCharPos, SimpleCharPos>> SIMPLE_EQUALITIES =
            List.of(
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
}
