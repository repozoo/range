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

    Value<T> min;
    Value<T> max;

    private RangeImpl(Value<T> min, Value<T> max) {
        Objects.requireNonNull(min);
        Objects.requireNonNull(max);
        if (min.isAfter(max)) {
            throw new IllegalArgumentException("min must not be after max, \nmin: " + min + "\nmax: " + max);
        }
        this.min = min;
        this.max = max;
    }

    public Value<T> min() {
        return min;
    }

    public Value<T> max() {
        return max;
    }

    static <X> Range<X> between(Value<X> min, Value<X> max) {
        return new RangeImpl<>(min, max);
    }
}
