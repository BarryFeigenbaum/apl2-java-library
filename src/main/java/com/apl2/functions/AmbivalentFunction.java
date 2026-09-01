package com.apl2.functions;

import com.apl2.types.APLType;

/**
 * Represents an ambivalent APL2 function that can operate in both monadic and dyadic modes.
 */
public interface AmbivalentFunction extends MonadicFunction, DyadicFunction {
    /**
     * Gets the monadic implementation.
     */
    MonadicFunction getMonadic();

    /**
     * Gets the dyadic implementation.
     */
    DyadicFunction getDyadic();

    @Override
    default APLType execute(APLType right) {
        return getMonadic().execute(right);
    }

    @Override
    default APLType execute(APLType left, APLType right) {
        return getDyadic().execute(left, right);
    }
}
