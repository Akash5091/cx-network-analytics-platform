package com.cx.analytics.service;

import com.cx.analytics.entity.ExperienceKpi;
import com.cx.analytics.model.NetworkEvent;
import com.cx.analytics.repo.ExperienceKpiRepo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class WindowAggregator {

  private final ExperienceKpiRepo repo;
  private final QoeScoring scoring;
  private final StringRedisTemplate redis;

  public WindowAggregator(ExperienceKpiRepo repo, QoeScoring scoring, StringRedisTemplate redis) {
    this.repo = repo;
    this.scoring = scoring;
    this.redis = redis;
  }

  // 5-min tumbling window in memory, flushed to DB
  private final Map<String, List<NetworkEvent>> buckets = new HashMap<>();

  public synchronized void add(NetworkEvent e) {
    Instant windowStart = e.timestamp().truncatedTo(ChronoUnit.MINUTES)
        .minusSeconds(e.timestamp().getEpochSecond() % 300); // align 5-min window
    Instant windowEnd = windowStart.plus(5, ChronoUnit.MINUTES);

    String key = windowStart + "|" + e.region() + "|" + e.deviceType() + "|" + e.appName();
    buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(e);

    // flush if bucket large
    if (buckets.get(key).size() >= 200) flush(key, windowStart, windowEnd);
  }

  public synchronized void flushExpired() {
    Instant now = Instant.now();
    for (String key : new ArrayList<>(buckets.keySet())) {
      String[] parts = key.split("\\|");
      Instant windowStart = Instant.parse(parts[0]);
      Instant windowEnd = windowStart.plus(5, ChronoUnit.MINUTES);
      if (windowEnd.isBefore(now.minusSeconds(30))) {
        flush(key, windowStart, windowEnd);
      }
    }
  }

  private void flush(String key, Instant ws, Instant we) {
    List<NetworkEvent> events = buckets.remove(key);
    if (events == null || events.isEmpty()) return;

    double avgLatency = events.stream().mapToDouble(NetworkEvent::latencyMs).average().orElse(0);
    double avgJitter  = events.stream().mapToDouble(NetworkEvent::jitterMs).average().orElse(0);
    double avgLoss    = events.stream().mapToDouble(NetworkEvent::packetLossPct).average().orElse(0);
    double avgSignal  = events.stream().mapToInt(NetworkEvent::signalStrengthDbm).average().orElse(0);

    String[] parts = key.split("\\|");
    String region = parts[1], device = parts[2], app = parts[3];

    double qoe = scoring.score(avgLatency, avgJitter, avgLoss, avgSignal);

    ExperienceKpi kpi = new ExperienceKpi();
    kpi.setWindowStart(ws);
    kpi.setWindowEnd(we);
    kpi.setRegion(region);
    kpi.setDeviceType(device);
    kpi.setAppName(app);
    kpi.setAvgLatencyMs(avgLatency);
    kpi.setAvgJitterMs(avgJitter);
    kpi.setAvgPacketLossPct(avgLoss);
    kpi.setAvgSignalDbm(avgSignal);
    kpi.setQoeScore(qoe);

    repo.save(kpi);

    // cache latest KPI for fast dashboard reads
    redis.opsForValue().set("latest:" + region + ":" + device + ":" + app,
        String.valueOf(qoe));
  }
}