package org.repozoo.commons.range;

import org.repozoo.commons.range.factory.impl.IntRange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RangeSetTest {

    @Test
    void add() {

        assertThat(
                RangeSet.of(
                        IntRange.between(1, 2),
                        IntRange.between(4, 6)
                ).add(
                        IntRange.between(8, 10)
                ).getRanges()
        ).containsExactly(
                IntRange.between(1, 2),
                IntRange.between(4, 6),
                IntRange.between(8, 10)
        );

        assertThat(RangeSet.of(IntRange.singleton(1), IntRange.singleton(2)).add(IntRange.between(1, 2))
                .getRanges()).containsExactly(IntRange.between(1, 2));

        assertThat(
                RangeSet.of(
                        IntRange.singleton(1),
                        IntRange.singleton(3)
                ).add(
                        IntRange.singleton(2)).getRanges()
        ).containsExactly(
                IntRange.singleton(1),
                IntRange.singleton(2),
                IntRange.singleton(3)
        );
    }

    @Test
    void contains() {
        assertThat(RangeSet.of(
                        IntRange.between(10, 30),
                        IntRange.between(40, 70),
                        IntRange.between(80, 110)
                ).contains(RangeSet.of(
                        IntRange.between(10, 30),
                        IntRange.between(40, 70),
                        IntRange.between(80, 110))
                )
        ).isTrue();

        assertThat(RangeSet.of(
                        IntRange.between(10, 30),
                        IntRange.between(40, 70),
                        IntRange.between(80, 110)
                ).contains(RangeSet.of(
                        IntRange.between(10, 10),
                        IntRange.between(40, 40),
                        IntRange.between(80, 80))
                )
        ).isTrue();

        assertThat(RangeSet.of(
                        IntRange.between(1, 2),
                        IntRange.between(4, 6)
                ).contains(
                        IntRange.between(0, 10)
                )
        ).isFalse();

        assertThat(RangeSet.of(
                        IntRange.between(10, 30),
                        IntRange.between(40, 70),
                        IntRange.between(80, 110)
                ).contains(IntRange.between(0, 9))
        ).isFalse();


        assertThat(RangeSet.of(
                        IntRange.between(10, 30),
                        IntRange.between(40, 70),
                        IntRange.between(80, 110)
                ).contains(RangeSet.of(
                        IntRange.between(30, 40),
                        IntRange.between(70, 80))
                )
        ).isFalse();

        assertThat(RangeSet.of(
                        IntRange.between(10, 30),
                        IntRange.between(40, 70),
                        IntRange.between(80, 110)
                ).contains(RangeSet.of(
                        IntRange.between(10, 20),
                        IntRange.between(40, 70),
                        IntRange.between(80, 111))
                )
        ).isFalse();
    }

    @Test
    void intersects() {
        assertThat(RangeSet.of(
                        IntRange.between(1, 2),
                        IntRange.between(4, 6)
                ).intersects(
                        IntRange.between(0, 10)
                )
        ).isTrue();
    }

    @Test
    void intersection() {
        assertThat(RangeSet.of(
                        IntRange.between(1, 2),
                        IntRange.between(4, 6)
                ).intersection(IntRange.between(0, 10)).getRanges()
        ).containsExactly(
                IntRange.between(1, 2),
                IntRange.between(4, 6)
        );
    }

    @Test
    void of() {
        assertThat(RangeSet.of(
                IntRange.between(1, 2),
                IntRange.between(4, 6)).getRanges()
        ).containsExactly(
                IntRange.between(1, 2),
                IntRange.between(4, 6)
        );

        assertThat(RangeSet.of(
                IntRange.between(1, 5),
                IntRange.between(4, 10)).getRanges()
        ).containsExactly(IntRange.between(1, 10));

        assertThat(RangeSet.of(
                IntRange.between(1, 3),
                IntRange.between(5, 6),
                IntRange.between(6, 10),
                IntRange.between(12, 15)).getRanges()
        ).containsExactly(
                IntRange.between(1, 3),
                IntRange.between(5, 10),
                IntRange.between(12, 15)
        );

        assertThat(RangeSet.of(
                IntRange.between(1, 5),
                IntRange.between(6, 8),
                IntRange.between(8, 10),
                IntRange.between(9, 15)).getRanges()
        ).containsExactly(
                IntRange.between(1, 5),
                IntRange.between(6, 15)
        );
    }

    @Test
    void sum() {
        assertThat(RangeSet.sum(
                        RangeSet.of(
                                IntRange.between(1, 2),
                                IntRange.between(4, 6)),
                        RangeSet.of(
                                IntRange.between(1, 2),
                                IntRange.between(4, 6))
                ).getRanges()
        ).containsExactly(
                IntRange.between(1, 2),
                IntRange.between(4, 6)
        );

        assertThat(RangeSet.sum(
                        RangeSet.of(
                                IntRange.between(1, 5),
                                IntRange.between(4, 10)),
                        RangeSet.of(
                                IntRange.between(1, 2),
                                IntRange.between(4, 6))
                ).getRanges()
        ).containsExactly(
                IntRange.between(1, 10)
        );

        assertThat(RangeSet.sum(
                        RangeSet.of(
                                IntRange.between(0, 10),
                                IntRange.between(20, 30),
                                IntRange.between(50, 70),
                                IntRange.between(91, 99)),
                        RangeSet.of(
                                IntRange.between(10, 11),
                                IntRange.between(19, 20),
                                IntRange.between(40, 50),
                                IntRange.between(92, 100))
                ).getRanges()
        ).containsExactly(
                IntRange.between(0, 11),
                IntRange.between(19, 30),
                IntRange.between(40, 70),
                IntRange.between(91, 100)
        );
    }
}