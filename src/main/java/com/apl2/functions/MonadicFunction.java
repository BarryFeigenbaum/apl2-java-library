package com.apl2.functions;

import com.apl2.types.APLType;

/**
 * Represents a monadic (1-argument) APL2 function.
 * These are functions that take a single right operand and return a result.
 */
@FunctionalInterface
public interface MonadicFunction {
    /**
     * Executes the monadic function.
     * @param right the right operand
     * @return the result of the function
     */
    APLType execute(APLType right);
}
