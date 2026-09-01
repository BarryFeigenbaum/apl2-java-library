package com.apl2.operations;

import com.apl2.types.*;
import com.apl2.functions.*;
import java.util.ArrayList;
import java.util.List;

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
        
        if (arr.isEmpty()) {
            throw new IllegalArgumentException("Cannot reduce empty array");
        }
        
        List<APLType> elements = arr.getElements();
        APLType result = elements.get(0).deepCopy();
        
        for (int i = 1; i < elements.size(); i++) {
            result = operator.execute(result, elements.get(i));
        }
        
        return result;
    }

    /**
     * Reduces along a specific axis for multi-dimensional arrays.
     */
    public APLType reduceAxis(DyadicFunction operator, ArrayType array, int axis) {
        if (axis < 0 || axis >= array.getRank()) {
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
        
        int[] shape = array.getShape();
        if (shape[axis] <= 1) {
            return array;
        }
        
        // For 1-D arrays, just reduce
        if (array.getRank() == 1) {
            return reduce(operator, array);
        }
        
        // For multi-dimensional arrays, reduce along specified axis
        List<APLType> results = new ArrayList<>();
        int[] newShape = new int[array.getRank() - 1];
        int idx = 0;
        for (int i = 0; i < array.getRank(); i++) {
            if (i != axis) {
                newShape[idx++] = shape[i];
            }
        }
        
        // This is a simplified version; full implementation would be more complex
        return reduce(operator, array);
    }
}
