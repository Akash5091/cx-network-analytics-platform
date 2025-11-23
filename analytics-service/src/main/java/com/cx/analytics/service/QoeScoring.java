package com.cx.analytics.service;

import org.springframework.stereotype.Component;

@Component
public class QoeScoring {
  // Simple weighted score. You can tune later.
  public double score(double latency, double jitter, double loss, double signalDbm) {
    double latencyScore = clamp(100 - (latency / 5.0), 0, 100);
    double jitterScore  = clamp(100 - (jitter * 2.0), 0, 100);
    double lossScore    = clamp(100 - (loss * 15.0), 0, 100);
    double signalScore  = clamp((signalDbm + 120) * 1.25, 0, 100);

    return 0.4 * latencyScore + 0.25 * jitterScore + 0.25 * lossScore + 0.1 * signalScore;
  }

  private double clamp(double v, double min, double max) {
    return Math.max(min, Math.min(max, v));
  }
}