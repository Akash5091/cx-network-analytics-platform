package com.cx.ingestion.controller;

import com.cx.ingestion.kafka.EventProducer;
import com.cx.ingestion.model.NetworkEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {
  private final EventProducer producer;

  public EventController(EventProducer producer) {
    this.producer = producer;
  }

  @PostMapping("/network")
  public ResponseEntity<?> ingest(@RequestBody NetworkEvent event) {
    producer.publish(event);
    return ResponseEntity.accepted().body(
        java.util.Map.of("status", "queued", "eventId", event.eventId())
    );
  }
}