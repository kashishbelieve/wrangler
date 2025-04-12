@Test
public void testTimeDurationParsing() {
    TimeDuration time1 = new TimeDuration("5ms");
    Assert.assertEquals(5_000_000L, time1.getValue()); // in nanoseconds

    TimeDuration time2 = new TimeDuration("2.1s");
    Assert.assertEquals(2_100_000_000L, time2.getValue());
}
