package org.repozoo.commons.range;

import java.util.Comparator;

public interface Value<X> extends Comparable<Value<X>> {

    X value();
    Value<X> next();
    Value<X> previous();

    Value<X> with(X value);

    default boolean isAfter(Value<X> other) {
        return this.compareTo(other) > 0;
    }

    default boolean isAfter(X x) {
        Value<X> other = with(x);
        return this.isAfter(other);
    }

    default boolean isAfterOrEqual(Value<X> other) {
        int i = this.compareTo(other);
        return i > 0 || i == 0;
    }

    default boolean isAfterOrEqual(X x) {
        Value<X> other = with(x);
        return this.isAfterOrEqual(other);
    }

    default boolean isBefore(Value<X> other) {
        return this.compareTo(other) < 0;
    }

    default boolean isBefore(X x) {
        Value<X> other = with(x);
        return this.isBefore(other);
    }

    default boolean isBeforeOrEqual(Value<X> other) {
        int i = this.compareTo(other);
        return i < 0 || i == 0;
    }

    default boolean isBeforeOrEqual(X x) {
        Value<X> other = this.with(x);
        return this.isAfterOrEqual(other);
    }

    static <X> Value<X> of(X x, ValueIterator<X> iterator, Comparator<X> comparator) {
        return new ValueImpl<>(x, iterator, comparator);
    }
}
