package com.cx.api.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "experience_kpis")
public class ExperienceKpi {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Instant windowStart;
  private Instant windowEnd;

  private String region;
  private String deviceType;
  private String appName;

  private double avgLatencyMs;
  private double avgJitterMs;
  private double avgPacketLossPct;
  private double avgSignalDbm;
  private double qoeScore;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Instant getWindowStart() { return windowStart; }
  public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }

  public Instant getWindowEnd() { return windowEnd; }
  public void setWindowEnd(Instant windowEnd) { this.windowEnd = windowEnd; }

  public String getRegion() { return region; }
  public void setRegion(String region) { this.region = region; }

  public String getDeviceType() { return deviceType; }
  public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

  public String getAppName() { return appName; }
  public void setAppName(String appName) { this.appName = appName; }

  public double getAvgLatencyMs() { return avgLatencyMs; }
  public void setAvgLatencyMs(double avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }

  public double getAvgJitterMs() { return avgJitterMs; }
  public void setAvgJitterMs(double avgJitterMs) { this.avgJitterMs = avgJitterMs; }

  public double getAvgPacketLossPct() { return avgPacketLossPct; }
  public void setAvgPacketLossPct(double avgPacketLossPct) { this.avgPacketLossPct = avgPacketLossPct; }

  public double getAvgSignalDbm() { return avgSignalDbm; }
  public void setAvgSignalDbm(double avgSignalDbm) { this.avgSignalDbm = avgSignalDbm; }

  public double getQoeScore() { return qoeScore; }
  public void setQoeScore(double qoeScore) { this.qoeScore = qoeScore; }
}