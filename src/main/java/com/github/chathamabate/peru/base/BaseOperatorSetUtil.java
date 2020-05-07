package com.github.chathamabate.peru.base;

import com.github.chathamabate.type.operator.OperatorSet;
import io.vavr.collection.List;
import io.vavr.collection.Seq;

import static com.github.chathamabate.peru.base.BaseOperatorUtil.*;

public final class BaseOperatorSetUtil {

    private BaseOperatorSetUtil() {
        // Class should never be initialized.
    }

    public static final Seq<BaseType>
    BYTE_SHORT = List.of(BaseType.BYTE, BaseType.SHORT),
    BYTE_INT = BYTE_SHORT.append(BaseType.INT),
    BYTE_LONG = BYTE_INT.append(BaseType.LONG),
    BYTE_FLOAT = BYTE_LONG.append(BaseType.FLOAT),

    ALL_EXCEPT_STRING = List.of(BaseType.BYTE, BaseType.SHORT, BaseType.INT, BaseType.LONG,
            BaseType.FLOAT, BaseType.DOUBLE, BaseType.CHARACTER, BaseType.ENUM, BaseType.BOOLEAN,
            BaseType.MAP, BaseType.SEQUENCE, BaseType.FUNCTION);

    public static final OperatorSet<BaseOperator, BaseType, BaseValue> BASE_OPERATOR_SET =
            OperatorSet.<BaseOperator, BaseType, BaseValue>empty()
                    // Byte Operations.
                    .withBinaryOverloads(BaseType.BYTE, BaseType.BYTE, BYTE_BINOPS)
                    .withUnaryOverloads(BaseType.BYTE, BYTE_UNOPS)

                    // Short Operations.
                    .withSymmetricBinaryOverloads(BaseType.BYTE, BaseType.SHORT, SHORT_BINOPS)
                    .withBinaryOverloads(BaseType.SHORT, BaseType.SHORT, SHORT_BINOPS)
                    .withUnaryOverloads(BaseType.SHORT, SHORT_UNOPS)

                    // Int Operations.
                    .withSymmetricBinaryOverloads(BYTE_SHORT, BaseType.INT, INT_BINOPS)
                    .withBinaryOverloads(BaseType.INT, BaseType.INT, INT_BINOPS)
                    .withUnaryOverloads(BaseType.INT, INT_UNOPS)

                    // Long Operations
                    .withSymmetricBinaryOverloads(BYTE_INT, BaseType.LONG, LONG_BINOPS)
                    .withBinaryOverloads(BaseType.LONG, BaseType.LONG, LONG_BINOPS)
                    .withUnaryOverloads(BaseType.LONG, LONG_UNOPS)

                    // Float Operations.
                    .withSymmetricBinaryOverloads(BYTE_LONG, BaseType.FLOAT, FLOAT_BINOPS)
                    .withBinaryOverloads(BaseType.FLOAT, BaseType.FLOAT, FLOAT_BINOPS)
                    .withUnaryOverloads(BaseType.FLOAT, FLOAT_UNOPS)

                    // Double Operations.
                    .withSymmetricBinaryOverloads(BYTE_FLOAT, BaseType.DOUBLE, DOUBLE_BINOPS)
                    .withBinaryOverloads(BaseType.DOUBLE, BaseType.DOUBLE, DOUBLE_BINOPS)
                    .withUnaryOverloads(BaseType.DOUBLE, DOUBLE_UNOPS)

                    // Character Operations.
                    .withSymmetricBinaryOverloads(BYTE_INT, BaseType.CHARACTER, CHARACTER_BINOPS)
                    .withBinaryOverloads(BaseType.CHARACTER, BaseType.CHARACTER, CHARACTER_BINOPS)

                    // Enum Operations.
                    .withBinaryOverloads(BaseType.ENUM, BaseType.ENUM, ENUM_COMPS)

                    // Boolean Operations.
                    .withBinaryOverloads(BaseType.BOOLEAN, BaseType.BOOLEAN, BOOLEAN_BINOPS)
                    .withUnaryOverload(BaseType.BOOLEAN, NOT_BOOLEAN)

                    // String Operations.
                    .withSymmetricBinaryOverloads(ALL_EXCEPT_STRING, BaseType.STRING, PLUS_STRING)
                    .withBinaryOverloads(BaseType.STRING, BaseType.STRING, STRING_BINOPS)

                    // Sequence Operations.
                    .withBinaryOverload(BaseType.SEQUENCE, BaseType.SEQUENCE, EQ_SEQUENCE)

                    // Map Operations.
                    .withBinaryOverload(BaseType.MAP, BaseType.MAP, EQ_MAP)

                    // Function Operations.
                    .withBinaryOverload(BaseType.FUNCTION, BaseType.FUNCTION, EQ_FUNCTION);
}
