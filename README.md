# Number Range Summarizer
 
A small Java library that takes a comma-delimited string of integers and
produces a comma-delimited **summary**, collapsing any run of consecutive
("sequential") numbers into a `start-end` range.
 
```
Input:  "1,3,6,7,8,12,13,14,15,21,22,23,24,31"
Output: "1, 3, 6-8, 12-15, 21-24, 31"
```
 
This project implements a provided interface, `NumberRangeSummarizer`, and
includes a full JUnit 5 test suite.
 
---
 
## Table of Contents
 
- [How it works](#how-it-works)
- [Project structure](#project-structure)
- [Requirements](#requirements)
- [Building and running tests](#building-and-running-tests)
- [Usage example](#usage-example)
- [Design decisions](#design-decisions)
- [Edge cases handled](#edge-cases-handled)
- [Test coverage](#test-coverage)
---
 
## How it works
 
The `NumberRangeSummarizer` interface defines two methods:
 
```java
Collection<Integer> collect(String input);
String summarizeCollection(Collection<Integer> input);
```
 
**`collect(String input)`**
Parses a comma-delimited string into a collection of integers.
 
- Splits on `,`
- Trims whitespace around each number (`" 3 "` → `3`)
- Returns an empty list for `null` or blank input
- Throws `IllegalArgumentException` if any token isn't a valid integer
**`summarizeCollection(Collection<Integer> input)`**
Takes any collection of integers and returns the grouped, comma-delimited
summary string.
 
- Removes duplicates
- Sorts the numbers in ascending order before scanning
- Walks the sorted numbers once, grouping consecutive runs into
  `start-end` ranges
- A run of exactly two consecutive numbers is still written as a range
  (e.g. `4, 5` → `4-5`)
- A number with no consecutive neighbor is written on its own
  (e.g. `31` stays `31`)
- Returns `""` for `null` or empty input
The two methods are intentionally independent: `summarizeCollection` does
**not** assume its input came from `collect` or is already sorted/de-duplicated.
This means it behaves correctly no matter what `Collection` implementation
(`List`, `Set`, etc.) or ordering it's given.
 
---
 
## Project structure
 
```
number-range-summarizer/
├── README.md
├── pom.xml
└── src
    ├── main
    │   └── java
    │       └── numberrangesummarizer
    │           ├── NumberRangeSummarizer.java       # provided interface (unmodified)
    │           └── NumberRangeSummarizerImpl.java   # implementation
    └── test
        └── java
            └── numberrangesummarizer
                └── NumberRangeSummarizerImplTest.java  # JUnit 5 tests
```
 
---
 
## Requirements
 
- **Java 8** or later (the project is compiled with `--source 8 --target 8`)
- **Maven** 3.6+ (to build and run tests)
---
 
## Building and running tests
 
Clone the repository, then from the project root:
 
```bash
# Run the full test suite
mvn test
 
# Compile only
mvn compile
 
# Build a jar
mvn package
```
 
A successful `mvn test` run will show all tests in
`NumberRangeSummarizerImplTest` passing.
 
---
 
## Usage example
 
```java
import numberrangesummarizer.NumberRangeSummarizer;
import numberrangesummarizer.NumberRangeSummarizerImpl;
import java.util.Collection;
 
public class Demo {
    public static void main(String[] args) {
        NumberRangeSummarizer summarizer = new NumberRangeSummarizerImpl();
 
        Collection<Integer> numbers =
                summarizer.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");
 
        String summary = summarizer.summarizeCollection(numbers);
 
        System.out.println(summary);
        // Output: 1, 3, 6-8, 12-15, 21-24, 31
    }
}
```
 
---
 
## Design decisions
 
| Decision | Reasoning |
|---|---|
| `collect()` preserves input order and duplicates | Keeps parsing behavior predictable and separate from summarization logic — no surprises about what was "in" the string. |
| `summarizeCollection()` sorts and de-duplicates internally | Makes it robust to any collection type or ordering, rather than relying on the caller to have sorted the data first. |
| Invalid numbers throw `IllegalArgumentException` | Fails fast and loudly on bad input rather than silently skipping or producing a wrong summary. |
| `null`/blank/empty input returns an empty result rather than throwing | These are "nothing to summarize" cases, not malformed input, so they're treated as valid edge cases. |
| Two consecutive numbers still form a range (`4-5`, not `4, 5`) | Consistent with the stated rule: *"grouping the numbers into a range when they are sequential."* Two numbers can still be sequential. |
 
---
 
## Edge cases handled
 
- Empty string / `null` input
- Whitespace around numbers (`" 1 , 2,3 ,4"`)
- Unsorted input (`8,1,7,3,2,5` → `1-3, 5, 7-8`)
- Duplicate numbers (`1,2,2,3,3,3` → `1-3`)
- Negative numbers, including negative-to-zero runs
  (`-3,-2,-1,0,2,3` → `-3-0, 2-3`)
- A single number with no neighbors (`5` → `5`)
- All numbers forming one continuous range (`1,2,3,4,5` → `1-5`)
- No numbers being sequential at all (`1,3,5,7` → `1, 3, 5, 7`)
- Non-`List` collections, e.g. `LinkedHashSet<Integer>`
- Invalid (non-numeric) tokens in the input string
---
 
## Test coverage
 
`NumberRangeSummarizerImplTest` covers both methods:
 
**`collect()`**
- Parses a comma-delimited string correctly
- Trims whitespace around numbers
- Returns empty collection for empty string
- Returns empty collection for `null`
- Throws `IllegalArgumentException` on invalid tokens
**`summarizeCollection()`**
- Matches the exact example from the specification
- Empty collection → `""`
- `null` collection → `""`
- Single number
- No sequential numbers
- All numbers sequential
- Exactly two consecutive numbers
- Unsorted input
- Duplicate numbers
- Negative numbers
- Works with non-`List` collections (e.g. `LinkedHashSet`)
- End-to-end: `collect()` output piped directly into `summarizeCollection()`
