package com.apl2.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for constructing APL2 arrays with fluent API.
 */
public class ArrayBuilder {
    private final List<APLType> elements;
    private int[] shape;

    public ArrayBuilder() {
        this.elements = new ArrayList<>();
    }

    public ArrayBuilder add(APLType element) {
        elements.add(element);
        return this;
    }

    public ArrayBuilder add(boolean value) {
        elements.add(new BooleanType(value));
        return this;
    }

    public ArrayBuilder add(long value) {
        elements.add(new IntegerType(value));
        return this;
    }

    public ArrayBuilder add(double value) {
        elements.add(new FloatingPointType(value));
        return this;
    }

    public ArrayBuilder add(char value) {
        elements.add(new CharacterType(value));
        return this;
    }

    public ArrayBuilder add(String value) {
        elements.add(new StringType(value));
        return this;
    }

    public ArrayBuilder withShape(int... shape) {
        this.shape = shape;
        return this;
    }

    public ArrayType build() {
        if (elements.isEmpty()) {
            throw new IllegalStateException("Cannot build empty array");
        }
        if (shape != null) {
            return new ArrayType(elements, shape);
        }
        return new ArrayType(elements);
    }
}
