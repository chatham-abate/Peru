package org.perudevteam.type.base;

import org.perudevteam.type.operator.BinaryOperator;

import static org.perudevteam.type.operator.BinaryOperator.*;
import static org.perudevteam.type.operator.UnaryOperator.*;
import static org.perudevteam.type.base.BaseValue.*;


public class BaseOperatorUtil {

    /*
     * Preset Operations.
     *
     * The widening rules are not identical to java.
     * With all math operations, the result conforms to the most precise input type.
     * Thus, all operations are closed under their input types.
     * byte + byte = byte.
     * int + byte = int.
     * byte ^ float = float.
     */

    public static BinaryOperator<BaseOperator, BaseType, BaseValue>

    // Arithmetic Plus Operations.
    PLUS_BYTE = binop(BaseOperator.PLUS, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() + i2.toByte()))),
    PLUS_SHORT = binop(BaseOperator.PLUS, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() + i2.toShort()))),
    PLUS_INT = binop(BaseOperator.PLUS, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() + i2.toInt())),
    PLUS_LONG = binop(BaseOperator.PLUS, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() + i2.toLong())),
    PLUS_FLOAT = binop(BaseOperator.PLUS, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() + i2.toFloat())),
    PLUS_DOUBLE = binop(BaseOperator.PLUS, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() + i2.toDouble())),

    // Arithmetic Minus Operations.
    MINUS_BYTE = binop(BaseOperator.MINUS, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() - i2.toByte()))),
    MINUS_SHORT = binop(BaseOperator.MINUS, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() - i2.toShort()))),
    MINUS_INT = binop(BaseOperator.MINUS, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() - i2.toInt())),
    MINUS_LONG = binop(BaseOperator.MINUS, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() - i2.toLong())),
    MINUS_FLOAT = binop(BaseOperator.MINUS, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() - i2.toFloat())),
    MINUS_DOUBLE = binop(BaseOperator.MINUS, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() - i2.toDouble())),

    // Arithmetic Times Operations.
    TIMES_BYTE = binop(BaseOperator.TIMES, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() * i2.toByte()))),
    TIMES_SHORT = binop(BaseOperator.TIMES, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() * i2.toShort()))),
    TIMES_INT = binop(BaseOperator.TIMES, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() * i2.toInt())),
    TIMES_LONG = binop(BaseOperator.TIMES, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() * i2.toLong())),
    TIMES_FLOAT = binop(BaseOperator.TIMES, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() * i2.toFloat())),
    TIMES_DOUBLE = binop(BaseOperator.TIMES, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() * i2.toDouble())),

    // Arithmetic Division Operations.
    OVER_BYTE = binop(BaseOperator.OVER, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() / i2.toByte()))),
    OVER_SHORT = binop(BaseOperator.OVER, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() / i2.toShort()))),
    OVER_INT = binop(BaseOperator.OVER, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() / i2.toInt())),
    OVER_LONG = binop(BaseOperator.OVER, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() / i2.toLong())),
    OVER_FLOAT = binop(BaseOperator.OVER, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() / i2.toFloat())),
    OVER_DOUBLE = binop(BaseOperator.OVER, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() / i2.toDouble())),

    // Arithmetic Power Operations.
    POWER_BYTE = binop(BaseOperator.POWER, BaseType.BYTE,
            (i1, i2) -> ofByte((byte)(Math.pow(i1.toDouble(), i2.toDouble())))),
    POWER_SHORT = binop(BaseOperator.POWER, BaseType.SHORT,
            (i1, i2) -> ofShort((short)(Math.pow(i1.toDouble(), i2.toDouble())))),
    POWER_INT = binop(BaseOperator.POWER, BaseType.INT,
            (i1, i2) -> ofInt((int)Math.pow(i1.toDouble(), i2.toDouble()))),
    POWER_LONG = binop(BaseOperator.POWER, BaseType.LONG,
            (i1, i2) -> ofLong((long)Math.pow(i1.toDouble(), i2.toDouble()))),
    POWER_FLOAT = binop(BaseOperator.POWER, BaseType.FLOAT,
            (i1, i2) -> ofFloat((float)Math.pow(i1.toDouble(), i2.toDouble()))),
    POWER_DOUBLE = binop(BaseOperator.POWER, BaseType.DOUBLE,
            (i1, i2) -> ofDouble(Math.pow(i1.toDouble(), i2.toDouble())));

}
