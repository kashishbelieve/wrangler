public class ByteSize extends Token {
  private final long bytes;

  public ByteSize(String value) {
    super(value);
    this.bytes = parseByteSize(value);
  }

  private long parseByteSize(String value) {
    String unit = value.replaceAll("[0-9.]", "").toUpperCase();
    double number = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
    switch (unit) {
      case "B": return (long) number;
      case "KB": return (long) (number * 1024);
      case "MB": return (long) (number * 1024 * 1024);
      case "GB": return (long) (number * 1024 * 1024 * 1024);
      // Add more if needed
      default: throw new IllegalArgumentException("Unknown byte unit: " + unit);
    }
  }

  public long getBytes() { return bytes; }
}
