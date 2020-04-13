package org.perudevteam.type.base;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.perudevteam.type.operator.OperatorSet;

import static org.perudevteam.type.base.BaseOperatorUtil.*;

public class BaseOperatorSetUtil {

    public static final Seq<BaseType>
    BYTE_SHORT = List.of(BaseType.BYTE, BaseType.SHORT),
    BYTE_INT = BYTE_SHORT.append(BaseType.INT),
    BYTE_LONG = BYTE_INT.append(BaseType.LONG),
    BYTE_FLOAT = BYTE_LONG.append(BaseType.FLOAT);

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
                    .withSymmetricBinaryOverloads(BYTE_INT, BaseType.CHARACTER, PLUS_CHARACTER)
                    .withBinaryOverload(BaseType.CHARACTER, BaseType.CHARACTER, PLUS_CHARACTER)
                    .withSymmetricBinaryOverloads(BYTE_INT, BaseType.CHARACTER, CHARACTER_COMPS)

                    // Enum Operations.
                    .withBinaryOverloads(BaseType.ENUM, BaseType.ENUM, ENUM_COMPS)

                    // Boolean Operations.
                    .withBinaryOverloads(BaseType.BOOLEAN, BaseType.BOOLEAN, BOOLEAN_BINOPS)
                    .withUnaryOverload(BaseType.BOOLEAN, NOT_BOOLEAN)

                    // String Operations.
                    .withBinaryOverloads(BaseType.STRING, BaseType.STRING, STRING_BINOPS)

                    // Sequence Operations.
                    .withBinaryOverload(BaseType.SEQUENCE, BaseType.SEQUENCE, EQ_SEQUENCE)

                    // Map Operations.
                    .withBinaryOverload(BaseType.MAP, BaseType.MAP, EQ_MAP)

                    // Function Operations.
                    .withBinaryOverloads(BaseType.FUNCTION, BaseType.FUNCTION, FUNCTION_BINOPS);
}
