package org.repozoo.commons.range;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Range<T> extends RangeSet<T> {

    Value<T> minValue();

    Value<T> maxValue();

    /**
     * Returns the inclusive minimum of this range.
     */
    default T min() {
        return minValue().value();
    }

    /**
     * Returns the inclusive maximum of this range.
     */
    default T max() {
        return maxValue().value();
    }

    default boolean isValidRange() {
        return min() != null && max() != null && minValue().isBeforeOrEqual(maxValue());
    }

    default void ensureValidRange() {
        if (!isValidRange()) {
            throw new IllegalArgumentException("min must not be after max, \nmin: " + min() + "\nmax: " + max());
        }
    }

    default boolean contains(Range<T> other) {
        return contains(other.minValue()) && contains(other.maxValue());
    }

    default boolean contains(Value<T> value) {
        return minValue().isBeforeOrEqual(value) && maxValue().isAfterOrEqual(value);
    }

    default boolean contains(T t) {
        return minValue().isBeforeOrEqual(t) && maxValue().isAfterOrEqual(t);
    }

    /**
     * Returns true if any {@link SimpleRange} of others intersects with this {@link SimpleRange}.
     */
    default boolean intersects(RangeSet<T> others) {
        return others.rangeStream().anyMatch(this::intersects);
    }

    default boolean intersects(Range<T> other) {
        return this.contains(other.minValue()) || this.contains(other.maxValue()) || other.contains(this);
    }

    default RangeSet<T> intersection(RangeSet<T> others) {
        return others.rangeStream()
            .map(other -> Range.intersection(this, other))
            .reduce(RangeSet.empty(), RangeSet::mergeOverlappingAndAdjacent);
    }

    /**
     * Returns true if this.max < other.min.
     */
    default boolean isBefore(Range<T> other) {
        return maxValue().isBefore(other.minValue());
    }

    /**
     * Returns true if this.min > other.max.
     */
    default boolean isAfter(Range<T> other) {
        return minValue().isAfter(other.maxValue());
    }

    default boolean isEmpty() {
        return false;
    }

    /**
     * Returns a single element stream containing this range.
     */
    default Stream<Range<T>> rangeStream() {
        return Stream.of(this);
    }

    default Stream<T> streamValues() {
        return Stream
            .iterate(minValue(), value -> value.isBeforeOrEqual(maxValue()), Value::next)
            .map(Value::value);
    }

    /**
     * Returns a String representation of this range in the form <pre>"Range[from=1, to=3]"</pre>
     */
    default String asString() {
        return "Range{from=" + min() + ", to=" + max() + '}';
    }

    static <T> RangeSet<T> remove(Range<T> aRange, Range<T> toRemove) {
        if (aRange.intersects(toRemove)) {
            if (aRange.equals(toRemove) || toRemove.contains(aRange)) {
                return RangeSet.empty();
            } else if (aRange.contains(toRemove) && (toRemove.minValue().isAfter(aRange.minValue()) && toRemove.maxValue().isBefore(aRange.maxValue()))) {
                Range<T> r1 = Range.between(aRange.minValue(), toRemove.minValue().previous());
                Range<T> r2 = Range.between(toRemove.maxValue().next(), aRange.maxValue());
                return RangeSet.of(r1, r2);
            } else {
                if (toRemove.minValue().isAfter(aRange.minValue())) {
                    return Range.between(aRange.minValue(), toRemove.minValue().previous());
                } else {
                    return Range.between(toRemove.maxValue().next(), aRange.maxValue());
                }
            }
        } else {
            return aRange;
        }
    }

    @SafeVarargs
    private static <T> Value<T> min(Function<Range<T>, Value<T>> extraction, Range<T>... ranges) {
        return Arrays.stream(ranges).min(Comparator.comparing(extraction)).map(extraction).orElseThrow();
    }

    @SafeVarargs
    private static <T> Value<T> max(Function<Range<T>, Value<T>> extraction, Range<T>... ranges) {
        return Arrays.stream(ranges).max(Comparator.comparing(extraction)).map(extraction).orElseThrow();
    }

    /**
     * Returns a {@link SimpleRange} with the global min max values of all supplied ranges<br>
     * example:
     * <ul>
     *     <li><pre>enclose([1-3], [5-8]) -> [1-8]</pre></li>
     *     <li><pre>enclose([1-6], [5-8]) -> [1-8]</pre></li>
     * </ul>
     */
    @SafeVarargs
    static <T> Range<T> newRangeFromGlobalMinMax(Range<T>... ranges) {
        Objects.requireNonNull(ranges);
        Value<T> minStart = min(Range::minValue, ranges);
        Value<T> maxEnd = max(Range::maxValue, ranges);
        return Range.between(minStart, maxEnd);
    }

    static <X> Range<X> between(Value<X> min, Value<X> max) {
        return new SimpleRange<>(min, max);
    }

    static <T> RangeSet<T> intersection(Range<T> aRange, Range<T> other) {
        if (aRange.intersects(other)) {
            Value<T> maxStart = max(Range::minValue, aRange, other);
            Value<T> minEnd = min(Range::maxValue, aRange, other);
            return between(maxStart, minEnd);
        } else {
            return RangeSet.empty();
        }
    }
}
