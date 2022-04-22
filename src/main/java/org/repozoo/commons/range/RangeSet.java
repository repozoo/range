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
    Stream<RangeImpl<T>> stream();


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
        List<RangeImpl<T>> intersections = stream()
                .map(others::intersection)
                .flatMap(RangeSet::stream)
                .collect(Collectors.toList());
        return newRangeSet(intersections);
    }

    default boolean isDistinct(RangeImpl<T> other) {
        return !intersects(other);
    }

    default RangeSet<T> add(RangeSet<T> others) {
        return RangeSet.sum(this, others);
    }

    default RangeSet<T> remove(RangeSet<T> others) {
        List<RangeImpl<T>> newRanges = new ArrayList<>();
        getRanges().forEach(range -> {
            Stack<RangeImpl<T>> stack = new Stack<>();
            stack.push(range);
            others.stream().forEach(other -> {
                if (!stack.isEmpty()) {
                    RangeImpl<T> topRange = stack.pop();
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

    default List<RangeImpl<T>> getRanges() {
        return stream().collect(Collectors.toList());
    }

    private static <T> RangeSet<T> remove(RangeImpl<T> aRange, RangeImpl<T> toRemove) {
        if (aRange.intersects(toRemove)) {
            if (aRange.equals(toRemove) || toRemove.contains(aRange)) {
                return RangeSet.empty();
            } else if (aRange.contains(toRemove) && (toRemove.min().isAfter(aRange.min()) && toRemove.max().isBefore(aRange.max()))) {
                RangeImpl<T> r1 = RangeImpl.between(aRange.min(), toRemove.min().previous());
                RangeImpl<T> r2 = RangeImpl.between(toRemove.max().next(), aRange.max());
                return RangeSet.of(r1, r2);
            } else {
                if (toRemove.min().isAfter(aRange.min())) {
                    return RangeImpl.between(aRange.min(), toRemove.min().previous());
                } else {
                    return RangeImpl.between(toRemove.max().next(), aRange.max());
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
    static <T> RangeSet<T> of(RangeImpl<T>... ranges) {
        Objects.requireNonNull(ranges);
        return normalize(Arrays.stream(ranges));
    }

    static <T> RangeSet<T> normalize(Stream<RangeImpl<T>> rangeStream) {
        Stack<RangeImpl<T>> stackedRanges = rangeStream.sorted(Comparator.comparing(RangeImpl::min)).collect(RangeSet.toStack());
        return newRangeSet(stackedRanges);
    }

    static <T> RangeSet<T> add(RangeImpl<T> aRange, RangeImpl<T> other) {
        if (aRange.intersects(other)) {
            return RangeImpl.sourround(aRange, other);
        } else {
            return newRangeSet(aRange, other);
        }
    }

    static <T> RangeSet<T> sum(RangeSet<T> set1, RangeSet<T> set2) {
        Stream<RangeImpl<T>> rangeStream = Stream.concat(set1.stream(), set2.stream());
        return RangeSet.normalize(rangeStream);
    }

    static String toString(RangeSet<?> rangeSet) {
        return rangeSet.stream().map(RangeImpl::toString).collect(Collectors.joining("\n"));
    }

    private static <T> RangeSet<T> newRangeSet(Collection<RangeImpl<T>> ranges) {
        return ranges::stream;
    }

    @SafeVarargs
    private static <T> RangeSet<T> newRangeSet(RangeImpl<T>... ranges) {
        return Arrays.asList(ranges)::stream;
    }

    private static <T> Collector<RangeImpl<T>, Stack<RangeImpl<T>>, Stack<RangeImpl<T>>> toStack() {
        return Collector.of(Stack::new, RangeSet::addOnTop, RangeSet::mergeStacks);
    }

    private static <T> void addOnTop(Stack<RangeImpl<T>> stack, RangeImpl<T> range) {
        if (stack.isEmpty()) {
            stack.push(range);
        } else {
            RangeImpl<T> topRange = stack.pop();
            RangeSet<T> sum = add(topRange, range);
            sum.getRanges().forEach(stack::push);
        }
    }

    private static <T> Stack<RangeImpl<T>> mergeStacks(Stack<RangeImpl<T>> stack1, Stack<RangeImpl<T>> stack2) {
        throw new UnsupportedOperationException("TODO implement ...");
    }
}
