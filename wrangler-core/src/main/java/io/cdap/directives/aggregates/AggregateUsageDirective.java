package io.cdap.wrangler.directive.aggregate;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.annotations.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.api.row.Row;
import io.cdap.wrangler.api.row.Record;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Store;
import io.cdap.wrangler.api.DirectiveContext;

import java.util.*;

/**
 * Directive to aggregate ByteSize and TimeDuration fields.
 */
@Directive(
    name = "aggregate-usage",
    type = DirectiveType.AGGREGATE,
    description = "Aggregates byte size and time duration columns to generate totals or averages."
)
public class AggregateUsageDirective implements Directive {
    private String byteSizeColumn;
    private String timeDurationColumn;
    private String targetByteSizeColumn;
    private String targetTimeDurationColumn;
    private String outputSizeUnit = "MB";
    private String outputTimeUnit = "seconds";
    private String aggregationType = "total"; // or "average"

    @Override
    public UsageDefinition define() {
        return UsageDefinition.builder()
            .define("byteSizeColumn", TokenType.COLUMN_NAME)
            .define("timeDurationColumn", TokenType.COLUMN_NAME)
            .define("targetByteSizeColumn", TokenType.COLUMN_NAME)
            .define("targetTimeDurationColumn", TokenType.COLUMN_NAME)
            .defineOptional("outputSizeUnit", TokenType.LITERAL)
            .defineOptional("outputTimeUnit", TokenType.LITERAL)
            .defineOptional("aggregationType", TokenType.LITERAL)
            .build();
    }

    @Override
    public void initialize(Arguments arguments) throws DirectiveParseException {
        byteSizeColumn = arguments.value("byteSizeColumn");
        timeDurationColumn = arguments.value("timeDurationColumn");
        targetByteSizeColumn = arguments.value("targetByteSizeColumn");
        targetTimeDurationColumn = arguments.value("targetTimeDurationColumn");

        if (arguments.contains("outputSizeUnit")) {
            outputSizeUnit = arguments.value("outputSizeUnit");
        }

        if (arguments.contains("outputTimeUnit")) {
            outputTimeUnit = arguments.value("outputTimeUnit");
        }

        if (arguments.contains("aggregationType")) {
            aggregationType = arguments.value("aggregationType");
        }
    }

    @Override
    public void execute(Row row, ExecutorContext ctx) throws DirectiveExecutionException {
        Store store = ctx.getStore();
        long currentTotalSize = store.getOrDefault("totalSizeBytes", 0L);
        long currentTotalTime = store.getOrDefault("totalTimeNanos", 0L);
        int count = store.getOrDefault("rowCount", 0);

        Object byteSizeValue = row.getValue(byteSizeColumn);
        Object timeDurationValue = row.getValue(timeDurationColumn);

        long bytes = toBytes(byteSizeValue.toString());
        long nanos = toNanos(timeDurationValue.toString());

        store.set("totalSizeBytes", currentTotalSize + bytes);
        store.set("totalTimeNanos", currentTotalTime + nanos);
        store.set("rowCount", count + 1);
    }

    @Override
    public List<Row> aggregate(ExecutorContext ctx) throws DirectiveExecutionException {
        Store store = ctx.getStore();
        long totalSizeBytes = store.getOrDefault("totalSizeBytes", 0L);
        long totalTimeNanos = store.getOrDefault("totalTimeNanos", 0L);
        int count = store.getOrDefault("rowCount", 0);

        double resultSize = aggregationType.equals("average") ? (double) totalSizeBytes / count : totalSizeBytes;
        double resultTime = aggregationType.equals("average") ? (double) totalTimeNanos / count : totalTimeNanos;

        // Convert to final units
        resultSize = convertSize(resultSize, outputSizeUnit);
        resultTime = convertTime(resultTime, outputTimeUnit);

        Row resultRow = new Row();
        resultRow.add(targetByteSizeColumn, resultSize);
        resultRow.add(targetTimeDurationColumn, resultTime);

        return Collections.singletonList(resultRow);
    }

    private long toBytes(String value) {
        value = value.toLowerCase();
        if (value.endsWith("kb")) {
            return (long)(Double.parseDouble(value.replace("kb", "")) * 1024);
        } else if (value.endsWith("mb")) {
            return (long)(Double.parseDouble(value.replace("mb", "")) * 1024 * 1024);
        } else if (value.endsWith("gb")) {
            return (long)(Double.parseDouble(value.replace("gb", "")) * 1024 * 1024 * 1024);
        } else {
            return Long.parseLong(value); // assume bytes
        }
    }

    private long toNanos(String value) {
        value = value.toLowerCase();
        if (value.endsWith("ms")) {
            return (long)(Double.parseDouble(value.replace("ms", "")) * 1_000_000);
        } else if (value.endsWith("s")) {
            return (long)(Double.parseDouble(value.replace("s", "")) * 1_000_000_000);
        } else if (value.endsWith("m")) {
            return (long)(Double.parseDouble(value.replace("m", "")) * 60L * 1_000_000_000);
        } else {
            return Long.parseLong(value); // assume nanoseconds
        }
    }

    private double convertSize(double bytes, String unit) {
        switch (unit.toLowerCase()) {
            case "kb": return bytes / 1024;
            case "mb": return bytes / (1024 * 1024);
            case "gb": return bytes / (1024 * 1024 * 1024);
            default: return bytes; // default is bytes
        }
    }

    private double convertTime(double nanos, String unit) {
        switch (unit.toLowerCase()) {
            case "ms": return nanos / 1_000_000;
            case "s":
            case "sec":
            case "seconds": return nanos / 1_000_000_000;
            case "m":
            case "min":
            case "minutes": return nanos / (60 * 1_000_000_000L);
            default: return nanos; // default nanoseconds
        }
    }
}

