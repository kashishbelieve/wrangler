package io.cdap.wrangler.api.parser;

public class TimeDuration extends Token {
    private final long nanoseconds;

    public TimeDuration(String text) {
        super(text);
        this.nanoseconds = parseTimeDuration(text);
    }

    private long parseTimeDuration(String text) {
        // Parse the duration in nanoseconds (e.g., "150ms" => 150000000 nanoseconds)
        String unit = text.replaceAll("[0-9.]", "").toUpperCase();
        long value = Long.parseLong(text.replaceAll("[^0-9]", ""));

        switch (unit) {
            case "MS":
                return value * 1_000_000; // milliseconds to nanoseconds
            case "S":
                return value * 1_000_000_000; // seconds to nanoseconds
            case "M":
                return value * 60 * 1_000_000_000; // minutes to nanoseconds
            case "H":
                return value * 60 * 60 * 1_000_000_000; // hours to nanoseconds
            default:
                return value; // Default in nanoseconds
        }
    }

    public long getNanoseconds() {
        return nanoseconds;
    }
}
