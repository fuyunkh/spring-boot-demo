package com.example.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zhangkh on 2018/5/8.
 */
public class GaugesExample {
    /**
     * 实例化一个registry，最核心的一个模块，相当于一个应用程序的metrics系统的容器，维护一个Map
     */
    private static final MetricRegistry metrics = new MetricRegistry();

    private static Queue<String> queue = new LinkedBlockingDeque<String>();

    /**
     * ConsoleReporter 在控制台上打印输出
     */
    private static ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).build();

    /**
     * 实例化一个Meter
     */
    private static final Meter requests = metrics.meter(MetricRegistry.name(GaugesExample.class, "request"));


    public static void handleRequest() {
        requests.mark();
    }

    public static void testGauges() throws InterruptedException {
        //实例化一个Gauge
        Gauge<Integer> gauge = new Gauge<Integer>() {
            public Integer getValue() {
                return queue.size();
            }
        };


        //注册到容器中
        metrics.register(MetricRegistry.name(GaugesExample.class, "pending.job.size"), gauge);


        //模拟数据
        for (int i = 0; i < 1000; i++) {
            queue.add("a");
            Thread.sleep(1000);
        }
    }

    public static void testMeter() throws InterruptedException {
        while (true) {
            handleRequest();
            Thread.sleep(100);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        reporter.start(5, TimeUnit.SECONDS);
        testMeter();
    }
}
