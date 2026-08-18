package numberrangesummarizer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberRangeSummarizerImplTest {

    private NumberRangeSummarizer summarizer;

    @BeforeEach
    void setUp() {
        summarizer = new NumberRangeSummarizerImpl();
    }

    //collect

    @Test
    void collect_parsesCommaDelimitedIntegers() {
        Collection<Integer> result = summarizer.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");
        assertEquals(
                Arrays.asList(1, 3, 6, 7, 8, 12, 13, 14, 15, 21, 22, 23, 24, 31),
                new java.util.ArrayList<>(result)
        );
    }

    @Test
    void collect_trimsWhitespaceAroundNumbers() {
        Collection<Integer> result = summarizer.collect(" 1 , 2,3 ,4");
        assertEquals(Arrays.asList(1, 2, 3, 4), new java.util.ArrayList<>(result));
    }

    @Test
    void collect_emptyStringReturnsEmptyCollection() {
        assertTrue(summarizer.collect("").isEmpty());
    }

    @Test
    void collect_nullReturnsEmptyCollection() {
        assertTrue(summarizer.collect(null).isEmpty());
    }

    @Test
    void collect_invalidTokenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1,2,abc,4"));
    }

    //Summarize  Collection

    @Test
    void summarize_sampleInputFromSpec() {
        Collection<Integer> input = summarizer.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");
        assertEquals("1, 3, 6-8, 12-15, 21-24, 31", summarizer.summarizeCollection(input));
    }

    @Test
    void summarize_emptyCollectionReturnsEmptyString() {
        assertEquals("", summarizer.summarizeCollection(Collections.emptyList()));
    }

    @Test
    void summarize_nullCollectionReturnsEmptyString() {
        assertEquals("", summarizer.summarizeCollection(null));
    }

    @Test
    void summarize_singleNumber() {
        assertEquals("5", summarizer.summarizeCollection(Collections.singletonList(5)));
    }

    @Test
    void summarize_noSequentialNumbers() {
        assertEquals("1, 3, 5, 7", summarizer.summarizeCollection(Arrays.asList(1, 3, 5, 7)));
    }

    @Test
    void summarize_allSequentialNumbers() {
        assertEquals("1-5", summarizer.summarizeCollection(Arrays.asList(1, 2, 3, 4, 5)));
    }

    @Test
    void summarize_twoConsecutiveNumbersFormRange() {
        // A run of exactly two consecutive numbers still forms a range (e.g. "4-5"),
        // consistent with the "sequential" grouping rule.
        assertEquals("4-5", summarizer.summarizeCollection(Arrays.asList(4, 5)));
    }

    @Test
    void summarize_unsortedInputIsSortedBeforeSummarizing() {
        assertEquals("1-3, 5, 7-8", summarizer.summarizeCollection(Arrays.asList(8, 1, 7, 3, 2, 5)));
    }

    @Test
    void summarize_duplicateNumbersAreIgnored() {
        assertEquals("1-3", summarizer.summarizeCollection(Arrays.asList(1, 2, 2, 3, 3, 3)));
    }

    @Test
    void summarize_negativeNumbers() {
        // -3, -2, -1, 0 are consecutive, so they collapse into a single range.
        assertEquals("-3-0, 2-3", summarizer.summarizeCollection(Arrays.asList(-3, -2, -1, 0, 2, 3)));
    }

    @Test
    void summarize_singleNegativeNumber() {
        assertEquals("-7", summarizer.summarizeCollection(Collections.singletonList(-7)));
    }

    @Test
    void summarize_worksWithAnyCollectionType() {
        Collection<Integer> set = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 10));
        assertEquals("1-3, 10", summarizer.summarizeCollection(set));
    }

    @Test
    void summarize_endToEndUsingCollectThenSummarize() {
        String input = "5,4,3,10,8,9,1,2";
        Collection<Integer> collected = summarizer.collect(input);
        assertEquals("1-5, 8-10", summarizer.summarizeCollection(collected));
    }
}
