package com.apl2.operations;

import com.apl2.types.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Additional primitive APL2 operations.
 */
public final class PrimitiveOperations {

    private PrimitiveOperations() {
    }

    public static APLType logicalAnd(APLType left, APLType right) {
        return new BooleanType(toBoolean(left) && toBoolean(right));
    }

    public static APLType logicalOr(APLType left, APLType right) {
        return new BooleanType(toBoolean(left) || toBoolean(right));
    }

    public static APLType logicalNand(APLType left, APLType right) {
        return new BooleanType(!(toBoolean(left) && toBoolean(right)));
    }

    public static APLType logicalNor(APLType left, APLType right) {
        return new BooleanType(!(toBoolean(left) || toBoolean(right)));
    }

    public static APLType logicalNot(APLType value) {
        if (value instanceof ArrayType array) {
            List<APLType> result = new ArrayList<>(array.size());
            for (APLType element : array.getElements()) {
                result.add(logicalNot(element));
            }
            return new ArrayType(result, array.getShape());
        }
        return new BooleanType(!toBoolean(value));
    }

    /**
     * Dyadic ~ (without): left minus elements present in right.
     */
    public static APLType without(APLType left, APLType right) {
        if (!(left instanceof ArrayType leftArray) || !(right instanceof ArrayType rightArray)) {
            throw new IllegalArgumentException("Without requires arrays");
        }
        List<APLType> rightElements = rightArray.getElements();
        List<APLType> result = new ArrayList<>();
        for (APLType element : leftArray.getElements()) {
            if (!rightElements.contains(element)) {
                result.add(element.deepCopy());
            }
        }
        return new ArrayType(result);
    }

    /**
     * Dyadic iota: indices of right values in left.
     */
    public static APLType iota(APLType operand) {
        return MathOperations.iota(operand);
    }

    /**
     * Dyadic iota: indices of right values in left.
     */
    public static APLType indexOf(APLType left, APLType right) {
        if (!(left instanceof ArrayType leftArray)) {
            throw new IllegalArgumentException("Left argument of indexOf must be an array");
        }
        if (right instanceof ArrayType rightArray) {
            List<APLType> indices = new ArrayList<>(rightArray.size());
            List<APLType> leftElements = leftArray.getElements();
            long defaultIndex = leftElements.size();
            for (APLType value : rightArray.getElements()) {
                int idx = leftElements.indexOf(value);
                indices.add(new IntegerType(idx >= 0 ? idx : defaultIndex));
            }
            return new ArrayType(indices);
        }
        int idx = leftArray.getElements().indexOf(right);
        return new IntegerType(idx >= 0 ? idx : leftArray.size());
    }

    /**
     * Monadic where (⍸): indices where argument is true/non-zero.
     */
    public static APLType indicesWhere(APLType value) {
        if (!(value instanceof ArrayType array)) {
            if (toBoolean(value)) {
                return new ArrayType(List.of(new IntegerType(0)));
            }
            return new ArrayType(List.of());
        }
        List<APLType> indices = new ArrayList<>();
        List<APLType> elements = array.getElements();
        for (int i = 0; i < elements.size(); i++) {
            if (toBoolean(elements.get(i))) {
                indices.add(new IntegerType(i));
            }
        }
        return new ArrayType(indices);
    }

    /**
     * Dyadic where (⍸): select values by boolean mask.
     */
    public static APLType where(APLType left, APLType right) {
        if (!(left instanceof ArrayType mask) || !(right instanceof ArrayType values)) {
            throw new IllegalArgumentException("Where requires array mask and array values");
        }
        if (mask.size() != values.size()) {
            throw new IllegalArgumentException("Where requires mask and values arrays of equal size");
        }
        List<APLType> selected = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (toBoolean(mask.getElement(i))) {
                selected.add(values.getElement(i).deepCopy());
            }
        }
        return new ArrayType(selected);
    }

    /**
     * Monadic roll (?): random integer in [1, n].
     */
    public static APLType roll(APLType operand) {
        int n = toPositiveInt(operand, "Roll argument");
        return new IntegerType(ThreadLocalRandom.current().nextInt(1, n + 1));
    }

    /**
     * Dyadic deal (?): left unique values in [1, right].
     */
    public static APLType deal(APLType left, APLType right) {
        int count = toNonNegativeInt(left, "Deal count");
        int upperBound = toPositiveInt(right, "Deal upper bound");
        if (count > upperBound) {
            throw new IllegalArgumentException("Deal count cannot exceed upper bound");
        }
        List<Integer> values = new ArrayList<>(upperBound);
        for (int i = 1; i <= upperBound; i++) {
            values.add(i);
        }
        Collections.shuffle(values, ThreadLocalRandom.current());
        List<APLType> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(new IntegerType(values.get(i)));
        }
        return new ArrayType(result);
    }

    public static APLType format(APLType operand) {
        return new StringType(operand.toString());
    }

    public static APLType formatWithPattern(APLType left, APLType right) {
        String pattern = left instanceof StringType s ? s.getValue() : left.toString();
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        if (right instanceof Scalar scalar) {
            return new StringType(decimalFormat.format(scalar.toNumeric()));
        }
        return new StringType(right.toString());
    }

    private static boolean toBoolean(APLType value) {
        if (value instanceof Scalar scalar) {
            return scalar.toBoolean();
        }
        if (value instanceof ArrayType array) {
            return !array.isEmpty();
        }
        throw new IllegalArgumentException("Cannot convert value to boolean");
    }

    private static int toPositiveInt(APLType value, String name) {
        int intValue = toNonNegativeInt(value, name);
        if (intValue <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return intValue;
    }

    private static int toNonNegativeInt(APLType value, String name) {
        if (!(value instanceof Scalar scalar)) {
            throw new IllegalArgumentException(name + " must be scalar");
        }
        double numeric = scalar.toNumeric();
        if (numeric < 0 || numeric != Math.floor(numeric)) {
            throw new IllegalArgumentException(name + " must be a non-negative integer");
        }
        return (int) numeric;
    }
}
