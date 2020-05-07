package com.github.chathamabate.peru.base;

import com.github.chathamabate.type.operator.BinaryOperator;
import com.github.chathamabate.type.operator.UnaryOperator;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

import static com.github.chathamabate.peru.base.BaseValue.*;


public final class BaseOperatorUtil {

    private BaseOperatorUtil() {
        // Class should never be initialized.
    }

    @SuppressWarnings("unchecked")
    public static int compareEnum(BaseValue i1, BaseValue i2) {
        Enum e1 = i1.toEnum();
        Enum e2 = i2.toEnum();

        if (e1.getClass() != e2.getClass()) {
            throw new ClassCastException("Cannot compare ENUMs of different type.");
        }

        return e1.compareTo(e2);
    }

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
    PLUS_BYTE = BinaryOperator.binop(BaseOperator.PLUS, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() + i2.toByte()))),
    PLUS_SHORT = BinaryOperator.binop(BaseOperator.PLUS, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() + i2.toShort()))),
    PLUS_INT = BinaryOperator.binop(BaseOperator.PLUS, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() + i2.toInt())),
    PLUS_LONG = BinaryOperator.binop(BaseOperator.PLUS, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() + i2.toLong())),
    PLUS_FLOAT = BinaryOperator.binop(BaseOperator.PLUS, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() + i2.toFloat())),
    PLUS_DOUBLE = BinaryOperator.binop(BaseOperator.PLUS, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() + i2.toDouble())),

    // Arithmetic Minus Operations.
    MINUS_BYTE = BinaryOperator.binop(BaseOperator.MINUS, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() - i2.toByte()))),
    MINUS_SHORT = BinaryOperator.binop(BaseOperator.MINUS, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() - i2.toShort()))),
    MINUS_INT = BinaryOperator.binop(BaseOperator.MINUS, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() - i2.toInt())),
    MINUS_LONG = BinaryOperator.binop(BaseOperator.MINUS, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() - i2.toLong())),
    MINUS_FLOAT = BinaryOperator.binop(BaseOperator.MINUS, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() - i2.toFloat())),
    MINUS_DOUBLE = BinaryOperator.binop(BaseOperator.MINUS, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() - i2.toDouble())),

    // Arithmetic Times Operations.
    TIMES_BYTE = BinaryOperator.binop(BaseOperator.TIMES, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() * i2.toByte()))),
    TIMES_SHORT = BinaryOperator.binop(BaseOperator.TIMES, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() * i2.toShort()))),
    TIMES_INT = BinaryOperator.binop(BaseOperator.TIMES, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() * i2.toInt())),
    TIMES_LONG = BinaryOperator.binop(BaseOperator.TIMES, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() * i2.toLong())),
    TIMES_FLOAT = BinaryOperator.binop(BaseOperator.TIMES, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() * i2.toFloat())),
    TIMES_DOUBLE = BinaryOperator.binop(BaseOperator.TIMES, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() * i2.toDouble())),

    // Arithmetic Division Operations.
    OVER_BYTE = BinaryOperator.binop(BaseOperator.OVER, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() / i2.toByte()))),
    OVER_SHORT = BinaryOperator.binop(BaseOperator.OVER, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() / i2.toShort()))),
    OVER_INT = BinaryOperator.binop(BaseOperator.OVER, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() / i2.toInt())),
    OVER_LONG = BinaryOperator.binop(BaseOperator.OVER, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() / i2.toLong())),
    OVER_FLOAT = BinaryOperator.binop(BaseOperator.OVER, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() / i2.toFloat())),
    OVER_DOUBLE = BinaryOperator.binop(BaseOperator.OVER, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() / i2.toDouble())),

    // Arithmetic Modulo Operations.
    MODULO_BYTE = BinaryOperator.binop(BaseOperator.MODULO, BaseType.BYTE, (i1, i2) -> ofByte((byte)(i1.toByte() % i2.toByte()))),
    MODULO_SHORT = BinaryOperator.binop(BaseOperator.MODULO, BaseType.SHORT, (i1, i2) -> ofShort((short)(i1.toShort() % i2.toShort()))),
    MODULO_INT = BinaryOperator.binop(BaseOperator.MODULO, BaseType.INT, (i1, i2) -> ofInt(i1.toInt() % i2.toInt())),
    MODULO_LONG = BinaryOperator.binop(BaseOperator.MODULO, BaseType.LONG, (i1, i2) -> ofLong(i1.toLong() % i2.toLong())),
    MODULO_FLOAT = BinaryOperator.binop(BaseOperator.MODULO, BaseType.FLOAT, (i1, i2) -> ofFloat(i1.toFloat() % i2.toFloat())),
    MODULO_DOUBLE = BinaryOperator.binop(BaseOperator.MODULO, BaseType.DOUBLE, (i1, i2) -> ofDouble(i1.toDouble() % i2.toDouble())),

    // Arithmetic Power Operations.
    POWER_BYTE = BinaryOperator.binop(BaseOperator.POWER, BaseType.BYTE,
            (i1, i2) -> ofByte((byte)(Math.pow(i1.toDouble(), i2.toDouble())))),
    POWER_SHORT = BinaryOperator.binop(BaseOperator.POWER, BaseType.SHORT,
            (i1, i2) -> ofShort((short)(Math.pow(i1.toDouble(), i2.toDouble())))),
    POWER_INT = BinaryOperator.binop(BaseOperator.POWER, BaseType.INT,
            (i1, i2) -> ofInt((int)Math.pow(i1.toDouble(), i2.toDouble()))),
    POWER_LONG = BinaryOperator.binop(BaseOperator.POWER, BaseType.LONG,
            (i1, i2) -> ofLong((long)Math.pow(i1.toDouble(), i2.toDouble()))),
    POWER_FLOAT = BinaryOperator.binop(BaseOperator.POWER, BaseType.FLOAT,
            (i1, i2) -> ofFloat((float)Math.pow(i1.toDouble(), i2.toDouble()))),
    POWER_DOUBLE = BinaryOperator.binop(BaseOperator.POWER, BaseType.DOUBLE,
            (i1, i2) -> ofDouble(Math.pow(i1.toDouble(), i2.toDouble()))),

    // Arithmetic Less Than Operations.
    LT_BYTE = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toByte() < i2.toByte())),
    LT_SHORT = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toShort() < i2.toShort())),
    LT_INT = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toInt() < i2.toInt())),
    LT_LONG = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toLong() < i2.toLong())),
    LT_FLOAT = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toFloat() < i2.toFloat())),
    LT_DOUBLE = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toDouble() < i2.toDouble())),

    // Arithmetic Greater Than Operations.
    GT_BYTE = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toByte() > i2.toByte())),
    GT_SHORT = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toShort() > i2.toShort())),
    GT_INT = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toInt() > i2.toInt())),
    GT_LONG = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toLong() > i2.toLong())),
    GT_FLOAT = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toFloat() > i2.toFloat())),
    GT_DOUBLE = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toDouble() > i2.toDouble())),

    // Arithmetic Less Than or Equal To Operations.
    LT_EQ_BYTE = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toByte() <= i2.toByte())),
    LT_EQ_SHORT = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toShort() <= i2.toShort())),
    LT_EQ_INT = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toInt() <= i2.toInt())),
    LT_EQ_LONG = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toLong() <= i2.toLong())),
    LT_EQ_FLOAT = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toFloat() <= i2.toFloat())),
    LT_EQ_DOUBLE = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toDouble() <= i2.toDouble())),

    // Arithmetic Greater Than or Equal To Operations.
    GT_EQ_BYTE = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toByte() >= i2.toByte())),
    GT_EQ_SHORT = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toShort() >= i2.toShort())),
    GT_EQ_INT = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toInt() >= i2.toInt())),
    GT_EQ_LONG = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toLong() >= i2.toLong())),
    GT_EQ_FLOAT = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toFloat() >= i2.toFloat())),
    GT_EQ_DOUBLE = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toDouble() >= i2.toDouble())),

    // Arithmetic Equal To Operations.
    EQ_BYTE = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toByte() == i2.toByte())),
    EQ_SHORT = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toShort() == i2.toShort())),
    EQ_INT = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toInt() == i2.toInt())),
    EQ_LONG = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toLong() == i2.toLong())),
    EQ_FLOAT = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toFloat() == i2.toFloat())),
    EQ_DOUBLE = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toDouble() == i2.toDouble())),


    // Character Operations.
    PLUS_CHARACTER = BinaryOperator.binop(BaseOperator.PLUS, BaseType.CHARACTER,
            (i1, i2) -> ofCharacter((char)(i1.toInt() + i2.toInt()))),
    MINUS_CHARACTER = BinaryOperator.binop(BaseOperator.MINUS, BaseType.CHARACTER,
            (i1, i2) -> ofCharacter((char)(i1.toInt() - i2.toInt()))),
    // Character Comparisons are Identical to Integer Comparisons.

    // Enum Operations. (Unsafe Comparison)
    LT_ENUM = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(compareEnum(i1, i2) < 0)),
    GT_ENUM = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(compareEnum(i1, i2) > 0)),
    LT_EQ_ENUM = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(compareEnum(i1, i2) <= 0)),
    GT_EQ_ENUM = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(compareEnum(i1, i2) >= 0)),
    EQ_ENUM = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toEnum().equals(i2.toEnum()))),


    // Boolean Operators.
    AND_BOOLEAN = BinaryOperator.binop(BaseOperator.AND, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toBoolean() && i2.toBoolean())),
    OR_BOOLEAN = BinaryOperator.binop(BaseOperator.OR, BaseType.BOOLEAN, (i1, i2) -> ofBoolean(i1.toBoolean() || i2.toBoolean())),
    EQ_BOOLEAN = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toBoolean() == i2.toBoolean())),


    // String Operations.
    PLUS_STRING = BinaryOperator.binop(BaseOperator.PLUS, BaseType.STRING,
            (i1, i2) -> ofString(i1.toString() + i2.toString())),
    LT_STRING = BinaryOperator.binop(BaseOperator.LT, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toString().compareTo(i2.toString()) < 0)),
    GT_STRING = BinaryOperator.binop(BaseOperator.GT, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toString().compareTo(i2.toString()) > 0)),
    LT_EQ_STRING = BinaryOperator.binop(BaseOperator.LT_EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toString().compareTo(i2.toString()) <= 0)),
    GT_EQ_STRING = BinaryOperator.binop(BaseOperator.GT_EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toString().compareTo(i2.toString()) >= 0)),
    EQ_STRING = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toString().equals(i2.toString()))),

    // Map Operations.
    EQ_MAP = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toMap().equals(i2.toMap()))),


    // Sequence Operations.
    PLUS_SEQUENCE = BinaryOperator.binop(BaseOperator.PLUS, BaseType.SEQUENCE,
            (i1, i2) -> ofSequence(i1.toSequence().appendAll(i2.toSequence()))),
    EQ_SEQUENCE = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toSequence().equals(i2.toSequence()))),


    // Function Operations.
    EQ_FUNCTION = BinaryOperator.binop(BaseOperator.EQ, BaseType.BOOLEAN,
            (i1, i2) -> ofBoolean(i1.toFunction().equals(i2.toFunction())));


    /*
     * Now for Unary Operations.
     * There are only 3.
     * Unary Plus (Arithmetic)
     * Unary Minus (Arithmetic)
     * Not (Boolean)
     */

    public static final UnaryOperator<BaseOperator, BaseType, BaseValue>

    // Unary Plus Operations.
    UN_PLUS_BYTE = UnaryOperator.unop(BaseOperator.PLUS, BaseType.BYTE, (i) -> i),
    UN_PLUS_SHORT = UnaryOperator.unop(BaseOperator.PLUS, BaseType.SHORT, (i) -> i),
    UN_PLUS_INT = UnaryOperator.unop(BaseOperator.PLUS, BaseType.INT, (i) -> i),
    UN_PLUS_LONG = UnaryOperator.unop(BaseOperator.PLUS, BaseType.LONG, (i) -> i),
    UN_PLUS_FLOAT = UnaryOperator.unop(BaseOperator.PLUS, BaseType.FLOAT, (i) -> i),
    UN_PLUS_DOUBLE = UnaryOperator.unop(BaseOperator.PLUS, BaseType.DOUBLE, (i) -> i),

    // Unary Minus Operations.
    UN_MINUS_BYTE = UnaryOperator.unop(BaseOperator.MINUS, BaseType.BYTE, (i) -> i.mapByte(v -> (byte)-v)),
    UN_MINUS_SHORT = UnaryOperator.unop(BaseOperator.MINUS, BaseType.SHORT, (i) -> i.mapShort(v -> (short)-v)),
    UN_MINUS_INT = UnaryOperator.unop(BaseOperator.MINUS, BaseType.INT, (i) -> i.mapInt(v -> -v)),
    UN_MINUS_LONG = UnaryOperator.unop(BaseOperator.MINUS, BaseType.LONG, (i) -> i.mapLong(v -> -v)),
    UN_MINUS_FLOAT = UnaryOperator.unop(BaseOperator.MINUS, BaseType.FLOAT, (i) -> i.mapFloat(v -> -v)),
    UN_MINUS_DOUBLE = UnaryOperator.unop(BaseOperator.MINUS, BaseType.DOUBLE, (i) -> i.mapDouble(v -> -v)),

    // Boolean Not Operation.
    NOT_BOOLEAN = UnaryOperator.unop(BaseOperator.NOT, BaseType.BOOLEAN, (i) -> i.mapBoolean(v -> !v));


    // Lists of Binary Operators.
    public static final Seq<BinaryOperator<BaseOperator, BaseType, BaseValue>>
    BYTE_BINOPS = List.of(PLUS_BYTE, MINUS_BYTE, TIMES_BYTE, OVER_BYTE, MODULO_BYTE, POWER_BYTE,
            LT_BYTE, GT_BYTE, LT_EQ_BYTE, GT_EQ_BYTE, EQ_BYTE),
    SHORT_BINOPS = List.of(PLUS_SHORT, MINUS_SHORT, TIMES_SHORT, OVER_SHORT, MODULO_SHORT, POWER_SHORT,
            LT_SHORT, GT_SHORT, LT_EQ_SHORT, GT_EQ_SHORT, EQ_SHORT),
    INT_BINOPS = List.of(PLUS_INT, MINUS_INT, TIMES_INT, OVER_INT, MODULO_INT, POWER_INT,
            LT_INT, GT_INT, LT_EQ_INT, GT_EQ_INT, EQ_INT),
    LONG_BINOPS = List.of(PLUS_LONG, MINUS_LONG, TIMES_LONG, OVER_LONG, MODULO_LONG, POWER_LONG,
            LT_LONG, GT_LONG, LT_EQ_LONG, GT_EQ_LONG, EQ_LONG),
    FLOAT_BINOPS = List.of(PLUS_FLOAT, MINUS_FLOAT, TIMES_FLOAT, OVER_FLOAT, MODULO_FLOAT, POWER_FLOAT,
            LT_FLOAT, GT_FLOAT, LT_EQ_FLOAT, GT_EQ_FLOAT, EQ_FLOAT),
    DOUBLE_BINOPS = List.of(PLUS_DOUBLE, MINUS_DOUBLE, TIMES_DOUBLE, OVER_DOUBLE, MODULO_DOUBLE, POWER_DOUBLE,
            LT_DOUBLE, GT_DOUBLE, LT_EQ_DOUBLE, GT_EQ_DOUBLE, EQ_DOUBLE),

    // Character Comparison...
    CHARACTER_BINOPS = List.of(PLUS_CHARACTER, MINUS_CHARACTER, LT_INT,
            GT_INT, LT_EQ_INT, GT_EQ_INT, EQ_INT),

    // Enum Comparisons...
    ENUM_COMPS = List.of(LT_ENUM, GT_ENUM, LT_EQ_ENUM, GT_EQ_ENUM, EQ_ENUM),

    // Boolean Binops...
    BOOLEAN_BINOPS = List.of(AND_BOOLEAN, OR_BOOLEAN, EQ_BOOLEAN),

    // String Binops...
    STRING_BINOPS = List.of(PLUS_STRING, LT_STRING, GT_STRING, LT_EQ_STRING, GT_EQ_STRING, EQ_STRING);

    // Lists of Unary Operators.
    public static final Seq<UnaryOperator<BaseOperator, BaseType, BaseValue>>
    BYTE_UNOPS = List.of(UN_MINUS_BYTE, UN_PLUS_BYTE),
    SHORT_UNOPS = List.of(UN_MINUS_SHORT, UN_PLUS_SHORT),
    INT_UNOPS = List.of(UN_MINUS_INT, UN_PLUS_INT),
    LONG_UNOPS = List.of(UN_MINUS_LONG, UN_PLUS_LONG),
    FLOAT_UNOPS = List.of(UN_MINUS_FLOAT, UN_PLUS_FLOAT),
    DOUBLE_UNOPS = List.of(UN_MINUS_DOUBLE, UN_PLUS_DOUBLE);
}
