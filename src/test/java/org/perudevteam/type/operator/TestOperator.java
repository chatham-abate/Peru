package org.perudevteam.type.operator;

import static org.perudevteam.type.operator.UnaryOperator.*;
import static org.perudevteam.type.operator.BinaryOperator.*;

import org.junit.jupiter.api.Test;
import org.perudevteam.type.base.BaseOperator;
import org.perudevteam.type.base.BaseType;
import org.perudevteam.type.base.BaseValue;
import org.perudevteam.type.operator.BinaryOperator;
import org.perudevteam.type.operator.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class TestOperator {

    @Test
    void testBasics() {
        BinaryOperator<BaseOperator, BaseType, BaseValue> binop = binop(
                BaseOperator.PLUS, BaseType.INT, (i1, i2) ->
                        BaseValue.ofInt(i1.toInt() + i2.toInt())
        );

        BaseValue i1 = BaseValue.ofInt(10);
        BaseValue i2 = BaseValue.ofInt(20);

        assertEquals(30, binop.tryApply(i1, i2).get().toInt());

        UnaryOperator<BaseOperator, BaseType, BaseValue> unop = unop(
                BaseOperator.MINUS, BaseType.INT, (i) -> BaseValue.ofInt(-i.toInt())
        );

        assertEquals(-10, unop.tryApply(i1).get().toInt());
    }

    @Test
    void testErrors() {
        assertThrows(NullPointerException.class,
                () -> binop(null, null, null));

        BinaryOperator<BaseOperator, BaseType, BaseValue> badOp = binop(
                BaseOperator.PLUS, BaseType.DOUBLE, (i1, i2) ->
                        BaseValue.ofInt(i1.toInt() + i2.toInt())
        );

        assertThrows(Exception.class, () -> badOp.apply(BaseValue.ofInt(1), BaseValue.ofInt(2)));
        assertThrows(Exception.class, () -> badOp.apply(BaseValue.ofString("as"), BaseValue.ofInt(2)));
    }
}
