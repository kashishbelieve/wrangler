package io.cdap.wrangler.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDuration implements Token {
    private static final Pattern PATTERN = Pattern.compile("(?i)(\\d+(\\.\\d+)?)(ms|s|sec|seconds|min|minutes|h|hours)");
    private final long nanoseconds;

    public TimeDuration(String value) {
        Matcher matcher = PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time duration format: " + value);
        }

        double number = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(3).toLowerCase();

        switch (unit) {
            case "ms":
                nanoseconds = (long) (number * 1_000_000);
                break;
            case "s":
            case "sec":
            case "seconds":
                nanoseconds = (long) (number * 1_000_000_000);
                break;
            case "min":
            case "minutes":
                nanoseconds = (long) (number * 60 * 1_000_000_000L);
                break;
            case "h":
            case "hours":
                nanoseconds = (long) (number * 3600 * 1_000_000_000L);
                break;
            default:
                throw new IllegalArgumentException("Unknown time unit: " + unit);
        }
    }

    public long getNanoseconds() {
        return nanoseconds;
    }

    @Override
    public String value() {
        return String.valueOf(nanoseconds);
    }
}
