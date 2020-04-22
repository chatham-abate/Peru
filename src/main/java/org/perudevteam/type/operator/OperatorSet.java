package org.perudevteam.type.operator;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

import static org.perudevteam.misc.MiscHelpers.*;
import java.util.Objects;

// Operator Set...
public class OperatorSet<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>> {

    @SuppressWarnings("all")
    private static final OperatorSet<?, ?, ?> EMPTY = new OperatorSet(HashMap.empty(), HashMap.empty());

    @SuppressWarnings("unchecked")
    public static <OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>> OperatorSet<OT, DT, DC> empty() {
        return (OperatorSet<OT, DT, DC>) EMPTY;
    }

    private final Map<OT, Map<DT, UnaryOperator<OT, DT, DC>>> unaries;
    private final Map<OT, Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>>> binaries;

    private OperatorSet(Map<OT, ? extends Map<DT, ? extends UnaryOperator<OT, DT, DC>>> uns,
                        Map<OT, ? extends Map<DT, ? extends Map<DT, ? extends BinaryOperator<OT, DT, DC>>>> bins) {
        unaries = Map.narrow(uns.mapValues(Map::narrow));
        binaries = Map.narrow(bins.mapValues(m -> Map.narrow(m.mapValues(Map::narrow))));
    }

    public DC applyUnary(OT unOpTag, DC i) throws Throwable {
        Objects.requireNonNull(unOpTag);
        Objects.requireNonNull(i);

        if (!unaries.containsKey(unOpTag)) {
            throw new Exception("Operator set does not contain unary operator "
                    + unOpTag.name() + ".");
        }

        Map<DT, UnaryOperator<OT, DT, DC>> overloads = unaries.get(unOpTag).get();
        DT dataTag = i.getTag();

        if (!overloads.containsKey(dataTag)) {
            throw new Exception("No overload for " + unOpTag.name() + " given " + dataTag.name() + ".");
        }

        return overloads.get(dataTag).get().apply(i);
    }

    public Try<DC> tryApplyUnary(OT unOpTag, DC i) {
        return Try.of(() -> applyUnary(unOpTag, i));
    }

    public Try<DC> tryApplyUnary(OT unOpTag, Try<DC> tryI) {
        return tryI.mapTry(i -> applyUnary(unOpTag, i));
    }

