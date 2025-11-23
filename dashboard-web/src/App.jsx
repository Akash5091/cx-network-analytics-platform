import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import {
  LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer
} from "recharts";

const API = "http://localhost:8080/api/kpis/latest?limit=200";

export default function App() {
  const [kpis, setKpis] = useState([]);
  const [region, setRegion] = useState("ALL");
  const [app, setApp] = useState("ALL");

  useEffect(() => {
    axios.get(API).then(r => setKpis(r.data));
    const id = setInterval(() => axios.get(API).then(r => setKpis(r.data)), 10000);
    return () => clearInterval(id);
  }, []);

  const regions = useMemo(() => ["ALL", ...new Set(kpis.map(k => k.region))], [kpis]);
  const apps = useMemo(() => ["ALL", ...new Set(kpis.map(k => k.appName))], [kpis]);

  const filtered = kpis.filter(k =>
    (region === "ALL" || k.region === region) &&
    (app === "ALL" || k.appName === app)
  ).map(k => ({
    time: new Date(k.windowEnd).toLocaleTimeString(),
    qoe: k.qoeScore,
    latency: k.avgLatencyMs,
    jitter: k.avgJitterMs,
    loss: k.avgPacketLossPct
  })).reverse();

  const latest = filtered[filtered.length - 1];

  return (
    <div style={{ fontFamily: "system-ui", padding: 20 }}>
      <h2>CX & Network Analytics Dashboard</h2>

      <div style={{ display:"flex", gap:12, marginBottom:16 }}>
        <label>
          Region:
          <select value={region} onChange={e=>setRegion(e.target.value)} style={{ marginLeft:6 }}>
            {regions.map(r => <option key={r}>{r}</option>)}
          </select>
        </label>

        <label>
          App:
          <select value={app} onChange={e=>setApp(e.target.value)} style={{ marginLeft:6 }}>
            {apps.map(a => <option key={a}>{a}</option>)}
          </select>
        </label>
      </div>

      {latest && (
        <div style={{ display:"flex", gap:12, marginBottom:20 }}>
          <KpiCard title="QoE Score" value={latest.qoe.toFixed(1)} />
          <KpiCard title="Latency (ms)" value={latest.latency.toFixed(1)} />
          <KpiCard title="Jitter (ms)" value={latest.jitter.toFixed(1)} />
          <KpiCard title="Packet Loss (%)" value={latest.loss.toFixed(2)} />
        </div>
      )}

      <ChartBlock title="QoE over time" data={filtered} dataKey="qoe" />
      <ChartBlock title="Latency over time" data={filtered} dataKey="latency" />
    </div>
  );
}

function KpiCard({title, value}) {
  return (
    <div style={{
      border:"1px solid #ddd", borderRadius:10, padding:12, minWidth:160
    }}>
      <div style={{ fontSize:12, color:"#666" }}>{title}</div>
      <div style={{ fontSize:24, fontWeight:700 }}>{value}</div>
    </div>
  );
}

function ChartBlock({title, data, dataKey}) {
  return (
    <div style={{ border:"1px solid #eee", borderRadius:10, padding:12, marginBottom:16 }}>
      <h4 style={{ margin:"4px 0 12px" }}>{title}</h4>
      <div style={{ width:"100%", height:240 }}>
        <ResponsiveContainer>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="time" />
            <YAxis />
            <Tooltip />
            <Line type="monotone" dataKey={dataKey} strokeWidth={2} dot={false} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}