package io.prometheus.metrics.model.snapshots;

import javax.annotation.Nullable;

@SuppressWarnings("this-escape")
public abstract class DataPointSnapshot {
  @Nullable private final String metricName;
  private final Labels labels;
  private final long createdTimestampMillis;
  private final long scrapeTimestampMillis;

  protected DataPointSnapshot(
      Labels labels, long createdTimestampMillis, long scrapeTimestampMillis) {
    this(labels, createdTimestampMillis, scrapeTimestampMillis, null);
  }

  protected DataPointSnapshot(
      Labels labels,
      long createdTimestampMillis,
      long scrapeTimestampMillis,
      @Nullable String metricName) {
    this.metricName = metricName;
    this.labels = labels;
    this.createdTimestampMillis = createdTimestampMillis;
    this.scrapeTimestampMillis = scrapeTimestampMillis;
    validate();
  }

  private void validate() {
    if (metricName != null && metricName.trim().isEmpty()) {
      throw new IllegalArgumentException("Metric name cannot be an empty string");
    }

    if (labels == null) {
      throw new IllegalArgumentException(
          "Labels cannot be null. Use Labels.EMPTY if there are no labels.");
    }
    if (createdTimestampMillis < 0) {
      throw new IllegalArgumentException(
          "Created timestamp cannot be negative. Use 0 if the metric doesn't have a created timestamp.");
    }
    if (scrapeTimestampMillis < 0) {
      throw new IllegalArgumentException(
          "Scrape timestamp cannot be negative. Use 0 to indicate that the Prometheus server should set the scrape timestamp.");
    }
    if (hasCreatedTimestamp() && hasScrapeTimestamp()) {
      if (scrapeTimestampMillis < createdTimestampMillis) {
        throw new IllegalArgumentException(
            "The scrape timestamp cannot be before the created timestamp");
      }
    }
  }

  public Labels getLabels() {
    return labels;
  }

  public boolean hasScrapeTimestamp() {
    return scrapeTimestampMillis != 0L;
  }

  /** This will only return a reasonable value if {@link #hasScrapeTimestamp()} is true. */
  public long getScrapeTimestampMillis() {
    return scrapeTimestampMillis;
  }

  public boolean hasCreatedTimestamp() {
    return createdTimestampMillis != 0L;
  }

  /**
   * This will only return a reasonable value if {@link #hasCreatedTimestamp()} is true. Some
   * metrics like Gauge don't have created timestamps. For these metrics {@link
   * #hasCreatedTimestamp()} is always false.
   */
  public long getCreatedTimestampMillis() {
    return createdTimestampMillis;
  }

  public String getMetricName() {
    return metricName;
  }

  public abstract static class Builder<T extends Builder<T>> {

    protected Labels labels = Labels.EMPTY;
    @Nullable protected String metricName = null;
    protected long scrapeTimestampMillis = 0L;

    public T labels(Labels labels) {
      this.labels = labels;
      return self();
    }

    /** Metric name for error messages */
    public Builder metricName(@Nullable String metricName) {
      this.metricName = metricName;
      return self();
    }

    /**
     * In most cases you should not set a scrape timestamp, because the scrape timestamp is set by
     * the Prometheus server during scraping. Exceptions include mirroring metrics with given
     * timestamps from other metric sources.
     */
    public T scrapeTimestampMillis(long scrapeTimestampMillis) {
      this.scrapeTimestampMillis = scrapeTimestampMillis;
      return self();
    }

    protected abstract T self();
  }
}
