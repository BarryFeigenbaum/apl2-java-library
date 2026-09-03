package com.apl2.operations;

import com.apl2.types.*;
import com.apl2.functions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Reduction operations for APL2 arrays.
 * Implements the APL2 reduction operator (/) that applies a dyadic function across array elements.
 */
public class ReductionOperations implements Reduction {

    /**
     * Reduces an array using a dyadic operator.
     * Example: +/ array sums all elements
     */
    @Override
    public APLType reduce(DyadicFunction operator, APLType array) {
        if (!(array instanceof ArrayType arr)) {
            throw new IllegalArgumentException("Reduction requires an array");
        }
        return reduce(arr, operator, null);
    }

    /**
     * Reduces along a specific axis for multi-dimensional arrays.
     */
    public APLType reduceAxis(DyadicFunction operator, ArrayType array, int axis) {
        return reduce(array, operator, axis);
    }

    public static APLType reduce(ArrayType array, DyadicFunction operator, Integer axis) {
        if (array.isEmpty()) {
            throw new IllegalArgumentException("Cannot reduce empty array");
        }
        int rank = array.getRank();
        int normalizedAxis = normalizeAxis(axis, rank);
        int[] shape = array.getShape();
        int axisLength = shape[normalizedAxis];
        if (axisLength == 0) {
            throw new IllegalArgumentException("Cannot reduce along zero-length axis");
        }

        int[] resultShape = removeAxis(shape, normalizedAxis);
        int resultSize = resultShape.length == 0 ? 1 : totalSize(resultShape);
        List<APLType> result = new ArrayList<>(resultSize);
        List<APLType> source = array.getElements();

        for (int flat = 0; flat < resultSize; flat++) {
            int[] resultIndex = resultShape.length == 0 ? new int[0] : toIndex(flat, resultShape);
            int[] sourceIndex = insertAxis(resultIndex, normalizedAxis, rank, 0);
            APLType accumulator = source.get(toFlatIndex(sourceIndex, shape)).deepCopy();
            for (int i = 1; i < axisLength; i++) {
                sourceIndex[normalizedAxis] = i;
                accumulator = operator.execute(accumulator, source.get(toFlatIndex(sourceIndex, shape)));
            }
            result.add(accumulator);
        }

        if (resultShape.length == 0) {
            return result.get(0);
        }
        return new ArrayType(result, resultShape);
    }

    public static APLType reduceFirstAxis(ArrayType array, DyadicFunction operator) {
        return reduce(array, operator, 0);
    }

    public static APLType reduce(ArrayType array, BinaryOperator<APLType> function, Integer axis) {
        return reduce(array, (DyadicFunction) function::apply, axis);
    }

    public static APLType reduceFirstAxis(ArrayType array, BinaryOperator<APLType> function) {
        return reduce(array, (DyadicFunction) function::apply, 0);
    }

    private static int normalizeAxis(Integer axis, int rank) {
        if (rank <= 0) {
            throw new IllegalArgumentException("Reduction requires rank > 0");
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
