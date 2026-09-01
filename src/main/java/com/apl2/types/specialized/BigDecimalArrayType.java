package com.apl2.types.specialized;

import com.apl2.types.APLType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

/**
 * Specialized array type for homogeneous BigDecimal arrays.
 * Uses BigDecimal[] for arbitrary-precision decimal arrays.
 * Supports nested arrays up to depth 15.
 */
public final class BigDecimalArrayType implements APLType {
    private final BigDecimal[] data;
    private final int[] shape;
    private final int rank;
    private final int depth;
    private static final int MAX_DEPTH = 15;

    /**
     * Creates a 1-D BigDecimal array.
     */
    public BigDecimalArrayType(BigDecimal... data) {
        this.data = Objects.requireNonNull(data, "Data cannot be null").clone();
        this.shape = new int[]{data.length};
        this.rank = 1;
        this.depth = 1;
    }

    /**
     * Creates a multi-dimensional BigDecimal array with specified shape.
     */
    public BigDecimalArrayType(BigDecimal[] data, int... shape) {
        Objects.requireNonNull(shape, "Shape cannot be null");
        if (shape.length == 0) {
            throw new IllegalArgumentException("Shape must have at least one dimension");
        }

        int expectedSize = 1;
        for (int dim : shape) {
            if (dim < 0) throw new IllegalArgumentException("Shape dimensions must be non-negative");
            expectedSize *= dim;
        }

        if (data.length != expectedSize) {
            throw new IllegalArgumentException(
                String.format("Data size %d doesn't match shape %s", data.length, Arrays.toString(shape))
            );
        }

        this.data = Objects.requireNonNull(data, "Data cannot be null").clone();
        this.shape = shape.clone();
        this.rank = shape.length;
        this.depth = 1;
    }

    public BigDecimal[] getData() {
        return data.clone();
    }

    public BigDecimal getElement(int index) {
        if (index < 0 || index >= data.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + data.length);
        }
        return data[index];
    }

    /**
     * Gets element at multi-dimensional index.
     */
    public BigDecimal getElement(int... indices) {
        if (indices.length != rank) {
            throw new IllegalArgumentException(
                String.format("Expected %d indices, got %d", rank, indices.length)
            );
        }
        int flatIndex = toFlatIndex(indices);
        return data[flatIndex];
    }

    private int toFlatIndex(int... indices) {
        int flatIndex = 0;
        int multiplier = 1;
        for (int i = rank - 1; i >= 0; i--) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new IndexOutOfBoundsException(
                    String.format("Index [%s] out of bounds for shape %s",
                        Arrays.toString(indices), Arrays.toString(shape))
                );
            }
            flatIndex += indices[i] * multiplier;
            multiplier *= shape[i];
        }
        return flatIndex;
    }

    @Override
    public String getTypeName() {
        return "BigDecimalArray";
    }

    @Override
    public APLType deepCopy() {
        return new BigDecimalArrayType(data.clone(), shape.clone());
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public int[] getShape() {
        return shape.clone();
    }

    public int getDepth() {
        return depth;
    }

    public int size() {
        return data.length;
    }

    public boolean isEmpty() {
        return data.length == 0;
    }

    /**
     * Reshapes the array to a new shape if element count matches.
     */
    public BigDecimalArrayType reshape(int... newShape) {
        int newSize = 1;
        for (int dim : newShape) {
            if (dim < 0) throw new IllegalArgumentException("Shape dimensions must be non-negative");
            newSize *= dim;
        }
        if (newSize != size()) {
            throw new IllegalArgumentException(
                String.format("Cannot reshape array of size %d to shape %s", size(), Arrays.toString(newShape))
            );
        }
        return new BigDecimalArrayType(data.clone(), newShape);
    }

    /**
     * Flattens the array to a 1-D array.
     */
    public BigDecimalArrayType flatten() {
        if (rank == 1) {
            return new BigDecimalArrayType(data.clone());
        }
        return new BigDecimalArrayType(data.clone());
    }

    /**
     * Transposes the array (for 2-D arrays).
     */
    public BigDecimalArrayType transpose() {
        if (rank != 2) {
            throw new IllegalArgumentException("Transpose only supported for 2-D arrays, got rank " + rank);
        }
        int rows = shape[0];
        int cols = shape[1];
        BigDecimal[] transposed = new BigDecimal[data.length];
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                transposed[j * rows + i] = data[i * cols + j];
            }
        }
        return new BigDecimalArrayType(transposed, cols, rows);
    }

    @Override
    public String toString() {
        return "BigDecimalArray" + Arrays.toString(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigDecimalArrayType that = (BigDecimalArrayType) o;
        return Arrays.equals(data, that.data) && Arrays.equals(shape, that.shape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(data), Arrays.hashCode(shape));
    }
}
