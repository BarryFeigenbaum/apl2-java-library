package com.apl2.operations;

import com.apl2.types.*;
import com.apl2.types.specialized.*;
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
        List<APLType> elements = new ArrayList<>(array.getElements());
        java.util.Collections.reverse(elements);
        int[] shape = array.getShape();
        return new ArrayType(elements, shape);
    }

    /**
     * Rotate an array along the first dimension by n positions.
     */
    public static ArrayType rotate(ArrayType array, int n) {
        List<APLType> elements = new ArrayList<>(array.getElements());
        int size = elements.size();
        if (size == 0) return array;
        
        n = n % size;
        if (n < 0) n += size;
        
        List<APLType> rotated = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            rotated.add(elements.get((i - n + size) % size));
        }
        
        int[] shape = array.getShape();
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
        List<APLType> combined = new ArrayList<>();
        combined.addAll(left.getElements());
        combined.addAll(right.getElements());
        
        int[] lShape = left.getShape();
        int newFirstDim = lShape[0] + right.getShape()[0];
        int[] newShape = lShape.clone();
        newShape[0] = newFirstDim;
        
        return new ArrayType(combined, newShape);
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
     * Ravel (flatten) an array.
     */
    public static ArrayType ravel(ArrayType array) {
        return flatten(array);
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
        
        APLType result = array.getElement(0).deepCopy();
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.add(result, array.getElement(i));
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
        
        APLType result = array.getElement(0).deepCopy();
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.multiply(result, array.getElement(i));
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
        
        APLType result = array.getElement(0);
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.max(result, array.getElement(i));
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
        
        APLType result = array.getElement(0);
        for (int i = 1; i < array.size(); i++) {
            result = MathOperations.min(result, array.getElement(i));
        }
        return result;
    }
}
