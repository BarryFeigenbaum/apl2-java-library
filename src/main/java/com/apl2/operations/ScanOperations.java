package com.apl2.operations;

import com.apl2.types.*;
import com.apl2.functions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Scan operations for APL2 arrays.
 * Implements the APL2 scan operator (\\) that returns intermediate results of a reduction.
 */
public class ScanOperations implements Scan {

    /**
     * Scans an array using a dyadic operator, returning intermediate results.
     * Example: +\\ array returns cumulative sum
     */
    @Override
    public APLType scan(DyadicFunction operator, APLType array) {
        if (!(array instanceof ArrayType arr)) {
            throw new IllegalArgumentException("Scan requires an array");
        }
        return scan(arr, operator, null);
    }

    /**
     * Scans along a specific axis for multi-dimensional arrays.
     */
    public APLType scanAxis(DyadicFunction operator, ArrayType array, int axis) {
        return scan(array, operator, axis);
    }

    public static APLType scan(ArrayType array, DyadicFunction operator, Integer axis) {
        if (array.isEmpty()) {
            return array;
        }
        int rank = array.getRank();
        int normalizedAxis = normalizeAxis(axis, rank);
        int[] shape = array.getShape();
        int axisLength = shape[normalizedAxis];
        List<APLType> source = array.getElements();
        List<APLType> results = new ArrayList<>(source);

        int[] otherShape = removeAxis(shape, normalizedAxis);
        int laneCount = otherShape.length == 0 ? 1 : totalSize(otherShape);

        for (int lane = 0; lane < laneCount; lane++) {
            int[] laneIndex = otherShape.length == 0 ? new int[0] : toIndex(lane, otherShape);
            int[] sourceIndex = insertAxis(laneIndex, normalizedAxis, rank, 0);
            int firstFlat = toFlatIndex(sourceIndex, shape);
            APLType accumulator = source.get(firstFlat).deepCopy();
            results.set(firstFlat, accumulator.deepCopy());
            for (int i = 1; i < axisLength; i++) {
                sourceIndex[normalizedAxis] = i;
                int currentFlat = toFlatIndex(sourceIndex, shape);
                accumulator = operator.execute(accumulator, source.get(currentFlat));
                results.set(currentFlat, accumulator.deepCopy());
            }
        }

        return new ArrayType(results, shape);
    }

    public static APLType scanFirstAxis(ArrayType array, DyadicFunction operator) {
        return scan(array, operator, 0);
    }

    public static APLType scan(ArrayType array, BinaryOperator<APLType> function, Integer axis) {
        DyadicFunction operator = (left, right) -> function.apply(left, right);
        return scan(array, operator, axis);
    }

    public static APLType scanFirstAxis(ArrayType array, BinaryOperator<APLType> function) {
        DyadicFunction operator = (left, right) -> function.apply(left, right);
        return scan(array, operator, 0);
    }

    private static int normalizeAxis(Integer axis, int rank) {
        if (rank <= 0) {
            throw new IllegalArgumentException("Scan requires rank > 0");
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

    private static int[] removeAxis(int[] shape, int axis) {
        if (shape.length == 1) {
            return new int[0];
        }
        int[] result = new int[shape.length - 1];
        for (int i = 0, j = 0; i < shape.length; i++) {
            if (i != axis) {
                result[j++] = shape[i];
            }
        }
        return result;
    }

    private static int[] insertAxis(int[] index, int axis, int rank, int axisValue) {
        int[] result = new int[rank];
        for (int i = 0, j = 0; i < rank; i++) {
            if (i == axis) {
                result[i] = axisValue;
            } else {
                result[i] = index[j++];
            }
        }
        return result;
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
}
