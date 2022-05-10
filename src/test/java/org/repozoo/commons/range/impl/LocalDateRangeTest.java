package org.repozoo.commons.range.impl;

import org.repozoo.commons.range.factories.LocalDateRange;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LocalDateRangeTest {

    @Test
    void create_neitherStartNorEndAllowedToBeNull() {

        LocalDate min = LocalDate.parse("2021-01-01");
        LocalDate max = LocalDate.parse("2021-02-01");

        assertThatThrownBy(() -> LocalDateRange.between(min, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> LocalDateRange.between(null, max)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> LocalDateRange.between(null, null)).isInstanceOf(NullPointerException.class);

        assertDoesNotThrow(() -> LocalDateRange.between(min, max));
    }

    @Test
    void create_startMustNotBeAfterEnd() {
        LocalDate jan2021 = LocalDate.parse("2021-01-01");
        LocalDate feb2021 = LocalDate.parse("2021-02-01");

        assertThatThrownBy(() -> LocalDateRange.between(feb2021, jan2021)).isInstanceOf(IllegalArgumentException.class);
        assertDoesNotThrow(() -> LocalDateRange.between(jan2021, feb2021));
    }

    @Test
    void create_startCanBeEqualsEnd() {
        LocalDate jan2021 = LocalDate.parse("2021-01-01");

        assertDoesNotThrow(() -> LocalDateRange.between(jan2021, jan2021));
    }

    @Test
    void intersects_isSymmetric() {
        LocalDate one = LocalDate.parse("2021-01-01");
        LocalDate three = LocalDate.parse("2021-03-01");
        LocalDate six = LocalDate.parse("2021-06-01");
        LocalDate four = LocalDate.parse("2021-04-01");

        assertThat(LocalDateRange.between(three, six).intersects(LocalDateRange.between(one, four))).isTrue();
        assertThat(LocalDateRange.between(one, four).intersects(LocalDateRange.between(three, six))).isTrue();
    }

    @Test
    void isAfter() {
        LocalDate one = LocalDate.parse("2021-01-01");
        LocalDate two = LocalDate.parse("2021-02-01");
        LocalDate three = LocalDate.parse("2021-03-01");
        LocalDate six = LocalDate.parse("2021-06-01");
        LocalDate seven = LocalDate.parse("2021-07-01");

        assertThat(LocalDateRange.between(three, six).isAfter(LocalDateRange.between(one, two))).isTrue();
        assertThat(LocalDateRange.between(three, six).isAfter(LocalDateRange.between(three, six))).isFalse();
        assertThat(LocalDateRange.between(three, six).isAfter(LocalDateRange.between(one, seven))).isFalse();
    }

    @Test
    void isBefore() {
        LocalDate one = LocalDate.parse("2021-01-01");
        LocalDate two = LocalDate.parse("2021-02-01");
        LocalDate three = LocalDate.parse("2021-03-01");
        LocalDate four = LocalDate.parse("2021-04-01");
        LocalDate six = LocalDate.parse("2021-06-01");
        LocalDate seven = LocalDate.parse("2021-07-01");

        assertThat(LocalDateRange.between(one, three).isBefore(LocalDateRange.between(four, six))).isTrue();
        assertThat(LocalDateRange.between(one, three).isBefore(LocalDateRange.between(one, three))).isFalse();
        assertThat(LocalDateRange.between(three, six).isBefore(LocalDateRange.between(one, two))).isFalse();
        assertThat(LocalDateRange.between(three, six).isBefore(LocalDateRange.between(one, seven))).isFalse();
    }

    @Test
    void intersects_isSymmetric2() {
        LocalDate one = LocalDate.parse("2021-01-01");
        LocalDate two = LocalDate.parse("2021-02-01");
        LocalDate three = LocalDate.parse("2021-03-01");
        LocalDate four = LocalDate.parse("2021-05-01");
        LocalDate five = LocalDate.parse("2021-06-01");
        LocalDate seven = LocalDate.parse("2021-07-01");


        assertThat(LocalDateRange.between(three, five).intersects(LocalDateRange.between(one, four))).isTrue();
        assertThat(LocalDateRange.between(three, five).intersects(LocalDateRange.between(four, seven))).isTrue();
        assertThat(LocalDateRange.between(three, five).intersects(LocalDateRange.between(four, four))).isTrue();
        assertThat(LocalDateRange.between(three, five).intersects(LocalDateRange.between(one, seven))).isTrue();
        assertThat(LocalDateRange.between(one, seven).intersects(LocalDateRange.between(three, five))).isTrue();

        assertThat(LocalDateRange.between(three, five).intersects(LocalDateRange.between(one, two))).isFalse();
    }

    @Test
    void intersect_isReflexive() {
        LocalDate three = LocalDate.parse("2021-03-01");
        LocalDate six = LocalDate.parse("2021-06-01");

        assertThat(LocalDateRange.between(three, six).intersects(LocalDateRange.between(three, six))).isTrue();
        assertThat(LocalDateRange.between(three, six).intersects(LocalDateRange.between(three, six))).isTrue();
    }
}