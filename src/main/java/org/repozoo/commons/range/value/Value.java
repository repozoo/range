package org.repozoo.commons.range.value;

import org.repozoo.commons.range.ValueIterator;

import java.util.Comparator;

public interface Value<X> extends Comparable<Value<X>> {

    X value();
    Value<X> next();
    Value<X> previous();

    default boolean isAfter(Value<X> other) {
        return compareTo(other) > 0;
    }

    default boolean isBefore(Value<X> other) {
        return compareTo(other) < 0;
    }

    static <X> Value<X> of(X x, ValueIterator<X> iterator, Comparator<X> comparator) {
        return new ValueImpl<>(x, iterator, comparator);
    }
}
