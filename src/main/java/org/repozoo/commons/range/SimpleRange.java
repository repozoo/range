package org.repozoo.commons.range;

import lombok.EqualsAndHashCode;

import java.util.Objects;
import java.util.stream.Stream;

@EqualsAndHashCode
public class SimpleRange<T> implements Range<T> {

    private final Value<T> min;
    private final Value<T> max;

    SimpleRange(Value<T> min, Value<T> max) {
        Objects.requireNonNull(min);
        Objects.requireNonNull(max);
        if (min.isAfter(max)) {
            throw new IllegalArgumentException("min must not be after max, \nmin: " + min + "\nmax: " + max);
        }
        this.min = min;
        this.max = max;
    }

    @Override public Value<T> minValue() {
        return min;
    }

    @Override public Value<T> maxValue() {
        return max;
    }

    /**
     * Returns true if this.max < other.min.
     */
    @Override public boolean isBefore(Range<T> other) {
        return maxValue().isBefore(other.minValue());
    }

    /**
     * Returns true if this.min > other.max.
     */
    @Override public boolean isAfter(Range<T> other) {
        return minValue().isAfter(other.maxValue());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * Returns a single element stream containing this range.
     */
    @Override
    public Stream<Range<T>> streamRanges() {
        return Stream.of(this);
    }

    @Override
    public Stream<T> streamValues() {
        return Stream
            .iterate(minValue(), value -> value.isBeforeOrEqual(maxValue()), Value::next)
            .map(Value::value);
    }


    /**
     * Returns true if this.max > other.max.
     */
    public boolean endsAfter(Range<T> other) {
        return maxValue().isAfter(other.maxValue());
    }


    /**
     * Returns a String representation of this range in the form <pre>"Range[from=1, to=3]"</pre>
     */
    @Override
    public String toString() {
        return "Range{from=" + min() + ", to=" + max() + '}';
    }


}
