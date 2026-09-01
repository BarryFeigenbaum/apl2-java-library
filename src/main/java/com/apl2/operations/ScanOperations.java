package com.apl2.operations;

import com.apl2.types.*;
import com.apl2.functions.*;
import java.util.ArrayList;
import java.util.List;

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
        
        if (arr.isEmpty()) {
            return arr;
        }
        
        List<APLType> elements = arr.getElements();
        List<APLType> results = new ArrayList<>();
        
        APLType accumulator = elements.get(0).deepCopy();
        results.add(accumulator);
        
        for (int i = 1; i < elements.size(); i++) {
            accumulator = operator.execute(accumulator, elements.get(i));
            results.add(accumulator.deepCopy());
        }
        
        int[] shape = arr.getShape();
        return new ArrayType(results, shape);
    }

    /**
     * Scans along a specific axis for multi-dimensional arrays.
     */
    public APLType scanAxis(DyadicFunction operator, ArrayType array, int axis) {
        if (axis < 0 || axis >= array.getRank()) {
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
        
        // For 1-D arrays, just scan
        if (array.getRank() == 1) {
            return scan(operator, array);
        }
        
        // For multi-dimensional arrays, scan along specified axis
        // This is a simplified version; full implementation would be more complex
        return scan(operator, array);
    }
}
