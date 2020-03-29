package org.perudevteam.misc;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static io.vavr.API.*;
import static io.vavr.Patterns.*;


public class TestDynamic {

    @Test
    public void testDefaults() {

        Try<Integer> a = Try.of(() -> {
          return 2;
        });

        System.out.println(a.toOption().isEmpty());

    }
}
