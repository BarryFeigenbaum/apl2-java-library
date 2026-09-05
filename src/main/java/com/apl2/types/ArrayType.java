package com.apl2.types;

import com.apl2.APLRuntime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a multi-dimensional array in APL2.
 * Supports nested arrays up to a depth of 15 with mixed types.
 */
public final class ArrayType implements APLType {
    private final List<APLType> elements;
    private final int[] shape;
    private final int rank;
    private final int depth;
    private static final int MAX_DEPTH = 15;

    /**
     * Creates a 1-D array from elements.
     */
    public ArrayType(List<APLType> elements) {
        this.elements = new ArrayList<>(Objects.requireNonNull(elements, "Elements cannot be null"));
        this.shape = new int[]{elements.size()};
        this.rank = 1;
        this.depth = calculateDepth();
        validateDepth();
    }

    /**
     * Creates a multi-dimensional array with specified shape.
     */
    public ArrayType(List<APLType> elements, int... shape) {
        Objects.requireNonNull(shape, "Shape cannot be null");
        if (shape.length == 0) {
            throw new IllegalArgumentException("Shape must have at least one dimension");
        }

        int expectedSize = 1;
        for (int dim : shape) {
            if (dim < 0) throw new IllegalArgumentException("Shape dimensions must be non-negative");
            expectedSize *= dim;
        }

        if (elements.size() != expectedSize) {
            throw new IllegalArgumentException(
                String.format("Elements size %d doesn't match shape %s", 
                    elements.size(), Arrays.toString(shape))
            );
        }

        this.elements = new ArrayList<>(elements);
        this.shape = shape.clone();
        this.rank = shape.length;
        this.depth = calculateDepth();
        validateDepth();
    }

    private int calculateDepth() {
        if (elements.isEmpty()) {
            return 1;
        }
        if (!(elements.get(0) instanceof ArrayType)) {
            return 1;
        }
        int maxChildDepth = 0;
        for (APLType elem : elements) {
            if (elem instanceof ArrayType arr) {
                maxChildDepth = Math.max(maxChildDepth, arr.depth);
            }
        }
        return maxChildDepth + 1;
    }

    private void validateDepth() {
        if (depth > MAX_DEPTH) {
            throw new IllegalArgumentException(
                String.format("Array nesting depth %d exceeds maximum allowed depth of %d", depth, MAX_DEPTH)
            );
        }
    }

    public List<APLType> getElements() {
        return new ArrayList<>(elements);
    }

    public APLType getRawElement(int index) {
        if (index < 0 || index >= elements.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + elements.size());
        }
        return elements.get(index);
    }

    public APLType getElement(int index) {
        return getRawElement(APLRuntime.getInstance().toZeroBasedIndex(index, elements.size()));
    }

    /**
     * Gets element at multi-dimensional index.
     */
    public APLType getElement(int... indices) {
        if (indices.length != rank) {
            throw new IllegalArgumentException(
                String.format("Expected %d indices, got %d", rank, indices.length)
            );
        }
        int flatIndex = toFlatIndex(APLRuntime.getInstance().toZeroBasedIndices(indices, shape));
        return elements.get(flatIndex);
    }

    /**
     * Converts multi-dimensional index to flat index.
     */
    private int toFlatIndex(int... indices) {
        int flatIndex = 0;
        int multiplier = 1;
        for (int i = rank - 1; i >= 0; i--) {
            flatIndex += indices[i] * multiplier;
            multiplier *= shape[i];
        }
        return flatIndex;
    }

    @Override
    public String getTypeName() {
        return "Array";
    }

    @Override
    public APLType deepCopy() {
        List<APLType> copiedElements = new ArrayList<>();
        for (APLType elem : elements) {
            copiedElements.add(elem.deepCopy());
        }
        return new ArrayType(copiedElements, shape.clone());
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
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    /**
     * Reshapes the array to a new shape if element count matches.
     */
    public ArrayType reshape(int... newShape) {
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
        return new ArrayType(elements, newShape);
    }

    /**
     * Flattens the array to a 1-D array.
     */
    public ArrayType flatten() {
        if (rank == 1) {
            return new ArrayType(elements);
        }
        List<APLType> flattened = new ArrayList<>(elements);
        return new ArrayType(flattened);
    }

    /**
     * Transposes the array (for 2-D arrays).
     */
    public ArrayType transpose() {
        if (rank != 2) {
            throw new IllegalArgumentException("Transpose only supported for 2-D arrays, got rank " + rank);
        }
        int rows = shape[0];
        int cols = shape[1];
        List<APLType> transposed = new ArrayList<>();
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                transposed.add(elements.get(i * cols + j));
            }
        }
        return new ArrayType(transposed, cols, rows);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayType arrayType = (ArrayType) o;
        return Objects.equals(elements, arrayType.elements) &&
               Arrays.equals(shape, arrayType.shape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements, Arrays.hashCode(shape));
    }
}
