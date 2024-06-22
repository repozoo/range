package org.repozoo.commons.range;

import org.repozoo.commons.range.factories.IntRange;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RangeTest {

    @Test
    void contains_test() {
        assertThat(IntRange.between(1, 1).contains(3)).isFalse();
        assertThat(IntRange.between(5, 5).contains(3)).isFalse();
    }

    @Test
    void isBefore_isOnlyTrueIfEntireRangeIsBeforeOther() {
        assertThat(IntRange.between(1, 2).isBefore(IntRange.between(3, 4))).isTrue();
        assertThat(IntRange.between(2, 3).isBefore(IntRange.between(3, 4))).isFalse();
        assertThat(IntRange.between(3, 3).isBefore(IntRange.between(3, 4))).isFalse();
        assertThat(IntRange.between(3, 4).isBefore(IntRange.between(3, 4))).isFalse();
        assertThat(IntRange.between(4, 5).isBefore(IntRange.between(3, 4))).isFalse();
        assertThat(IntRange.between(5, 5).isBefore(IntRange.between(3, 4))).isFalse();
    }

    @Test
    void isAfter_isTrueIfEntireRangeIsAfterOther() {
        assertThat(IntRange.between(2, 2).isAfter(IntRange.between(1, 1))).isTrue();
        assertThat(IntRange.between(3, 4).isAfter(IntRange.between(1, 2))).isTrue();
    }

    @Test
    void isAfter_isFalseIfRangesAreEqual() {
        assertThat(IntRange.between(1, 1).isAfter(IntRange.between(1, 1))).isFalse();
        assertThat(IntRange.between(1, 2).isAfter(IntRange.between(1, 2))).isFalse();
    }

    @Test
    void isAfter_isFalseIfRangesIntersect() {
        assertThat(IntRange.between(2, 4).isAfter(IntRange.between(1, 3))).isFalse();
        assertThat(IntRange.between(2, 4).isAfter(IntRange.between(1, 2))).isFalse();
    }

    @Test
    void contains() {
        assertThat(IntRange.between(1, 4).contains(IntRange.between(1, 1))).isTrue();
        assertThat(IntRange.between(1, 4).contains(IntRange.between(4, 4))).isTrue();

        assertThat(IntRange.between(1, 4).contains(IntRange.between(1, 2))).isTrue();
        assertThat(IntRange.between(1, 4).contains(IntRange.between(1, 3))).isTrue();
        assertThat(IntRange.between(1, 4).contains(IntRange.between(1, 4))).isTrue();

        assertThat(IntRange.between(1, 4).contains(IntRange.between(3, 4))).isTrue();
        assertThat(IntRange.between(1, 4).contains(IntRange.between(2, 4))).isTrue();
        assertThat(IntRange.between(1, 4).contains(IntRange.between(1, 4))).isTrue();

        assertThat(IntRange.between(2, 4).contains(IntRange.between(1, 2))).isFalse();
        assertThat(IntRange.between(2, 4).contains(IntRange.between(3, 5))).isFalse();
        assertThat(IntRange.between(2, 3).contains(IntRange.between(1, 5))).isFalse();
    }

    @Test
    void distinct() {
        assertThat(IntRange.between(1, 1).isDistinct(IntRange.between(2, 2))).isTrue();
        assertThat(IntRange.between(2, 2).isDistinct(IntRange.between(1, 1))).isTrue();

        assertThat(IntRange.between(1, 1).isDistinct(IntRange.between(1, 1))).isFalse();
        assertThat(IntRange.between(1, 4).isDistinct(IntRange.between(2, 3))).isFalse();
        assertThat(IntRange.between(2, 3).isDistinct(IntRange.between(1, 4))).isFalse();
        assertThat(IntRange.between(1, 3).isDistinct(IntRange.between(2, 4))).isFalse();
        assertThat(IntRange.between(2, 4).isDistinct(IntRange.between(1, 3))).isFalse();
    }

    @Test
    void intersects() {
        assertThat(IntRange.between(1, 1).intersects(IntRange.between(1, 1))).isTrue();
        assertThat(IntRange.between(1, 4).intersects(IntRange.between(2, 3))).isTrue();
        assertThat(IntRange.between(2, 3).intersects(IntRange.between(1, 4))).isTrue();
        assertThat(IntRange.between(1, 3).intersects(IntRange.between(2, 4))).isTrue();
        assertThat(IntRange.between(2, 4).intersects(IntRange.between(1, 3))).isTrue();

        assertThat(IntRange.between(1, 1).intersects(IntRange.between(2, 2))).isFalse();
        assertThat(IntRange.between(2, 2).intersects(IntRange.between(1, 1))).isFalse();

        assertThat(IntRange.between(7, 10).intersects(IntRange.between(9, 11))).isTrue();

        assertThat(IntRange.between(11, 100).intersects(RangeSet.of(
                IntRange.between(10, 20),
                IntRange.between(30, 40),
                IntRange.between(60, 70)
        ))).isTrue();
    }

    @Test
    void intersection() {
        assertThat(IntRange.between(1, 1).intersection(IntRange.between(1, 1)).rangeStream()).containsExactly(IntRange.between(1, 1));
        assertThat(IntRange.between(1, 4).intersection(IntRange.between(2, 3)).rangeStream()).containsExactly(IntRange.between(2, 3));
        assertThat(IntRange.between(2, 3).intersection(IntRange.between(1, 4)).rangeStream()).containsExactly(IntRange.between(2, 3));
        assertThat(IntRange.between(1, 3).intersection(IntRange.between(2, 4)).rangeStream()).containsExactly(IntRange.between(2, 3));
        assertThat(IntRange.between(2, 4).intersection(IntRange.between(1, 3)).rangeStream()).containsExactly(IntRange.between(2, 3));

        Assertions.assertThat(IntRange.between(1, 1).intersection(IntRange.between(2, 2)).rangeStream()).isEmpty();
        Assertions.assertThat(IntRange.between(2, 2).intersection(IntRange.between(1, 1)).rangeStream()).isEmpty();

        assertThat(IntRange.between(7, 10).intersection(IntRange.between(9, 11)).rangeStream()).containsExactly(IntRange.between(9, 10));
        assertThat(
                IntRange.between(11, 100)
                        .intersection(
                                RangeSet.of(
                                        IntRange.between(10, 20),
                                        IntRange.between(30, 40),
                                        IntRange.between(60, 70)
                                )).getRanges())
                .containsExactly(
                        IntRange.between(11, 20),
                        IntRange.between(30, 40),
                        IntRange.between(60, 70)
                );
    }

    @Test
    void add_singeRange() {
        assertThat(IntRange.between(1, 1).add(IntRange.between(1, 1)).getRanges()).containsExactly(IntRange.between(1, 1));
        assertThat(IntRange.between(1, 1).add(IntRange.between(2, 2)).getRanges()).containsExactly(IntRange.between(1, 2));
        assertThat(IntRange.between(1, 2).add(IntRange.between(2, 3)).getRanges()).containsExactly(IntRange.between(1, 3));
        assertThat(IntRange.between(1, 3).add(IntRange.between(2, 4)).getRanges()).containsExactly(IntRange.between(1, 4));
    }

    @Test
    void add_rangeSet() {
        assertThat(
                IntRange.between(1, 10)
                        .add(RangeSet.of(
                                        IntRange.between(1, 1),
                                        IntRange.between(9, 11),
                                        IntRange.between(90, 100)
                                )
                        ).getRanges()
        ).containsExactly(
                IntRange.between(1, 11),
                IntRange.between(90, 100)
        );

        assertThat(IntRange.between(1, 1).add(IntRange.between(2, 2)).getRanges()).containsExactly(IntRange.between(1, 2));
        assertThat(IntRange.between(1, 2).add(IntRange.between(2, 3)).getRanges()).containsExactly(IntRange.between(1, 3));
        assertThat(IntRange.between(1, 3).add(IntRange.between(2, 4)).getRanges()).containsExactly(IntRange.between(1, 4));
    }

    @Test
    void subtract_singeRange() {

        assertThat(IntRange.between(2, 4).remove(IntRange.between(1, 2)).contains(IntRange.between(3, 4))).isTrue();
        assertThat(IntRange.between(2, 4).remove(IntRange.between(1, 5)).isEmpty()).isTrue();

        assertThat(IntRange.between(2, 4).remove(IntRange.between(2, 4)).isEmpty()).isTrue();
        assertThat(IntRange.between(2, 4).remove(IntRange.between(1, 2)).contains(IntRange.between(2, 4))).isFalse();

        assertThat(IntRange.between(2, 4).remove(IntRange.between(1, 2)).getRanges()).containsExactly(IntRange.between(3, 4));

        assertThat(IntRange.between(2, 4).remove(IntRange.between(1, 3)).getRanges()).containsExactly(IntRange.between(4, 4));
        assertThat(IntRange.between(2, 5).remove(IntRange.between(1, 3)).getRanges()).containsExactly(IntRange.between(4, 5));

        assertThat(IntRange.between(2, 5).remove(IntRange.between(5, 5)).getRanges()).containsExactly(IntRange.between(2, 4));
        assertThat(IntRange.between(2, 5).remove(IntRange.between(2, 2)).getRanges()).containsExactly(IntRange.between(3, 5));

        assertThat(IntRange.between(2, 5).remove(RangeSet.of(
                IntRange.between(2, 2),
                IntRange.between(5, 5)
        )).getRanges()).containsExactly(IntRange.between(3, 4));
    }
}