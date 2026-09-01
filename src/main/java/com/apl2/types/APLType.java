package com.apl2.types;

import java.util.Objects;

/**
 * Base interface for all APL2 data types.
 * Represents the fundamental type hierarchy in APL2.
 */
public sealed interface APLType permits
        BooleanType,
        IntegerType,
        FloatingPointType,
        ComplexType,
        CharacterType,
        StringType,
        ArrayType {

    /**
     * Returns the type name for debugging and display purposes.
     */
    String getTypeName();

    /**
     * Returns a deep copy of this type.
     */
    APLType deepCopy();

    /**
     * Returns the rank (number of dimensions) of this type.
     * Scalars have rank 0.
     */
    int getRank();

    /**
     * Returns the shape (dimensions) of this type.
     * For scalars, returns an empty array.
     */
    int[] getShape();
}
