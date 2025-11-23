#!/bin/bash

# Sample script to send test events to the platform

API_URL="http://localhost:8081/events/network"

echo "Sending sample network events..."

# Event 1: Dallas, Android
curl -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "eventId":"e-1001",
    "timestamp":"'$(date -u +%Y-%m-%dT%H:%M:%SZ)'",
    "userId":"u-1",
    "region":"Dallas",
    "deviceType":"Android",
    "appName":"VideoStream",
    "latencyMs":85.2,
    "jitterMs":4.1,
    "packetLossPct":0.4,
    "signalStrengthDbm":-82
  }'

echo -e "\n\nEvent 1 sent!"

# Event 2: New York, iOS
curl -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "eventId":"e-1002",
    "timestamp":"'$(date -u +%Y-%m-%dT%H:%M:%SZ)'",
    "userId":"u-2",
    "region":"New York",
    "deviceType":"iOS",
    "appName":"SocialMedia",
    "latencyMs":42.5,
    "jitterMs":2.3,
    "packetLossPct":0.1,
    "signalStrengthDbm":-68
  }'

echo -e "\n\nEvent 2 sent!"

# Event 3: San Francisco, Android
curl -X POST "$API_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "eventId":"e-1003",
    "timestamp":"'$(date -u +%Y-%m-%dT%H:%M:%SZ)'",
    "userId":"u-3",
    "region":"San Francisco",
    "deviceType":"Android",
    "appName":"Gaming",
    "latencyMs":120.8,
    "jitterMs":8.5,
    "packetLossPct":1.2,
    "signalStrengthDbm":-95
  }'

echo -e "\n\nEvent 3 sent!"
echo -e "\nAll sample events sent successfully!"
echo "Check the dashboard at http://localhost:3000"