package io.cdap.wrangler.api.parser;

public class ByteSize extends Token {
    private final long bytes;

    public ByteSize(String text) {
        super(text);
        this.bytes = parseByteSize(text);
    }

    private long parseByteSize(String text) {
        // Parse the size in bytes (e.g., "10KB" => 10240 bytes)
        String unit = text.replaceAll("[0-9.]", "").toUpperCase();
        long value = Long.parseLong(text.replaceAll("[^0-9]", ""));

        switch (unit) {
            case "KB":
                return value * 1024;
            case "MB":
                return value * 1024 * 1024;
            case "GB":
                return value * 1024 * 1024 * 1024;
            case "TB":
                return value * 1024 * 1024 * 1024 * 1024;
            default:
                return value; // Default is in bytes
        }
    }

    public long getBytes() {
        return bytes;
    }
}

    @Override
    public String value() {
        return String.valueOf(bytes);
    }
}
