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
    BYTE_FLOAT = BYTE_LONG.append(BaseType.FLOAT),
    BYTE_DOUBLE = BYTE_FLOAT.append(BaseType.DOUBLE);

    public static final OperatorSet<BaseOperator, BaseType, BaseValue> BASE_OPERATOR_SET =
            OperatorSet.<BaseOperator, BaseType, BaseValue>empty()
                    // Byte Operations.
                    .withBinaryOverloads(BaseType.BYTE, BaseType.BYTE, BYTE_BINOPS)

                    // Short Operations.
                    .withSymmetricBinaryOverloads(BaseType.BYTE, BaseType.SHORT, SHORT_BINOPS)
                    .withBinaryOverloads(BaseType.SHORT, BaseType.SHORT, SHORT_BINOPS)

                    // Int Operations.
                    .withSymmetricBinaryOverloads(BYTE_SHORT, BaseType.INT, INT_BINOPS)
                    .withBinaryOverloads(BaseType.INT, BaseType.INT, INT_BINOPS)

                    // Long Operations
                    .withSymmetricBinaryOverloads(BYTE_INT, BaseType.LONG, LONG_BINOPS)
                    .withBinaryOverloads(BaseType.LONG, BaseType.LONG, LONG_BINOPS)

                    // Float Operations.
                    .withSymmetricBinaryOverloads(BYTE_LONG, BaseType.FLOAT, FLOAT_BINOPS)
                    .withBinaryOverloads(BaseType.FLOAT, BaseType.FLOAT, FLOAT_BINOPS)

                    // Double Operations.
                    .withSymmetricBinaryOverloads(BYTE_FLOAT, BaseType.DOUBLE, DOUBLE_BINOPS)
                    .withBinaryOverloads(BaseType.DOUBLE, BaseType.DOUBLE, DOUBLE_BINOPS)

                    // Character Operations.
                    .withBinaryOverload(BaseType.CHARACTER, BaseType.CHARACTER, PLUS_CHARACTER)
                    .withSymmetricBinaryOverloads(BYTE_INT, BaseType.CHARACTER, CHARACTER_COMPS)
}
