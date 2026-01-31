package com.telemetry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryData {
    private long timestamp;
    private double speed;
    private int rpm;
    private double engineTemp;
    private double battery;
    private double fuel;
    private double throttle;
    private double brake;
    private String gear;
    private List<String> warnings;
}
