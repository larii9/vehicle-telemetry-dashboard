package com.telemetry.service;

import com.telemetry.model.TelemetryData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class VehicleSimulator {

    private static final double MAX_SPEED = 280;
    private static final int MAX_RPM = 8000;
    private static final int IDLE_RPM = 800;
    
    private final Random random = new Random();
    
    private double speed = 0;
    private int rpm = IDLE_RPM;
    private double engineTemp = 90;
    private double battery = 100;
    private double fuel = 100;
    private double throttle = 0;
    private double brake = 0;
    private String gear = "P";
    
    private double targetThrottle = 0;
    private double targetBrake = 0;

    public synchronized void update() {
        throttle += (targetThrottle - throttle) * 0.1;
        brake += (targetBrake - brake) * 0.1;
        
        autoBehavior();
        
        if (!gear.equals("P") && !gear.equals("N")) {
            if (throttle > 0) {
                speed = Math.min(MAX_SPEED, speed + throttle * 0.8);
            }
            if (brake > 0) {
                speed = Math.max(0, speed - brake * 1.2);
            }
            if (throttle < 0.1 && brake < 0.1 && speed > 0) {
                speed = Math.max(0, speed - 0.2);
            }
        } else {
            speed *= 0.95;
            if (speed < 0.1) speed = 0;
        }
        
        updateRpm();
        updateTemperature();
        updateFuel();
        updateBattery();
        autoShift();
    }

    private void autoBehavior() {
        if (random.nextDouble() < 0.02) {
            if (random.nextDouble() < 0.7) {
                targetThrottle = random.nextDouble() * 0.6 + 0.2;
                targetBrake = 0;
                if (gear.equals("P")) gear = "1";
            } else {
                targetThrottle = 0;
                targetBrake = random.nextDouble() * 0.5 + 0.2;
            }
        }
        if (random.nextDouble() < 0.01) {
            targetThrottle = random.nextDouble() * 0.3;
            targetBrake = 0;
        }
    }

    private void updateRpm() {
        int targetRpm;
        if (gear.equals("P") || gear.equals("N")) {
            targetRpm = IDLE_RPM + (int)(throttle * 2000);
        } else {
            int gearNum = gear.equals("R") ? 1 : Integer.parseInt(gear);
            targetRpm = IDLE_RPM + (int)(speed * 25 * (9 - gearNum) / 8);
        }
        rpm = Math.min(MAX_RPM, Math.max(IDLE_RPM, rpm + (targetRpm - rpm) / 10 + random.nextInt(100) - 50));
    }

    private void updateTemperature() {
        double heatGen = (rpm / (double)MAX_RPM) * 0.5 + throttle * 0.3;
        double cooling = (speed / MAX_SPEED) * 0.3 + 0.1;
        engineTemp += (heatGen - cooling) * 0.5;
        engineTemp += (90 - engineTemp) * 0.01;
        engineTemp = Math.max(70, Math.min(120, engineTemp));
    }

    private void updateFuel() {
        if (!gear.equals("P") && !gear.equals("N") && speed > 0) {
            fuel = Math.max(0, fuel - rpm / (double)MAX_RPM * 0.005 - throttle * 0.002);
        }
    }

    private void updateBattery() {
        if (rpm > IDLE_RPM) {
            battery = Math.min(100, battery + 0.005);
        } else {
            battery = Math.max(0, battery - 0.001);
        }
    }

    private void autoShift() {
        if (gear.equals("P") || gear.equals("R") || gear.equals("N")) return;
        int current = Integer.parseInt(gear);
        if (rpm > 6000 && current < 8) gear = String.valueOf(current + 1);
        else if (rpm < 1500 && current > 1) gear = String.valueOf(current - 1);
    }

    public synchronized TelemetryData getTelemetry() {
        List<String> warnings = new ArrayList<>();
        if (engineTemp > 105) warnings.add("HIGH_TEMP");
        if (fuel < 10) warnings.add("LOW_FUEL");
        if (battery < 20) warnings.add("LOW_BATTERY");
        if (rpm > 7000) warnings.add("HIGH_RPM");

        return TelemetryData.builder()
                .timestamp(System.currentTimeMillis())
                .speed(Math.round(speed * 10) / 10.0)
                .rpm(rpm)
                .engineTemp(Math.round(engineTemp * 10) / 10.0)
                .battery(Math.round(battery * 10) / 10.0)
                .fuel(Math.round(fuel * 10) / 10.0)
                .throttle(Math.round(throttle * 1000) / 10.0)
                .brake(Math.round(brake * 1000) / 10.0)
                .gear(gear)
                .warnings(warnings)
                .build();
    }

    public synchronized void accelerate(double intensity) {
        targetThrottle = Math.max(0, Math.min(1, intensity));
        targetBrake = 0;
        if (gear.equals("P")) gear = "1";
    }

    public synchronized void brake(double intensity) {
        targetBrake = Math.max(0, Math.min(1, intensity));
        targetThrottle = 0;
    }

    public synchronized void setGear(String g) {
        gear = g;
    }

    public synchronized void reset() {
        speed = 0;
        rpm = IDLE_RPM;
        engineTemp = 90;
        battery = 100;
        fuel = 100;
        throttle = 0;
        brake = 0;
        gear = "P";
        targetThrottle = 0;
        targetBrake = 0;
    }
}
