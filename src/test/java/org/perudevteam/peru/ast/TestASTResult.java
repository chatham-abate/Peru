package org.perudevteam.peru.ast;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.perudevteam.peru.ast.ASTResult;
import static org.perudevteam.peru.ast.ASTResult.*;

import static org.perudevteam.peru.base.BaseValue.*;

public class TestASTResult {

    @Test
    void testBasics() {
        assertTrue(empty().isEmpty() && !empty().isPositioned());
        assertEquals(1, positioned(1, 2).getLine());

        assertEquals(empty(), empty());
        assertEquals(fullResult(1,2, ofInt(10)), fullResult(1,2 , ofInt(10)));
        assertNotEquals(positioned(1, 2), fullResult(1, 2, ofInt(10)));

        System.out.println(positioned(1, 3));
    }
}
