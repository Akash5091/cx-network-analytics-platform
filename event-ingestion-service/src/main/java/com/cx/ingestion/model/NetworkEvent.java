package com.cx.ingestion.model;

import java.time.Instant;

public record NetworkEvent(
    String eventId,
    Instant timestamp,
    String userId,
    String region,
    String deviceType,
    String appName,
    double latencyMs,
    double jitterMs,
    double packetLossPct,
    int signalStrengthDbm
) {}