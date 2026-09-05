package com.apl2.operations;

import com.apl2.APLRuntime;
import com.apl2.types.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Array operations and transformations for APL2.
 */
public class ArrayOperations {

    /**
     * Reshape an array to a new shape.
     */
    public static ArrayType reshape(ArrayType array, int... newShape) {
        return array.reshape(newShape);
    }

    /**
     * Flatten an array to 1-D.
     */
    public static ArrayType flatten(ArrayType array) {
        return array.flatten();
    }

    /**
     * Reverse an array along the first dimension.
     */
    public static ArrayType reverse(ArrayType array) {
        return reverse(array, 0);
    }

    /**
     * Reverse an array along an axis. Null defaults to the last axis.
     */
    public static ArrayType reverse(ArrayType array, Integer axis) {
        int normalizedAxis = normalizeAxis(axis, array.getRank());
        List<APLType> source = array.getElements();
        List<APLType> reversed = new ArrayList<>(source);
        int[] shape = array.getShape();
        int stride = strideForAxis(shape, normalizedAxis);
        int axisLength = shape[normalizedAxis];
        int blockSize = stride * axisLength;
        int total = source.size();
        int outerCount = total / blockSize;

        for (int outer = 0; outer < outerCount; outer++) {
            int base = outer * blockSize;
            for (int i = 0; i < axisLength; i++) {
                int reversedI = axisLength - 1 - i;
                for (int p = 0; p < stride; p++) {
                    int src = base + i * stride + p;
                    int dest = base + reversedI * stride + p;
                    reversed.set(dest, source.get(src).deepCopy());
                }
            }
        }
        return new ArrayType(reversed, shape);
    }

    /**
     * Rotate an array along the first dimension by n positions.
     */
    public static ArrayType rotate(ArrayType array, int n) {
        return rotate(array, n, 0);
    }

    /**
     * Rotate an array along an axis by n positions. Null defaults to the last axis.
     */
    public static ArrayType rotate(ArrayType array, int n, Integer axis) {
        List<APLType> source = array.getElements();
        int size = source.size();
        if (size == 0) {
            return array;
        }

        int normalizedAxis = normalizeAxis(axis, array.getRank());
        int[] shape = array.getShape();
        int stride = strideForAxis(shape, normalizedAxis);
        int axisLength = shape[normalizedAxis];
        int blockSize = stride * axisLength;
        int outerCount = size / blockSize;

        int shift = n % axisLength;
        if (shift < 0) {
            shift += axisLength;
        }

        List<APLType> rotated = new ArrayList<>(source);
        for (int outer = 0; outer < outerCount; outer++) {
            int base = outer * blockSize;
            for (int i = 0; i < axisLength; i++) {
                int srcI = (i - shift + axisLength) % axisLength;
                for (int p = 0; p < stride; p++) {
                    int src = base + srcI * stride + p;
                    int dest = base + i * stride + p;
                    rotated.set(dest, source.get(src).deepCopy());
                }
            }
        }

        return new ArrayType(rotated, shape);
    }

    /**
     * Transpose a 2-D array.
     */
    public static ArrayType transpose(ArrayType array) {
        if (array.getRank() != 2) {
            throw new IllegalArgumentException("Transpose only works on 2-D arrays");
        }
        return array.transpose();
    }

    /**
     * Concatenate two arrays along the first dimension.
     */
    public static ArrayType concatenate(ArrayType left, ArrayType right) {
        return concatenate(left, right, 0);
    }

    /**
     * Concatenate two arrays along an axis. Null defaults to the last axis.
     */
    public static ArrayType concatenate(ArrayType left, ArrayType right, Integer axis) {
        if (left.getRank() != right.getRank()) {
            throw new IllegalArgumentException("Concatenate requires arrays with the same rank");
        }

        int rank = left.getRank();
        int normalizedAxis = normalizeAxis(axis, rank);
        int[] leftShape = left.getShape();
        int[] rightShape = right.getShape();
        for (int i = 0; i < rank; i++) {
            if (i != normalizedAxis && leftShape[i] != rightShape[i]) {
                throw new IllegalArgumentException("Shapes are incompatible for concatenation along axis " + normalizedAxis);
            }
        }

        int[] newShape = leftShape.clone();
        newShape[normalizedAxis] += rightShape[normalizedAxis];
        int resultSize = totalSize(newShape);
        List<APLType> combined = new ArrayList<>(resultSize);

        List<APLType> leftElements = left.getElements();
        List<APLType> rightElements = right.getElements();
        for (int flat = 0; flat < resultSize; flat++) {
            int[] index = toIndex(flat, newShape);
            if (index[normalizedAxis] < leftShape[normalizedAxis]) {
                combined.add(leftElements.get(toFlatIndex(index, leftShape)).deepCopy());
            } else {
                index[normalizedAxis] -= leftShape[normalizedAxis];
                combined.add(rightElements.get(toFlatIndex(index, rightShape)).deepCopy());
            }
        }

        return new ArrayType(combined, newShape);
    }

