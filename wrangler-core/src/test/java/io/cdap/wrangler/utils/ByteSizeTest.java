@Test
public void testByteSizeParsing() {
    ByteSize size1 = new ByteSize("10kb");
    Assert.assertEquals(10240L, size1.getValue()); // 10 * 1024

    ByteSize size2 = new ByteSize("1.5MB");
    Assert.assertEquals(1572864L, size2.getValue()); // 1.5 * 1024 * 1024
}
