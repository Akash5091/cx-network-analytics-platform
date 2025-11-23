package com.cx.ingestion.kafka;

import com.cx.ingestion.model.NetworkEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
  private final KafkaTemplate<String, NetworkEvent> kafkaTemplate;

  @Value("${topics.network-events}")
  private String topic;

  public EventProducer(KafkaTemplate<String, NetworkEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(NetworkEvent event) {
    kafkaTemplate.send(topic, event.userId(), event);
  }
}