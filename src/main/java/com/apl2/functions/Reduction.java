package com.apl2.functions;

import com.apl2.types.APLType;
import java.util.List;

/**
 * Represents a higher-order function that reduces an array using a dyadic operator.
 * Common in APL2 functional programming.
 */
@FunctionalInterface
public interface Reduction {
    /**
     * Reduces an array by applying a dyadic function across all elements.
     * @param operator the dyadic operator to apply
     * @param array the array to reduce
     * @return the reduced result
     */
    APLType reduce(DyadicFunction operator, APLType array);
}