    /**
     * Alias for dyadic comma.
     */
    public static ArrayType catenate(ArrayType left, ArrayType right, Integer axis) {
        return concatenate(left, right, axis);
    }

    /**
     * Reverse along first axis (monadic ⊖).
     */
    public static ArrayType reverseFirstAxis(ArrayType array) {
        return reverse(array, 0);
    }

    /**
     * Rotate along first axis (dyadic ⊖).
     */
    public static ArrayType rotateFirstAxis(ArrayType array, int n) {
        return rotate(array, n, 0);
    }

    /**
     * First element/cell (monadic ↑).
     */
    public static APLType first(ArrayType array) {
        if (array.isEmpty()) {
            throw new IllegalArgumentException("Cannot take first element of empty array");
        }
        if (array.getRank() == 1) {
            return array.getRawElement(0).deepCopy();
        }
        int[] shape = array.getShape();
        int cellSize = 1;
        for (int i = 1; i < shape.length; i++) {
            cellSize *= shape[i];
        }
        List<APLType> cell = new ArrayList<>(cellSize);
        List<APLType> elements = array.getElements();
        for (int i = 0; i < cellSize; i++) {
            cell.add(elements.get(i).deepCopy());
        }
        int[] cellShape = new int[shape.length - 1];
        System.arraycopy(shape, 1, cellShape, 0, cellShape.length);
        return new ArrayType(cell, cellShape);
    }

    /**
     * Split/drop first element (monadic ↓).
     */
    public static ArrayType split(ArrayType array) {
        if (array.isEmpty()) {
            return array;
        }
        if (array.getRank() == 1) {
            return drop(array, 1);
        }
        int[] shape = array.getShape();
        int cellSize = 1;
        for (int i = 1; i < shape.length; i++) {
            cellSize *= shape[i];
        }
        List<APLType> elements = array.getElements();
        List<APLType> remainder = new ArrayList<>();
        for (int i = cellSize; i < elements.size(); i++) {
            remainder.add(elements.get(i).deepCopy());
        }
        int[] newShape = shape.clone();
        newShape[0] = Math.max(0, shape[0] - 1);
        return new ArrayType(remainder, newShape);
    }

    /**
     * Transpose with explicit axis permutation.
     */
    public static ArrayType transposeAxes(ArrayType array, int... axes) {
        int rank = array.getRank();
        if (axes.length != rank) {
            throw new IllegalArgumentException("Axis permutation length must match rank");
        }
        boolean[] seen = new boolean[rank];
        for (int i = 0; i < rank; i++) {
            int axis = axes[i];
            if (axis < 0 || axis >= rank || seen[axis]) {
                throw new IllegalArgumentException("Invalid axis permutation");
            }
            seen[axis] = true;
        }

        int[] oldShape = array.getShape();
        int[] newShape = new int[rank];
        for (int i = 0; i < rank; i++) {
            newShape[i] = oldShape[axes[i]];
        }

        List<APLType> source = array.getElements();
        List<APLType> transposed = new ArrayList<>(source.size());
        for (int flat = 0; flat < source.size(); flat++) {
            int[] newIndex = toIndex(flat, newShape);
            int[] oldIndex = new int[rank];
            for (int i = 0; i < rank; i++) {
                oldIndex[axes[i]] = newIndex[i];
            }
            transposed.add(source.get(toFlatIndex(oldIndex, oldShape)).deepCopy());
        }
        return new ArrayType(transposed, newShape);
    }

