package org.perudevteam.misc;

import io.vavr.collection.Stream;
import io.vavr.control.Try;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class SeqHelpers {
    public static Stream<Character> fileUnchecked(String filename) {
        return file(filename).get().map(Try::get);
    }

    public static Try<Stream<Try<Character>>> file(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            return Try.success(charStream(br));
        } catch (FileNotFoundException e) {
            return Try.failure(e);
        }
    }

    public static Stream<Try<Character>> charStream(BufferedReader br) {
        try {
            int c = br.read();
            return c == -1 ? Stream.empty()
                    : Stream.cons(Try.success((char)c), () -> charStream(br));
        } catch (IOException e) {
            return Stream.of(Try.failure(e));
        }
    }
}
