package org.perudevteam.statemachine;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import io.vavr.control.Option;

import static org.perudevteam.statemachine.DFStateMachine.*;

public class TestStateMachine {

    @Test
    void testBasicErrors() {
        assertThrows(IllegalArgumentException.class, () -> {
            emptyDFSM(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            emptyDFSM(3).withAcceptingState(5, "Hello");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            emptyDFSM(3).withAcceptingState(-1, "Good Bye");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            emptyDFSM(3).withEdge(1, -1, "Input");
        });
    }

    enum Input {
        A, B
    }

    @Test
    void testBasics() {
        DFStateMachine<Input, String> dfsm = DFStateMachine.<Input, String>emptyDFSM(3)
                .withAcceptingState(1, "Nice")
                .withEdge(0, 0, Input.B)
                .withEdge(0, 1, Input.A)
                .withEdge(1, 2, Input.B)
                .withEdge(2, 0, Input.A);

        Option<String> output = dfsm.getOutput(1);
        assertFalse(output.isEmpty());

        assertEquals("Nice", output.get());

        assertEquals(0, dfsm.getNextState(0, Input.B).get());
        assertEquals(2, dfsm.getNextState(1, Input.B).get());
    }
}
