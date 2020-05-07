package com.github.chathamabate.peru.type.operator;

import com.github.chathamabate.peru.preset.base.BaseOperator;
import com.github.chathamabate.peru.preset.base.BaseType;
import com.github.chathamabate.peru.preset.base.BaseValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestOperator {

    @Test
    void testBasics() {
        BinaryOperator<BaseOperator, BaseType, BaseValue> binop = BinaryOperator.binop(
                BaseOperator.PLUS, BaseType.INT, (i1, i2) ->
                        BaseValue.ofInt(i1.toInt() + i2.toInt())
        );

        BaseValue i1 = BaseValue.ofInt(10);
        BaseValue i2 = BaseValue.ofInt(20);

        assertEquals(30, binop.tryApply(i1, i2).get().toInt());

        UnaryOperator<BaseOperator, BaseType, BaseValue> unop = UnaryOperator.unop(
                BaseOperator.MINUS, BaseType.INT, (i) -> BaseValue.ofInt(-i.toInt())
        );

        assertEquals(-10, unop.tryApply(i1).get().toInt());
    }

    @Test
    void testErrors() {
        assertThrows(NullPointerException.class,
                () -> BinaryOperator.binop(null, null, null));

        BinaryOperator<BaseOperator, BaseType, BaseValue> badOp = BinaryOperator.binop(
                BaseOperator.PLUS, BaseType.DOUBLE, (i1, i2) ->
                        BaseValue.ofInt(i1.toInt() + i2.toInt())
        );

        assertThrows(Exception.class, () -> badOp.apply(BaseValue.ofInt(1), BaseValue.ofInt(2)));
        assertThrows(Exception.class, () -> badOp.apply(BaseValue.ofString("as"), BaseValue.ofInt(2)));
    }
}
