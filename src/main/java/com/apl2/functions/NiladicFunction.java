package com.apl2.functions;

import com.apl2.types.APLType;

/**
 * Represents a niladic (0-argument) APL2 function.
 * These are functions that take no arguments and return a result.
 */
@FunctionalInterface
public interface NiladicFunction {
    /**
     * Executes the niladic function.
     * @return the result of the function
     */
    APLType execute();
}
