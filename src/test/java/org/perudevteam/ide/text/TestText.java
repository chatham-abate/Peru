package org.perudevteam.ide.text;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.collection.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.perudevteam.TestingUtil;

import static org.perudevteam.ide.text.SimpleText.*;

public class TestText {

    private static final Seq<Tuple2<SimpleText, SimpleText>> EQUALITIES = Array.<Tuple2<SimpleText, SimpleText>>of(
            Tuple.of(singleLine(), singleLine()),
            Tuple.of(
                    singleLine().insertChar(0, 0, 's'),
                    singleLine().insertString(0, 0, "s")
            ),
            Tuple.of(
                    of("Hello"),
                    singleLine().insertString(0, 0, "Hell").insertChar(0, 4, 'o')
            ),
            Tuple.of(
                    of("Hello\nWorld"),
                    singleLine().insertString(0, 0, "Hello\nWorld")
            ),
            Tuple.of(
                    singleLine(),
                    of("ab\nc").deleteRangeInclusiveUnchecked(0, 1, 1, 0).deleteChar(0, 0)
            ),
            Tuple.of(
                    of(Vector.of("Hello", "World")),
                    singleLine().insertString(0, 0, "Hello\nW").insertString(1, 1, "orld")
            ),
            Tuple.of(
                    of("Hello\na\nb\nWorld"),
                    singleLine().insertString(0, 0, "Horld").insertString(0, 1, "ello\na\nb\nW")
            ),
            Tuple.of(
                    of("ac"),
                    of("abc").deleteChar(0, 1)
            ),
            Tuple.of(
                    singleLine(),
                    of("abc\nabc\nabc").deleteRangeInclusive(0, 0, 2, 2)
            ),
            Tuple.of(
                    of("Hello"),
                    of("Ho").insertString(0, 1, "ell")
            ),
            Tuple.of(
                    of("Ho"),
                    of("Hello").deleteRangeInclusive(0, 1, 0, 3)
            ),
            Tuple.of(
                    of("o"),
                    of("H\nello").deleteRangeInclusive(0, 0, 1, 2)
            ),
            Tuple.of(
                    of("GoodPerson"),
                    of("Gooda\na\naPerson").deleteRangeInclusive(0, 4, 2, 0)
            ),
            Tuple.of(
                    of("b"),
                    of("\nb").breakLine(1)
            ),
            Tuple.of(
                    of("abc\nabc\nabc"),
                    of("abc\na\nbc\nabc").breakLine(2)
            ),
            Tuple.of(
                    singleLine(),
                    of("\n").breakLine(1)
            ),
            Tuple.of(
                    of("\n\n\n"),
                    of("\n\n\n\n").breakLine(2)
            )
    );

    @TestFactory
    Seq<DynamicTest> testBasicEqualities() {
        return TestingUtil.testTuples(EQUALITIES, Assertions::assertEquals);
    }

    Seq<Tuple2<String, Executable>> ERRORS = Array.of(
            Tuple.of(
                    "Bad Insertion Line",
                    () -> singleLine().insertChar(1, 0, 'a')
            ),
            Tuple.of(
                    "Bad Insertion Line Position",
                    () -> singleLine().insertChar(0, 1, 'a')
            ),
            Tuple.of(
                    "Bad Deletion Line",
                    () -> of("abc").deleteChar(1, 0)
            ),
            Tuple.of(
                    "Bad Deletion Line Position",
                    () -> of("abc").deleteChar(0, 3)
            ),
            Tuple.of(
                    "Bad Line Break 1",
                    () -> of("").breakLine(0)
            ),
            Tuple.of(
                    "Bad Line Break 2",
                    () -> of("abc\nas").breakLine(2)
            )
    );

    @TestFactory
    Seq<DynamicTest> testBasicErrors() {
        return TestingUtil.buildThrowTests(Exception.class, ERRORS);
    }
}
