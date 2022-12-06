package io.prometheus.metrics.model;

public enum MetricType {
    COUNTER,
    GAUGE,
    SUMMARY,
    UNKNOWN,
    EXPLICIT_BUCKETS_HISTOGRAM,
    EXPONENTIAL_BUCKETS_HISTOGRAM,
    GAUGE_HISTOGRAM,
    STATE_SET,
    INFO
}