    private static int normalizeAxis(Integer axis, int rank) {
        if (rank <= 0) {
            throw new IllegalArgumentException("Axis operations require array rank greater than zero");
        }
        int normalized = axis == null ? rank - 1 : axis;
        if (normalized < 0) {
            normalized += rank;
        }
        if (normalized < 0 || normalized >= rank) {
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
        return normalized;
    }

    private static int strideForAxis(int[] shape, int axis) {
        int stride = 1;
        for (int i = axis + 1; i < shape.length; i++) {
            stride *= shape[i];
        }
        return stride;
    }

    private static int totalSize(int[] shape) {
        int size = 1;
        for (int dim : shape) {
            size *= dim;
        }
        return size;
    }

    private static int[] toIndex(int flat, int[] shape) {
        int[] index = new int[shape.length];
        int remaining = flat;
        for (int i = shape.length - 1; i >= 0; i--) {
            index[i] = remaining % shape[i];
            remaining /= shape[i];
        }
        return index;
    }

    private static int toFlatIndex(int[] index, int[] shape) {
        int flat = 0;
        int multiplier = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            flat += index[i] * multiplier;
            multiplier *= shape[i];
        }
        return flat;
    }

    /**
     * Ravel (flatten) an array.
     */
    public static ArrayType ravel(ArrayType array) {
        return flatten(array);
    }

    /**
     * Membership (dyadic ∊).
     */
    public static ArrayType membership(ArrayType left, ArrayType right) {
        List<APLType> rightElements = right.getElements();
        List<APLType> result = new ArrayList<>(left.size());
        for (APLType value : left.getElements()) {
            result.add(new BooleanType(rightElements.contains(value)));
        }
        return new ArrayType(result);
    }

    /**
     * Enclose (monadic ⊂).
     */
    public static ArrayType enclose(APLType value) {
        return new ArrayType(List.of(value.deepCopy()));
    }

    /**
     * Disclose (monadic ⊃).
     */
    public static APLType disclose(APLType value) {
        if (value instanceof ArrayType array && !array.isEmpty()) {
            return array.getRawElement(0).deepCopy();
        }
        return value.deepCopy();
    }

    /**
     * Pick (dyadic ⊃) using the current runtime index origin.
     */
    public static APLType pick(APLType selector, APLType value) {
        if (!(value instanceof ArrayType array)) {
            throw new IllegalArgumentException("Pick requires right argument to be an array");
        }
        if (!(selector instanceof Scalar scalar)) {
            throw new IllegalArgumentException("Pick requires scalar selector");
        }
        double numericIndex = scalar.toNumeric();
        if (numericIndex != Math.floor(numericIndex)) {
            throw new IllegalArgumentException("Pick selector must be an integer");
        }
        if (numericIndex < Integer.MIN_VALUE || numericIndex > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Pick selector out of range");
        }
        int index = (int) numericIndex;
        return array.getElement(index).deepCopy();
    }

