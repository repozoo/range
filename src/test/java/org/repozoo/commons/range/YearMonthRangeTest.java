package org.repozoo.commons.range;

import org.junit.jupiter.api.Test;
import org.repozoo.commons.range.factories.YearMonthRange;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class YearMonthRangeTest {

    @Test
    void create_neitherStartNorEndAllowedToBeNull() {

        YearMonth min = YearMonth.parse("2021-01");
        YearMonth max = YearMonth.parse("2021-02");

        assertThatThrownBy(() -> YearMonthRange.between(min, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> YearMonthRange.between(null, max)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> YearMonthRange.between(null, null)).isInstanceOf(NullPointerException.class);

        assertDoesNotThrow(() -> YearMonthRange.between(min, max));
    }

    @Test
    void create_startMustNotBeAfterEnd() {
        YearMonth jan2021 = YearMonth.parse("2021-01");
        YearMonth feb2021 = YearMonth.parse("2021-02");

        assertThatThrownBy(() -> YearMonthRange.between(feb2021, jan2021)).isInstanceOf(IllegalArgumentException.class);
        assertDoesNotThrow(() -> YearMonthRange.between(jan2021, feb2021));
    }

    @Test
    void create_startCanBeEqualsEnd() {
        YearMonth jan2021 = YearMonth.parse("2021-01");

        assertDoesNotThrow(() -> YearMonthRange.between(jan2021, jan2021));
    }

    @Test
    void intersects_isSymmetric() {
        YearMonth one = YearMonth.parse("2021-01");
        YearMonth three = YearMonth.parse("2021-03");
        YearMonth six = YearMonth.parse("2021-06");
        YearMonth four = YearMonth.parse("2021-04");

        assertThat(YearMonthRange.between(three, six).intersects(YearMonthRange.between(one, four))).isTrue();
        assertThat(YearMonthRange.between(one, four).intersects(YearMonthRange.between(three, six))).isTrue();
    }

    @Test
    void isAfter() {
        YearMonth one = YearMonth.parse("2021-01");
        YearMonth two = YearMonth.parse("2021-02");
        YearMonth three = YearMonth.parse("2021-03");
        YearMonth six = YearMonth.parse("2021-06");
        YearMonth seven = YearMonth.parse("2021-07");

        assertThat(YearMonthRange.between(three, six).isAfter(YearMonthRange.between(one, two))).isTrue();
        assertThat(YearMonthRange.between(three, six).isAfter(YearMonthRange.between(three, six))).isFalse();
        assertThat(YearMonthRange.between(three, six).isAfter(YearMonthRange.between(one, seven))).isFalse();
    }

    @Test
    void isBefore() {
        YearMonth one = YearMonth.parse("2021-01");
        YearMonth two = YearMonth.parse("2021-02");
        YearMonth three = YearMonth.parse("2021-03");
        YearMonth four = YearMonth.parse("2021-04");
        YearMonth six = YearMonth.parse("2021-06");
        YearMonth seven = YearMonth.parse("2021-07");

        assertThat(YearMonthRange.between(one, three).isBefore(YearMonthRange.between(four, six))).isTrue();
        assertThat(YearMonthRange.between(one, three).isBefore(YearMonthRange.between(one, three))).isFalse();
        assertThat(YearMonthRange.between(three, six).isBefore(YearMonthRange.between(one, two))).isFalse();
        assertThat(YearMonthRange.between(three, six).isBefore(YearMonthRange.between(one, seven))).isFalse();
    }

    @Test
    void union() {
        YearMonth one = YearMonth.parse("2021-01");
        YearMonth two = YearMonth.parse("2021-02");
        YearMonth three = YearMonth.parse("2021-03");
        YearMonth four = YearMonth.parse("2021-05");

        assertThat(YearMonthRange.between(one, two).add(YearMonthRange.between(two, four)).getRanges())
                .containsExactly(YearMonthRange.between(one, four));
        assertThat(YearMonthRange.between(one, two).add(YearMonthRange.between(three, four)).getRanges())
                .containsExactly(YearMonthRange.between(one, two), YearMonthRange.between(three, four));

    }

    @Test
    void intersection() {
        YearMonth one = YearMonth.parse("2021-01");
        YearMonth two = YearMonth.parse("2021-02");
        YearMonth three = YearMonth.parse("2021-03");
        YearMonth four = YearMonth.parse("2021-05");
        YearMonth six = YearMonth.parse("2021-06");

        assertThat(YearMonthRange.between(three, six).intersection(YearMonthRange.between(three, six)).stream()).containsExactly(YearMonthRange.between(three, six));

        assertThat(YearMonthRange.between(one, three).intersection(YearMonthRange.between(two, four)).stream()).containsExactly(YearMonthRange.between(two, three));
        assertThat(YearMonthRange.between(two, four).intersection(YearMonthRange.between(one, three)).stream()).containsExactly(YearMonthRange.between(two, three));

        assertThat(YearMonthRange.between(one, six).intersection(YearMonthRange.between(two, four)).stream()).containsExactly(YearMonthRange.between(two, four));
        assertThat(YearMonthRange.between(two, four).intersection(YearMonthRange.between(one, six)).stream()).containsExactly(YearMonthRange.between(two, four));
    }

    @Test
    void intersects_isSymmetric2() {
        YearMonth one = YearMonth.parse("2021-01");
        YearMonth two = YearMonth.parse("2021-02");
        YearMonth three = YearMonth.parse("2021-03");
        YearMonth four = YearMonth.parse("2021-05");
        YearMonth five = YearMonth.parse("2021-06");
        YearMonth seven = YearMonth.parse("2021-07");


        assertThat(YearMonthRange.between(three, five).intersects(YearMonthRange.between(one, four))).isTrue();
        assertThat(YearMonthRange.between(three, five).intersects(YearMonthRange.between(four, seven))).isTrue();
        assertThat(YearMonthRange.between(three, five).intersects(YearMonthRange.between(four, four))).isTrue();
        assertThat(YearMonthRange.between(three, five).intersects(YearMonthRange.between(one, seven))).isTrue();
        assertThat(YearMonthRange.between(one, seven).intersects(YearMonthRange.between(three, five))).isTrue();

        assertThat(YearMonthRange.between(three, five).intersects(YearMonthRange.between(one, two))).isFalse();
    }

    @Test
    void intersect_isReflexive() {
        YearMonth three = YearMonth.parse("2021-03");
        YearMonth six = YearMonth.parse("2021-06");

        assertThat(YearMonthRange.between(three, six).intersects(YearMonthRange.between(three, six))).isTrue();
        assertThat(YearMonthRange.between(three, six).intersects(YearMonthRange.between(three, six))).isTrue();
    }
}