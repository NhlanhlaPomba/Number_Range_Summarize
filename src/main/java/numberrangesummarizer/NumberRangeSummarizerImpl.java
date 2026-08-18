package numberrangesummarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;


public class NumberRangeSummarizerImpl implements NumberRangeSummarizer {

    private static final String INPUT_DELIMITER = ",";
    private static final String OUTPUT_DELIMITER = ", ";
    private static final String RANGE_DELIMITER = "-";


    @Override
    public Collection<Integer> collect(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> result = new ArrayList<>();
        for (String token : input.split(INPUT_DELIMITER)) {
            String trimmed = token.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                result.add(Integer.valueOf(trimmed));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Invalid integer value in input: '" + trimmed + "'", e);
            }
        }
        return result;
    }


    @Override
    public String summarizeCollection(Collection<Integer> input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        List<Integer> sorted = input.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        StringJoiner joiner = new StringJoiner(OUTPUT_DELIMITER);

        int rangeStart = sorted.get(0);
        int rangeEnd = rangeStart;

        for (int i = 1; i <= sorted.size(); i++) {
            boolean isLast = i == sorted.size();
            Integer current = isLast ? null : sorted.get(i);

            if (!isLast && current == rangeEnd + 1) {
                // still sequential, extend the current range
                rangeEnd = current;
                continue;
            }

            joiner.add(formatRange(rangeStart, rangeEnd));

            if (!isLast) {
                rangeStart = current;
                rangeEnd = current;
            }
        }

        return joiner.toString();
    }

    private String formatRange(int start, int end) {
        return start == end
                ? String.valueOf(start)
                : start + RANGE_DELIMITER + end;
    }
}