    public DC applyBinary(OT binOpTag, DC i1, DC i2) throws Throwable {
        Objects.requireNonNull(binOpTag);
        Objects.requireNonNull(i1);
        Objects.requireNonNull(i2);

        if (!binaries.containsKey(binOpTag)) {
            throw new Exception("Operator set does not contain binary operator "
                    + binOpTag.name() + ".");
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

        throw new Exception("No overload for " + binOpTag.name() + " given "  + dataTag1.name()
                + " and " + dataTag2.name() + ".");
    }

    public Try<DC> tryApplyBinary(OT binOpTag, DC i1, DC i2) {
        return Try.of(() -> applyBinary(binOpTag, i1, i2));
    }

    public Try<DC> tryApplyBinary(OT binOpTag, Try<DC> tryI1, Try<DC> tryI2) {
        return tryI1.flatMap(i1 -> tryI2.mapTry(i2 -> applyBinary(binOpTag, i1, i2)));
    }

    private OperatorSet<OT, DT, DC> withUnaryOverloadUnchecked(DT dataTag, UnaryOperator<OT, DT, DC> op) {
        OT operatorTag = op.getTag();
        Map<OT, Map<DT, UnaryOperator<OT, DT, DC>>> newUnaries;

        if (unaries.containsKey(operatorTag)) {
            Map<DT, UnaryOperator<OT, DT, DC>> overloads = unaries.get(operatorTag).get();
            newUnaries = unaries.put(operatorTag, overloads.put(dataTag, op));
        } else {
            newUnaries = unaries.put(operatorTag, HashMap.of(dataTag, op));
        }

        return new OperatorSet<>(newUnaries, binaries);
    }

    public OperatorSet<OT, DT, DC> withUnaryOverload(DT dataTag, UnaryOperator<OT, DT, DC> op) {
        Objects.requireNonNull(dataTag);
        Objects.requireNonNull(op);
        return withUnaryOverloadUnchecked(dataTag, op);
    }

    public OperatorSet<OT, DT, DC> withUnaryOverloads(DT dataTag, Seq<UnaryOperator<OT, DT, DC>> ops) {
        Objects.requireNonNull(dataTag);
        Objects.requireNonNull(ops);
        ops.forEach(Objects::requireNonNull);

        OperatorSet<OT, DT, DC> result = this;

        for (UnaryOperator<OT, DT, DC> op: ops) {
            result = result.withUnaryOverloadUnchecked(dataTag, op);
        }

        return result;
    }

    private OperatorSet<OT, DT, DC>
    withBinaryOverloadUnchecked(DT dataTag1, DT dataTag2, BinaryOperator<OT, DT, DC> op) {
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

    private OperatorSet<OT, DT, DC>
    withBinaryOverloadsUnchecked(Seq<DT> dataTags1, Seq<DT> dataTags2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        OperatorSet<OT, DT, DC> result = this;

        for (BinaryOperator<OT, DT, DC> op: ops) {
            for (DT dataTag1: dataTags1) {
                for (DT dataTag2: dataTags2) {
                    result = result.withBinaryOverloadUnchecked(dataTag1, dataTag2, op);
                }
            }
        }

        return result;
    }

    public OperatorSet<OT, DT, DC> withBinaryOverload(DT dataTag1, DT dataTag2, BinaryOperator<OT, DT, DC> op) {
        Objects.requireNonNull(dataTag1);
        Objects.requireNonNull(dataTag2);
        Objects.requireNonNull(op);
        return withBinaryOverloadUnchecked(dataTag1, dataTag2, op);
    }

    public OperatorSet<OT, DT, DC>
    withBinaryOverloads(Seq<DT> dataTags1, Seq<DT> dataTags2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        requireNonNullNestedSeq(List.of(dataTags1, dataTags2, ops));
        return withBinaryOverloadsUnchecked(dataTags1, dataTags2, ops);
    }

    public OperatorSet<OT, DT, DC>
    withBinaryOverloads(Seq<DT> dataTags1, DT dataTag2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        return withBinaryOverloads(dataTags1, List.of(dataTag2), ops);
    }

    public OperatorSet<OT, DT, DC>
    withBinaryOverloads(DT dataTag1, Seq<DT> dataTags2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        return withBinaryOverloads(List.of(dataTag1), dataTags2, ops);
    }

    public OperatorSet<OT, DT, DC>
    withBinaryOverloads(DT dataTag1, DT dataTag2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        return withBinaryOverloads(List.of(dataTag1), List.of(dataTag2), ops);
    }

    public OperatorSet<OT, DT, DC>
    withBinaryOverloads(Seq<DT> dataTags1, DT dataTag2, BinaryOperator<OT, DT, DC> op) {
        return withBinaryOverloads(dataTags1, List.of(dataTag2), List.of(op));
    }

    public OperatorSet<OT, DT, DC>
    withBinaryOverloads(DT dataTag1, Seq<DT> dataTags2, BinaryOperator<OT, DT, DC> op) {
        return withBinaryOverloads(List.of(dataTag1), dataTags2, List.of(op));
    }

    public OperatorSet<OT, DT, DC>
    withSymmetricBinaryOverload(DT dataTag1, DT dataTag2, BinaryOperator<OT, DT, DC> op) {
        Objects.requireNonNull(dataTag1);
        Objects.requireNonNull(dataTag2);
        Objects.requireNonNull(op);
        return withBinaryOverloadUnchecked(dataTag1, dataTag2, op)
                .withBinaryOverloadUnchecked(dataTag2, dataTag1, op);
    }

    public OperatorSet<OT, DT, DC>
    withSymmetricBinaryOverloads(Seq<DT> dataTags1, Seq<DT> dataTags2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        requireNonNullNestedSeq(List.of(dataTags1, dataTags2, ops));

        return withBinaryOverloadsUnchecked(dataTags1, dataTags2, ops)
                .withBinaryOverloadsUnchecked(dataTags2, dataTags1, ops);
    }

    public OperatorSet<OT, DT, DC>
    withSymmetricBinaryOverloads(Seq<DT> dataTags1, DT dataTag2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        return withSymmetricBinaryOverloads(dataTags1, List.of(dataTag2), ops);
    }

    public OperatorSet<OT, DT, DC>
    withSymmetricBinaryOverloads(DT dataTag1, Seq<DT> dataTags2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        return withSymmetricBinaryOverloads(List.of(dataTag1), dataTags2, ops);
    }

    public OperatorSet<OT, DT, DC>
    withSymmetricBinaryOverloads(DT dataTag1, DT dataTag2, Seq<BinaryOperator<OT, DT, DC>> ops) {
        return withSymmetricBinaryOverloads(List.of(dataTag1), List.of(dataTag2), ops);
    }

    public OperatorSet<OT, DT, DC>
    withSymmetricBinaryOverloads(Seq<DT> dataTags1, DT dataTag2, BinaryOperator<OT, DT, DC> op) {
        return withSymmetricBinaryOverloads(dataTags1, List.of(dataTag2), List.of(op));
    }

    public OperatorSet<OT, DT, DC>
    withSymmetricBinaryOverloads(DT dataTag1, Seq<DT> dataTags2, BinaryOperator<OT, DT, DC> op) {
        return withSymmetricBinaryOverloads(List.of(dataTag1), dataTags2, List.of(op));
    }

    public OperatorSet<OT, DT, DC> removeBinaryOperator(OT binOpTag) {
        return new OperatorSet<>(unaries, binaries.remove(binOpTag));
    }

    public OperatorSet<OT, DT, DC> removeUnaryOperator(OT unOpTag) {
        return new OperatorSet<>(unaries.remove(unOpTag), binaries);
    }
}
