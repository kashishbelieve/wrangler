@Test
public void testAggregateStatsDirective() throws Exception {
    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "10KB").add("response_time", "500ms"));
    rows.add(new Row("data_transfer_size", "1MB").add("response_time", "1.5s"));

    String[] recipe = {
        "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    List<Row> results = TestingRig.execute(recipe, rows);

    Assert.assertEquals(1, results.size());

    double expectedTotalSizeMB = (10240 + 1024 * 1024) / (1024.0 * 1024.0);
    double expectedTotalTimeSec = (500_000_000L + 1_500_000_000L) / 1_000_000_000.0;

    Assert.assertEquals(expectedTotalSizeMB,
        (double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTotalTimeSec,
        (double) results.get(0).getValue("total_time_sec"), 0.001);
}
