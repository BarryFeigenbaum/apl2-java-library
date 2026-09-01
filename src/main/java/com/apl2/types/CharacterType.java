package com.apl2.types;

import java.util.Objects;

/**
 * Represents an APL2 Character (single character).
 */
public final class CharacterType implements Scalar {
    private final char value;

    public CharacterType(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "Character";
    }

    @Override
    public APLType deepCopy() {
        return new CharacterType(value);
    }

    @Override
    public double toNumeric() {
        return (double) value;
    }

    @Override
    public boolean toBoolean() {
        return value != 0;
    }

    @Override
    public char toCharacter() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterType that = (CharacterType) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
