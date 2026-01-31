# Vehicle Telemetry Dashboard

Real-time vehicle telemetry visualization with simulated sensor data.

## Screenshots

### Dashboard Overview
<img width="1510" height="686" alt="Captura de Tela 2026-01-31 às 16 11 12" src="https://github.com/user-attachments/assets/7891f8b7-f627-4926-aaac-80b9352a8972" />


### High Speed Driving
<img width="1504" height="694" alt="Captura de Tela 2026-01-31 às 16 12 14" src="https://github.com/user-attachments/assets/d1a68ba0-7496-4ecf-a54e-6ac24ed347cd" />


### Braking
<img width="1504" height="659" alt="Captura de Tela 2026-01-31 às 16 14 09" src="https://github.com/user-attachments/assets/c574816c-f1cd-4ac8-ad39-f38e641bb38f" />


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

## Author

Larissa Oliveira - Software Engineering @ Universität Stuttgart
