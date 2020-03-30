package org.perudevteam.dynamic;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.collection.List;
import org.junit.jupiter.api.Test;
import static org.perudevteam.dynamic.Dynamic.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestBuilder {

    // Adds together the top three elements of a sequence at a time.
    // Keeps track of how many groups of 3 have been read in context.
    static final Builder<Integer, Integer> TRI_SUM = ((input, context) -> {
        int sum = 0;
        Seq<Integer> list = input;

        int i;
        for (i = 0; i < Math.min(3, input.length()); i++) {
            sum += list.head();
            list = list.tail();
        }

        Dynamic newContext = i == 0
                ? context
                : ofInt(context.asInt() + 1);

        return Tuple.of(sum, newContext, list);
    });

    @Test
    void testBasics() {
        Seq<Integer> input = List.of(1, 2, 3, 2, 3);
        Dynamic context = ofInt(0);

        Try<Tuple3<Integer, Dynamic, Seq<Integer>>> tryOutput = TRI_SUM.tryBuild(input, context);
        assertTrue(tryOutput.isSuccess());
        Tuple3<Integer, Dynamic, Seq<Integer>> output = tryOutput.get();
        assertEquals(6, output._1);
        assertEquals(1, output._2.asInt());

        Try<Tuple3<Integer, Dynamic, Seq<Integer>>> tryOutput2 = TRI_SUM.tryBuild(output._3, output._2);
        assertTrue(tryOutput2.isSuccess());
        Tuple3<Integer, Dynamic, Seq<Integer>> output2 = tryOutput2.get();
        assertEquals(5, output2._1);
        assertEquals(2, output2._2.asInt());

        assertEquals(0, TRI_SUM.tryBuild(List.empty(), ofInt(0)).get()._2.asInt());
    }
}
