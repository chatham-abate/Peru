package com.github.chathamabate;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.DynamicTest;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;

import java.util.function.BiConsumer;

public final class TestingUtil {
    private TestingUtil() {
        // Static Final Class should never be initialized.
    }

    public static <T> Seq<DynamicTest> testTuples(Seq<? extends Tuple2<? extends T, ? extends T>> tuples,
                                                  BiConsumer<? super T, ? super T> f) {
        return tuples.map(tup -> DynamicTest.dynamicTest(
                tup._1 + " = " + tup._2,
                () -> f.accept(tup._1, tup._2)
        ));
    }

    public static <T extends Throwable>  Seq<DynamicTest> buildThrowTests(Class<T> expectedThrow,
            Seq<? extends Tuple2<? extends String, ? extends Executable>> errorTests) {
        return errorTests.map(tuple -> DynamicTest.dynamicTest(tuple._1,
                () -> assertThrows(expectedThrow, tuple._2)));
    }
}
