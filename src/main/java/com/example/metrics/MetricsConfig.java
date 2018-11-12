package com.example.metrics;
//
//import com.codahale.metrics.ConsoleReporter;
//import com.codahale.metrics.MetricFilter;
//import com.codahale.metrics.MetricRegistry;

//import com.codahale.metrics.ConsoleReporter;
//import com.codahale.metrics.MetricRegistry;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.example.util.ip.IPUtil;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import metrics_influxdb.HttpInfluxdbProtocol;
import metrics_influxdb.InfluxdbReporter;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by Zhangkh on 2018/5/8.
 */
@Configuration
@EnableMetrics
public class MetricsConfig extends MetricsConfigurerAdapter {

    @Override
    public void configureReporters(MetricRegistry metricRegistry) {
        // registerReporter allows the MetricsConfigurerAdapter to
        // shut down the reporter when the Spring context is closed
//        registerReporter(ConsoleReporter
//                .forRegistry(metricRegistry)
//                .build())
//                .start(1, TimeUnit.MINUTES);

        registerReporter(InfluxdbReporter
                .forRegistry(metricRegistry)
                .protocol(new HttpInfluxdbProtocol("http", "10.29.37.123", 8086, "metrics", "123456", "metrics"))
                .filter(MetricFilter.ALL)
                .tag("app", "taskcenter")
                .tag("host", IPUtil.getLocalIPAddress())
                .build())
                .start(1, TimeUnit.MINUTES);

//        final ScheduledReporter reporter = InfluxdbReporter.forRegistry(registry)
//                .protocol(new HttpInfluxdbProtocol("http", "influxdb-server", 8086, "admin", "53CR3TP455W0RD", "metrics"))
//                .convertRatesTo(TimeUnit.SECONDS)
//                .convertDurationsTo(TimeUnit.MILLISECONDS)
//                .filter(MetricFilter.ALL)
//                .skipIdleMetrics(false)
//                .tag("cluster", "CL01")
//                .tag("client", "OurImportantClient")
//                .tag("server", serverIP)
//                .transformer(new CategoriesMetricMeasurementTransformer("module", "artifact"))
//                .build();
//        reporter.start(10, TimeUnit.SECONDS);

    }

}