package org.perudevteam.dynamic;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import static org.perudevteam.dynamic.Dynamic.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestTransformer {
    @Test
    public void testBasics() {
        Transformer<Integer, String> counter = ((inputEle, context) -> {
            Dynamic newContext = ofInt(context.asInt() + inputEle);
            return Tuple.of(inputEle.toString(), newContext);
        });

        Try<Tuple2<String, Dynamic>> output =
                counter.tryTransform(2, ofInt(3));

        assertTrue(output.isSuccess());
        assertEquals(5, output.get()._2.asInt());
        assertEquals("2", output.get()._1);

        Transformer<Integer, Boolean> counter2 = counter.andThen((inputEle, context) ->
            Tuple.of(inputEle.length() % 2 == 0, ofInt(context.asInt() * 2))
        );

        Try<Tuple2<Boolean, Dynamic>> output2 = counter2.tryTransform(10, ofInt(3));

        assertTrue(output2.isSuccess());
        assertTrue(output2.get()._1);
        assertEquals(26, output2.get()._2.asInt());
    }
}
