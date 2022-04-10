package org.repozoo.commons.range;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.repozoo.commons.range.value.Value;

import java.util.Objects;

@ToString
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
@EqualsAndHashCode
class RangeImpl<T> implements Range<T> {

    Value<T> from;
    Value<T> to;

    private RangeImpl(Value<T> from, Value<T> to) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to, \nfrom: " + from + "\nto: " + to);
        }
        this.from = from;
        this.to = to;
    }

    public Value<T> from() {
        return from;
    }

    public Value<T> to() {
        return to;
    }

    static <X> Range<X> between(Value<X> from, Value<X> to) {
        return new RangeImpl<>(from, to);
    }
}
