package com.cx.analytics.kafka;

import com.cx.analytics.model.NetworkEvent;
import com.cx.analytics.service.WindowAggregator;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
  private final WindowAggregator aggregator;

  public EventConsumer(WindowAggregator aggregator) {
    this.aggregator = aggregator;
  }

  @KafkaListener(topics = "${topics.network-events}")
  public void consume(NetworkEvent event) {
    aggregator.add(event);
  }
}