# Vehicle Telemetry Dashboard

Real-time vehicle telemetry visualization with simulated sensor data.

## Tech Stack

- **Backend:** Java 17, Spring Boot 3, WebSocket
- **Frontend:** Vue 3, Vite, TailwindCSS

## Quick Start

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

## Telemetry Data

| Metric | Unit | Range |
|--------|------|-------|
| Speed | km/h | 0-280 |
| RPM | rpm | 800-8000 |
| Engine Temp | °C | 70-120 |
| Battery | % | 0-100 |
| Fuel | % | 0-100 |
| Throttle | % | 0-100 |

## API

| Endpoint | Description |
|----------|-------------|
| `WS /ws/telemetry` | Real-time data stream |
| `GET /api/telemetry` | Current snapshot |
| `POST /api/control/accelerate` | Simulate acceleration |
| `POST /api/control/brake` | Simulate braking |

## Author

Larissa Oliveira - Software Engineering @ Universität Stuttgart
