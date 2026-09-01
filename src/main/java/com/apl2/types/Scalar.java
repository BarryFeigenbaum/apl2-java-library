package com.apl2.types;

/**
 * Base interface for all scalar (0-rank) APL2 types.
 */
public sealed interface Scalar extends APLType permits
        BooleanType,
        IntegerType,
        FloatingPointType,
        ComplexType,
        CharacterType,
        StringType {

    @Override
    default int getRank() {
        return 0;
    }

    @Override
    default int[] getShape() {
        return new int[0];
    }

    /**
     * Returns the numeric value if applicable.
     * Throws if type doesn't support numeric conversion.
     */
    double toNumeric();

    /**
     * Returns the boolean value if applicable.
     * For numeric types: 0 → false, non-zero → true
     */
    boolean toBoolean();

    /**
     * Returns the character representation.
     */
    char toCharacter();
}