    /**
     * Partition (dyadic ⊂). True values start a new partition.
     */
    public static ArrayType partition(ArrayType marks, ArrayType values) {
        if (marks.size() != values.size()) {
            throw new IllegalArgumentException("Partition requires marker and value arrays of equal size");
        }
        List<APLType> partitions = new ArrayList<>();
        List<APLType> current = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (((Scalar) marks.getRawElement(i)).toBoolean() && !current.isEmpty()) {
                partitions.add(new ArrayType(new ArrayList<>(current)));
                current.clear();
            }
            current.add(values.getRawElement(i).deepCopy());
        }
        if (!current.isEmpty()) {
            partitions.add(new ArrayType(current));
        }
        return new ArrayType(partitions);
    }

    /**
     * Grade up (monadic ⍋).
     */
    public static ArrayType gradeUp(ArrayType array) {
        return grade(array, true);
    }

    /**
     * Grade down (monadic ⍒).
     */
    public static ArrayType gradeDown(ArrayType array) {
        return grade(array, false);
    }

    /**
     * Sort by left key (dyadic ⍋).
     */
    public static ArrayType sortBy(ArrayType keys, ArrayType values) {
        return reorderByGrade(keys, values, true);
    }

    /**
     * Reverse sort by left key (dyadic ⍒).
     */
    public static ArrayType reverseSortBy(ArrayType keys, ArrayType values) {
        return reorderByGrade(keys, values, false);
    }

    private static ArrayType grade(ArrayType array, boolean ascending) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            indices.add(i);
        }
        indices.sort((a, b) -> {
            APLType left = array.getRawElement(a);
            APLType right = array.getRawElement(b);
            if (!(left instanceof Scalar leftScalar) || !(right instanceof Scalar rightScalar)) {
                throw new IllegalArgumentException("Grade only supports scalar array elements");
            }
            double da = leftScalar.toNumeric();
            double db = rightScalar.toNumeric();
            int cmp = Double.compare(da, db);
            return ascending ? cmp : -cmp;
        });

        List<APLType> result = new ArrayList<>(indices.size());
        for (Integer index : indices) {
            result.add(new IntegerType(APLRuntime.getInstance().toOriginIndex(index)));
        }
        return new ArrayType(result);
    }

    private static ArrayType reorderByGrade(ArrayType keys, ArrayType values, boolean ascending) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("Sort requires key and value arrays of equal size");
        }
        ArrayType grade = grade(keys, ascending);
        List<APLType> sorted = new ArrayList<>(values.size());
        for (APLType indexValue : grade.getElements()) {
            int idx = APLRuntime.getInstance().toZeroBasedIndex((int) ((IntegerType) indexValue).getValue(), values.size());
            sorted.add(values.getRawElement(idx).deepCopy());
        }
        return new ArrayType(sorted, values.getShape());
    }

    /**
     * Take the first n elements of an array.
     */
    public static ArrayType take(ArrayType array, int n) {
        List<APLType> elements = array.getElements();
        int size = Math.min(n, elements.size());
        List<APLType> taken = new ArrayList<>(elements.subList(0, Math.max(0, size)));
        
        // Pad with zeros if necessary
        if (n > elements.size()) {
            for (int i = elements.size(); i < n; i++) {
                taken.add(new IntegerType(0));
            }
        }
        
        int[] shape = array.getShape();
        shape[0] = n;
        return new ArrayType(taken, shape);
    }

    /**
     * Drop the first n elements of an array.
     */
    public static ArrayType drop(ArrayType array, int n) {
        List<APLType> elements = array.getElements();
        int size = Math.max(0, elements.size() - n);
        int start = Math.min(n, elements.size());
        
        List<APLType> dropped = new ArrayList<>(elements.subList(start, elements.size()));
        
        int[] shape = array.getShape();
        shape[0] = size;
        return new ArrayType(dropped, shape);
    }

    /**
     * Mix (combine) nested arrays into a single array.
     */
    public static ArrayType mix(ArrayType array) {
        // If array contains arrays, flatten one level
        List<APLType> result = new ArrayList<>();
        for (APLType elem : array.getElements()) {
            if (elem instanceof ArrayType arr) {
                result.addAll(arr.getElements());
            } else {
                result.add(elem);
            }
        }
        return new ArrayType(result);
    }

    /**
     * Count the number of non-zero elements.
     */
    public static long count(ArrayType array) {
        return array.getElements().stream()
            .filter(elem -> {
                if (elem instanceof Scalar s) {
                    return s.toBoolean();
                }
                return true;
            })
            .count();
    }

    /**
     * Sum all elements in an array.
     */
    public static APLType sum(ArrayType array) {
        if (array.isEmpty()) {
            return new IntegerType(0);
        }
        
        APLType result = array.getRawElement(0).deepCopy();
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.add(result, array.getRawElement(i));
        }
        return result;
    }

    /**
     * Product of all elements in an array.
     */
    public static APLType product(ArrayType array) {
        if (array.isEmpty()) {
            return new IntegerType(1);
        }
        
        APLType result = array.getRawElement(0).deepCopy();
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.multiply(result, array.getRawElement(i));
        }
        return result;
    }

    /**
     * Maximum element in an array.
     */
    public static APLType maximum(ArrayType array) {
        if (array.isEmpty()) {
            throw new IllegalArgumentException("Cannot find maximum of empty array");
        }
        
        APLType result = array.getRawElement(0);
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.max(result, array.getRawElement(i));
        }
        return result;
    }

    /**
     * Minimum element in an array.
     */
    public static APLType minimum(ArrayType array) {
        if (array.isEmpty()) {
            throw new IllegalArgumentException("Cannot find minimum of empty array");
        }
        
        APLType result = array.getRawElement(0);
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.min(result, array.getRawElement(i));
        }
        return result;
    }
}
