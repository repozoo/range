package org.repozoo.commons.range;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SimpleRange<T> implements Range<T> {

    private final Value<T> min;
    private final Value<T> max;

    SimpleRange(Value<T> min, Value<T> max) {
        ensureValidRange();
        this.min = min;
        this.max = max;
    }

    @Override
    public Value<T> minValue() {
        return min;
    }

    @Override
    public Value<T> maxValue() {
        return max;
    }

    @Override public String toString() {
        return asString();
    }
}
