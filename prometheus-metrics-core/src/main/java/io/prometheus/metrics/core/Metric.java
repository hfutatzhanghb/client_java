package io.prometheus.metrics.core;

import io.prometheus.metrics.model.Labels;
import io.prometheus.metrics.model.MetricMetadata;
import io.prometheus.metrics.model.MetricSnapshot;
import io.prometheus.metrics.model.MetricType;

public abstract class Metric {

    private final MetricMetadata metadata;
    protected final Labels constLabels;

    protected Metric(Builder<?, ?> builder) {
        this.metadata = new MetricMetadata(makeName(builder.name, builder.unit), builder.help, builder.getType(), builder.unit);
        this.constLabels = builder.constLabels;
    }

    private String makeName(String name, String unit) {
        if (unit != null) {
            String suffix = "_" + unit;
            if (!name.endsWith(suffix)) {
                name = name + suffix;
            }
        }
        return name;
    }

    public abstract MetricSnapshot collect();

    protected MetricMetadata getMetadata() {
        return metadata;
    }

    static abstract class Builder<B extends Builder<B, M>, M extends Metric> {
        protected String name;
        private String unit;
        private String help;
        private Labels constLabels = Labels.EMPTY;

        protected Builder() {}

        protected abstract MetricType getType();
        public B withName(String name) {
            this.name = name;
            return self();
        }

        public B withUnit(String unit) {
            this.unit = unit;
            return self();
        }

        public B withHelp(String help) {
            this.help = help;
            return self();
        }

        // ConstLabels are only used rarely. In particular, do not use them to
        // attach the same labels to all your metrics. Those use cases are
        // better covered by target labels set by the scraping Prometheus
        // server, or by one specific metric (e.g. a build_info or a
        // machine_role metric). See also
        // https://prometheus.io/docs/instrumenting/writing_exporters/#target-labels-not-static-scraped-labels
        public B withConstLabels(Labels constLabels) {
            this.constLabels = constLabels;
            return self();
        }

        public abstract M build();

        /*
        public M register() {
            return register(PrometheusRegistry.defaultRegistry);
        }

        public M register(PrometheusRegistry registry) {
            M metric = build();
            registry.register(metric);
            return metric;
        }
         */

        protected abstract B self();
    }
}
