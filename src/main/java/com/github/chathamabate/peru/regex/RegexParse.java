package com.github.chathamabate.peru.regex;

import com.github.chathamabate.fa.NFAutomaton;
import io.vavr.Function1;
import io.vavr.collection.Set;

import java.util.Objects;

public interface RegexParse {

    static RegexParse ofCharacter(char v) {
        return new CharacterParse(v);
    }

    static RegexParse ofCharacterSet(Set<? extends Character> v) {
        Objects.requireNonNull(v);
        v.forEach(Objects::requireNonNull);
        return new CharacterSetParse(Set.narrow(v));
    }

    static RegexParse ofString(String v) {
        Objects.requireNonNull(v);
        return new StringParse(v);
    }

    static RegexParse ofNFAutomaton(NFAutomaton<? super Character, ? extends Character, ?> v) {
        return new NFAutomatonParse(NFAutomaton.narrow(v));
    }

    default char asCharacter() {
        throw new NullPointerException("RegexParse contains no Character.");
    }

    default Set<Character> asCharacterSet() {
        throw new NullPointerException("RegexParse contains no Character Set.");
    }

    default String asString() {
        throw new NullPointerException("RegexParse contains no String.");
    }

    default NFAutomaton<Character, Character, Object> asNFAutomaton() {
        throw new NullPointerException("RegexParse contains no NFA.");
    }

    // Mapper Functions.

    default RegexParse mapCharacter(Function1<? super Character, ? extends Character> f) {
        return ofCharacter(f.apply(asCharacter()));
    }

    default RegexParse mapCharacterSet(Function1<? super Set<Character>, ? extends Set<Character>> f) {
        return ofCharacterSet(f.apply(asCharacterSet()));
    }

    default RegexParse mapString(Function1<? super String, ? extends String> f) {
        return ofString(f.apply(asString()));
    }

    default RegexParse mapNFAutomaton(Function1<? super NFAutomaton<Character, Character, Object>,
            ? extends NFAutomaton<Character, Character, Object>> f) {
        return ofNFAutomaton(f.apply(asNFAutomaton()));
    }

    class CharacterParse implements RegexParse {
        private final char value;
        private CharacterParse(char v) {
            value = v;
        }

        @Override
        public char asCharacter() {
            return value;
        }
    }

    class CharacterSetParse implements RegexParse {
        private final Set<Character> value;
        private CharacterSetParse(Set<Character> v) {
            value = v;
        }

        @Override
        public Set<Character> asCharacterSet() {
            return value;
        }
    }

    class StringParse implements RegexParse {
        private final String value;
        private StringParse(String v) {
            value = v;
        }

        @Override
        public String asString() {
            return value;
        }
    }

    class NFAutomatonParse implements RegexParse {
        private final NFAutomaton<Character, Character, Object> value;
        private NFAutomatonParse(NFAutomaton<Character, Character, Object> v) {
            value = v;
        }

        @Override
        public NFAutomaton<Character, Character, Object> asNFAutomaton() {
            return value;
        }
    }
}
