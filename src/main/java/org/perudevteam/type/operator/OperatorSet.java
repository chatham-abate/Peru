package org.perudevteam.type.operator;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

import java.util.Objects;

public class OperatorSet<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>> {

    private static final OperatorSet EMPTY = new OperatorSet<>(HashMap.empty(), HashMap.empty());

    @SuppressWarnings("unchecked")
    public static <OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>> OperatorSet<OT, DT, DC> empty() {
        return (OperatorSet<OT, DT, DC>) EMPTY;
    }

    private Map<OT, Map<DT, UnaryOperator<OT, DT, DC>>> unaries;
    private Map<OT, Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>>> binaries;

    private OperatorSet(Map<OT, Map<DT, UnaryOperator<OT, DT, DC>>> uns,
                        Map<OT, Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>>> bins) {
        unaries = uns;
        binaries = bins;
    }

    public Try<DC> applyUnary(OT unOpTag, DC i) {
        if (!unaries.containsKey(unOpTag)) {
            return Try.failure(new Exception("Operator set does not contain unary operator "
                    + unOpTag.name() + "."));
        }

        Map<DT, UnaryOperator<OT, DT, DC>> overloads = unaries.get(unOpTag).get();
        DT dataTag = i.getTag();

        if (!overloads.containsKey(dataTag)) {
            return Try.failure(new Exception("No overload for " + unOpTag.name() + " given " + dataTag.name() + "."));
        }

        return overloads.get(dataTag).get().apply(i);
    }

    public Try<DC> applyBinary(OT binOpTag, DC i1, DC i2) {
        if (!binaries.containsKey(binOpTag)) {
            return Try.failure(new Exception("Operator set does not contain binary operator "
                    + binOpTag.name() + "."));
        }

        Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>> overloads1 = binaries.get(binOpTag).get();
        DT dataTag1 = i1.getTag();
        DT dataTag2 = i2.getTag();

        if (overloads1.containsKey(dataTag1)) {
            Map<DT, BinaryOperator<OT, DT, DC>> overloads2 = overloads1.get(dataTag1).get();
            if (overloads2.containsKey(dataTag2)) {
                return overloads2.get(dataTag2).get().apply(i1, i2);
            }
        }

        return Try.failure(new Exception("No overload for " + binOpTag.name() + " given "  + dataTag1.name()
                + " and " + dataTag2.name() + "."));
    }

    public OperatorSet<OT, DT, DC> withUnaryOperator(DT dataTag, UnaryOperator<OT, DT, DC> op) {
        Objects.requireNonNull(dataTag);
        Objects.requireNonNull(op);

        OT operatorTag = op.getTag();
        Map<OT, Map<DT, UnaryOperator<OT, DT, DC>>> newUnaries;

        if (unaries.containsKey(operatorTag)) {
            // If unaries does contain the given operator tag,
            // we no the given operator must have at least 1 overload.
            // Simply put this new overload in the overload map.
            Map<DT, UnaryOperator<OT, DT, DC>> overloads = unaries.get(operatorTag).get();
            newUnaries = unaries.put(operatorTag, overloads.put(dataTag, op));
        } else {
            newUnaries = unaries.put(operatorTag, HashMap.of(dataTag, op));
        }

        return new OperatorSet<>(newUnaries, binaries);
    }

    public OperatorSet<OT, DT, DC> withBinaryOperator(DT dataTag1, DT dataTag2, BinaryOperator<OT, DT, DC> op) {
        Objects.requireNonNull(dataTag1);
        Objects.requireNonNull(dataTag2);
        Objects.requireNonNull(op);

        OT operatorTag = op.getTag();

        Map<OT, Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>>> newBinaries;

        if (binaries.containsKey(operatorTag)) {
            Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>> overloads1 = binaries.get(operatorTag).get();

            if (overloads1.containsKey(dataTag1)) {
                Map<DT, BinaryOperator<OT, DT, DC>> overloads2 = overloads1.get(dataTag1).get();
                newBinaries = binaries.put(operatorTag, overloads1.put(dataTag1, overloads2.put(dataTag2, op)));
            } else {
                newBinaries = binaries.put(operatorTag, overloads1.put(dataTag1, HashMap.of(dataTag2, op)));
            }
        } else {
            newBinaries = binaries.put(operatorTag, HashMap.of(dataTag1, HashMap.of(dataTag2, op)));
        }

        return new OperatorSet<>(unaries, newBinaries);
    }
}
