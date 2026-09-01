package com.apl2.functions;

import com.apl2.types.APLType;

/**
 * Represents a scan operation that applies a dyadic operator across an array,
 * returning intermediate results (unlike reduction which returns only the final result).
 */
@FunctionalInterface
public interface Scan {
    /**
     * Scans an array by applying a dyadic function across all elements.
     * Returns an array of intermediate results.
     * @param operator the dyadic operator to apply
     * @param array the array to scan
     * @return an array of intermediate results
     */
    APLType scan(DyadicFunction operator, APLType array);
}
