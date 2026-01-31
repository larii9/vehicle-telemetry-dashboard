package com.telemetry.controller;

import com.telemetry.model.TelemetryData;
import com.telemetry.service.VehicleSimulator;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TelemetryController {

    private final VehicleSimulator simulator;

    public TelemetryController(VehicleSimulator simulator) {
        this.simulator = simulator;
    }

    @GetMapping("/telemetry")
    public TelemetryData getTelemetry() {
        return simulator.getTelemetry();
    }

    @PostMapping("/control/accelerate")
    public Map<String, Object> accelerate(@RequestParam(defaultValue = "0.5") double intensity) {
        simulator.accelerate(intensity);
        return Map.of("status", "ok", "intensity", intensity);
    }

    @PostMapping("/control/brake")
    public Map<String, Object> brake(@RequestParam(defaultValue = "0.5") double intensity) {
        simulator.brake(intensity);
        return Map.of("status", "ok", "intensity", intensity);
    }

    @PostMapping("/control/gear")
    public Map<String, Object> setGear(@RequestParam String gear) {
        simulator.setGear(gear.toUpperCase());
        return Map.of("status", "ok", "gear", gear.toUpperCase());
    }

    @PostMapping("/control/reset")
    public Map<String, String> reset() {
        simulator.reset();
        return Map.of("status", "ok");
    }
}
