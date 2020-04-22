package org.perudevteam.peru.regex;

import io.vavr.collection.Set;
import org.perudevteam.fa.NFAutomaton;

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

    default NFAutomaton<Character, Character, ?> asNFAutomaton() {
        throw new NullPointerException("RegexParse contains no NFA.");
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
        private final NFAutomaton<Character, Character, ?> value;
        private NFAutomatonParse(NFAutomaton<Character, Character, ?> v) {
            value = v;
        }

        @Override
        public NFAutomaton<Character, Character, ?> asNFAutomaton() {
            return value;
        }
    }
}
