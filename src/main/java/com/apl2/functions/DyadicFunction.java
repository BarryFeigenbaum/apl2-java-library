package com.apl2.functions;

import com.apl2.types.APLType;

/**
 * Represents a dyadic (2-argument) APL2 function.
 * These are functions that take left and right operands and return a result.
 */
@FunctionalInterface
public interface DyadicFunction {
    /**
     * Executes the dyadic function.
     * @param left the left operand
     * @param right the right operand
     * @return the result of the function
     */
    APLType execute(APLType left, APLType right);
}
