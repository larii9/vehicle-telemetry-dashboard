package com.telemetry.controller;

import com.telemetry.service.VehicleSimulator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class TelemetryWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final VehicleSimulator simulator;
    private final ObjectMapper mapper = new ObjectMapper();

    public TelemetryWebSocketHandler(VehicleSimulator simulator) {
        this.simulator = simulator;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Scheduled(fixedRate = 100)
    public void broadcast() {
        if (sessions.isEmpty()) return;
        
        simulator.update();
        
        try {
            String json = mapper.writeValueAsString(simulator.getTelemetry());
            TextMessage msg = new TextMessage(json);
            
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
