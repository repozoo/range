[![License](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)
![JDK](https://img.shields.io/badge/jdk-11-yellowgreen.svg)
[![CodeQL](https://github.com/repozoo/range/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/repozoo/range/actions/workflows/codeql-analysis.yml)

# range
- - - -
Range is a small java library,<br>
helping to work with range(s) or interval(s). 
- - - -
- [Usage](#usage)
    * [Examples](#examples)
        + [add - Example](#add---example)
        + [remove - Example](#remove---example)
        + [intersection - Example](#intersection---example)
        + [custom RangeFactory - Example](#custom-rangefactory---example)
    * [Maven](#maven)
- [Architecture](#architecture)

## Usage
You can either
- use predefined Range factories
    - `Range<LocalDate> dateRange = LocalDateRange.between(monday, friday);`
    - `Range<LocalDateTime> timeRange = LocalTimeRange.between(now, now.plusHours(8));`
    - ...
- or create custom ones (see [custom RangeFactory - Example](#custom-rangefactory---example))

### Examples
#### add - Example
```java
// Assing
YearMonth dec2021 = YearMonth.parse("2021-12");
RangeSet<LocalDate> vacationSahrah = RangeSet.of(
    LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
    LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(26)),
    LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(31))
);
RangeSet<LocalDate> vacationJimmy = RangeSet.of(
    LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
    LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(29))
);
RangeSet<LocalDate> vacationReggy = RangeSet.of(
    LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9))
);
// Act
RangeSet<LocalDate> atLeastOneIsAbsent = vacationSahrah.add(vacationJimmy).add(vacationReggy);
// Assert
assertThat(atLeastOneIsAbsent.stream())
    .containsExactly(
        LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
        LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(31))
);
```
#### remove - Example
```java
// Assing
YearMonth dec2021 = YearMonth.parse("2021-12");
Range<LocalDate> december = LocalDateRange.between(dec2021.atDay(1), dec2021.atEndOfMonth());
RangeSet<LocalDate> vacationSahrah = RangeSet.of(
        LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
        LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(26)),
        LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(31))
);
RangeSet<LocalDate> vacationJimmy = RangeSet.of(
        LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
        LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(29))
);
// Act
RangeSet<LocalDate> everybodyIsPresent = december.remove(vacationSahrah).remove(vacationJimmy);
// Assert
assertThat(everybodyIsPresent.stream())
        .containsExactly(
                LocalDateRange.between(dec2021.atDay(10), dec2021.atDay(13))
        );
```

#### intersection - Example
```java
// Assing
YearMonth dec2021 = YearMonth.parse("2021-12");
RangeSet<LocalDate> vacationSahrah = RangeSet.of(
        LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
        LocalDateRange.between(dec2021.atDay(14), dec2021.atDay(26)),
        LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(31))
);
RangeSet<LocalDate> vacationJimmy = RangeSet.of(
        LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(9)),
        LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(29))
);
// Act
RangeSet<LocalDate> everybodyIsAbsent = vacationSahrah.intersection(vacationJimmy);
// Assert
assertThat(everybodyIsAbsent.stream())
        .containsExactly(
                LocalDateRange.between(dec2021.atDay(1), dec2021.atDay(5)),
                LocalDateRange.between(dec2021.atDay(23), dec2021.atDay(26)),
                LocalDateRange.between(dec2021.atDay(28), dec2021.atDay(29))
        );
```
#### custom RangeFactory - Example
Let's say we want to create a Range of type `YearMonth` and a factory for that type does not exist yet.

```java
//build up the factory
UnaryOperator<YearMonth> next = n -> n.plusMonths(1);
UnaryOperator<YearMonth> previous = n ->  n.minusMonths(1);
RangeFactory.CreateRange<YearMonth> createRange = RangeFactory.forType(YearMonth.class)
                                                            .withComparator(YearMonth::compareTo)
                                                            .withIterator(next, previous)
                                                            .build();

YearMonth jan = YearMonth.parse("2022-01");
YearMonth dec = YearMonth.parse("2022-12");
// use it 
Range<YearMonth> range = createRange.between(jan, dec);
```
### Maven
```
<dependency>
  <groupId>org.repozoo.commons</groupId>
  <artifactId>range</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Architecture
The most general type is `RangeSet`, basically representing a list of `Range`s.<br>
Why `RangeSet`? The more specific `Range` type does not suffice to cover all basic range functionality.<br>
Example: Imagine 
- some range `a[1 to 10]` and `b[3 to 5]`.<br>
- `a.remove(b)` results in _(some scattered range)_ `c1[1 to 2]` and `c2[6 to 10]`

Therefore `RangeSet` is the main building bloc.<br>
The type `Range` is just a special `RangeSet` containing exactly one `Range`<br>
and the two `Value`s `from` and `to` _(inclusive)_.<br>
`RangeSet` as well as `Range` are both _immutable_.

<img src="./documentation/images/range-and-rangeset-uml.svg" alt="range-uml">

Most important operations
<img src="./documentation/images/range-API.svg" alt="range-api">
