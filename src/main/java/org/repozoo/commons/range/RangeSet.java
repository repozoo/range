package org.repozoo.commons.range;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface RangeSet<T> {

    /**
     * Returns a {@link Range} stream containing all ranges of this set<br>
     * or an empty stream if this {@link RangeSet} is empty.
     */
    Stream<Range<T>> stream();


    /**
     * Returns true if the following is true for each {@link Range} of others:<br>
     * At least one {@link Range} of this set contains 'other'.
     */
    default boolean contains(RangeSet<T> others) {
        return others.stream().allMatch(other -> stream().anyMatch(r -> r.contains(other)));
    }

    /**
     * Returns true if any {@link Range} of this set intersects with at least one {@link Range} of others.
     */
    default boolean intersects(RangeSet<T> others) {
        return stream().anyMatch(aRange -> others.stream().anyMatch(aRange::intersects));
    }

    /**
     * Returns a new RangeSet containing all {@link Range} parts that exist in this and others.
     */
    default RangeSet<T> intersection(RangeSet<T> others) {
        List<Range<T>> intersections = stream()
                .map(others::intersection)
                .flatMap(RangeSet::stream)
                .collect(Collectors.toList());
        return newRangeSet(intersections);
    }

    default boolean isDistinct(Range<T> other) {
        return !intersects(other);
    }

    default RangeSet<T> add(RangeSet<T> others) {
        return RangeSet.sum(this, others);
    }

    default RangeSet<T> remove(RangeSet<T> others) {
        List<Range<T>> newRanges = new ArrayList<>();
        getRanges().forEach(range -> {
            Stack<Range<T>> stack = new Stack<>();
            stack.push(range);
            others.stream().forEach(other -> {
                if (!stack.isEmpty()) {
                    Range<T> topRange = stack.pop();
                    RangeSet<T> result = RangeSet.remove(topRange, other);
                    result.stream().filter(Predicate.not(RangeSet::isEmpty)).forEach(stack::push);
                }
            });
            newRanges.addAll(stack);
        });
        return newRangeSet(newRanges);
    }

    /**
     * True if this set has no range
     */
    default boolean isEmpty() {
        return getRanges().isEmpty();
    }

    default List<Range<T>> getRanges() {
        return stream().collect(Collectors.toList());
    }

    private static <T> RangeSet<T> remove(Range<T> aRange, Range<T> subtrahend) {
        if (aRange.intersects(subtrahend)) {
            if (subtrahend.contains(aRange)) {
                return RangeSet.empty();
            } else if (aRange.contains(subtrahend) && (subtrahend.min().isAfter(aRange.min()) && subtrahend.max().isBefore(aRange.max()))) {
                Range<T> r1 = RangeImpl.between(aRange.min(), subtrahend.min().previous());
                Range<T> r2 = RangeImpl.between(subtrahend.max().next(), aRange.max());
                return RangeSet.of(r1, r2);
            } else {
                if (subtrahend.min().isAfter(aRange.min())) {
                    return RangeImpl.between(aRange.min(), subtrahend.min().previous());
                } else {
                    return RangeImpl.between(subtrahend.max().next(), aRange.max());
                }
            }
        } else {
            return aRange;
        }
    }

    static <T> RangeSet<T> empty() {
        return Stream::empty;
    }

    @SafeVarargs
    static <T> RangeSet<T> of(Range<T>... ranges) {
        Objects.requireNonNull(ranges);
        return normalize(Arrays.stream(ranges));
    }

    static <T> RangeSet<T> newRangeSet(Collection<Range<T>> ranges) {
        return ranges::stream;
    }

    static String toString(RangeSet<?> rangeSet) {
        return rangeSet.stream().map(Range::toString).collect(Collectors.joining("\n"));
    }

    static <T> RangeSet<T> normalize(Stream<Range<T>> rangeStream) {
        Stack<Range<T>> stackedRanges = rangeStream.sorted(Comparator.comparing(Range::min)).collect(RangeSet.toStack());
        return RangeSet.newRangeSet(stackedRanges);
    }

    static <T> RangeSet<T> add(Range<T> aRange, Range<T> other) {
        if (aRange.intersects(other)) {
            return Range.sourround(aRange, other);
        } else {
            return RangeSet.newRangeSet(List.of(aRange, other));
        }
    }

    static <T> RangeSet<T> sum(RangeSet<T> set1, RangeSet<T> set2) {
        return RangeSet.normalize(Stream.concat(set1.stream(), set2.stream()));
    }

    private static <T> Collector<Range<T>, Stack<Range<T>>, Stack<Range<T>>> toStack() {
        return Collector.of(Stack::new, RangeSet::addOnTop, RangeSet::mergeStacks);
    }

    private static <T> void addOnTop(Stack<Range<T>> stack, Range<T> range) {
        if (stack.isEmpty()) {
            stack.push(range);
        } else {
            Range<T> topRange = stack.pop();
            RangeSet<T> sum = add(topRange, range);
            sum.stream().forEach(stack::push);
        }
    }

    private static <T> Stack<Range<T>> mergeStacks(Stack<Range<T>> stack1, Stack<Range<T>> stack2) {
        throw new UnsupportedOperationException("TODO implement ...");
    }
}
