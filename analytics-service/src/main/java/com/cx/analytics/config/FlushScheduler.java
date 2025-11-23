package com.cx.analytics.config;

import com.cx.analytics.service.WindowAggregator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class FlushScheduler {
  private final WindowAggregator aggregator;

  public FlushScheduler(WindowAggregator aggregator) {
    this.aggregator = aggregator;
  }

  @Scheduled(fixedRate = 15000)
  public void flush() {
    aggregator.flushExpired();
  }
}